package uk.ac.kent.co600.project.stylechecker.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class FieldAssignedToInConstructorCheck extends Check {

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
        DetailAST parent = ast.getParent().getParent();
        if (parent.getType() != TokenTypes.CLASS_DEF) {
            /* not a class field */
            return;
        }
        DetailAST ctor = parent.findFirstToken(TokenTypes.OBJBLOCK).findFirstToken(TokenTypes.CTOR_DEF);
        if (ctor == null) {
            /* no ctor */
            return;
        }
        while (ctor != null) {
            DetailAST varIdent = ctor.findFirstToken(TokenTypes.IDENT);
            while (varIdent != null) {
                if (varIdent.getText().equals(ast.getText()) && varIdent.getFirstChild().getType() == TokenTypes.ASSIGN) {
                    return;
                }
                varIdent = varIdent.getNextSibling();
            }
            DetailAST nextCtor = ctor.getNextSibling();
            while (nextCtor != null && nextCtor.getType() != TokenTypes.CTOR_DEF) {
                nextCtor = nextCtor.getNextSibling();
            }
            ctor = nextCtor;
        }
        log(ast.getLineNo(), "missing.fieldAssignment");
    }
}