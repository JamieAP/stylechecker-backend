package uk.ac.kent.co600.project.jar;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtractionResult {

    private final UUID sessionUuid;
    private final ImmutableList<String> ignoredFiles;
    private final ImmutableList<File> extractedFiles;

    public ExtractionResult(UUID sessionUuid, Iterable<String> ignoredFiles, Iterable<File> extractedFiles) {
        this.sessionUuid = checkNotNull(sessionUuid);
        this.ignoredFiles = ImmutableList.copyOf(ignoredFiles);
        this.extractedFiles = ImmutableList.copyOf(extractedFiles);
    }

    public UUID getSessionUuid() {
        return sessionUuid;
    }

    public ImmutableList<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public ImmutableList<File> getExtractedFiles() {
        return extractedFiles;
    }

    public static ExtractionResult of(UUID uuid, Iterable<String> ignoredFiles, Iterable<File> extractedFiles) {
        return new ExtractionResult(uuid, ignoredFiles, extractedFiles);
    }
}
