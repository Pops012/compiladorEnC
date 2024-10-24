public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    public String toString() {
        return "StmtIf {condition: " + condition.toString() +
                ", thenBranch: " + thenBranch.toString() +
                ", elseBranch: " + (elseBranch != null ? elseBranch.toString() : "null") + "}";
    }
}
