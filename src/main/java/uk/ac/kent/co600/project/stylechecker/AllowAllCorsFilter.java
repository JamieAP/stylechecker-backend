package uk.ac.kent.co600.project.stylechecker;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class AllowAllCorsFilter implements ContainerResponseFilter {

    private static final Joiner JOINER = Joiner.on(", ");

    @Override
    public void filter(ContainerRequestContext reqCtx, ContainerResponseContext resCtx) throws IOException {
        if (reqCtx.getMethod().equals(HttpMethod.OPTIONS)) {
            resCtx.setEntity("");
            resCtx.setStatus(200);

            resCtx.getHeaders().putSingle(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                    JOINER.join(HttpMethod.GET, HttpMethod.POST)
            );
            resCtx.getHeaders().putSingle(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                    JOINER.join(
                            HttpHeaders.ORIGIN,
                            HttpHeaders.CONTENT_TYPE,
                            HttpHeaders.ACCEPT,
                            HttpHeaders.AUTHORIZATION
                    )
            );
            resCtx.getHeaders().putSingle(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1800");
        }

        String domain = reqCtx.getHeaderString(HttpHeaders.ORIGIN);

        if (!Strings.isNullOrEmpty(domain)) {
            resCtx.getHeaders().putSingle(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, domain);
            resCtx.getHeaders().putSingle(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
        }
    }
}
