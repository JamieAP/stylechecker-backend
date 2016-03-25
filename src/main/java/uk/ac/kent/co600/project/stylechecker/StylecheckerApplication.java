package uk.ac.kent.co600.project.stylechecker;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.xml.sax.InputSource;
import uk.ac.kent.co600.project.stylechecker.api.cli.CheckerCommand;
import uk.ac.kent.co600.project.stylechecker.api.http.BluejResource;
import uk.ac.kent.co600.project.stylechecker.api.http.CheckerResource;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.FauxHealthCheck;

import java.util.Arrays;

public class StylecheckerApplication extends Application<StylecheckerConfiguration> {

    private static final String CHECKSTYLE_CONFIG_FILE = "checkstyle-configuration.xml";
    private static final String STYLECHECKER_CONFIG_FILE = "stylechecker-config.yml";
    private static final String SERVER = "server";

    public static void main(String[] cliArgs) throws Exception {
        if (!Strings.isNullOrEmpty(cliArgs[0]) && cliArgs[0].equals(SERVER)) {
            cliArgs = addConfigFromClasspath(cliArgs);
        }
        new StylecheckerApplication().run(cliArgs);
    }

    /*
        This adds the path for the default server configuration to the CLI arguments,
        allowing Dropwizard to load it from the JAR without it being specified by the user

        We do this as there is no way to override the default server configuration without
        specifying a configuration file on the CLI

        Configuration can still be overridden using system properties,
        e.g. -Ddw.server.connector.port=9090
     */
    private static String[] addConfigFromClasspath(String[] cliArgs) {
        return ImmutableList.<String>builder()
                    .addAll(Arrays.asList(cliArgs))
                    .add(STYLECHECKER_CONFIG_FILE)
                    .build()
                    .toArray(new String[cliArgs.length + 1]);
    }

    @Override
    public void run(StylecheckerConfiguration conf, Environment env) throws Exception {
        env.jersey().register(CheckerResource.class);
        env.jersey().register(MultiPartFeature.class);
        env.jersey().register(instanceBindings(conf));
        env.jersey().register(AllowAllCorsFilter.class);
        env.jersey().register(BluejResource.class);
        env.healthChecks().register("fakeHealthCheck", new FauxHealthCheck());
    }

    @Override
    public void initialize(Bootstrap<StylecheckerConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        bootstrap.addBundle(new AssetsBundle("/assets/frontend/", "/", "index.html"));
        bootstrap.addCommand(
                new CheckerCommand(
                        "checker",
                        "Check a directory of JAR & ZIP files",
                        createCheckerFactory(),
                        new SourcesJarExtractor()
                )
        );
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

    private AbstractBinder instanceBindings(StylecheckerConfiguration conf) {
        return new AbstractBinder() {
            @Override protected void configure() {
                bind(new SourcesJarExtractor()).to(SourcesJarExtractor.class);
                bind(createCheckerFactory()).to(CheckerFactory.class);
                bind(new AuditScorer(conf.getWeights())).to(AuditScorer.class);
            }
        };
    }
}