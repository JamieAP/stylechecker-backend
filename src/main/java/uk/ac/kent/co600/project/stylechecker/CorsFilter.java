package uk.ac.kent.co600.project.stylechecker;

import com.google.common.base.Strings;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final String OPTIONS = "OPTIONS";

    @Override
    public void filter(ContainerRequestContext reqCtx, ContainerResponseContext resCtx) throws IOException {
        if (reqCtx.getMethod().equals(OPTIONS)) {
            resCtx.setEntity("");
            resCtx.setStatus(200);

            resCtx.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST");
            resCtx.getHeaders().putSingle("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            resCtx.getHeaders().putSingle("Access-Control-Max-Age", "1800");
        }

        String domain = reqCtx.getHeaderString("Origin");

        if (!Strings.isNullOrEmpty(domain)) {
            resCtx.getHeaders().putSingle("Access-Control-Allow-Origin", domain);
            resCtx.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        }
    }
}
