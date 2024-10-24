import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Tabla {
    private final Stack<Map<String, Object>> scopes = new Stack<>();
    public Tabla() {
        // Iniciar con un alcance global
        scopes.push(new HashMap<>());
    }
    void iniciarNuevoAlcance() {
        scopes.push(new HashMap<>());
    }

    void cerrarAlcanceActual() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    boolean existeIdentificador(String identificador) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identificador)) {
                return true;
            }
        }
        return false;
    }

    Object obtener(String identificador) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(identificador)) {
                return scopes.get(i).get(identificador);
            }
        }
        throw new RuntimeException("Variable no definida '" + identificador + "'.");
    }

    void asignar(String identificador, Object valor) {
        if (!scopes.peek().containsKey(identificador)) {
            throw new RuntimeException("Variable no declarada: '" + identificador + "'.");
        }
        scopes.peek().put(identificador, valor);
    }

    void declarar(String identificador, Object valor) {
        scopes.peek().put(identificador, valor);
    }
}