package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import uk.ac.kent.co600.project.stylechecker.StylecheckerConfiguration;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.api.model.Score;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Performs the scoring/grading calculations of an {@link AuditReport}
 */
public class AuditScorer {

    private static final String LAYOUT = "LAYOUT";
    private static final String NAMING = "NAMING";
    private static final String DOCUMENTATION = "DOCUMENTATION";
    private static final ImmutableMultimap<String, String> RULE_TO_CATEGORY = ImmutableMultimap.<String, String>builder()
            .put("1.2", LAYOUT)
            .put("1.4", LAYOUT)
            .put("1.5", LAYOUT)
            .put("2.1", LAYOUT)
            .put("2.2", LAYOUT)
            .put("2.3", LAYOUT)
            .put("2.4", LAYOUT)
            .put("2.5", LAYOUT)
            .put("2.6", LAYOUT)
            .put("2.7", LAYOUT)
            .put("2.8", LAYOUT)
            .put("3.1", DOCUMENTATION)
            .put("3.2", DOCUMENTATION)
            .put("4.1", NAMING)
            .put("4.2", NAMING)
            .put("4.3", NAMING)
            .put("4.4", LAYOUT)
            .put("4.5", NAMING)
            .put("4.6", NAMING)
            .build();

    private final StylecheckerConfiguration.Weights weights;

    public AuditScorer(StylecheckerConfiguration.Weights weights) {
        this.weights = checkNotNull(weights);
    }

    public AuditReport score(AuditReport report) {
        List<String> failedCategories = distinctFailuresByCategory(report);
        return AuditReport.newBuilder(report)
                .withGrade(calculateScore(failedCategories))
                .build();
    }

    private Score calculateScore(List<String> failedCategories) {
        Float documentationScore = 100f;
        Float namingScore = 100f;
        Float layoutScore = 100f;

        int namingCategories = RULE_TO_CATEGORY.inverse().get(NAMING).size();
        int documentationCategories = RULE_TO_CATEGORY.inverse().get(DOCUMENTATION).size();
        int layoutCategories = RULE_TO_CATEGORY.inverse().get(LAYOUT).size();

        int namingErrors = 0;
        int documentationErrors = 0;
        int layoutErrors = 0;

        for (String cat : failedCategories) {
            if (DOCUMENTATION.equals(cat)) {
                documentationScore -= (100f / documentationCategories);
                documentationErrors++;
            }
            if (NAMING.equals(cat)) {
                namingScore -= (100f / namingCategories);
                namingErrors++;
            }
            if (LAYOUT.equals(cat)) {
                layoutScore -= (100f / layoutCategories);
                layoutErrors++;
            }
        }

        return Score.newBuilder()
                .withDocumentationErrors(documentationErrors)
                .withDocumentationRules(documentationCategories)
                .withDocumentationScore(documentationScore)
                .withLayoutErrors(layoutErrors)
                .withLayoutScore(layoutScore)
                .withLayoutRules(layoutCategories)
                .withNamingErrors(namingErrors)
                .withNamingRules(namingCategories)
                .withNamingScore(namingScore)
                .withWeights(weights)
                .build();
    }

    /**
     * Projects the category of distinct broken Style Guide rules
     *
     * @param report - a report
     * @return categories - the list of categories
     */
    private List<String> distinctFailuresByCategory(AuditReport report) {
        return report.getFileAudits().stream()
                .flatMap(fa -> fa.getAuditEntries().stream())
                .map(FileAuditEntry::getStyleGuideRule)
                .distinct()
                .map(r -> Iterables.getOnlyElement(RULE_TO_CATEGORY.get(r.substring(0, 3))))
                .collect(Collectors.toList());
    }
}
