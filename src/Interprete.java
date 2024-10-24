import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interprete {

    static boolean existenErrores = false;
    public static void main(String[] args) throws IOException, Exception {
        if(args.length > 1) {
            System.out.println("Uso correcto: interprete [archivo.txt]");
            // Convención defininida en el archivo "system.h" de UNIX
            System.exit(64);
        } else if(args.length == 1){
            ejecutarArchivo(args[0]);
        } else{
            ejecutarPrompt();
        }
    }
        /*Cadena automata = new Cadena();
        String[] testCases = {"\"cadena\"", "\"otra cadena\"", "cadena", "\"cadenas no cerradas"};
        for (String testCase : testCases) {
            boolean resultado = automata.reconocer(testCase);
            System.out.println("La cadena " + testCase + " es válida: " + resultado);
        }*/


    private static void ejecutarArchivo(String path) throws IOException, Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));
        // Se indica que existe un error
        if(existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws IOException, Exception {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for(;;){
            System.out.print(">>>");
            String linea = reader.readLine();
            if(linea == null) break; // Presionar Ctrl + D
            ejecutar(linea);
            existenErrores = false;
        }
    }

    /*private static void ejecutar(String source) {
        try{
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scan();
            Parser parser = new AST(tokens);
            parser.parse();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    private static void ejecutar(String source) throws Exception {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scan();
        AST parser = new AST(tokens);

        // Ejecuta el análisis sintáctico.
        if (parser.parse()) { // Si el análisis fue exitoso
            // Obtiene las declaraciones del programa.
            List<Statement> statements = parser.getStatements(); // Asume que este método existe en ASDR.
            // Imprime cada Statement en la lista.
            for (Statement statement : statements) {
                //System.out.println(statement); // Aquí asumimos que cada Statement tiene un toString bien definido.
            }
        } else {
            // Si el análisis sintáctico falla, puedes manejar los errores como prefieras.
            System.out.println("Se encontraron errores durante el análisis.");
        }
    }
    static void error(int linea, String mensaje){
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }
}
