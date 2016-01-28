package uk.ac.kent.co600.project.stylechecker.api.http;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Path("/check")
public class CheckerResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * The main audit endpoint. Submitting a JAR produces a report of on the compliance of any Java
     * source files to the CO320 style guide.
     */
    public AuditReport auditSourceCode(
            @FormDataParam("file") InputStream is,
            @FormDataParam("file") FormDataBodyPart bodyPart,
            @Context SourcesJarExtractor extractor,
            @Context CheckerFactory checkerFactory
    ) throws IOException, CheckstyleException {
        Checker checker = checkerFactory.createChecker();
        ExtractionResult extractionResult = extractor.extract(is);
        AuditReport report = createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checker,
                extractionResult
        );
        extractionResult.getExtractedFiles().stream().forEach(f -> f.getFile().delete());
        return report;
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

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        return Response.ok("\n" +
                "<form action=\"http://jamiep.net/check\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "    Select file to upload:\n" +
                "    <input type=\"file\" name=\"file\" id=\"file\">\n" +
                "    <input type=\"submit\" value=\"Upload File\" name=\"submit\">\n" +
                "</form>").build();
    }
}