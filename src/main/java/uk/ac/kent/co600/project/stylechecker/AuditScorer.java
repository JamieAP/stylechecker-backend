package uk.ac.kent.co600.project.stylechecker;

import com.google.common.collect.ImmutableList;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

public class AuditScorer {

    public static AuditReport score(Integer numberOfChecks, Iterable<FileAudit> auditedFiles) {
        ImmutableList<FileAudit> fileAudits = ImmutableList.copyOf(auditedFiles);
        ImmutableList<String> checkNames = fileAudits.stream()
                .flatMap(e -> e.getAuditEntries().stream())
                .map(FileAuditEntry::getCheckClassName)
                .collect(ImmutableCollectors.toList());

        Long uniqueFailedChecks = checkNames.stream().distinct().count();
        Integer failuresTotal = checkNames.size();
        return AuditReport.of(numberOfChecks, uniqueFailedChecks.intValue(), failuresTotal, fileAudits);
    }
}
