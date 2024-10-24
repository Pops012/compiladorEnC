import java.util.List;

public class ExprCallFunction extends Expression {
    final Expression callee;
    final List<Expression> arguments;

    ExprCallFunction(Expression callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExprCallFunction (");
        sb.append("callee=" + callee.toString());
        sb.append(", arguments=[");
        for (Expression arg : arguments) {
            sb.append(arg.toString() + ", ");
        }
        sb.append("])");
        return sb.toString();
    }
}