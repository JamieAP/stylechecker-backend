package uk.ac.kent.co600.project.stylechecker;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Configuration file bean, instances of this class are typically constructed by
 * deserialising a YAML config file. See https://dropwizard.github.io/dropwizard/0.9.1/docs/manual/core.html#configuration
 * for more details
 */
public class StylecheckerConfiguration extends Configuration {

    @NotNull
    private Weights weights;

    @JsonProperty("weights")
    public Weights getWeights() {
        return weights;
    }

    public static class Weights {

        @Min(0)
        @Max(100)
        @JsonProperty("layout")
        private Float layout;
        @Min(0)
        @Max(100)
        @JsonProperty("documentation")
        private Float documentation;
        @Min(0)
        @Max(100)
        @JsonProperty("naming")
        private Float naming;

        public Weights() {}

        public Weights(Float layout, Float documentation, Float naming) {
            this.layout = layout;
            this.documentation = documentation;
            this.naming = naming;
        }

        public Float getLayoutWeight() {
            return layout;
        }

        public Float getDocumentationWeight() {
            return documentation;
        }

        public Float getNamingWeight() {
            return naming;
        }
    }
}