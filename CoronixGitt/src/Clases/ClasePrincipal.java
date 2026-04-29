package Clases;

import java.util.*;
import Analizador.*;

public class ClasePrincipal {

    public static TablaSimbolos tabla = new TablaSimbolos();

    public static void main(String[] args) {
        new Interfaz.InterfazCoronix();
    }

    // ================= PROCESAR =================
    public static String procesarLinea(String linea) {

        linea = linea.trim().replace("!!", "");

        String[] partes = linea.split("=>");

        if (partes.length != 2)
            throw new RuntimeException("Sintaxis inválida");

        String izquierda = partes[0].trim();
        String expr = partes[1].trim();

        String[] datos = izquierda.split(" ");

        if (datos.length != 2)
            throw new RuntimeException("Declaración incorrecta");

        String tipo = datos[0];
        String nombre = datos[1];

        if (nombre.length() > 10)
            throw new RuntimeException("Variable > 10 caracteres");

        switch (tipo) {

            case "cuarto":
                int r1 = (int) evaluarNumerica(expr);
                tabla.guardar(nombre, "cuarto", new Cuarto(r1));
                return nombre + " = " + r1;

            case "media":
                validarMedia(expr);
                double r2 = evaluarNumerica(expr);
                tabla.guardar(nombre, "media", new Media(r2));
                return nombre + " = " + r2;

            case "mega":
                String r3 = evaluarCadena(expr);
                if (r3.length() > 64)
                    throw new RuntimeException("Cadena > 64 caracteres");
                tabla.guardar(nombre, "mega", new Mega(r3));
                return nombre + " = " + r3;
        }

        throw new RuntimeException("Tipo inválido");
    }

    // ================= VALIDAR MEDIA =================
    public static void validarMedia(String expr) {

        if (expr.matches("\\d+\\.\\d+")) {

            String[] p = expr.split("\\.");

            if (p[0].length() > 10 || p[1].length() > 10)
                throw new RuntimeException("Media excede 10 enteros o 10 decimales");
        }
    }

    // ================= NUMÉRICO =================
    public static double evaluarNumerica(String expr) {

        expr = expr.replace("@", " @ ")
                   .replace("#", " # ")
                   .replace("&", " & ")
                   .replace("$", " $ ");

        String[] p = expr.split("\\s+");

        double res = obtenerNumero(p[0]);

        for (int i = 1; i < p.length; i += 2) {

            double val = obtenerNumero(p[i + 1]);

            switch (p[i]) {
                case "@": res += val; break;
                case "#": res -= val; break;
                case "&": res *= val; break;
                case "$":
                    if (val == 0) throw new RuntimeException("División entre cero");
                    res /= val;
                    break;
            }
        }

        return res;
    }

    public static double obtenerNumero(String t) {

        if (t.matches("\\d+")) return Integer.parseInt(t);
        if (t.matches("\\d+\\.\\d+")) return Double.parseDouble(t);

        var sim = tabla.obtener(t);

        if (sim.valor instanceof Cuarto)
            return ((Cuarto) sim.valor).getValor();

        if (sim.valor instanceof Media)
            return ((Media) sim.valor).getValor();

        throw new RuntimeException("Tipo no numérico: " + t);
    }

    // ================= CADENA =================
    public static String evaluarCadena(String expr) {

        List<String> partes = tokenizar(expr.replace("@", " @ "));

        String res = obtenerCadena(partes.get(0));

        for (int i = 1; i < partes.size(); i += 2) {
            res += obtenerCadena(partes.get(i + 1));
        }

        return res;
    }

    public static String obtenerCadena(String t) {

        if (t.startsWith("\"") && t.endsWith("\""))
            return t.substring(1, t.length() - 1);

        var sim = tabla.obtener(t);

        if (sim.valor instanceof Mega)
            return ((Mega) sim.valor).getValor();

        throw new RuntimeException("Tipo no cadena: " + t);
    }

    // ================= TOKENIZADOR =================
    public static List<String> tokenizar(String linea) {

        List<String> tokens = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enCadena = false;

        for (char c : linea.toCharArray()) {

            if (c == '"') {
                enCadena = !enCadena;
                actual.append(c);
            }
            else if (c == ' ' && !enCadena) {
                if (actual.length() > 0) {
                    tokens.add(actual.toString());
                    actual.setLength(0);
                }
            }
            else {
                actual.append(c);
            }
        }

        if (actual.length() > 0)
            tokens.add(actual.toString());

        return tokens;
    }

    // ================= TOKENS =================
    public static List<Token> obtenerTokens(String linea, int ln) {

        List<Token> lista = new ArrayList<>();

        linea = linea.replace("!!", " !! ")
                     .replace("=>", " => ")
                     .replace("@", " @ ")
                     .replace("#", " # ")
                     .replace("&", " & ")
                     .replace("$", " $ ");

        List<String> partes = tokenizar(linea);

        for (String s : partes) {

            String tipo;

            if (s.equals("cuarto")) tipo = "PR_CUARTO";
            else if (s.equals("media")) tipo = "PR_MEDIA";
            else if (s.equals("mega")) tipo = "PR_MEGA";
            else if (s.equals("=>")) tipo = "ASIGNACION";
            else if (s.equals("!!")) tipo = "FIN";
            else if (s.equals("@")) tipo = "SUMA";
            else if (s.equals("#")) tipo = "RESTA";
            else if (s.equals("&")) tipo = "MULT";
            else if (s.equals("$")) tipo = "DIV";
            else if (s.matches("\\d+")) tipo = "NUM";
            else if (s.matches("\\d+\\.\\d+")) tipo = "DECIMAL";
            else if (s.startsWith("\"")) tipo = "CADENA";
            else if (s.matches("[a-zA-Z]+")) tipo = "ID";
            else tipo = "ERROR";

            lista.add(new Token(s, tipo, ln, s));
        }

        return lista;
    }

    // ================= TABLA =================
    public static List<String[]> tablaUI() {

        List<String[]> lista = new ArrayList<>();

        for (var entry : tabla.getTabla().entrySet()) {

            var s = entry.getValue();

            lista.add(new String[]{
                s.nombre,
                s.tipo,
                s.valor.toString()
            });
        }

        return lista;
    }
}