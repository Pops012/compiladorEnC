public class StmtVar extends Statement {
    final Token name;
    final Expression initializer;

    StmtVar(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }
    @Override
    public String toString() {
        return "StmtVar {name: " + name.lexema +
                ", initializer: " + (initializer != null ? initializer.toString() : "null") + "}";
    }
}
