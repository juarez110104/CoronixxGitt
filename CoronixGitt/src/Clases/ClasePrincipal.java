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

            case "media":
                validarExpresionMedia(expr, ln);
                double d = evaluarNumerica(expr, ln);
                tabla.guardar(nombre, tipo, new Media(d));
                return nombre + " = " + d;

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
            if (s.isEmpty()) {
                continue;
            }

            // ❌ Número entero puro — no tiene punto decimal
            if (s.matches("\\d+")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "El tipo 'media' no acepta enteros puros ('" + s + "'). "
                        + "Debe escribirse con punto decimal y dígitos después, "
                        + "ejemplo: " + s + ".0");
            }

            // ❌ Tiene punto pero sin ningún dígito después (ej: 3.)
            if (s.matches("\\d+\\.")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '" + s + "' tiene punto decimal "
                        + "pero no hay ningún dígito después de él. "
                        + "Debes escribir al menos un dígito tras el punto, "
                        + "ejemplo: " + s + "0");
            }

            // ❌ Solo un punto sin nada (ej: .)
            if (s.equals(".")) {
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '.' no es un número válido. "
                        + "Escribe un número decimal completo, ejemplo: 3.14");
            }

            // ✅ Decimal válido: dígitos, punto, dígitos
            if (s.matches("\\d+\\.\\d+")) {
                String[] p = s.split("\\.");
                String antes = p[0];
                String despues = p[1];

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

            } else if (!s.matches("[a-zA-Z]+")) {
                // ❌ Cualquier otro formato no reconocido como decimal ni como identificador
                throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln,
                        "Valor inválido para 'media': '" + s + "'. "
                        + "Se esperaba un número decimal con dígitos antes y después "
                        + "del punto, ejemplo: 3.14");
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
                        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "División entre cero");
                    }
                    res /= val;
                    break;
            }
        }

        return res;
    }

    public static double obtenerNumero(String t, int ln) throws ErrorC {

        if (t.matches("\\d+")) {
            return Integer.parseInt(t);
        }

        if (t.matches("\\d+\\.\\d*") || t.matches("\\.\\d+")) {
            return Double.parseDouble(t);
        }

        TablaSimbolos.Entrada sim = tabla.obtener(t, ln);

        if (sim.valor instanceof Cuarto) {
            return ((Cuarto) sim.valor).getValor();
        }
        if (sim.valor instanceof Media) {
            return ((Media) sim.valor).getValor();
        }

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

        if (sim.valor instanceof Mega) {
            return ((Mega) sim.valor).getValor();
        }

        throw new ErrorC(ErrorC.Tipo.SEMANTICO, ln, "No es cadena");
    }

    public static List<Token> obtenerTokens(String linea) {

        List<Token> lista = new ArrayList<>();
        List<String> partes = new ArrayList<>();
        boolean dentroComillas = false;
        StringBuilder actual = new StringBuilder();

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
        if (actual.length() > 0) {
            partes.add(actual.toString());
        }

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
            } else if (s.matches("[@#&$]")) {
                lista.add(new Token("OP", s, "[@#&$]", "NO"));
            } else if (s.matches("\\d+") || s.matches("\\d+\\.\\d*") || s.matches("\\.\\d+")) {
                lista.add(new Token("NUM", s, "(num)", "NO"));
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

    public static List<String[]> clasificacion(String linea) {

        List<String[]> lista = new ArrayList<>();

        for (String s : linea.split("\\s+")) {

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
