package Clases;

import java.util.*;

public class ClasePrincipal {

    public static Map<String, Object> variables = new HashMap<>();
    public static Map<String, String> tipos = new HashMap<>();

    // 🔥 MAIN
    public static void main(String[] args) {
        new Interfaz.InterfazCoronix();
    }

    // 🔹 PROCESAR LÍNEA
    public static String procesarLinea(String linea) {

        linea = linea.trim().replace("!!", "");

        if (linea.startsWith("cuarto") ||
            linea.startsWith("media") ||
            linea.startsWith("mega")) {

            String[] partes = linea.split("=>");

            String tipo = partes[0].trim().split(" ")[0];
            String nombre = partes[0].trim().split(" ")[1];
            String expr = partes[1].trim();

            switch (tipo) {

                case "cuarto":
                    int resInt = (int) evaluarNumerica(expr);
                    variables.put(nombre, resInt);
                    tipos.put(nombre, "cuarto");
                    return nombre + " = " + resInt;

                case "media":
                    double resDouble = evaluarNumerica(expr);
                    variables.put(nombre, resDouble);
                    tipos.put(nombre, "media");
                    return nombre + " = " + resDouble;

                case "mega":
                    String resStr = evaluarCadena(expr);
                    variables.put(nombre, resStr);
                    tipos.put(nombre, "mega");
                    return nombre + " = " + resStr;
            }
        }

        return "Línea no reconocida";
    }

    // 🔥 EXPRESIONES NUMÉRICAS
    public static double evaluarNumerica(String expr) {

        expr = expr.replace("@", " @ ")
                   .replace("#", " # ")
                   .replace("&", " & ")
                   .replace("$", " $ ");

        String[] partes = expr.split("\\s+");

        double resultado = obtenerNumero(partes[0]);

        for (int i = 1; i < partes.length; i += 2) {

            String op = partes[i];
            double val = obtenerNumero(partes[i + 1]);

            switch (op) {
                case "@": resultado += val; break;
                case "#": resultado -= val; break;
                case "&": resultado *= val; break;
                case "$":
                    if (val == 0) throw new RuntimeException("División entre cero");
                    resultado /= val;
                    break;
            }
        }

        return resultado;
    }

    // 🔥 CADENA
    public static String evaluarCadena(String expr) {

        expr = expr.replace("@", " @ ");
        String[] partes = expr.split("\\s+");

        String res = obtenerCadena(partes[0]);

        for (int i = 1; i < partes.length; i += 2) {
            res += obtenerCadena(partes[i + 1]);
        }

        return res;
    }

    // 🔹 NUMÉRICO
    public static double obtenerNumero(String t) {

        if (t.matches("\\d+")) return Integer.parseInt(t);
        if (t.matches("\\d+\\.\\d+")) return Double.parseDouble(t);

        if (!variables.containsKey(t))
            throw new RuntimeException("Variable no definida: " + t);

        return Double.parseDouble(variables.get(t).toString());
    }

    // 🔹 CADENA
    public static String obtenerCadena(String t) {

        if (t.startsWith("\"") && t.endsWith("\""))
            return t.substring(1, t.length() - 1);

        if (!variables.containsKey(t))
            throw new RuntimeException("Variable no definida: " + t);

        return variables.get(t).toString();
    }

    // 🔥 TOKENS
    public static List<String[]> obtenerTokens(String linea, int ln) {

        List<String[]> tokens = new ArrayList<>();

        linea = linea.replace("!!", " !! ")
                     .replace("=>", " => ")
                     .replace("@", " @ ")
                     .replace("#", " # ")
                     .replace("&", " & ")
                     .replace("$", " $ ");

        for (String p : linea.split("\\s+")) {

            String tipo;

            if (p.equals("cuarto")) tipo = "PR_CUARTO";
            else if (p.equals("media")) tipo = "PR_MEDIA";
            else if (p.equals("mega")) tipo = "PR_MEGA";
            else if (p.equals("=>")) tipo = "ASIGNACION";
            else if (p.equals("!!")) tipo = "FIN";
            else if (p.equals("@")) tipo = "SUMA";
            else if (p.equals("#")) tipo = "RESTA";
            else if (p.equals("&")) tipo = "MULT";
            else if (p.equals("$")) tipo = "DIV";
            else if (p.matches("\\d+")) tipo = "NUM";
            else if (p.matches("\\d+\\.\\d+")) tipo = "DECIMAL";
            else if (p.startsWith("\"")) tipo = "CADENA";
            else if (p.matches("[a-zA-Z]+")) tipo = "ID";
            else tipo = "ERROR";

            tokens.add(new String[]{p, tipo, String.valueOf(ln)});
        }

        return tokens;
    }

    // 🔥 TABLA DE SÍMBOLOS
    public static List<String[]> obtenerTabla() {

        List<String[]> tabla = new ArrayList<>();

        for (String var : variables.keySet()) {
            tabla.add(new String[]{
                var,
                tipos.get(var),
                variables.get(var).toString()
            });
        }

        return tabla;
    }
}