package uk.ac.kent.co600.project.stylechecker.api.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Ordering;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

public class AuditReport {

    private final String originalJarName;
    private final ImmutableList<FileAudit> fileAudits;
    private final Integer numberOfChecks;
    private final Integer uniqueFailedChecks;
    private final Integer totalFailedChecks;
    private final ImmutableList<String> ignoredFiles;
    private final Score grade;

    public AuditReport(
            String originalJarName,
            Integer numberOfChecks,
            Integer uniqueFailedChecks,
            Integer totalFailedChecks,
            Iterable<FileAudit> fileAudits,
            Iterable<String> ignoredFiles,
            Score grade
    ) {
        this.originalJarName = originalJarName;
        this.numberOfChecks = numberOfChecks;
        this.uniqueFailedChecks = uniqueFailedChecks;
        this.totalFailedChecks = totalFailedChecks;
        this.fileAudits = fileAudits == null ? null : ImmutableList.copyOf(fileAudits);
        this.ignoredFiles = ignoredFiles == null ? null : ImmutableList.copyOf(ignoredFiles);
        this.grade = grade;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(AuditReport copy) {
        return new Builder(copy);
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

    public Score getGrade() {
        return grade;
    }

    public ImmutableList<String> toText() {
        ImmutableMultiset<String> failedRules = getFileAudits().stream()
                .flatMap(audit -> audit.getAuditEntries().stream())
                .map(FileAuditEntry::getStyleGuideRule)
                .collect(ImmutableCollectors.toMultiset());

        ImmutableList.Builder<String> textReport = ImmutableList.builder();

        textReport.add("---------Results---------\r\n");
        textReport.add(String.format("Total Rules: %d%n", getNumberOfChecks()));
        textReport.add(String.format("Total Errors: %d%n", getUniqueFailedChecks()));
        textReport.add(String.format("Mark: %.2f%%%n", getGrade().getTotalScore()));
        textReport.add("\r\n");

        textReport.add(String.format("Documentation Rules: %d%n", getGrade().getDocumentationRules()));
        textReport.add(String.format("Documentation Errors: %d%n", getGrade().getDocumentationErrors()));
        textReport.add(String.format("Documentation Mark: %.2f%%%n", getGrade().getDocumentationScore()));
        textReport.add("\r\n");

        textReport.add(String.format("Naming Rules: %d%n", getGrade().getNamingRules()));
        textReport.add(String.format("Naming Errors: %d%n", getGrade().getNamingErrors()));
        textReport.add(String.format("Naming Mark: %.2f%%%n", getGrade().getNamingScore()));
        textReport.add("\r\n");

        textReport.add(String.format("Layout Rules: %d%n", getGrade().getLayoutRules()));
        textReport.add(String.format("Layout Errors: %d%n", getGrade().getLayoutErrors()));
        textReport.add(String.format("Layout Mark: %.2f%%%n", getGrade().getLayoutScore()));
        textReport.add("\r\n");

        textReport.add("---------Summary---------\r\n");
        Ordering.natural()
                .onResultOf(failedRules::count)
                .reverse()
                .immutableSortedCopy(failedRules.elementSet())
                .forEach(s -> textReport.add(
                        String.format("Errors: %d Rule: %s%n", failedRules.count(s), s))
                );
        textReport.add("\r\n");

        textReport.add("---------Source File Details---------\r\n");
        getFileAudits().forEach(f -> {
                f.getAuditEntries().forEach(a ->
                        textReport.add(
                                String.format("File: %s Line: %d Col: %d Rule: %s%n",
                                        f.getFilePath(),
                                        a.getLine(),
                                        a.getColumn(),
                                        a.getStyleGuideRule()
                                )
                        )
                );
                textReport.add("\r\n");
        });

        return textReport.build();
    }

    public static final class Builder {
        private String originalJarName;
        private Iterable<FileAudit> fileAudits;
        private Integer numberOfChecks;
        private Integer uniqueFailedChecks;
        private Integer totalFailedChecks;
        private Iterable<String> ignoredFiles;
        private Score grade;

        private Builder() {
        }

        private Builder(AuditReport copy) {
            this.originalJarName = copy.originalJarName;
            this.fileAudits = copy.fileAudits;
            this.numberOfChecks = copy.numberOfChecks;
            this.uniqueFailedChecks = copy.uniqueFailedChecks;
            this.totalFailedChecks = copy.totalFailedChecks;
            this.ignoredFiles = copy.ignoredFiles;
            this.grade = copy.grade;
        }

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

        public Builder withGrade(Score grade) {
            this.grade = grade;
            return this;
        }

        public AuditReport build() {
            return new AuditReport(
                    originalJarName,
                    numberOfChecks,
                    uniqueFailedChecks,
                    totalFailedChecks,
                    fileAudits,
                    ignoredFiles,
                    grade
            );
        }
    }
}
