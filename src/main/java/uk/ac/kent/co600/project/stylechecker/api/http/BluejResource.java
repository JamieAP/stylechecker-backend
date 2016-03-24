package uk.ac.kent.co600.project.stylechecker.api.http;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Ordering;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.eclipse.jetty.io.UncheckedPrintWriter;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;
import uk.ac.kent.co600.project.stylechecker.utils.TextReport;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

@Path("/bluej")
public class BluejResource {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String RESULTS_FILE_SUFFIX = "-results.txt";

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response auditSourceCode(
            @FormDataParam("file1") InputStream inputStream,
            @FormDataParam("file1") FormDataBodyPart bodyPart,
            @Context SourcesJarExtractor extractor,
            @Context CheckerFactory checkerFactory
    ) throws IOException, CheckstyleException {
        Checker checker = checkerFactory.createChecker();
        ExtractionResult extractionResult = extractor.extract(
                bodyPart.getContentDisposition().getFileName(), inputStream
        );
        AuditReport report = createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checker,
                extractionResult
        );

        TextReport textReport = new TextReport(report);
        textReport.generateSingleReport();
        List<String> textReportLines = textReport.getTextReport();
        StringBuilder response = new StringBuilder();

        response.append("Click <a href=\"http://stylechecker.jkeeys.co.uk\">HERE</a> to " +
                "pretty print your results<br><br>");
        textReportLines.forEach(line -> response.append(line + "<br>"));

        return Response.ok(response.toString(), MediaType.TEXT_HTML_TYPE).build();
    }

    @VisibleForTesting
    public AuditReport createAuditReport(
            Integer numberOfChecks,
            Checker checker,
            ExtractionResult extractionResult
    ) throws CheckstyleException {
        AuditReportGenerator auditor = new AuditReportGenerator(numberOfChecks);
        checker.addListener(auditor);
        ImmutableList<File> files = extractionResult.getExtractedFiles().stream()
                .map(ExtractedFile::getFile)
                .collect(ImmutableCollectors.toList());
        checker.process(files);
        return auditor.buildReport(extractionResult);
    }
}