package uk.ac.kent.co600.project;

import com.google.common.base.Throwables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import uk.ac.kent.co600.project.api.http.CheckerResource;
import uk.ac.kent.co600.project.jar.JarExtractor;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;

public class StylecheckerApplication extends Application<StylecheckerConfiguration> {

    public static void main(String[] args) throws Exception {
        new StylecheckerApplication().run(args);
    }

    @Override
    public void run(StylecheckerConfiguration conf, Environment env) throws Exception {
        env.jersey().register(CheckerResource.class);
        env.jersey().register(MultiPartFeature.class);
        env.jersey().register(instanceBindings());
    }

    private Checker initializeCheckstyle() {
        try {
            Checker checker = new Checker();
            checker.setModuleClassLoader(ClassLoader.getSystemClassLoader());
            checker.configure(ConfigurationLoader.loadConfiguration(loadCheckstyleConfiguration(), null, false));
            return checker;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private InputStream loadCheckstyleConfiguration() {
        return ClassLoader.getSystemResourceAsStream("checkstyle-configuration.xml");
    }

    private AbstractBinder instanceBindings() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new JarExtractor()).to(JarExtractor.class);
                bind(initializeCheckstyle()).to(Checker.class);
            }
        };
    }
}
