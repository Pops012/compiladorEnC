import java.util.*;
import java.util.List;
public class AST implements Parser {
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;
    private List<Statement> statements;
    private Tabla tablaDeSimbolos;

    public AST(List<Token> tokens) {
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
        statements = new ArrayList<>();
        this.tablaDeSimbolos = new Tabla();
    }

    @Override
    public boolean parse() {
        statements = program();
        if (preanalisis.tipo == TipoToken.EOF && !hayErrores) {
            //System.out.println("Entrada correcta");
            printTree();
            return true;
        } else {
            //System.out.println("Se encontraron errores");
            return false;
        }
    }
    public List<Statement> program(){
        statements.clear();
        if(preanalisis.tipo!=TipoToken.EOF) {
            declaration(statements);
            return statements;
        }
        return null;
    }
    public List<Statement> getStatements() {
        return statements;
    }
    //****************************************************************************
    //***********************DECLARATIONS******************************************
    //****************************************************************************

    private void declaration(List<Statement> statements){
        switch (preanalisis.tipo){
            case FUN:
                Statement funDecl = funDecl();
                statements.add(funDecl);
                declaration(statements);
                break;
            case VAR:
                Statement varDecl = varDecl();
                statements.add(varDecl);
                declaration(statements);
                break;
            case TRUE, FALSE, NUMBER, STRING, NULL,  IDENTIFIER, LEFT_PAREN, EQUAL, BANG, MINUS, FOR, IF, PRINT, RETURN, WHILE, LEFT_BRACE:
                Statement statement = statement();
                statements.add(statement);
                declaration(statements);
                break;
        }
    }
    private Statement funDecl(){
        if(preanalisis.tipo==TipoToken.FUN) {
            //tablaDeSimbolos.iniciarNuevoAlcance();
            match(TipoToken.FUN);
            return function();
        }else{
            hayErrores = true;
            System.out.println("Error en la sintaxis, se esperaba una funcion");
            return null;
        }
    }
    private Statement varDecl(){
        if(preanalisis.tipo == TipoToken.VAR) {
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token nombre = previous();
            if (tablaDeSimbolos.existeIdentificador(nombre.lexema)) {
                hayErrores = true;
                System.out.println("Error: Variable ya declarada: " + nombre.lexema +" "+ i);
                return null;
            }else{
                tablaDeSimbolos.declarar(nombre.lexema, nombre.literal);
            }
            Expression inicio = varInit();
            match(TipoToken.SEMICOLON);

            return new StmtVar(nombre, inicio);
        }
        return null;
    }
    private Expression varInit(){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            return expresion();
        }
        return null;
    }
    //****************************************************************************
    //***********************STATEMENT******************************************
    //****************************************************************************

    private Statement statement(){
        switch (preanalisis.tipo){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                return exprStmt();
            case FOR:
                return forStmt();
            case IF:
                return ifStmt();
            case PRINT:
                return printStmt();
            case RETURN:
                return returnStmt();
            case WHILE:
                return whileStmt();
            case LEFT_BRACE:
                return block();
            default:
                hayErrores = true;
                System.out.println("Error de sentencia");
                return null;
        }
    }

    private Statement  exprStmt(){
        Expression exp = expresion();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(exp);
    }
    private Statement forStmt(){
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        tablaDeSimbolos.iniciarNuevoAlcance();
        Statement inicio = forStmt1();
        Expression condicion =  forStmt2();
        Expression incremento = forStmt3();
        match(TipoToken.RIGHT_PAREN);
        Statement cuerpo = statement();
        if(incremento!=null){
            cuerpo = new StmtBlock(Arrays.asList(cuerpo, new StmtExpression(incremento)));
        }
        if(condicion==null){
            condicion = new ExprLiteral(true);
        }
        cuerpo = new StmtLoop(condicion,cuerpo);
        if(inicio!=null){
            cuerpo= new StmtBlock(Arrays.asList(inicio,cuerpo));
        }
        tablaDeSimbolos.cerrarAlcanceActual();
        return cuerpo;
    }
    private Statement forStmt1(){
        switch (preanalisis.tipo){
            case VAR:
                return varDecl();
            case BANG:
            case MINUS:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
            case STRING:
            case IDENTIFIER:
            case LEFT_PAREN:
                Statement stmt = exprStmt();
                return stmt;
            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                return null;
            default:
                hayErrores = true;
                System.out.println("Error en la posicion inicial del for");
                return null;
        }
    }
    private Expression forStmt2() {
        switch (preanalisis.tipo) {
            case BANG:
            case MINUS:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
            case STRING:
            case IDENTIFIER:
            case LEFT_PAREN:
                Expression expr = expresion();
                match(TipoToken.SEMICOLON);
                return expr;
            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                return null;

            default:
                hayErrores = true;
                System.out.println("Error: "+ preanalisis.lexema+" "+i );
                return null;
        }
    }
    private Expression forStmt3(){
        switch (preanalisis.tipo) {
            case BANG:
            case MINUS:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
            case STRING:
            case IDENTIFIER:
            case LEFT_PAREN:
                return expresion();
        }
        return null;
    }
    private Statement ifStmt(){
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression condicion = expresion();
        match(TipoToken.RIGHT_PAREN);
        Statement llave = statement();
        Statement elseLlave = elseStmt();
        return new StmtIf(condicion,llave,elseLlave);
    }
    private Statement elseStmt(){
        if(preanalisis.tipo==TipoToken.ELSE) {
            match(TipoToken.ELSE);
            return statement();
        }
        return null;
    }
    private Statement printStmt(){
        match(TipoToken.PRINT);
        Expression exp = expresion();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(exp);
    }
    private Statement returnStmt(){
        if(preanalisis.tipo==TipoToken.RETURN) {
            match(TipoToken.RETURN);
            Expression exp = null;
            exp = returnExpOpc(exp);
            match(TipoToken.SEMICOLON);
            return new StmtReturn(exp);
        }else{
            hayErrores = true;
            System.out.println("Error, se esperaba un retorno");
            return null;
        }
    }
    private Expression returnExpOpc(Expression exp){
        exp = expresion();
        return exp;
    }
    private Statement whileStmt(){
        if(preanalisis.tipo == TipoToken.WHILE) {
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            Expression condicion = expresion();
            match(TipoToken.RIGHT_PAREN);
            tablaDeSimbolos.iniciarNuevoAlcance();
            Statement cuerpo = statement();
            tablaDeSimbolos.cerrarAlcanceActual();
            return new StmtLoop(condicion, cuerpo);
        }
        return null;
    }
    private Statement block(){
        if(preanalisis.tipo==TipoToken.LEFT_BRACE) {
            tablaDeSimbolos.iniciarNuevoAlcance();
            List<Statement> sentencias = new ArrayList<>();
            match(TipoToken.LEFT_BRACE);
            declaration(sentencias);
            match(TipoToken.RIGHT_BRACE);
            tablaDeSimbolos.cerrarAlcanceActual();
            return  new StmtBlock(sentencias);
        }
        return null;
    }
    //****************************************************************************
    //***********************EXPRESSIONS******************************************
    //****************************************************************************

    private Expression expresion(){
        return assigment();
    }
    private Expression assigment(){
        Expression exp = logicOr();
        exp = assigmentOpc(exp);
        return exp;
    }
    private Expression assigmentOpc(Expression expr){
        if(preanalisis.tipo == TipoToken.EQUAL){
            Token variable = previous();
            match(TipoToken.EQUAL);
            Token operador = previous();
            Expression expr1 = expresion();
            if (!tablaDeSimbolos.existeIdentificador(variable.lexema)) {
                hayErrores = true;
                System.out.println("Error: Variable no declarada: " + operador.lexema + " token previo "+ " "+previous().lexema+ " "+i);
                //return null;
            }else{
                tablaDeSimbolos.declarar(variable.lexema,expr1);
            }
            return new ExprAssign(operador, expr1);
        }
        return expr;
    }
    private Expression logicOr(){
        Expression expr1 =logicAnd();
        expr1 = logicOr2(expr1);
        return expr1;

    }
    private Expression logicOr2(Expression expr){
        if (preanalisis.tipo == TipoToken.OR) {
            match(TipoToken.OR);
            Token operador = previous();
            Expression expr2 = logicAnd();
            ExprLogical expl = new ExprLogical(expr, operador, expr2);
            return logicOr2(expl);
        }
        return expr;
    }
    private Expression logicAnd(){
        Expression expr1 = equality();
        expr1 = logicAnd2(expr1);
        return expr1;

    }
    private Expression logicAnd2(Expression expr){
        if (preanalisis.tipo == TipoToken.AND) {
            match(TipoToken.AND);
            Token operador = previous();
            Expression expr2 = equality();
            ExprLogical expl = new ExprLogical(expr, operador, expr2);
            return logicAnd2(expl);
        }
        return expr;
    }
    private Expression equality(){
        Expression expr1 =comparison();
        expr1 = equality2(expr1);
        return expr1;

    }
    private Expression equality2(Expression expr){
        switch (preanalisis.tipo){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                Token operador = previous();
                Expression expr2 = comparison();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                operador = previous();
                expr2 = comparison();
                expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
        }
        return expr;
    }
    private Expression comparison(){
        Expression expr1 =term();
        expr1 = comparison2(expr1);
        return expr1;
    }
    private Expression comparison2(Expression expr){
        switch (preanalisis.tipo){
            case GREATER:
                match(TipoToken.GREATER);
                Token operador = previous();
                Expression expr2 = term();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            case GREATER_EQUAL:
                match(TipoToken.GREATER_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            case LESS:
                match(TipoToken.LESS);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            case LESS_EQUAL:
                match(TipoToken.LESS_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
        }
        return expr;
    }
    private Expression term(){
        Expression expr1 =factor();
        expr1 = term2(expr1);
        return expr1;
    }
    private Expression term2(Expression expr){
        switch (preanalisis.tipo){
            case MINUS:
                match(TipoToken.MINUS);
                Token operador = previous();
                Expression expr2 = factor();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
            case PLUS:
                match(TipoToken.PLUS);
                operador = previous();
                expr2 = factor();
                expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
        }
        return expr;
    }

    private Expression factor(){
        Expression expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr){
        switch (preanalisis.tipo){
            case SLASH:
                match(TipoToken.SLASH);
                Token operador = previous();
                Expression expr2 = unary();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
        }
        return expr;
    }

    private Expression unary(){
        switch (preanalisis.tipo){
            case BANG:
                match(TipoToken.BANG);
                Token operador = previous();
                Expression expr = unary();
                return new ExprUnary(operador, expr);
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            default:
                return call();
        }
    }

    private Expression call(){
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr){
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            List<Expression> lstArguments = argumentsOptional();
            match(TipoToken.RIGHT_PAREN);
            return new ExprCallFunction(expr, lstArguments);
        }
        return expr;
    }

    private Expression primary(){
        switch (preanalisis.tipo){
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.literal);
            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.literal);
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                if (preanalisis.tipo == TipoToken.LEFT_PAREN&&!tablaDeSimbolos.existeIdentificador(id.lexema)) {
                    hayErrores = true;
                    System.out.println("Error: Funcion no declarada: " + id.lexema +" Token: "+i);
                }else if (!tablaDeSimbolos.existeIdentificador(id.lexema)) {
                    hayErrores = true;
                    System.out.println("Error: Variable no declarada: " + id.lexema + " "+i);
                }
                return new ExprVariable(id);
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = expresion();
                // Tiene que ser cachado aquello que retorna
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
        }
        return null;
    }
    //****************************************************************************
    //***********************OTRAS******************************************
    //****************************************************************************
    private Statement function(){
        if(preanalisis.tipo == TipoToken.IDENTIFIER) {
            match(TipoToken.IDENTIFIER);
            Token nombre = previous();
            match(TipoToken.LEFT_PAREN);
            tablaDeSimbolos.iniciarNuevoAlcance();
            List<Token> parametros = parametersOpc();
            for (Token param : parametros) {
                if (tablaDeSimbolos.existeIdentificador(param.lexema)) {
                    hayErrores = true;
                    System.out.println("Error: Parámetro duplicado: " + param.lexema);
                } else {
                    tablaDeSimbolos.declarar(param.lexema, param.literal); // Añadir cada parámetro a la tabla de símbolos
                }
            }
            match(TipoToken.RIGHT_PAREN);
            // Añade los parámetros a la tabla de símbolos aquí
            Statement cuerpo = block();
            tablaDeSimbolos.cerrarAlcanceActual();
            StmtFunction funcion = new StmtFunction(nombre, parametros, (StmtBlock) cuerpo);
            // Aquí usamos el mismo método declarar para registrar la función
            tablaDeSimbolos.declarar(nombre.lexema, funcion);
            return funcion;
        }else{
            hayErrores = true;
            System.out.println("Error, se esperaba un identificador");
            return null;
        }
    }
    private void functions(){
        if (preanalisis.tipo == TipoToken.FUN) {
            funDecl();
            functions();
        }
    }
    private List<Token> parametersOpc(){

        if (preanalisis.tipo == TipoToken.IDENTIFIER) {
            List<Token> parametros = new ArrayList<>();
            parameters(parametros);
            return parametros;
        }
        return null;
    }
    private List<Token> parameters(List<Token> parametros){
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            Token paramTok = preanalisis;
            match(TipoToken.IDENTIFIER);
            parametros.add(paramTok);
            parameters2(parametros);
        }
        else{
            hayErrores = true;
            System.out.println("Error, se esperaba un identificador");
        }
        return null;
    }
    private List<Token> parameters2(List<Token> parametros){
        if(preanalisis.tipo == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token nombre = previous();
            parametros.add(nombre);
            parameters2(parametros);
        }
        return parametros;
        // No se requiere mensaje de error aquí, podría ser el fin de la lista de parámetros
    }
    private List<Expression> argumentsOptional(){

        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS ||
                preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE ||
                preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER ||
                preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER ||
                preanalisis.tipo == TipoToken.LEFT_PAREN){
            List<Expression> arguments = new ArrayList<>();
            arguments.add(expresion());
            arguments(arguments);
            return arguments;
        }
        return null;
    }
    private List<Expression> arguments(List<Expression> argumentos){
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            argumentos.add(expresion());
            arguments(argumentos);
        }
        if(hayErrores)
            return null;
        return argumentos;
    }

    private void match(TipoToken tt) {
        if (preanalisis.tipo == tt) {
            i++;
            preanalisis = tokens.get(i);
        } else {
            hayErrores = true;
            System.out.println("Error encontrado en el token "+ preanalisis.lexema+i + " Token esperado: "+ tt + "Token encontrado: "+ preanalisis.tipo);
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }
    public void printTree() {
        for (Statement stmt : statements) {
            System.out.println(stmt.toString());
            // Imprime un salto de línea adicional después de cada Statement
            System.out.println();
        }
    }

}

