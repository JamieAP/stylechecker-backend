package uk.ac.kent.co600.project.stylechecker.api.model;

import com.google.common.collect.ImmutableList;

public class FileAudit {

    private final String filePath;
    private final ImmutableList<FileAuditEntry> auditEntries;

    public FileAudit(String filePath, ImmutableList<FileAuditEntry> auditEntries) {
        this.auditEntries = auditEntries;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public ImmutableList<FileAuditEntry> getAuditEntries() {
        return auditEntries;
    }
}
