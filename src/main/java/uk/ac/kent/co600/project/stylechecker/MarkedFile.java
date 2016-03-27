package uk.ac.kent.co600.project.stylechecker;

import com.google.common.collect.ImmutableList;


public class MarkedFile {

    private final String filename;
    private final float documentationMark;
    private final float namingMark;
    private final float layoutMark;
    private final float totalMark;

    public MarkedFile(
            String filename,
            float documentationMark,
            float namingMark,
            float layoutMark,
            float totalMark
    ) {
        this.filename = filename;
        this.documentationMark = documentationMark;
        this.namingMark = namingMark;
        this.layoutMark = layoutMark;
        this.totalMark = totalMark;
    }

    public String getFilename() {
        return filename;
    }

    public float getDocumentationMark() {
        return documentationMark;
    }

    public float getNamingMark() {
        return namingMark;
    }

    public float getLayoutMark() {
        return layoutMark;
    }

    public float getTotalMark() {
        return totalMark;
    }

    public ImmutableList<String> toText(){
        ImmutableList.Builder<String> textReport = ImmutableList.builder();
        textReport.add(String.format("---------%s---------%n", getFilename()));
        textReport.add(String.format("Documentation Mark:%.2f%%%n", getDocumentationMark()));
        textReport.add(String.format("Naming Mark: %.2f%%%n", getNamingMark()));
        textReport.add(String.format("Layout Mark: %.2f%%%n", getLayoutMark()));
        textReport.add(String.format("Total Mark: %.2f%%%n", getTotalMark()));
        textReport.add("\r\n");

        return textReport.build();
    }
}
