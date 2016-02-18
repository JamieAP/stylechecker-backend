package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileSnippet {

    private final String firstLine;
    private final String secondLine;
    private final String targetLine;
    private final String forthLine;
    private final String fifthLine;

    public FileSnippet(String firstLine, String secondLine, String targetLine, String forthLine, String fifthLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.targetLine = targetLine;
        this.forthLine = forthLine;
        this.fifthLine = fifthLine;
    }
    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public String getTargetLine() {
        return targetLine;
    }

    public String getForthLine() {
        return forthLine;
    }

    public String getFifthLine() {
        return fifthLine;
    }
}
