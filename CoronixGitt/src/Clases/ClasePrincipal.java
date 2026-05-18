package Clases;

import java.util.*;
import Analizador.*;

public class ClasePrincipal {

    public static TablaSimbolos tabla = new TablaSimbolos();

    public static void main(String[] args) {
        new Interfaz.InterfazCoronix();
    }

    public static String procesarLinea(String linea, int ln) throws ErrorC {

        if (!linea.endsWith("!!")) {
            throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln, "Falta '!!'");
        }

        linea = linea.replace("!!", "").trim();
        String[] partes = linea.split("=>");

        if (partes.length != 2) {
            throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln, "Falta '=>'");
        }

        String[] izq = partes[0].trim().split(" ");

        if (izq.length != 2) {
            throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln, "Declaración inválida");
        }

        String tipo = izq[0];
        String nombre = izq[1];

        if (tabla.existe(nombre)) {
            throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                    "Variable duplicada: '" + nombre + "' ya fue declarada previamente. "
                    + "No se permite declarar dos o más variables con el mismo nombre.");
        }

        if (nombre.length() > 10) {
            throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                    "El nombre de variable '" + nombre + "' excede 10 caracteres "
                    + "(tiene " + nombre.length() + "). Máximo permitido: 10.");
        }

        String expr = partes[1].trim();

        switch (tipo) {

            case "cuarto":
                validarExpresionCuarto(expr, ln);
                double v = evaluarNumerica(expr, ln);

                if (v % 1 != 0) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "El tipo 'cuarto' solo acepta enteros. "
                            + "El resultado '" + v + "' contiene decimales.");
                }

                int entero = (int) v;

                if (String.valueOf(Math.abs(entero)).length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "El valor '" + entero + "' excede 10 dígitos para el tipo 'cuarto'.");
                }

                tabla.guardar(nombre, tipo, new Cuarto(entero));
                return nombre + " = " + entero;

            case "media":
                validarExpresionMedia(expr, ln);
                double d = evaluarNumerica(expr, ln);
                tabla.guardar(nombre, tipo, new Media(d));
                return nombre + " = " + d;

            case "mega":
                String s = evaluarCadena(expr, ln);

                if (s.length() > 64) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "La cadena excede 64 caracteres (tiene " + s.length() + ").");
                }

                tabla.guardar(nombre, tipo, new Mega(s));
                return nombre + " = " + s;
        }

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                "Tipo inválido: '" + tipo + "'. Los tipos válidos son: cuarto, media, mega.");
    }

    // ---------------------------------------------------------------
    //  VALIDACIÓN CUARTO
    // ---------------------------------------------------------------
    public static void validarExpresionCuarto(String expr, int ln) throws ErrorC {

        String limpia = expr.replace("@", " ")
                .replace("#", " ")
                .replace("&", " ")
                .replace("$", " ");

        for (String s : limpia.split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }

            // No se permiten decimales como literales en cuarto
            if (s.matches("\\d+\\.\\d*") || s.matches("\\.\\d+")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "El tipo 'cuarto' no acepta números decimales. "
                        + "El valor '" + s + "' tiene punto decimal. "
                        + "Usa solo números enteros.");
            }

            if (s.matches("\\d+")) {
                if (s.length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "El número '" + s + "' excede 10 dígitos en el tipo 'cuarto' "
                            + "(tiene " + s.length() + "). Máximo permitido: 10.");
                }
            }
        }
    }

    // ---------------------------------------------------------------
    //  VALIDACIÓN MEDIA
    // ---------------------------------------------------------------
    public static void validarExpresionMedia(String expr, int ln) throws ErrorC {

        String limpia = expr.replace("@", " ")
                .replace("#", " ")
                .replace("&", " ")
                .replace("$", " ");

        for (String s : limpia.split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }

            // Entero puro — no permitido en media
            if (s.matches("\\d+")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "El tipo 'media' no acepta enteros puros. "
                        + "El valor '" + s + "' no tiene punto decimal. "
                        + "Escríbelo como: " + s + ".0");
            }

            // Tiene punto pero sin dígitos después (ej: 3.)
            if (s.matches("\\d+\\.")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '" + s + "'. "
                        + "Hay un punto decimal pero no hay dígitos después de él. "
                        + "Ejemplo correcto: " + s + "0");
            }

            // Solo un punto
            if (s.equals(".")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '.' no es un número válido. "
                        + "Escribe un decimal completo, ejemplo: 3.14");
            }

            // Decimal válido con dígitos antes y después del punto
            if (s.matches("\\d+\\.\\d+")) {
                String[] p = s.split("\\.");
                String antes = p[0];
                String despues = p[1];

                if (antes.length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "La parte entera de '" + s + "' excede 10 dígitos "
                            + "(tiene " + antes.length() + "). Máximo permitido: 10.");
                }

                if (despues.length() > 8) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "La parte decimal de '" + s + "' excede 8 dígitos "
                            + "(tiene " + despues.length() + "). Máximo permitido: 8.");
                }

            } else if (!s.matches("[a-zA-Z]+")) {
                // No es identificador ni decimal válido
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '" + s + "'. "
                        + "Se esperaba un número decimal con dígitos antes y después "
                        + "del punto, ejemplo: 3.14");
            }
        }
    }

    // ---------------------------------------------------------------
    //  EVALUACIÓN NUMÉRICA
    // ---------------------------------------------------------------
    public static double evaluarNumerica(String expr, int ln) throws ErrorC {

        expr = expr.replace("@", " @ ")
                .replace("#", " # ")
                .replace("&", " & ")
                .replace("$", " $ ");

        String[] p = expr.trim().split("\\s+");
        double res = obtenerNumero(p[0], ln);

        for (int i = 1; i < p.length; i += 2) {

            if (i + 1 >= p.length) {
                throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln,
                        "Falta operando después del operador '" + p[i] + "'.");
            }

            double val = obtenerNumero(p[i + 1], ln);

            switch (p[i]) {
                case "@":
                    res += val;
                    break;
                case "#":
                    res -= val;
                    break;
                case "&":
                    res *= val;
                    break;
                case "$":
                    if (val == 0) {
                        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                                "División entre cero: el divisor no puede ser 0.");
                    }
                    res /= val;
                    break;
                default:
                    throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln,
                            "Operador desconocido: '" + p[i] + "'. "
                            + "Operadores válidos: @ + , # - , & * , $ /");
            }
        }

        return res;
    }

    public static double obtenerNumero(String t, int ln) throws ErrorC {

        if (t.matches("\\d+")) {
            return Integer.parseInt(t);
        }

        if (t.matches("\\d+\\.\\d+")) {
            return Double.parseDouble(t);
        }

        // Es un identificador → buscar en tabla
        if (t.matches("[a-zA-Z]+")) {
            TablaSimbolos.Entrada sim = tabla.obtener(t, ln);

            if (sim.valor instanceof Cuarto) {
                return ((Cuarto) sim.valor).getValor();
            }
            if (sim.valor instanceof Media) {
                return ((Media) sim.valor).getValor();
            }

            throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                    "La variable '" + t + "' no es numérica. "
                    + "Solo se pueden usar variables de tipo 'cuarto' o 'media' en expresiones numéricas.");
        }

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                "Token inesperado en expresión numérica: '" + t + "'.");
    }

    // ---------------------------------------------------------------
    //  EVALUACIÓN DE CADENAS
    // ---------------------------------------------------------------
    public static String evaluarCadena(String expr, int ln) throws ErrorC {

        String[] p = expr.split("@");
        String res = obtenerCadena(p[0].trim(), ln);

        for (int i = 1; i < p.length; i++) {
            res += obtenerCadena(p[i].trim(), ln);
        }

        return res;
    }

    public static String obtenerCadena(String t, int ln) throws ErrorC {

        if (t.startsWith("\"") && t.endsWith("\"")) {
            return t.substring(1, t.length() - 1);
        }

        TablaSimbolos.Entrada sim = tabla.obtener(t, ln);

        if (sim.valor instanceof Mega) {
            return ((Mega) sim.valor).getValor();
        }

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                "La variable '" + t + "' no es de tipo 'mega'. "
                + "Solo se pueden concatenar cadenas con '@'.");
    }

    // ---------------------------------------------------------------
    //  TOKENS
    // ---------------------------------------------------------------
    public static List<Token> obtenerTokens(String linea) {

        List<Token> lista = new ArrayList<>();
        List<String> partes = new ArrayList<>();

        // 1. Separar !! y => con espacios
        linea = linea.replace("!!", " !! ")
                .replace("=>", " => ");

        // 2. Expandir operadores @, #, &, $ respetando comillas
        StringBuilder expandida = new StringBuilder();
        boolean enComillas = false;

        for (char c : linea.toCharArray()) {
            if (c == '"') {
                enComillas = !enComillas;
                expandida.append(c);
            } else if (!enComillas && (c == '@' || c == '#' || c == '&' || c == '$')) {
                expandida.append(' ').append(c).append(' ');
            } else {
                expandida.append(c);
            }
        }

        linea = expandida.toString();

        // 3. Dividir en tokens respetando cadenas entre comillas
        enComillas = false;
        StringBuilder actual = new StringBuilder();

        for (char c : linea.toCharArray()) {
            if (c == '"') {
                enComillas = !enComillas;
                actual.append(c);
            } else if (c == ' ' && !enComillas) {
                if (actual.length() > 0) {
                    partes.add(actual.toString());
                    actual = new StringBuilder();
                }
            } else {
                actual.append(c);
            }
        }
        if (actual.length() > 0) {
            partes.add(actual.toString());
        }

        // 4. Clasificar cada parte
        for (String s : partes) {
            if (s.isEmpty()) {
                continue;
            }

            if (s.equals("cuarto") || s.equals("media") || s.equals("mega")) {
                lista.add(new Token("PR", s, "(" + s + ")", "SI"));

            } else if (s.matches("[a-zA-Z]+")) {
                lista.add(new Token("ID", s, "[a-zA-Z]+", "NO"));

            } else if (s.equals("=>")) {
                lista.add(new Token("ASIG", s, "(=>)", "SI"));

            } else if (s.equals("@")) {
                lista.add(new Token("OP_SUMA", s, "(@)", "SI"));

            } else if (s.equals("#")) {
                lista.add(new Token("OP_RESTA", s, "(#)", "SI"));

            } else if (s.equals("&")) {
                lista.add(new Token("OP_MULT", s, "(&)", "SI"));

            } else if (s.equals("$")) {
                lista.add(new Token("OP_DIV", s, "($)", "SI"));

            } else if (s.matches("\\d+")) {
                lista.add(new Token("NUM_ENT", "\\d[0-9]", "(\\d[0-9])", "NO"));

            } else if (s.matches("\\d+\\.\\d+")) {
                lista.add(new Token("NUM_DEC", "\\d[0-9].\\d[0-9]", "(\\d[0-9].\\d[0-9])", "NO"));

            } else if (s.startsWith("\"") && s.endsWith("\"")) {
                lista.add(new Token("CAD", s, "\".*\"", "NO"));

            } else if (s.equals("!!")) {
                lista.add(new Token("BREAK", s, "(!!)", "SI"));

            } else {
                lista.add(new Token("ERROR", s, "?", "NO"));
            }
        }

        return lista;
    }

    // ---------------------------------------------------------------
    //  CLASIFICACIÓN
    // ---------------------------------------------------------------
    public static List<String[]> clasificacion(String linea) {

        List<String[]> lista = new ArrayList<>();

        // Expandir operadores pegados para que se clasifiquen bien
        StringBuilder expandida = new StringBuilder();
        boolean enComillas = false;

        for (char c : linea.toCharArray()) {
            if (c == '"') {
                enComillas = !enComillas;
                expandida.append(c);
            } else if (!enComillas && (c == '@' || c == '#' || c == '&' || c == '$')) {
                expandida.append(' ').append(c).append(' ');
            } else {
                expandida.append(c);
            }
        }

        for (String s : expandida.toString().split("\\s+")) {
            if (s.isEmpty()) {
                continue;
            }

            if (s.equals("cuarto") || s.equals("media") || s.equals("mega")) {
                lista.add(new String[]{s, "palabra reservada"});

            } else if (s.matches("[a-zA-Z]+")) {
                lista.add(new String[]{s, "identificador"});

            } else if (s.matches("[@#&$]") || s.equals("=>") || s.equals("!!")) {
                lista.add(new String[]{s, "palabra reservada"});

            } else if (s.matches("\\d+") || s.matches("\\d+\\.\\d+")) {
                lista.add(new String[]{s, "constante"});

            } else if (s.startsWith("\"") && s.endsWith("\"")) {
                lista.add(new String[]{s, "cadena"});
            }
        }

        return lista;
    }
}
