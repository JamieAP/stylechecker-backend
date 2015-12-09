package uk.ac.kent.co600.project.stylechecker.api.model;

public class FileAuditEntry {

    private final Integer column;
    private final Integer line;
    private final String message;
    private final String checkClassName;

    public FileAuditEntry(String message, Integer line, Integer column, String checkClassName) {
        this.message = message;
        this.line = line;
        this.column = column;
        this.checkClassName = checkClassName;
    }

    public Integer getColumn() {
        return column;
    }

    public Integer getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    public String getCheckClassName() {
        return checkClassName;
    }

    public static FileAuditEntry of(String msg, Integer line, Integer col, String checkClassName) {
        return new FileAuditEntry(msg, line, col, checkClassName);
    }
}