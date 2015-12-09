package uk.ac.kent.co600.project.stylechecker.checkstyle;

import com.google.common.base.Throwables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import static com.google.common.base.Preconditions.checkNotNull;

public class CheckerFactory {

    private final Configuration checkstyleConf;

    public CheckerFactory(Configuration checkstyleConf) {
        this.checkstyleConf = checkNotNull(checkstyleConf);
    }

    public Checker createChecker() {
        try {
            Checker checker = new Checker();
            checker.setModuleClassLoader(ClassLoader.getSystemClassLoader());
            checker.configure(checkstyleConf);
            return checker;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Integer getNumberOfChecks() {
        return checkstyleConf.getChildren()[0].getChildren().length;
    }
}
