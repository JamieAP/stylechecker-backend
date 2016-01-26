package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import uk.ac.kent.co600.project.stylechecker.api.http.CheckerResource;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CheckStyleRulesTest {

    private AuditReport auditReport;

    private FileInputStream getTestJar() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("test.jar").toURI());
        return new FileInputStream(file);
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

    private FileAudit findFile(String name) {
        return auditReport.getFileAudits().stream()
                .filter(r -> r.getFilePath().equals(name))
                .findAny()
                .get();
    }

    @Before
    public void setUp() throws Exception {
        CheckerFactory checkerFactory = createCheckerFactory();
        CheckerResource checkerResource = new CheckerResource();
        auditReport = checkerResource.auditSourceCode(getTestJar(), null, new SourcesJarExtractor(), checkerFactory);
    }

    @Test
    public void checkThing() throws Exception {
        FileAudit file = findFile("Thing.java");
        assertThat(file.getAuditEntries().size(), is(2));
        assertNotNull(Iterables.find(file.getAuditEntries(), i -> i.getStyleGuideRule().equals("4.5 Always include a constructor (even if the body is empty)") && i.getLine().equals(8)));
        assertNotNull(Iterables.find(file.getAuditEntries(), i -> i.getStyleGuideRule().equals("4.3 Always use an access modifier") && i.getLine().equals(19)));
    }

    @Test
    public void checkTestSender() throws Exception {
        FileAudit file = findFile("TestSender.java");
        assertThat(file.getAuditEntries().size(), is(15));
    }
}
