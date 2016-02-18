package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

public class ExtractionResult {

    private final String originalJarName;
    private final ImmutableList<String> ignoredFiles;
    private final ImmutableList<ExtractedFile> extractedFiles;

    public ExtractionResult(
            String originalJarName,
            Iterable<String> ignoredFiles,
            Iterable<ExtractedFile> extractedFiles
    ) {
        this.originalJarName = originalJarName;
        this.ignoredFiles = ImmutableList.copyOf(ignoredFiles);
        this.extractedFiles = ImmutableList.copyOf(extractedFiles);
    }

    public String getOriginalJarName() {
        return originalJarName;
    }

    public ImmutableList<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public ImmutableList<ExtractedFile> getExtractedFiles() {
        return extractedFiles;
    }

    public ImmutableMap<String, ExtractedFile> mapPathsToFiles() {
        return extractedFiles.stream()
                .collect(ImmutableCollectors.toMap(f -> f.getFile().getAbsolutePath(), f -> f));
    }

    public static ExtractionResult of(String fileName, Iterable<String> ignoredFiles, Iterable<ExtractedFile> extractedFiles) {
        return new ExtractionResult(fileName, ignoredFiles, extractedFiles);
    }
}