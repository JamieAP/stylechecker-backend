package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.collect.ImmutableList;

public class ExtractionResult {

    private final ImmutableList<String> ignoredFiles;
    private final ImmutableList<ExtractedFile> extractedFiles;

    public ExtractionResult(Iterable<String> ignoredFiles, Iterable<ExtractedFile> extractedFiles) {
        this.ignoredFiles = ImmutableList.copyOf(ignoredFiles);
        this.extractedFiles = ImmutableList.copyOf(extractedFiles);
    }

    public ImmutableList<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public ImmutableList<ExtractedFile> getExtractedFiles() {
        return extractedFiles;
    }

    public static ExtractionResult of(Iterable<String> ignoredFiles, Iterable<ExtractedFile> extractedFiles) {
        return new ExtractionResult(ignoredFiles, extractedFiles);
    }
}