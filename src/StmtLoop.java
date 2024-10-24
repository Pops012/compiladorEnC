public class StmtLoop extends Statement {
    final Expression condition;
    final Statement body;

    StmtLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "StmtLoop {condition: " + condition.toString() +
                ", body: " + body.toString() + "}";
    }
}
