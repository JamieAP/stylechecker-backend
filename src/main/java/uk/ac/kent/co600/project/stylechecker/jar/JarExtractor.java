package uk.ac.kent.co600.project.stylechecker.jar;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtractor {

    private static final String JAVA_SOURCE_FILE_EXTENSION = ".java";
    private static final String JAR_FILE_EXTENSION = ".jar";
    private static final String DASH_SEPARATOR = "-";
    private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"));

    public ExtractionResult extract(InputStream is) throws IOException {
        UUID sessionUuid = UUID.randomUUID();
        JarFile jarFile = saveJarToFs(is, sessionUuid);
        return processEntries(sessionUuid, jarFile);
    }

    private ExtractionResult processEntries(UUID sessionUuid, JarFile jarFile) throws IOException {
        ImmutableList.Builder<String> ignoredFileNames = ImmutableList.builder();
        ImmutableList.Builder<ExtractedFile> extractedSourceFiles = ImmutableList.builder();

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (Strings.isNullOrEmpty(entryName) || !entryName.endsWith(JAVA_SOURCE_FILE_EXTENSION)) {
                ignoredFileNames.add(entryName);
            } else {
                extractedSourceFiles.add(extractFile(sessionUuid, jarFile, entry));
            }
        }

        return ExtractionResult.of(ignoredFileNames.build(), extractedSourceFiles.build());
    }

    private JarFile saveJarToFs(InputStream is, UUID sessionUuid) throws IOException {
        Path tempPathForJar = Files.createTempFile(TEMP_DIR, sessionUuid.toString(), JAR_FILE_EXTENSION);
        Files.copy(is, tempPathForJar, StandardCopyOption.REPLACE_EXISTING);
        return new JarFile(tempPathForJar.toFile());
    }

    private ExtractedFile extractFile(UUID sessionUuid, JarFile jar, JarEntry entry) throws IOException {
        java.nio.file.Path fileDest = Files.createTempFile(
                sessionUuid.toString() + DASH_SEPARATOR + sanitizeFileName(entry),
                JAVA_SOURCE_FILE_EXTENSION
        );
        FileOutputStream fileOs = new FileOutputStream(fileDest.toFile());
        InputStream fileIs = jar.getInputStream(entry);
        while (fileIs.available() != 0) {
            fileOs.write(fileIs.read());
        }
        return ExtractedFile.of(
                fileDest.toFile(),
                entry.getName(),
                IOUtils.readLines(jar.getInputStream(entry))
        );
    }

    private String sanitizeFileName(JarEntry entry) {
        return entry.getName().replace("/", "-").replace(File.pathSeparator, DASH_SEPARATOR);
    }
}