public class ExprSuper extends Expression {
    // final Token keyword;
    final Token method;

    ExprSuper(Token method) {
        // this.keyword = keyword;
        this.method = method;
    }
    @Override
    public String toString() {
        return "ExprSuper {method: " + method.lexema + "}";
    }
}