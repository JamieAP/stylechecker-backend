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

    private Score (
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

    private Score(Builder builder) {
        documentationScore = builder.documentationScore;
        namingScore = builder.namingScore;
        layoutScore = builder.layoutScore;
        weights = builder.weights;
        documentationErrors = builder.documentationErrors;
        namingErrors = builder.namingErrors;
        layoutErrors = builder.layoutErrors;
        documentationRules = builder.documentationRules;
        namingRules = builder.namingRules;
        layoutRules = builder.layoutRules;
    }

    public static Builder newBuilder() {
        return new Builder();
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


    public static final class Builder {
        private Float documentationScore;
        private Float namingScore;
        private Float layoutScore;
        private StylecheckerConfiguration.Weights weights;
        private int documentationErrors;
        private int namingErrors;
        private int layoutErrors;
        private int documentationRules;
        private int namingRules;
        private int layoutRules;

        private Builder() {
        }

        public Builder withDocumentationScore(Float val) {
            documentationScore = val;
            return this;
        }

        public Builder withNamingScore(Float val) {
            namingScore = val;
            return this;
        }

        public Builder withLayoutScore(Float val) {
            layoutScore = val;
            return this;
        }

        public Builder withWeights(StylecheckerConfiguration.Weights val) {
            weights = val;
            return this;
        }

        public Builder withDocumentationErrors(int val) {
            documentationErrors = val;
            return this;
        }

        public Builder withNamingErrors(int val) {
            namingErrors = val;
            return this;
        }

        public Builder withLayoutErrors(int val) {
            layoutErrors = val;
            return this;
        }

        public Builder withDocumentationRules(int val) {
            documentationRules = val;
            return this;
        }

        public Builder withNamingRules(int val) {
            namingRules = val;
            return this;
        }

        public Builder withLayoutRules(int val) {
            layoutRules = val;
            return this;
        }

        public Score build() {
            return new Score(this);
        }
    }
}
