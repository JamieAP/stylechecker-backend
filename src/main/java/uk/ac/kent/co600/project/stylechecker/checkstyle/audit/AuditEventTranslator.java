package uk.ac.kent.co600.project.stylechecker.checkstyle.audit;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Check;
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
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;
import uk.ac.kent.co600.project.stylechecker.api.model.FileSnippet;
import uk.ac.kent.co600.project.stylechecker.checkstyle.checks.InvokesSuperConstructorCheck;
import uk.ac.kent.co600.project.stylechecker.jar.ExtractedFile;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

/**
 * A class that translates a {@link com.puppycrawl.tools.checkstyle.Checker}'s {@link AuditEvent}s into our model.
 * A part of this process is to map {@link com.puppycrawl.tools.checkstyle.api.Check}s to the
 * Objects First style guide.
 * <p>
 * Due to some CheckStyle Checks mapping to more than one of Object First's rules we have to
 * use reflection to extract certain discriminating information from the {@link LocalizedMessage}
 * carried by each {@link AuditEvent} as its not exposed by the API.
 * <p>
 * Most checks can be matched to a style guide rule by the {@link Class} of the check alone.
 * <p>
 * Some checks are matched through a combination of the {@link Class} of the check and the message
 * key the check erred with. This message key normally tells CheckStyle which message to display
 * but here it also helps us identify the error case and map it to one of our rules.
 * <p>
 * Finally some checks are matched through a combination of the {@link Class} of the check and the
 * arguments the check used. For example, the WhitespaceAround check will error with an argument
 * of '{' if there is whitespace missing from a left curly brace. This tells us which of our two
 * rules covered by WhitespaceAround we should map an error to.
 */
public class AuditEventTranslator {

    private static final ImmutableMap<Class<? extends Check>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> TRANSLATORS =
            createTranslators();
    private static final String SOURCE_CLASS_FIELD_NAME = "sourceClass";
    private static final String KEY_FIELD_NAME = "key";
    private static final String ARGS_FIELD_NAME = "args";
    private static final String LINE_NEW = "line.new";
    private static final String LEFT_CURLY = "{";

    public static FileAuditEntry translate(AuditEvent checkResult, ExtractedFile file) {
        return TRANSLATORS.get(checkClassOf(checkResult)).apply(checkResult, file);
    }

    private static Class<? extends Check> checkClassOf(AuditEvent checkResult) {
        try {
            LocalizedMessage msg = checkResult.getLocalizedMessage();
            Field sourceClass = msg.getClass().getDeclaredField(SOURCE_CLASS_FIELD_NAME);
            sourceClass.setAccessible(true);
            return (Class<? extends Check>) sourceClass.get(msg);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static String checkKeyOf(AuditEvent e) {
        try {
            Field keyField = LocalizedMessage.class.getDeclaredField(KEY_FIELD_NAME);
            keyField.setAccessible(true);
            return (String) keyField.get(e.getLocalizedMessage());
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private static <T> T firstArgOf(AuditEvent e, Class<T> argType) {
        try {
            Field keyField = LocalizedMessage.class.getDeclaredField(ARGS_FIELD_NAME);
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

    /*
        Extracts the problematic line and the two lines either side of it.
     */
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

    /**
     * Provides the mapping between {@link com.puppycrawl.tools.checkstyle.api.Check} classes
     * and a {@link BiFunction} that knows how to turn an {@link AuditEvent} produced by that check
     * and the {@link ExtractedFile} the source came from into a {@link FileAuditEntry}
     */
    private static ImmutableMap<Class<? extends Check>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> createTranslators() {
        ImmutableMap.Builder<Class<? extends Check>, BiFunction<AuditEvent, ExtractedFile, FileAuditEntry>> builder = ImmutableMap.builder();
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
                (e, f) -> checkKeyOf(e).equals(LINE_NEW) ?
                        toAuditEntry("2.3 Braces for classes and methods are alone on one line", e, f) :
                        toAuditEntry("2.4 For all other blocks, braces open at the end of a line", e, f)
        );
        builder.put(
                WhitespaceAroundCheck.class,
                (e, f) -> firstArgOf(e, String.class).equals(LEFT_CURLY) ?
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
        builder.put(
                InvokesSuperConstructorCheck.class,
                (e, f) -> toAuditEntry("4.6 Always include superclass constructor call", e, f)
        );
        return builder.build();
    }
}