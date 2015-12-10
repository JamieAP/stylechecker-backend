package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileSnippet {

    private final String previousLine;
    private final String targetLine;
    private final String nextLine;

    public FileSnippet(String previousLine, String targetLine, String nextLine) {
        this.previousLine = previousLine;
        this.targetLine = targetLine;
        this.nextLine = nextLine;
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
}
