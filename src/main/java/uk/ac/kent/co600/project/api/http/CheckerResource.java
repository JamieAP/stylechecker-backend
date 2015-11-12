package uk.ac.kent.co600.project.api.http;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.ac.kent.co600.project.jar.ExtractionResult;
import uk.ac.kent.co600.project.jar.JarExtractor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;

@Path("/check")
public class CheckerResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(
            @FormDataParam("file") InputStream is,
            @FormDataParam("file") FormDataBodyPart bodyPart,
            @Context JarExtractor extractor,
            @Context Checker checker
    ) throws IOException, CheckstyleException {
        ExtractionResult result = extractor.extract(is);
        final ImmutableList.Builder<String> errors = ImmutableList.builder();
        checker.addListener(createReporter(errors));
        checker.process(result.getExtractedFiles());
        return Joiner.on(System.lineSeparator()).join(errors.build());
    }

    private AuditListener createReporter(final ImmutableList.Builder<String> errors) {
        return new AuditListener() {
            public void auditStarted(AuditEvent event) {

            }

            public void auditFinished(AuditEvent event) {

            }

            public void fileStarted(AuditEvent event) {

            }

            public void fileFinished(AuditEvent event) {

            }

            public void addError(AuditEvent event) {
                errors.add(
                        String.format(
                                "File %s Line %d Col %d: %s",
                                event.getFileName(),
                                event.getLine(),
                                event.getColumn(),
                                event.getMessage()
                        )
                );
            }

            public void addException(AuditEvent event, Throwable throwable) {

            }
        };
    }
}