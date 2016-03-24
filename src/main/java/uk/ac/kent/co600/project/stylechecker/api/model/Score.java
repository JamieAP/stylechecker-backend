package uk.ac.kent.co600.project.stylechecker.api.model;

import uk.ac.kent.co600.project.stylechecker.StylecheckerConfiguration;

public class Score {

    private final Float documentationScore;
    private final Float namingScore;
    private final Float layoutScore;
    private final StylecheckerConfiguration.Weights weights;

    private final int documentationErrors;
    private final int namingErrors;
    private final int layoutErrors;

    private final int documentationRules;
    private final int namingRules;
    private final int layoutRules;

    public Score(
            Float layoutScore,
            Float namingScore,
            Float documentationScore,
            StylecheckerConfiguration.Weights weights,
            int documentationErrors,
            int namingErrors,
            int layoutErrors,
            int documentationRules,
            int namingRules,
            int layoutRules
    ) {
        this.layoutScore = layoutScore;
        this.namingScore = namingScore;
        this.documentationScore = documentationScore;
        this.weights = weights;
        this.documentationErrors = documentationErrors;
        this.namingErrors = namingErrors;
        this.layoutErrors = layoutErrors;
        this.documentationRules = documentationRules;
        this.namingRules = namingRules;
        this.layoutRules = layoutRules;
    }

    public Float getDocumentationScore() {
        return documentationScore;
    }

    public Float getNamingScore() {
        return namingScore;
    }

    public Float getLayoutScore() {
        return layoutScore;
    }

    public int getDocumentationErrors() { return documentationErrors; }

    public int getNamingErrors() { return namingErrors; }

    public int getLayoutErrors() { return layoutErrors; }

    public int getDocumentationRules() { return documentationRules; }

    public int getNamingRules() { return namingRules; }

    public int getLayoutRules() { return layoutRules; }

    public Float getTotalScore() {
        return (getDocumentationScore() * weights.getDocumentationWeight()) +
                (getLayoutScore() * weights.getLayoutWeight()) +
                (getNamingScore() * weights.getNamingWeight());
    }
}
