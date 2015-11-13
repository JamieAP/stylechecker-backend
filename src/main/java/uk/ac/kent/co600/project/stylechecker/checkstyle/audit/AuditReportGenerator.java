package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

/**
 * Consumes error events from a run of {@link Checker} and produces a report.
 * This class is NOT thread safe.
 */
@NotThreadSafe
public class AuditReportGenerator extends ErrorOnlyAuditListener {

    private final ImmutableList.Builder<AuditEvent> errors = ImmutableList.builder();
    private final AtomicBoolean consumed = new AtomicBoolean(false);

    @Override
    public void addError(AuditEvent event) {
         errors.add(event);
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {
        Throwables.propagate(throwable);
    }

    public String buildReport(Map<String, ExtractedFile> pathToFile) {
        checkState(consumed.compareAndSet(false, true), "This instance has already been used!");
        return Joiner.on(System.lineSeparator()).join(stringify(errors.build(), pathToFile));
    }

    private ImmutableList<String> stringify(List<AuditEvent> errors, Map<String, ExtractedFile> pathToFile) {
        return errors.stream()
                .map(e -> eventToString(e, pathToFile))
                .collect(ImmutableCollectors.toList());
    }

    private String eventToString(AuditEvent event, Map<String, ExtractedFile> pathToFile) {
        return String.format(
                "File %s Line %d Col %d: %s",
                pathToFile.get(event.getFileName()).getFriendlyFileName(),
                event.getLine(),
                event.getColumn(),
                event.getMessage()
        );
    }
}