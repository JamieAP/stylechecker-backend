package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import uk.ac.kent.co600.project.stylechecker.AuditScorer;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Consumes error events from a run of {@link Checker} and produces a report.
 */
public class AuditReportGenerator extends ErrorOnlyAuditListener {

    private final ImmutableList.Builder<AuditEvent> errors = ImmutableList.builder();
    private final AtomicBoolean consumed = new AtomicBoolean(false);
    private final Integer numberOfChecks;
    private final AuditScorer scorer;

    public AuditReportGenerator(Integer numberOfChecks, AuditScorer scorer) {
        this.numberOfChecks = checkNotNull(numberOfChecks);
        this.scorer = checkNotNull(scorer);
    }

    @Override
    public void addError(AuditEvent event) {
        errors.add(event);
    }

    @Override
    public void addException(AuditEvent event, Throwable throwable) {
        Throwables.propagate(throwable);
    }

    public AuditReport buildReport(ExtractionResult extResult) {
        checkState(consumed.compareAndSet(false, true), "This instance has already been used!");
        ImmutableList<FileAudit> fileAudits = mapEventsToFileAudits(
                errors.build(),
                extResult.mapPathsToFiles()
        );

        ImmutableList<String> checkNames = fileAudits.stream()
                .flatMap(e -> e.getAuditEntries().stream())
                .map(FileAuditEntry::getStyleGuideRule)
                .collect(ImmutableCollectors.toList());

        Long uniqueFailedChecks = checkNames.stream().distinct().count();
        Integer failuresTotal = checkNames.size();

        AuditReport report = AuditReport.newBuilder()
                .withOriginalJarName(extResult.getOriginalJarName())
                .withFileAudits(fileAudits)
                .withIgnoredFiles(extResult.getIgnoredFiles())
                .withNumberOfChecks(numberOfChecks)
                .withUniqueFailedChecks(uniqueFailedChecks.intValue())
                .withTotalFailedChecks(failuresTotal)
                .build();

        return scorer.score(report);
    }

    private ImmutableList<FileAudit> mapEventsToFileAudits(
            List<AuditEvent> errors,
            Map<String, ExtractedFile> pathToFile
    ) {
        ImmutableListMultimap<ExtractedFile, FileAuditEntry> fileToEvents = groupEventsByFile(
                errors,
                pathToFile
        );
        return fileToEvents.keySet().stream()
                .map(file -> new FileAudit(file.getFriendlyFileName(), fileToEvents.get(file)))
                .collect(ImmutableCollectors.toList());
    }

    private ImmutableListMultimap<ExtractedFile, FileAuditEntry> groupEventsByFile(
            List<AuditEvent> errors,
            Map<String, ExtractedFile> pathToFile
    ) {
        return errors.stream()
                .collect(
                        ImmutableCollectors.toListMultiMap(
                                e -> pathToFile.get(e.getFileName()),
                                e -> AuditEventTranslator.translate(e, pathToFile.get(e.getFileName()))
                        )
                );
    }
}