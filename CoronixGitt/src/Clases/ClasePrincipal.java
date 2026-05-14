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
            throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "Variable excede 10 caracteres");
        }

        String expr = partes[1].trim();

        switch (tipo) {

            /*
             * PALABRA RESERVADA: cuarto
             * Equivalente a: int (entero)
             * Descripción: Almacena números enteros sin decimales.
             * Restricciones: Máximo 10 dígitos. No acepta punto decimal.
             * Ejemplo de uso: cuarto edad => 25!!
             */
            case "cuarto":
                validarExpresionCuarto(expr, ln);
                double v = evaluarNumerica(expr, ln);

                if (v % 1 != 0) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "cuarto no acepta decimales");
                }

                int entero = (int) v;

                if (String.valueOf(Math.abs(entero)).length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "cuarto excede 10 dígitos");
                }

                tabla.guardar(nombre, tipo, new Cuarto(entero));
                return nombre + " = " + entero;

            /*
             * PALABRA RESERVADA: media
             * Equivalente a: double (decimal / punto flotante)
             * Descripción: Almacena números con punto decimal.
             * Restricciones: Parte entera máx. 10 dígitos, parte decimal máx. 8 dígitos.
             *                NO acepta enteros puros; debe incluir punto decimal (ej: 3.0).
             * Ejemplo de uso: media precio => 19.99!!
             */
            case "media":
                validarExpresionMedia(expr, ln);
                double d = evaluarNumerica(expr, ln);
                tabla.guardar(nombre, tipo, new Media(d));
                return nombre + " = " + d;

            /*
             * PALABRA RESERVADA: mega
             * Equivalente a: String (cadena de texto)
             * Descripción: Almacena texto entre comillas dobles.
             * Restricciones: Máximo 64 caracteres.
             * Ejemplo de uso: mega saludo => "Hola mundo"!!
             */
            case "mega":
                String s = evaluarCadena(expr, ln);

                if (s.length() > 64) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "Cadena excede 64 caracteres");
                }

                tabla.guardar(nombre, tipo, new Mega(s));
                return nombre + " = " + s;
        }

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "Tipo inválido");
    }

    public static void validarExpresionCuarto(String expr, int ln) throws ErrorC {

        expr = expr.replace("@", " ")
                .replace("#", " ")
                .replace("&", " ")
                .replace("$", " ");

        for (String s : expr.split("\\s+")) {
            if (s.matches("\\d+")) {
                if (s.length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "Número excede 10 dígitos en 'cuarto'");
                }
            }
        }
    }

    public static void validarExpresionMedia(String expr, int ln) throws ErrorC {

        expr = expr.replace("@", " ")
                .replace("#", " ")
                .replace("&", " ")
                .replace("$", " ");

        for (String s : expr.split("\\s+")) {
            if (s.isEmpty()) continue;

            if (s.matches("\\d+")) {
                // Número entero puro → inválido para 'media'
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "El tipo 'media' no acepta enteros puros (" + s + "). "
                        + "Debe escribirse con punto decimal, ejemplo: " + s + ".0");
            }

            if (s.matches("\\d+\\.\\d*") || s.matches("\\.\\d+")) {
                String[] p = s.split("\\.");
                String antes = p.length > 0 ? p[0] : "";
                String despues = p.length > 1 ? p[1] : "";

                if (antes.length() > 10) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "La parte entera de '" + s + "' excede 10 dígitos (tiene "
                            + antes.length() + ")");
                }

                if (despues.length() > 8) {
                    throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                            "La parte decimal de '" + s + "' excede 8 dígitos (tiene "
                            + despues.length() + "). Máximo permitido: 8");
                }
            }
        }
    }

    public static double evaluarNumerica(String expr, int ln) throws ErrorC {

        expr = expr.replace("@", " @ ")
                .replace("#", " # ")
                .replace("&", " & ")
                .replace("$", " $ ");

        String[] p = expr.trim().split("\\s+");
        double res = obtenerNumero(p[0], ln);

        for (int i = 1; i < p.length; i += 2) {

            if (i + 1 >= p.length) {
                throw new ErrorC(ErrorC.Tipo.SINTACTICO, ln, "Falta operando");
            }

            double val = obtenerNumero(p[i + 1], ln);

            switch (p[i]) {
                case "@": res += val; break;
                case "#": res -= val; break;
                case "&": res *= val; break;
                case "$":
                    if (val == 0) {
                        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "División entre cero");
                    }
                    res /= val;
                    break;
            }
        }

        return res;
    }

    public static double obtenerNumero(String t, int ln) throws ErrorC {

        if (t.matches("\\d+")) return Integer.parseInt(t);

        if (t.matches("\\d+\\.\\d*") || t.matches("\\.\\d+")) return Double.parseDouble(t);

        TablaSimbolos.Entrada sim = tabla.obtener(t, ln);

        if (sim.valor instanceof Cuarto) return ((Cuarto) sim.valor).getValor();
        if (sim.valor instanceof Media)  return ((Media)  sim.valor).getValor();

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "No es número: " + t);
    }

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

        if (sim.valor instanceof Mega) return ((Mega) sim.valor).getValor();

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "No es cadena");
    }

    public static List<Token> obtenerTokens(String linea) {

        List<Token> lista = new ArrayList<>();

        // Proteger cadenas con espacios antes de tokenizar
        List<String> partes = new ArrayList<>();
        boolean dentroComillas = false;
        StringBuilder actual = new StringBuilder();

        // Primero separamos los símbolos especiales pegados al texto
        linea = linea.replace("!!", " !! ")
                .replace("=>", " => ");

        for (char c : linea.toCharArray()) {
            if (c == '"') {
                dentroComillas = !dentroComillas;
                actual.append(c);
            } else if (c == ' ' && !dentroComillas) {
                if (actual.length() > 0) {
                    partes.add(actual.toString());
                    actual = new StringBuilder();
                }
            } else {
                actual.append(c);
            }
        }
        if (actual.length() > 0) partes.add(actual.toString());

        for (String s : partes) {
            if (s.isEmpty()) continue;

            /*
             * Tokens de palabras reservadas:
             * "cuarto" → equivale a int
             * "media"  → equivale a double
             * "mega"   → equivale a String
             */
            if (s.equals("cuarto") || s.equals("media") || s.equals("mega")) {
                lista.add(new Token("PR", s, "(" + s + ")", "SI"));

            } else if (s.matches("[a-zA-Z]+")) {
                // Identificador: nombre de variable
                lista.add(new Token("ID", s, "[a-zA-Z]+", "NO"));

            } else if (s.equals("=>")) {
                // Operador de asignación del lenguaje
                lista.add(new Token("ASIG", s, "(=>)", "SI"));

            } else if (s.matches("[@#&$]")) {
                // Operadores aritméticos: @ suma, # resta, & multiplicación, $ división
                lista.add(new Token("OP", s, "[@#&$]", "NO"));

            } else if (s.matches("\\d+") || s.matches("\\d+\\.\\d*") || s.matches("\\.\\d+")) {
                // Constante numérica: entero o decimal
                lista.add(new Token("NUM", s, "(num)", "NO"));

            } else if (s.startsWith("\"") && s.endsWith("\"")) {
                // Cadena de texto entre comillas dobles
                lista.add(new Token("CAD", s, "\".*\"", "NO"));

            } else if (s.equals("!!")) {
                // Fin de sentencia (equivale a ; en Java)
                lista.add(new Token("BREAK", s, "(!!)", "SI"));

            } else {
                lista.add(new Token("ERROR", s, "?", "NO"));
            }
        }

        return lista;
    }

    public static List<String[]> clasificacion(String linea) {

        List<String[]> lista = new ArrayList<>();

        for (String s : linea.split("\\s+")) {

            /*
             * Palabras reservadas del lenguaje Coronix:
             * cuarto → int | media → double | mega → String
             */
            if (s.equals("cuarto") || s.equals("media") || s.equals("mega")) {
                lista.add(new String[]{s, "palabra reservada"});

            } else if (s.matches("[a-zA-Z]+")) {
                lista.add(new String[]{s, "identificador"});

            } else if (s.matches("[@#&$]") || s.equals("=>")) {
                lista.add(new String[]{s, "signo"});

            } else if (s.matches("\\d+") || s.matches("\\d+\\.\\d*") || s.matches("\\.\\d+")) {
                lista.add(new String[]{s, "constante"});

            } else if (s.equals("!!")) {
                lista.add(new String[]{s, "break"});
            }
        }

        return lista;
    }
}