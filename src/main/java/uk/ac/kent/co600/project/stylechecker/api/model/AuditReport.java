package uk.ac.kent.co600.project.stylechecker.api.model;

import com.google.common.collect.ImmutableList;

public class AuditReport {

    private final String originalJarName;
    private final ImmutableList<FileAudit> fileAudits;
    private final Integer numberOfChecks;
    private final Integer uniqueFailedChecks;
    private final Integer totalFailedChecks;
    private final ImmutableList<String> ignoredFiles;

    public AuditReport(
            String originalJarName,
            Integer numberOfChecks,
            Integer uniqueFailedChecks,
            Integer totalFailedChecks,
            Iterable<FileAudit> fileAudits,
            Iterable<String> ignoredFiles
    ) {
        this.originalJarName = originalJarName;
        this.numberOfChecks = numberOfChecks;
        this.uniqueFailedChecks = uniqueFailedChecks;
        this.totalFailedChecks = totalFailedChecks;
        this.fileAudits = ImmutableList.copyOf(fileAudits);
        this.ignoredFiles = ImmutableList.copyOf(ignoredFiles);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getOriginalJarName() {
        return originalJarName;
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

    public Float getGrade() {
        return ((float) getUniqueFailedChecks() / (float) getNumberOfChecks())
                * 100;
    }

    public static final class Builder {
        private String originalJarName;
        private Iterable<FileAudit> fileAudits;
        private Integer numberOfChecks;
        private Integer uniqueFailedChecks;
        private Integer totalFailedChecks;
        private Iterable<String> ignoredFiles;

        private Builder() {}

        public Builder withOriginalJarName(String val) {
            originalJarName = val;
            return this;
        }

        public Builder withFileAudits(Iterable<FileAudit> val) {
            fileAudits = val;
            return this;
        }

        public Builder withNumberOfChecks(Integer val) {
            numberOfChecks = val;
            return this;
        }

        public Builder withUniqueFailedChecks(Integer val) {
            uniqueFailedChecks = val;
            return this;
        }

        public Builder withTotalFailedChecks(Integer val) {
            totalFailedChecks = val;
            return this;
        }

        public Builder withIgnoredFiles(Iterable<String> val) {
            ignoredFiles = val;
            return this;
        }

        public AuditReport build() {
            return new AuditReport(
                    originalJarName,
                    numberOfChecks,
                    uniqueFailedChecks,
                    totalFailedChecks,
                    fileAudits,
                    ignoredFiles
            );
        }
    }
}
