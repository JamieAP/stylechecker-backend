package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileAuditEntry {

    private final Integer column;
    private final Integer line;
    private final String styleGuideRule;
    private final String checkClassName;
    private final String checkErrorMessge;

    public FileAuditEntry(
            String styleGuideRule,
            Integer line,
            Integer column,
            String checkClassName,
            String checkErrorMessge
    ) {
        this.styleGuideRule = styleGuideRule;
        this.line = line;
        this.column = column;
        this.checkClassName = checkClassName;
        this.checkErrorMessge = checkErrorMessge;
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

    public String getCheckErrorMessge() {
        return checkErrorMessge;
    }

    public static FileAuditEntry of(
            String msg,
            Integer line,
            Integer col,
            String checkClassName,
            String checkErrorMessge
    ) {
        return new FileAuditEntry(msg, line, col, checkClassName, checkErrorMessge);
    }
}