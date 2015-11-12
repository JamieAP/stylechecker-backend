package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**
 * An {@link AuditListener} that consumes all events except errors by no-oping,
 * leaving a concrete implementation to handle errors.
 */
public abstract class ErrorOnlyAuditListener implements AuditListener {

    public void auditStarted(AuditEvent event) {

    }

    public void auditFinished(AuditEvent event) {

    }

    public void fileStarted(AuditEvent event) {

    }

    public void fileFinished(AuditEvent event) {

    }

    public void addException(AuditEvent event, Throwable throwable) {

    }
}
