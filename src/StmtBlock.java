import java.util.List;

public class StmtBlock extends Statement {
    final List<Statement> statements;

    StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StmtBlock {");
        for (Statement stmt : statements) {
            sb.append(stmt.toString() + "\\n");
        }
        sb.append("}");
        return sb.toString();
    }
}

