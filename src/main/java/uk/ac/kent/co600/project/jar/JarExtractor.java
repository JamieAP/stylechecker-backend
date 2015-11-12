package uk.ac.kent.co600.project.jar;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtractor {

    private static final String JAVA_SOURCE_FILE_EXTENSION = ".java";
    private static final String TEMPORARY_FILE_PREFIX = "/tmp/";
    private static final String DASH_SEPARATOR = "-";

    public ExtractionResult extract(InputStream is) throws IOException {
        UUID sessionUuid = UUID.randomUUID();
        JarFile jarFile = saveJarToFs(is, sessionUuid);
        return processEntries(sessionUuid, jarFile);
    }

    private ExtractionResult processEntries(UUID sessionUuid, JarFile jarFile) throws IOException {
        ImmutableList.Builder<String> ignoredFileNames = ImmutableList.builder();
        ImmutableList.Builder<File> extractedSourceFiles = ImmutableList.builder();

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (Strings.isNullOrEmpty(entryName) || !entryName.endsWith(JAVA_SOURCE_FILE_EXTENSION)) {
                ignoredFileNames.add(entryName);
            } else {
                extractedSourceFiles.add(extractFile(sessionUuid, jarFile, entry));
            }
        }

        return ExtractionResult.of(
                sessionUuid,
                ignoredFileNames.build(),
                extractedSourceFiles.build()
        );
    }

    private JarFile saveJarToFs(InputStream is, UUID sessionUuid) throws IOException {
        Path tempPathForJar = Paths.get(TEMPORARY_FILE_PREFIX + sessionUuid.toString());
        Files.copy(is, tempPathForJar);
        return new JarFile(tempPathForJar.toFile());
    }

    private File extractFile(UUID sessionUuid, JarFile jar, JarEntry entry) throws IOException {
        java.nio.file.Path fileDest = Files.createTempFile(sessionUuid.toString() + DASH_SEPARATOR + sanitizeFileName(entry), ".java");
        InputStream fileIs = jar.getInputStream(entry);
        FileOutputStream fileOs = new FileOutputStream(fileDest.toFile());
        while (fileIs.available() != 0) {
            fileOs.write(fileIs.read());
        }
        return fileDest.toFile();
    }

    private String sanitizeFileName(JarEntry entry) {
        return entry.getName().replace("/", "-").replace(File.pathSeparator, DASH_SEPARATOR);
    }
}