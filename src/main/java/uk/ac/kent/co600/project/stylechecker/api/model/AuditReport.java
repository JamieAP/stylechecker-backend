package uk.ac.kent.co600.project.stylechecker.api.model;

import com.google.common.collect.ImmutableList;

public class AuditReport {

    private final ImmutableList<FileAudit> fileAudits;
    private final Integer numberOfChecks;
    private final Integer uniqueFailedChecks;
    private final Integer totalFailedChecks;
    private final ImmutableList<String> ignoredFiles;

    public AuditReport(
            Integer numberOfChecks,
            Integer uniqueFailedChecks,
            Integer totalFailedChecks,
            ImmutableList<FileAudit> fileAudits,
            Iterable<String> ignoredFiles
    ) {
        this.numberOfChecks = numberOfChecks;
        this.uniqueFailedChecks = uniqueFailedChecks;
        this.totalFailedChecks = totalFailedChecks;
        this.fileAudits = fileAudits;
        this.ignoredFiles = ImmutableList.copyOf(ignoredFiles);
    }

    public Integer getNumberOfChecks() {
        return numberOfChecks;
    }

    public Integer getUniqueFailedChecks() {
        return uniqueFailedChecks;
    }

    public Integer getTotalFailedChecks() {
        return totalFailedChecks;
    }

    public ImmutableList<FileAudit> getFileAudits() {
        return fileAudits;
    }

    public ImmutableList<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public static AuditReport of(
            Integer numberOfChecks,
            Integer uniqueFailedChecks,
            Integer totalFailedChecks,
            ImmutableList<FileAudit> fileAudits,
            Iterable<String> ignoredFiles
    ) {
        return new AuditReport(
                numberOfChecks,
                uniqueFailedChecks,
                totalFailedChecks,
                fileAudits,
                ignoredFiles
        );
    }
}
