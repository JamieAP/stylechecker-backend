package uk.ac.kent.co600.project.stylechecker;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAudit;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditScorer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AuditScorerTest {

    private StylecheckerConfiguration.Weights equalWeights =
            new StylecheckerConfiguration.Weights(.50f, .25f, .25f);


    @Test
    public void score() throws Exception {
        AuditScorer scorer = new AuditScorer(equalWeights);
        AuditReport report = AuditReport.newBuilder()
                .withFileAudits(
                        ImmutableList.of(
                                new FileAudit(
                                        "file1",
                                        ImmutableList.of(
                                                new FileAuditEntry(
                                                        "1.2", 0, 0, "", "", null
                                                ),
                                                new FileAuditEntry(
                                                        "3.1", 0, 0, "", "", null
                                                ),
                                                new FileAuditEntry(
                                                        "4.1", 0, 0, "", "", null
                                                )
                                        )
                                )
                        )
                )
                .build();
        report = scorer.score(report);
        assertThat(report.getScore().getTotalScore(), is(78.33333F));
    }
}