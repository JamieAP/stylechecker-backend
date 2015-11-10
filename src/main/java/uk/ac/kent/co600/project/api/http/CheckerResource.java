package uk.ac.kent.co600.project.api.http;

import com.puppycrawl.tools.checkstyle.Checker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("/check")
@Produces("text/plain")
public class CheckerResource {

    /* TODO implement checker endpoint */
    @GET
    public String get(@Context Checker checker) {
        return "hello world!";
    }
}
