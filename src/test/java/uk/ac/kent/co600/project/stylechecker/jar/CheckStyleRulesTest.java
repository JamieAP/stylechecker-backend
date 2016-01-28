package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import uk.ac.kent.co600.project.stylechecker.api.http.CheckerResource;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CheckStyleRulesTest {

    private CheckerResource checkerResource;
    private CheckerFactory checkerFactory;

    private ExtractionResult getTestSourceFile(String name) throws Exception {
        File file = new File("src/test/resources/".concat(name));
        return new ExtractionResult(
                ImmutableList.of(),
                ImmutableList.of(
                        ExtractedFile.of(file, name, IOUtils.readLines(new FileInputStream(file)))
                )
        );
    }

    private CheckerFactory createCheckerFactory() {
        try {
            InputSource checkstyleConfigXml = new InputSource(
                    ClassLoader.getSystemResourceAsStream("checkstyle-configuration.xml")
            );
            Configuration checkstyleConfig = ConfigurationLoader.loadConfiguration(
                    checkstyleConfigXml, null, true
            );
            return new CheckerFactory(checkstyleConfig);
        } catch (CheckstyleException e) {
            throw Throwables.propagate(e);
        }
    }

    @Before
    public void setUp() throws Exception {
        this.checkerFactory = createCheckerFactory();
        this.checkerResource = new CheckerResource();
    }

    @Test
    public void checkThing() throws Exception {
        ExtractionResult extracted = getTestSourceFile("FrameSender.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );

        assertNotNull(report);
        /*assertThat(file.getAuditEntries().size(), is(2));
        assertNotNull(Iterables.find(file.getAuditEntries(), i -> i.getStyleGuideRule().equals("4.5 Always include a constructor (even if the body is empty)") && i.getLine().equals(8)));
        assertNotNull(Iterables.find(file.getAuditEntries(), i -> i.getStyleGuideRule().equals("4.3 Always use an access modifier") && i.getLine().equals(19)));*/
    }
}
