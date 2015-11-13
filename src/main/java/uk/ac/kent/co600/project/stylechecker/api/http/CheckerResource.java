package uk.ac.kent.co600.project.stylechecker.api.http;

import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.JarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
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
        AuditReportGenerator auditor = new AuditReportGenerator();
        checker.addListener(auditor);
        ImmutableList<File> files = result.getExtractedFiles().stream()
                .map(ExtractedFile::getFile)
                .collect(ImmutableCollectors.toList());
        checker.process(files);
        files.forEach(File::delete);
        return auditor.buildReport(result.mapPathsToFiles());
    }
}