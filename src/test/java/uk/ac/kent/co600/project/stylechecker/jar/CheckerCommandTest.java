package uk.ac.kent.co600.project.stylechecker.jar;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import uk.ac.kent.co600.project.stylechecker.StylecheckerApplication;
import uk.ac.kent.co600.project.stylechecker.StylecheckerConfiguration;
import uk.ac.kent.co600.project.stylechecker.api.cli.CheckerCommand;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CheckerCommandTest {

    static Logger logger;

    static {
        logger = ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));
        logger.setLevel(Level.INFO);
    }

    private CheckerCommand checkerCommand;

    @Before
    public void setUp() throws Exception {
        CheckerFactory checkerFactory = new CheckerFactory(
                CheckerFactory.loadConfigFromClassPath("checkstyle-configuration.xml")

        );
        checkerCommand = new CheckerCommand("", "", checkerFactory, new SourcesJarExtractor());
    }

    public void processDirectory(String directory) throws Exception {
        Bootstrap<StylecheckerConfiguration> bs = new Bootstrap<>(new StylecheckerApplication());
        bs.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        checkerCommand.run(
                bs,
                new Namespace(
                        ImmutableMap.of(
                                "inputDir", ImmutableList.of(directory),
                                "outputDir", ImmutableList.of(directory),
                                "file", "stylechecker-config.yml"
                        )
                )
        );
    }

    public int verifyResultsExist(String directory) {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        List<String> fileList = new ArrayList();
        int resultsMissing = 0;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileList.add(files[i].getName());
            }
        }

        for (String file : fileList) {
            if (file.substring(file.lastIndexOf('.')).equals(".jar") && !fileList.contains(file +
                    "-results.txt")) resultsMissing++;
        }
        return resultsMissing;
    }

    public boolean verifyResultContent(File file1, File file2) throws IOException {
        boolean compare1and2 = FileUtils.contentEquals(file1, file2);
        return compare1and2;
    }

    @Test
    public void validJarsResultsCount() throws Exception {
        String testDir = "src/test/resources/CheckerCommandTestResources/validJars";
        processDirectory(testDir);
        int resultsMissing = verifyResultsExist(testDir);
        assertThat(resultsMissing, is(0));
    }

    @Test
    public void invalidJarsResultsCount() throws Exception {
        String testDir = "src/test/resources/CheckerCommandTestResources/invalidJars";
        processDirectory(testDir);
        int resultsMissing = verifyResultsExist(testDir);
        assertThat(resultsMissing, is(0));
    }

    @Test
    public void validJarResult() throws Exception {
        String testDir = "src/test/resources/CheckerCommandTestResources/validJar";
        processDirectory(testDir);
        File result = new File("src/test/resources/CheckerCommandTestResources/validJar/test" +
                ".jar-results.txt");
        File expected = new File("src/test/resources/CheckerCommandTestResources/validJar/test" +
                ".jar-results.txt.expected");
        assertTrue(verifyResultContent(result, expected));
    }

    @Test
    public void invalidJarResult() throws Exception {
        String testDir = "src/test/resources/CheckerCommandTestResources/invalidJar";
        processDirectory(testDir);
        /* TODO create invalid jar files
        File result = new File("src/test/resources/CheckerCommandTestResources/invalidJar/test" +
                ".jar-results.txt");
        File expected = new File("src/test/resources/CheckerCommandTestResources/invalidJar/test" +
                ".jar-results.txt.expected");
        assertTrue(verifyResultContent(result,expected));
        */
        assertTrue(true);
    }
}