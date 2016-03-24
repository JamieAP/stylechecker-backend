package uk.ac.kent.co600.project.stylechecker.api.http;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.ac.kent.co600.project.stylechecker.AuditScorer;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

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
import java.nio.charset.Charset;

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
            @Context CheckerFactory checkerFactory,
            @Context AuditScorer scorer
    ) throws IOException, CheckstyleException {
        Checker checker = checkerFactory.createChecker();
        ExtractionResult extractionResult = extractor.extract(
                bodyPart.getContentDisposition().getFileName(), inputStream
        );
        AuditReport report = createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checker,
                extractionResult,
                scorer
        );

        StringBuilder responseBody = new StringBuilder();
        responseBody.append("Click <a href=\"http://stylechecker.jkeeys.co.uk\">HERE</a> to " +
                "pretty print your results<br><br>");

        report.toText().forEach(line -> responseBody.append(line).append("<br>"));
        return Response.ok(responseBody.toString(), MediaType.TEXT_HTML_TYPE).build();
    }

    @VisibleForTesting
    public AuditReport createAuditReport(
            Integer numberOfChecks,
            Checker checker,
            ExtractionResult extractionResult,
            AuditScorer scorer) throws CheckstyleException {
        AuditReportGenerator auditor = new AuditReportGenerator(numberOfChecks, scorer);
        checker.addListener(auditor);
        ImmutableList<File> files = extractionResult.getExtractedFiles().stream()
                .map(ExtractedFile::getFile)
                .collect(ImmutableCollectors.toList());
        checker.process(files);
        return auditor.buildReport(extractionResult);
    }
}