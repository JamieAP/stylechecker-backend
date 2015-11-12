package uk.ac.kent.co600.project;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import uk.ac.kent.co600.project.api.http.CheckerResource;

public class StylecheckerApplication extends Application<StylecheckerConfiguration> {

    public static void main(String[] args) throws Exception {
        new StylecheckerApplication().run(args);
    }

    @Override
    public void run(StylecheckerConfiguration conf, Environment env) throws Exception {
        env.jersey().register(CheckerResource.class);
        env.jersey().register(MultiPartFeature.class);
        /* TODO Set up CO320 config rules */
        Checker checker = new Checker();
        checker.setModuleClassLoader(ClassLoader.getSystemClassLoader());
        checker.configure(new DefaultConfiguration("co320-style"));

        env.jersey().register(checker);
    }
}
