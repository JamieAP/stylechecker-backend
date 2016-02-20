package uk.ac.kent.co600.project.stylechecker.jar;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SourcesJarExtractorTest {

    static Logger logger;

    static {
        logger = ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
        logger.setLevel(Level.INFO);
    }


    @Test
    public void testExtractsCorrectNumberOfFiles() throws Exception {
        FileInputStream fos = getTestJar();
        SourcesJarExtractor jarExtractor = new SourcesJarExtractor();
        ExtractionResult result = jarExtractor.extract("", fos);

        assertThat(result.getIgnoredFiles().size(), is(10));
        assertThat(result.getExtractedFiles().size(), is(5));
    }

    @Test
    public void testExtractedFilesAreReadableSourceFiles() throws Exception {
        FileInputStream fos = getTestJar();
        SourcesJarExtractor jarExtractor = new SourcesJarExtractor();
        ExtractionResult result = jarExtractor.extract("", fos);

        for (ExtractedFile extractedFile : result.getExtractedFiles()) {
            assertThat(extractedFile.getFile().isFile(), is(true));
            assertThat(extractedFile.getFile().canRead(), is(true));
            assertThat(extractedFile.getFile().toPath().toString().endsWith(".java"), is(true));
        }
    }

    private FileInputStream getTestJar() throws Exception {
        File file = new File(getClass()
                .getClassLoader()
                .getResource("SourcesJarExtractorTestResources/test.jar")
                .toURI()
        );
        return new FileInputStream(file);
    }
}