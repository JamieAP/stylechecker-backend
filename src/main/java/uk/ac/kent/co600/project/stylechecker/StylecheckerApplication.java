package uk.ac.kent.co600.project.stylechecker;

import com.google.common.base.Throwables;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.xml.sax.InputSource;
import uk.ac.kent.co600.project.stylechecker.api.cli.CheckerCommand;
import uk.ac.kent.co600.project.stylechecker.api.http.CheckerResource;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;

import java.io.File;

public class StylecheckerApplication extends Application<StylecheckerConfiguration> {

    private static final String CHECKSTYLE_CONFIG_FILE = "checkstyle-configuration.xml";

    public static void main(String[] args) throws Exception {
        new StylecheckerApplication().run(args);
    }

    @Override
    public void run(StylecheckerConfiguration conf, Environment env) throws Exception {
        env.jersey().register(CheckerResource.class);
        env.jersey().register(MultiPartFeature.class);
        env.jersey().register(instanceBindings());
        env.jersey().register(AllowAllCorsFilter.class);
    }

    @Override
    public void initialize(Bootstrap<StylecheckerConfiguration> bootstrap) {
        bootstrap.addCommand(
                new CheckerCommand(
                        "checker",
                        "Check a directory of JAR & ZIP files",
                        createCheckerFactory(),
                        new SourcesJarExtractor()
                )
        );
        bootstrap.addBundle(new AssetsBundle("/assets/frontend/", "/", "index.html"));
    }

    private CheckerFactory createCheckerFactory() {
        try {
            InputSource checkstyleConfigXml = new InputSource(
                ClassLoader.getSystemResourceAsStream(CHECKSTYLE_CONFIG_FILE)
            );
            Configuration checkstyleConfig = ConfigurationLoader.loadConfiguration(
                    checkstyleConfigXml, null, true
            );
            return new CheckerFactory(checkstyleConfig);
        } catch (CheckstyleException e) {
            throw Throwables.propagate(e);
        }
    }

    private AbstractBinder instanceBindings() {
        return new AbstractBinder() {
            @Override protected void configure() {
                bind(new SourcesJarExtractor()).to(SourcesJarExtractor.class);
                bind(createCheckerFactory()).to(CheckerFactory.class);
            }
        };
    }
}