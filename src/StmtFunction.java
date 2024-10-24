import java.util.List;

public class StmtFunction extends Statement {
    final Token name;
    final List<Token> params;
    final StmtBlock body;

    StmtFunction(Token name, List<Token> params, StmtBlock body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StmtFunction {name: " + name.lexema);
        sb.append(", params: [");
        for (Token param : params) {
            sb.append(param.lexema + ", ");
        }
        sb.append("], body: " + body.toString());
        sb.append("}");
        return sb.toString();
    }
}
