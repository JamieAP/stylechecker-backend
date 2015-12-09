package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
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

    public ImmutableList<FileAudit> buildReport(Map<String, ExtractedFile> pathToFile) {
        checkState(consumed.compareAndSet(false, true), "This instance has already been used!");
        return mapEventsToFileAudits(errors.build(), pathToFile);
    }

    private ImmutableList<FileAudit> mapEventsToFileAudits(
            List<AuditEvent> errors,
            Map<String, ExtractedFile> pathToFile
    ) {
        ImmutableListMultimap<String, FileAuditEntry> filePathToEvents = groupEventsByFile(
                errors,
                pathToFile
        );
        return filePathToEvents.keySet().stream()
                .map(file -> new FileAudit(file, filePathToEvents.get(file)))
                .collect(ImmutableCollectors.toList());
    }

    private ImmutableListMultimap<String, FileAuditEntry> groupEventsByFile(
            List<AuditEvent> errors,
            Map<String, ExtractedFile> pathToFile
    ) {
        return errors.stream()
                .collect(
                        ImmutableCollectors.toListMultiMap(
                                e -> pathToFile.get(e.getFileName()).getFriendlyFileName(),
                                e -> FileAuditEntry.of(e.getMessage(), e.getLine(), e.getColumn(), e.getSourceName())
                        )
                );
    }
}