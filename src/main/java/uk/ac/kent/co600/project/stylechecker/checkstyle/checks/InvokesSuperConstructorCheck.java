package uk.ac.kent.co600.project.stylechecker.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class InvokesSuperConstructorCheck extends Check {

    public static final String KEY = "missing.superCtorCall";

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.CTOR_DEF};
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
        boolean invokesSuperCtor = ast.branchContains(TokenTypes.SUPER_CTOR_CALL);
        DetailAST classDef = ast.getParent().getParent();
        DetailAST modifiers = classDef.findFirstToken(TokenTypes.MODIFIERS);
        boolean isAbstract = modifiers.branchContains(TokenTypes.ABSTRACT);
        boolean isSubClass = classDef.branchContains(TokenTypes.EXTENDS_CLAUSE);
        if (isSubClass && !isAbstract && !invokesSuperCtor) {
            log(ast.getLineNo(), KEY);
        }
    }
}
