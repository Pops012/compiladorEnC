public class ExprBinary extends Expression {
    final Expression left;
    final Token operator;
    final Expression right;

    ExprBinary(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    @Override
    public String toString() {
        return "ExprBinary(" + left.toString() + " " + operator.lexema + " " + right.toString() + ")";
    }

    private String indentString(String str) {
        return str.replaceAll("(?m)^", "  |");
    }
}
