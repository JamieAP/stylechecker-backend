package uk.ac.kent.co600.project.stylechecker.checkstyle;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.checks.blocks.LeftCurlyCheck;
import com.puppycrawl.tools.checkstyle.checks.blocks.NeedBracesCheck;
import com.puppycrawl.tools.checkstyle.checks.coding.DeclarationOrderCheck;
import com.puppycrawl.tools.checkstyle.checks.coding.MissingCtorCheck;
import com.puppycrawl.tools.checkstyle.checks.design.VisibilityModifierCheck;
import com.puppycrawl.tools.checkstyle.checks.imports.AvoidStarImportCheck;
import com.puppycrawl.tools.checkstyle.checks.indentation.IndentationCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.MemberNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.MethodNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.TypeNameCheck;
import com.puppycrawl.tools.checkstyle.checks.whitespace.EmptyLineSeparatorCheck;
import com.puppycrawl.tools.checkstyle.checks.whitespace.WhitespaceAroundCheck;
import org.apache.commons.io.IOUtils;
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.api.model.FileSnippet;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiFunction;

public class CheckResultTranslator {

    private static final ImmutableMap<Class<?>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> TRANSLATORS =
            createTranslators();
    private static final String SOURCE_CLASS_FIELD_NAME = "sourceClass";

    public static FileAuditEntry translate(AuditEvent checkResult, ExtractedFile file) {
        try {
            return TRANSLATORS.get(checkClassOf(checkResult)).apply(checkResult, file);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static Class<?> checkClassOf(AuditEvent checkResult) throws NoSuchFieldException, IllegalAccessException {
        LocalizedMessage msg = checkResult.getLocalizedMessage();
        Field sourceClass = msg.getClass().getDeclaredField(SOURCE_CLASS_FIELD_NAME);
        sourceClass.setAccessible(true);
        return (Class) sourceClass.get(msg);
    }

    private static String checkKeyOf(AuditEvent e) {
        try {
            Field keyField = LocalizedMessage.class.getDeclaredField("key");
            keyField.setAccessible(true);
            return (String) keyField.get(e.getLocalizedMessage());
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private static <T> T firstArgOf(AuditEvent e, Class<T> argType) {
        try {
            Field keyField = LocalizedMessage.class.getDeclaredField("args");
            keyField.setAccessible(true);
            return (T) ((Object[]) keyField.get(e.getLocalizedMessage()))[0];
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private static FileAuditEntry toAuditEntry(String msg, AuditEvent e, ExtractedFile file) {
        return FileAuditEntry.of(
                msg,
                e.getLine(),
                e.getColumn(),
                e.getSourceName(),
                e.getMessage(),
                getLines(file, e.getLine())
        );
    }

    private static FileSnippet getLines(ExtractedFile file, Integer line) {
        ImmutableList<String> lines = file.getLines();
        int previousLine = line - 2;
        int nextLine = line;
        return new FileSnippet(
                lines.size() > previousLine && previousLine >= 0 ? lines.get(previousLine) : null,
                lines.get(line - 1),
                lines.size() > nextLine && nextLine >= 0 ? lines.get(nextLine) : null
        );
    }

    private static ImmutableMap<Class<?>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> createTranslators() {
        ImmutableMap.Builder<Class<?>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> builder = ImmutableMap.builder();
        builder.put(
                TypeNameCheck.class,
                (e, f) -> toAuditEntry("1.2 Class names start with a capital letter", e, f)
        );
        builder.put(
                MethodNameCheck.class,
                (e, f) -> toAuditEntry("1.4 Method and variable names start with lowercase letters", e, f)
        );
        builder.put(
                MemberNameCheck.class,
                (e, f) -> toAuditEntry("1.4 Method and variable names start with lowercase letters", e, f)
        );
        builder.put(
                ConstantNameCheck.class,
                (e, f) -> toAuditEntry("1.5 Constants are written in UPPERCASE", e, f)
        );
        builder.put(
                IndentationCheck.class,
                (e, f) -> checkKeyOf(e).equals("indentation.error") ?
                        toAuditEntry("2.2 All statements within a block are indented one level", e, f) :
                        toAuditEntry("2.1 One level of indentation is four spaces", e, f)
        );
        builder.put(
                MissingCtorCheck.class,
                (e, f) -> toAuditEntry("4.5 Always include a constructor (even if the body is empty)", e, f)
        );
        builder.put(
                NeedBracesCheck.class,
                (e, f) -> toAuditEntry("2.5 Always use braces in control structures", e, f)
        );
        builder.put(
                DeclarationOrderCheck.class,
                (e, f) -> toAuditEntry("4.1 Order of declarations: fields, constructors, methods", e, f)
        );
        builder.put(
                LeftCurlyCheck.class,
                (e, f) -> checkKeyOf(e).equals("line.new") ?
                        toAuditEntry("2.3 Braces for classes and methods are alone on one line", e, f) :
                        toAuditEntry("2.4 For all other blocks, braces open at the end of a line", e, f)
        );
        builder.put(
                WhitespaceAroundCheck.class,
                (e, f) -> firstArgOf(e, String.class).equals("{") ?
                        toAuditEntry("2.6 Use a space before the opening brace of a control structure's block", e, f) :
                        toAuditEntry("2.7 Use a space around operators", e, f)
        );
        builder.put(
                EmptyLineSeparatorCheck.class,
                (e, f) -> toAuditEntry("2.8 Use a blank line between methods (and constructors)", e, f)
        );
        builder.put(
                JavadocTypeCheck.class,
                (e, f) -> toAuditEntry("3.1 Every class has a class comment at the top", e, f)
        );
        builder.put(
                JavadocMethodCheck.class,
                (e, f) -> toAuditEntry("3.2 Every method has a method comment", e, f)
        );
        builder.put(
                AvoidStarImportCheck.class,
                (e, f) -> toAuditEntry("4.4 Import classes separately", e, f)
        );
        builder.put(
                VisibilityModifierCheck.class,
                (e, f) -> toAuditEntry("4.2 Fields may not be public (except for final fields)", e, f)
        );
        return builder.build();
    }
}