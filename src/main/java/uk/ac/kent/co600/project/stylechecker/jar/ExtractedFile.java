package uk.ac.kent.co600.project.stylechecker.jar;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtractedFile {

    private final String friendlyFileName;
    private final File file;

    public ExtractedFile(File file, String friendlyFileName) {
        this.file = checkNotNull(file);
        this.friendlyFileName = checkNotNull(friendlyFileName);
    }

    public String getFriendlyFileName() {
        return friendlyFileName;
    }

    public File getFile() {
        return file;
    }

    public static ExtractedFile of(File file, String friendlyFileName) {
        return new ExtractedFile(file, friendlyFileName);
    }
}
