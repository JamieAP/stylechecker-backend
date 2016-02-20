package uk.ac.kent.co600.project.stylechecker.checkstyle;

import com.google.common.base.Throwables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.xml.sax.InputSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class CheckerFactory {

    private final Configuration checkstyleConf;

    public CheckerFactory(Configuration checkstyleConf) {
        this.checkstyleConf = checkNotNull(checkstyleConf);
    }


    public Checker createChecker() {
        return createChecker(checkstyleConf);
    }

    public static Checker createChecker(Configuration config) {
        try {
            Checker checker = new Checker();
            checker.setModuleClassLoader(ClassLoader.getSystemClassLoader());
            checker.configure(config);
            return checker;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static Configuration loadConfigFromClassPath(String filename) {
        try {
            InputSource checkstyleConfigXml = new InputSource(
                    ClassLoader.getSystemResourceAsStream(filename)
            );
            return ConfigurationLoader.loadConfiguration(
                    checkstyleConfigXml, null, true
            );
        } catch (CheckstyleException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Because multiple Checkstyle checks map to multiple style guide rules, there is way to
     * programmatically determine at runtime the number of checks we will report to the user.
     *
     * Thus we hardcode it here, it should be equal to the number of unique check style rules
     * used in {@link uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditEventTranslator}
     */
    public Integer getNumberOfChecks() {
        return 19;
    }
}
