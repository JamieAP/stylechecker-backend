package uk.ac.kent.co600.project.stylechecker.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
    A check that the checks any class fields have access modifiers, i.e. they are not package
    private.
 */
public class FieldHasAccessModifierCheck extends Check {

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.VARIABLE_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getParent().getParent().getType() != TokenTypes.CLASS_DEF) {
            /* ast is not a class field */
            return;
        }
        DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        boolean hasNonPackageAccessModifier =
                modifiers.branchContains(TokenTypes.LITERAL_PUBLIC) ||
                modifiers.branchContains(TokenTypes.LITERAL_PRIVATE) ||
                modifiers.branchContains(TokenTypes.LITERAL_PROTECTED);
        if (!hasNonPackageAccessModifier) {
            String methodName = ast.findFirstToken(TokenTypes.IDENT).getText();
            log(ast.getLineNo(), String.format("Field %s is missing an access modifier", methodName));
        }
    }
}