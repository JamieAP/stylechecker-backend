package uk.ac.kent.co600.project;

import com.google.common.base.Throwables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.xml.sax.InputSource;
import uk.ac.kent.co600.project.api.http.CheckerResource;
import uk.ac.kent.co600.project.jar.JarExtractor;

public class StylecheckerApplication extends Application<StylecheckerConfiguration> {

    private static final String CHECKSTYLE_CONFIG_FILE_NAME = "checkstyle-configuration.xml";

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
            InputSource checkstyleConfigXml = new InputSource(
                    ClassLoader.getSystemResourceAsStream(CHECKSTYLE_CONFIG_FILE_NAME)
            );
            Checker checker = new Checker();
            checker.setModuleClassLoader(ClassLoader.getSystemClassLoader());
            checker.configure(ConfigurationLoader.loadConfiguration(checkstyleConfigXml, null, true));
            return checker;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private AbstractBinder instanceBindings() {
        return new AbstractBinder() {
            @Override protected void configure() {
                bind(new JarExtractor()).to(JarExtractor.class);
                bind(initializeCheckstyle()).to(Checker.class);
            }
        };
    }
}
