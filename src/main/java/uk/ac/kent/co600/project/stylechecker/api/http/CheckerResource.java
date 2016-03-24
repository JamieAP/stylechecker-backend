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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Path("/check")
public class CheckerResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public AuditReport auditSourceCode(
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataBodyPart bodyPart,
            @Context SourcesJarExtractor extractor,
            @Context CheckerFactory checkerFactory,
            @Context AuditScorer auditScorer
    ) throws IOException, CheckstyleException {
        Checker checker = checkerFactory.createChecker();
        ExtractionResult extractionResult = extractor.extract(
                bodyPart.getContentDisposition().getFileName(), inputStream
        );
        AuditReport report = createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checker,
                extractionResult,
                auditScorer
        );
        extractionResult.getExtractedFiles().forEach(f -> f.getFile().delete());
        return report;
    }

    @VisibleForTesting
    public AuditReport createAuditReport(
            Integer numberOfChecks,
            Checker checker,
            ExtractionResult extractionResult,
            AuditScorer auditScorer
    ) throws CheckstyleException {
        AuditReportGenerator auditor = new AuditReportGenerator(numberOfChecks, auditScorer);
        checker.addListener(auditor);
        ImmutableList<File> files = extractionResult.getExtractedFiles().stream()
                .map(ExtractedFile::getFile)
                .collect(ImmutableCollectors.toList());
        checker.process(files);
        return auditor.buildReport(extractionResult);
    }
}