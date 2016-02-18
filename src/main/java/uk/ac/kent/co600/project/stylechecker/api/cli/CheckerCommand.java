package uk.ac.kent.co600.project.stylechecker.api.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CheckerCommand extends Command {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String RESULTS_FILE_SUFFIX = "-results.txt";
    private final CheckerFactory checkerFactory;
    private final SourcesJarExtractor extractor;

    public CheckerCommand(
            String name,
            String description,
            CheckerFactory checkerFactory,
            SourcesJarExtractor extractor
    ) {
        super(name, description);
        this.checkerFactory = checkNotNull(checkerFactory);
        this.extractor = checkNotNull(extractor);
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("-d", "--file-dir")
                .action(Arguments.append())
                .dest("filePath")
                .help("The path containing checkable JAR/ZIP files");
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        File srcDir = getSourcesDirectory(namespace);
        ImmutableList<Path> paths = ImmutableList.copyOf(
                Files.newDirectoryStream(
                        srcDir.toPath(),
                        entry -> entry.toString().endsWith("zip") || entry.toString().endsWith("jar")
                )
        );

        System.out.printf("Found %d JAR/ZIP files \n", paths.size());
        paths.forEach(p -> System.out.println(p.getFileName().toString()));

        paths.stream()
                .map(pathToJarExtractionResult())
                .map(this::checkSourceFiles)
                .forEach(this::writeToFile);
    }

    private Function<Path, ExtractionResult> pathToJarExtractionResult() {
        return p -> {
            try {
                return extractor.extract(p.getFileName().toString(), new FileInputStream(p.toFile()));
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        };
    }

    private File getSourcesDirectory(Namespace namespace) throws IOException {
        String filePath = Iterables.getOnlyElement(namespace.get("filePath"));
        File file = Paths.get(filePath).toFile();
        System.out.println("Looking in: " + file.getAbsolutePath());
        checkArgument(
                file.isDirectory() && file.exists(),
                file.getAbsolutePath() + " does not exist or is not a readable directory"
        );
        return file;
    }

    private AuditReport checkSourceFiles(ExtractionResult srcFiles) {
        AuditReportGenerator auditor = new AuditReportGenerator(checkerFactory.getNumberOfChecks());

        Checker checker = checkerFactory.createChecker();
        checker.addListener(auditor);

        ImmutableList<File> files = srcFiles.getExtractedFiles()
                .stream()
                .map(ExtractedFile::getFile)
                .collect(ImmutableCollectors.toList());

        try {
            checker.process(files);
        } catch (CheckstyleException e) {
            throw Throwables.propagate(e);
        }

        AuditReport report = auditor.buildReport(srcFiles);
        srcFiles.getExtractedFiles().forEach(f -> f.getFile().delete());
        return report;
    }

    private void writeToFile(AuditReport auditReport) {
        Path resultsFile = Paths.get(
                WORKING_DIR, auditReport.getOriginalJarName() + RESULTS_FILE_SUFFIX
        );
        System.out.printf(
                "Writing report for %s to %s\n",
                auditReport.getOriginalJarName(),
                resultsFile.toAbsolutePath().toAbsolutePath()
        );

        ImmutableMultiset<String> failedRules = auditReport.getFileAudits().stream()
                .flatMap(audit -> audit.getAuditEntries().stream())
                .map(FileAuditEntry::getStyleGuideRule)
                .collect(ImmutableCollectors.toMultiset());

        try (PrintWriter writer = new PrintWriter(resultsFile.toFile(), UTF8.name())) {
            writer.println("---------Results---------");
            writer.printf("Total Rules: %d%n", auditReport.getNumberOfChecks());
            writer.printf("Total Errors: %d%n", auditReport.getUniqueFailedChecks());
            writer.printf("Mark: %.2f%%%n", auditReport.getGrade());

            writer.println("\n---------Summary---------");
            Ordering.natural()
                    .onResultOf(failedRules::count)
                    .reverse()
                    .immutableSortedCopy(failedRules.elementSet())
                    .forEach(s -> writer.printf("Errors: %d Rule: %s%n", failedRules.count(s), s));

            writer.println("\n---------Source File Details---------");
            auditReport.getFileAudits().forEach(f -> {
                f.getAuditEntries().forEach(a ->
                        writer.printf(
                                "File: %s Line: %d Col: %d Rule: %s%n",
                                f.getFilePath(),
                                a.getLine(),
                                a.getColumn(),
                                a.getStyleGuideRule()
                        )
                );
                writer.println();
            });
            writer.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}