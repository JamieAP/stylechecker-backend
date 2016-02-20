package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.kent.co600.project.stylechecker.api.http.CheckerResource;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;

import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CheckStyleRulesTest {

    private CheckerResource checkerResource;
    private CheckerFactory checkerFactory;

    private ExtractionResult getTestSourceFile(String name) throws Exception {
        File file = new File("src/test/resources/CheckStyleRulesTestResources/".concat(name));
        return new ExtractionResult(
                "",
                ImmutableList.of(),
                ImmutableList.of(
                        ExtractedFile.of(file, name, IOUtils.readLines(new FileInputStream(file)))
                )
        );
    }

    @Before
    public void setUp() throws Exception {
        this.checkerFactory = new CheckerFactory(
                CheckerFactory.loadConfigFromClassPath("checkstyle-configuration.xml")
        );
        this.checkerResource = new CheckerResource();
    }

    @Test
    public void checkThing() throws Exception {
        ExtractionResult extracted = getTestSourceFile("Thing.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );
        assertNotNull(report);
        assertThat(report.getFileAudits().get(0).getAuditEntries().size(), is(2));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.5 Always include a constructor (even if the body is empty)") && i.getLine().equals(8)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.3 Always use an access modifier") && i.getLine().equals(19)));
    }

    @Test
    public void checkMessageSender() throws Exception {
        ExtractionResult extracted = getTestSourceFile("MessageSender.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );
        assertNotNull(report);
        assertThat(report.getFileAudits().get(0).getAuditEntries().size(), is(12));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.1 Every class has a class comment at the top") && i.getLine().equals(27)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(27)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.6 Always include superclass constructor call") && i.getLine().equals(54)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(54)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(79)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(100)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(100)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(123)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(123)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(139)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.3 Braces for classes and methods are alone on one line") && i.getLine().equals(139)));
    }

    @Test
    public void checkFrameSender() throws Exception {
        ExtractionResult extracted = getTestSourceFile("FrameSender.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );
        assertNotNull(report);
        assertThat(report.getFileAudits().get(0).getAuditEntries().size(), is(7));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.1 Every class has a class comment at the top") && i.getLine().equals(14)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(77)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(82)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(84)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(104)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(106)));
    }

    @Test
    public void checkProtocolException() throws Exception {
        ExtractionResult extracted = getTestSourceFile("ProtocolException.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );
        assertNotNull(report);
        assertThat(report.getFileAudits().get(0).getAuditEntries().size(), is(4));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.1 Every class has a class comment at the top") && i.getLine().equals(14)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(20)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(27)));
    }

    @Test
    public void checkTestSender() throws Exception {
        ExtractionResult extracted = getTestSourceFile("TestSender.java");
        AuditReport report = checkerResource.createAuditReport(
                checkerFactory.getNumberOfChecks(),
                checkerFactory.createChecker(),
                extracted
        );
        assertNotNull(report);
        assertThat(report.getFileAudits().get(0).getAuditEntries().size(), is(15));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.4 Import classes separately") && i.getLine().equals(4)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.5 Always include a constructor (even if the body is empty)") && i.getLine().equals(94)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.1 Every class has a class comment at the top") && i.getLine().equals(94)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("1.5 Constants are written in UPPERCASE") && i.getLine().equals(98)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("1.5 Constants are written in UPPERCASE") && i.getLine().equals(100)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.2 All statements within a block are indented one level") && i.getLine().equals(102)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.3 Always use an access modifier") && i.getLine().equals(102)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("4.2 Fields may not be public (except for final fields)") && i.getLine().equals(102)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(112)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.5 Always use braces in control structures") && i.getLine().equals(148)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(178)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("3.2 Every method has a method comment") && i.getLine().equals(187)));
        assertNotNull(Iterables.find(report.getFileAudits().get(0).getAuditEntries(), i -> i.getStyleGuideRule().equals("2.1 One level of indentation is four spaces") && i.getLine().equals(241)));


    }
}
