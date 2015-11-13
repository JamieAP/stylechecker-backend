package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**
 * An {@link AuditListener} that consumes all events except errors by no-oping,
 * leaving a concrete implementation to handle errors.
 */
public abstract class ErrorOnlyAuditListener implements AuditListener {

    public final void auditStarted(AuditEvent event) {

    }

    public final void auditFinished(AuditEvent event) {

    }

    public final void fileStarted(AuditEvent event) {

    }

    public final void fileFinished(AuditEvent event) {

    }
}