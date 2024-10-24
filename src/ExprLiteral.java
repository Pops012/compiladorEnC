public class ExprLiteral extends Expression {
    final Object value;

    ExprLiteral(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ExprLiteral {value: " + (value == null ? "null" : value.toString()) + "}";
    }


}
