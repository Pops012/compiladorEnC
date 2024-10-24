public class StmtExpression extends Statement {
    final Expression expression;

    StmtExpression(Expression expression) {
        this.expression = expression;
    }

    public String toString() {
        return "StmtExpression(" + expression.toString()+" )";
    }
}
