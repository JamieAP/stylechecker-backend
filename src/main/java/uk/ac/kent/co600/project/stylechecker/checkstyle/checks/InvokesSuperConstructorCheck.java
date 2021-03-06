package uk.ac.kent.co600.project.stylechecker.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * A check that checks any constructor of a subclass either calls into another constructor
 * of the same class, or calls the super constructor of its superclass.
 *
 * If none of a class's constructors call into a superclass constructor, it will log an error.
 */
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
        boolean invokesOtherCtor = ast.branchContains(TokenTypes.CTOR_CALL);
        DetailAST classDef = ast.getParent().getParent();
        DetailAST modifiers = classDef.findFirstToken(TokenTypes.MODIFIERS);
        boolean isAbstract = modifiers.branchContains(TokenTypes.ABSTRACT);
        boolean isSubClass = classDef.branchContains(TokenTypes.EXTENDS_CLAUSE);
        if (isSubClass && !isAbstract && !invokesSuperCtor && !invokesOtherCtor) {
            String className = classDef.findFirstToken(TokenTypes.IDENT).getText();
            log(ast.getLineNo(), String.format("Class %s does not invoke superclass constructor", className));
        }
    }
}