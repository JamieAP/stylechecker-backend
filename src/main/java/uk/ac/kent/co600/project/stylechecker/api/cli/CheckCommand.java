package uk.ac.kent.co600.project.stylechecker.api.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CheckCommand extends Command {

    private final CheckerFactory checkerFactory;
    private final SourcesJarExtractor extractor;

    public CheckCommand(
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
        DirectoryStream<Path> paths = Files.newDirectoryStream(
                srcDir.toPath(),
                entry -> entry.toString().endsWith("zip") || entry.toString().endsWith("jar")
        );
        ImmutableList<AuditReport> reports = StreamSupport.stream(paths.spliterator(), false)
                .map(extractJar())
                .map(this::checkSourceFiles)
                .collect(ImmutableCollectors.toList());

        reports.stream().forEach(this::writeToFile);
    }

    private File getSourcesDirectory(Namespace namespace) throws IOException {
        String filePath = Iterables.getOnlyElement(namespace.get("filePath"));
        System.out.println("Looking in: " + filePath);
        File file = Paths.get(filePath).toFile();
        checkArgument(
                file.isDirectory() && file.exists(),
                file.getAbsolutePath() + " does not exist or is not a readable directory"
        );
        return file;
    }

    private Function<Path, ExtractionResult> extractJar() {
        return p -> {
            try {
                return extractor.extract(new FileInputStream(p.toFile()));
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        };
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

        return auditor.buildReport(srcFiles);
    }

    // TODO: Needs to write a textual report to the working dir
    private void writeToFile(AuditReport auditReport) {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
    }
}
