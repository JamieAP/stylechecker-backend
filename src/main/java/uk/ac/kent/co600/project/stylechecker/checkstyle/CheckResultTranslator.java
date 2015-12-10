package uk.ac.kent.co600.project.stylechecker.checkstyle;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
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
import uk.ac.kent.co600.project.stylechecker.api.model.FileAuditEntry;

import java.lang.reflect.Field;

public class CheckResultTranslator {

    private static final ImmutableMap<Class<?>, Function<AuditEvent, FileAuditEntry>> TRANSLATORS =
            createTranslators();
    private static final String SOURCE_CLASS_FIELD_NAME = "sourceClass";

    public static FileAuditEntry translate(AuditEvent checkResult) {
        try {
            return TRANSLATORS.get(checkClassOf(checkResult)).apply(checkResult);
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

    private static ImmutableMap<Class<?>, Function<AuditEvent, FileAuditEntry>> createTranslators() {
        ImmutableMap.Builder<Class<?>, Function<AuditEvent, FileAuditEntry>> builder = ImmutableMap.builder();
        builder.put(
                TypeNameCheck.class,
                e -> toAuditEntry("1.2 Class names start with a capital letter", e)
        );
        builder.put(
                MethodNameCheck.class,
                e -> toAuditEntry("1.4 Method and variable names start with lowercase letters", e)
        );
        builder.put(
                MemberNameCheck.class,
                e -> toAuditEntry("1.4 Method and variable names start with lowercase letters", e)
        );
        builder.put(
                ConstantNameCheck.class,
                e -> toAuditEntry("1.5 Constants are written in UPPERCASE", e)
        );
        builder.put(
                IndentationCheck.class,
                e -> toAuditEntry("2.2 All statements within a block are indented one level / 2.1 One level of indentation is four spaces", e)
        );
        builder.put(
                MissingCtorCheck.class,
                e -> toAuditEntry("4.5 Always include a constructor (even if the body is empty)", e)
        );
        builder.put(
                NeedBracesCheck.class,
                e -> toAuditEntry("2.5 Always use braces in control structures", e)
        );
        builder.put(
                DeclarationOrderCheck.class,
                e -> toAuditEntry("4.1 Order of declarations: fields, constructors, methods", e)
        );
        builder.put(
                LeftCurlyCheck.class,
                e -> toAuditEntry("2.3 Braces for classes and methods are alone on one line / 2.4 For all other blocks, braces open at the end of a line", e)
        );
        builder.put(
                WhitespaceAroundCheck.class,
                e -> toAuditEntry("2.7 Use a space around operators / 2.6 Use a space before the opening brace of a control structure's block", e)
        );
        builder.put(
                EmptyLineSeparatorCheck.class,
                e -> toAuditEntry("2.8 Use a blank line between methods (and constructors)", e)
        );
        builder.put(
                JavadocTypeCheck.class,
                e -> toAuditEntry("3.1 Every class has a class comment at the top", e)
        );
        builder.put(
                JavadocMethodCheck.class,
                e -> toAuditEntry("3.2 Every method has a method comment", e)
        );
        builder.put(
                AvoidStarImportCheck.class,
                e -> toAuditEntry("4.4 Import classes separately", e)
        );
        builder.put(
                VisibilityModifierCheck.class,
                e -> toAuditEntry("4.2 Fields may not be public (except for final fields)", e)
        );
        return builder.build();
    }

    private static FileAuditEntry toAuditEntry(String msg, AuditEvent e) {
        return FileAuditEntry.of(
                msg,
                e.getLine(),
                e.getColumn(),
                e.getSourceName(),
                e.getMessage()
        );
    }
}
