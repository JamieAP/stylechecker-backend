package uk.ac.kent.co600.project.stylechecker.api.cli;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditScorer;
import uk.ac.kent.co600.project.stylechecker.StylecheckerConfiguration;
import uk.ac.kent.co600.project.stylechecker.api.model.AuditReport;
import uk.ac.kent.co600.project.stylechecker.checkstyle.CheckerFactory;
import uk.ac.kent.co600.project.stylechecker.checkstyle.audit.AuditReportGenerator;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractionResult;
import uk.ac.kent.co600.project.stylechecker.jar.SourcesJarExtractor;
import uk.ac.kent.co600.project.stylechecker.utils.ImmutableCollectors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the CLI command to allow bulk checking of JARs for style compliance.
 */
public class CheckerCommand extends ConfiguredCommand<StylecheckerConfiguration> {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String OUTPUT_DIR = "outputDir";
    private static final String INPUT_DIR = "inputDir";
    private static final String RESULTS_FILE_SUFFIX = "-results.txt";
    private static final String JAR = ".jar";
    private static final String ZIP = ".zip";

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
        subparser.addArgument("--input", "-i")
                .action(Arguments.append())
                .dest("inputDir")
                .required(true)
                .help("The directory containing ZIPs/JARs to be audited");
        subparser.addArgument("--output", "-o")
                .action(Arguments.append())
                .setDefault(WORKING_DIR)
                .dest("outputDir")
                .help("The directory to output reports to");
        subparser.addArgument("--conf")
                .action(Arguments.store())
                .dest("file")
                .required(true)
                .help("The configuration file");
    }

    @Override
    protected void run(
            Bootstrap<StylecheckerConfiguration> bootstrap,
            Namespace namespace,
            StylecheckerConfiguration conf
    ) throws Exception {
        File srcDir = getSourceDirectory(namespace);
        File targetDir = getOutputDirectory(namespace);

        ImmutableList<Path> paths = ImmutableList.copyOf(
                Files.newDirectoryStream(
                        srcDir.toPath(),
                        entry -> entry.toString().endsWith(ZIP) || entry.toString().endsWith(JAR)
                )
        );
        System.out.printf("Found %d JAR/ZIP files %n", paths.size());
        paths.forEach(p -> System.out.println(p.getFileName().toString()));
        AuditScorer scorer = new AuditScorer(conf.getWeights());
        ImmutableList<AuditReport> reports = paths.stream()
                .map(pathToJarExtractionResult())
                .map(r -> checkSourceFiles(r, scorer))
                .collect(ImmutableCollectors.toList());

        reports.forEach(r -> writeReport(r, targetDir));
        System.out.printf(
                "Writing Summary to %s \n",
                targetDir.toPath().toAbsolutePath()
        );
        reports.forEach(r -> writeSummary(r, targetDir));
    }

    /*
        Pulls the input directory from the command line args and checks it is an existing directory
     */
    private File getSourceDirectory(Namespace namespace) throws IOException {
        String path = Iterables.getOnlyElement(namespace.get(INPUT_DIR));
        File inputDir = Paths.get(path).toFile();
        System.out.println("Looking in: " + inputDir.getAbsolutePath());
        checkArgument(
                inputDir.isDirectory() && inputDir.exists(),
                inputDir.getAbsolutePath() + " does not exist or is not a readable directory"
        );
        return inputDir;
    }

    /*
        Pulls the output directory from the command line args, creating it if it does not exist
     */
    private File getOutputDirectory(Namespace namespace) {
        try {
            String path = Iterables.getOnlyElement(namespace.get(OUTPUT_DIR));
            File outputDir = Paths.get(path).toFile();
            if (!outputDir.exists()) {
                Files.createDirectory(outputDir.toPath());
            }
            return outputDir;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
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

    private AuditReport checkSourceFiles(ExtractionResult srcFiles, AuditScorer scorer) {
        AuditReportGenerator auditor = new AuditReportGenerator(
                checkerFactory.getNumberOfChecks(),
                scorer
        );

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

    /*
        Prints a detailed report of an audit to a file in the output directory
     */
    private void writeReport(AuditReport auditReport, File outputDirectory) {
        File outputFile = new File(
                outputDirectory, auditReport.getOriginalJarName() + RESULTS_FILE_SUFFIX
        );
        System.out.printf(
                "Writing report for %s to %s\n",
                auditReport.getOriginalJarName(),
                outputFile.toPath().toAbsolutePath()
        );

        try (PrintWriter writer = new PrintWriter(outputFile, UTF8.name())) {
            auditReport.toText().forEach(writer::write);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /*
        Prints a summarized form of an AuditReport to the summary file
     */
    private void writeSummary(AuditReport report, File outputDirectory) {
        File outputFile = new File(
                outputDirectory, "results.txt"
        );
        // append to file if it already exists
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(outputFile, true))) {
            writer.write("---------Marking Summary---------\r\n\r\n");
            writer.println(String.format("---------%s---------%n", report.getOriginalJarName()));
            writer.println(String.format("Documentation Mark:%.2f%%", report.getScore().getDocumentationScore()));
            writer.println(String.format("Naming Mark: %.2f%%", report.getScore().getNamingScore()));
            writer.println(String.format("Layout Mark: %.2f%%", report.getScore().getLayoutScore()));
            writer.println(String.format("Total Mark: %.2f%%", report.getScore().getTotalScore()));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}