package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileAuditEntry {

    private final Integer column;
    private final Integer line;
    private final String styleGuideRule;
    private final String checkClassName;
    private final String checkErrorMessage;
    private final FileSnippet lines;

    public FileAuditEntry(
            String styleGuideRule,
            Integer line,
            Integer column,
            String checkClassName,
            String checkErrorMessage,
            FileSnippet lines
    ) {
        this.styleGuideRule = styleGuideRule;
        this.line = line;
        this.column = column;
        this.checkClassName = checkClassName;
        this.checkErrorMessage = checkErrorMessage;
        this.lines = lines;
    }

    public Integer getColumn() {
        return column;
    }

    public Integer getLine() {
        return line;
    }

    public String getStyleGuideRule() {
        return styleGuideRule;
    }

    public String getCheckClassName() {
        return checkClassName;
    }

    public String getCheckErrorMessage() {
        return checkErrorMessage;
    }

    public FileSnippet getLines() {
        return lines;
    }

    public static FileAuditEntry of(
            String msg,
            Integer line,
            Integer col,
            String checkClassName,
            String checkErrorMessage,
            FileSnippet lines
    ) {
        return new FileAuditEntry(msg, line, col, checkClassName, checkErrorMessage, lines);
    }
}