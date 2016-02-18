package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileSnippet {

    private final String previousLine2;
    private final String previousLine;
    private final String targetLine;
    private final String nextLine;
    private final String nextLine2;

    public FileSnippet(String previousLine2, String previousLine, String targetLine, String nextLine, String nextLine2) {
        this.previousLine2 = previousLine2;
        this.previousLine = previousLine;
        this.targetLine = targetLine;
        this.nextLine = nextLine;
        this.nextLine2 = nextLine2;
    }
    public String getPreviousLine2() {
        return previousLine2;
    }

    public String getPreviousLine() {
        return previousLine;
    }

    public String getTargetLine() {
        return targetLine;
    }

    public String getNextLine() {
        return nextLine;
    }

    public String getNextLine2() {
        return nextLine2;
    }
}
