package uk.ac.kent.co600.project.stylechecker.utils;

import com.codahale.metrics.health.HealthCheck;

/*
 * A fake health check to prevent Dropwizard logging scary warning.
 * This application has no health checkable functionality and the
 * check would not be monitored regardless
 */
public class FakeHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }

}
