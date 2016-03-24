package uk.ac.kent.co600.project.stylechecker.utils;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Ordering;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;

import java.util.ArrayList;
import java.util.List;

public class TextReport {

    private final AuditReport auditReport;
    private final List<String> textReport;

    public TextReport(AuditReport auditReport){
        this.auditReport = auditReport;
        this.textReport = new ArrayList<String>();
    }

    public void generateSingleReport() {
        ImmutableMultiset<String> failedRules = auditReport.getFileAudits().stream()
                .flatMap(audit -> audit.getAuditEntries().stream())
                .map(FileAuditEntry::getStyleGuideRule)
                .collect(ImmutableCollectors.toMultiset());

        textReport.add("---------Results---------");
        textReport.add("Total Rules: " + auditReport.getNumberOfChecks());
        textReport.add("Total Errors: " + auditReport.getUniqueFailedChecks());
        textReport.add("Mark: " + auditReport.getGrade());
        textReport.add("");

        textReport.add("---------Summary---------");
        Ordering.natural()
                .onResultOf(failedRules::count)
                .reverse()
                .immutableSortedCopy(failedRules.elementSet())
                .forEach(s -> textReport.add(
                        "Errors: " + failedRules.count(s) + " Rule: " + s));
        textReport.add("");

        textReport.add("---------Source File Details---------");
        auditReport.getFileAudits().forEach(f -> {
            f.getAuditEntries().forEach(a ->
                    textReport.add(
                            "File: " + f.getFilePath() +
                                    " Line: " + a.getLine() +
                                    " Col: " + a.getColumn() +
                                    " Rule: " + a.getStyleGuideRule()));
        });
    }

    public List<String> getTextReport(){
        return this.textReport;
    }
}
