package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.collect.ImmutableList;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtractedFile {

    private final String friendlyFileName;
    private final File file;
    private final ImmutableList<String> lines;

    public ExtractedFile(File file, String friendlyFileName, Iterable<String> lines) {
        this.file = checkNotNull(file);
        this.friendlyFileName = checkNotNull(friendlyFileName);
        this.lines = ImmutableList.copyOf(lines);
    }

    public String getFriendlyFileName() {
        return friendlyFileName;
    }

    public File getFile() {
        return file;
    }

    public ImmutableList<String> getLines() {
        return lines;
    }

    public static ExtractedFile of(File file, String friendlyFileName, Iterable<String> lines) {
        return new ExtractedFile(file, friendlyFileName, lines);
    }
}
