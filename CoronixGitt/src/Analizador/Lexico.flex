/*
 * =============================================================
 *  CORONIX — Analizador Léxico
 *  Archivo : Lexico.flex
 *  Paquete : Analizador
 *
 *  Palabras reservadas:
 *    cuarto  →  int    (entero)
 *    media   →  double (decimal)
 *    mega    →  String (cadena)
 *
 *  Operadores Coronix:
 *    =>   asignación
 *    @    suma / concatenación
 *    ~    resta
 *    #    multiplicación
 *    %    división
 *    !!   fin de sentencia
 *    ( )  agrupación
 *
 *  Restricción: identificadores máximo 10 caracteres.
 *  Comentarios: // texto
 * =============================================================
 */

package Analizador;

import java_cup.runtime.Symbol;
import java.util.ArrayList;
import java.util.List;

%%

%class   Lexer
%public
%unicode
%cup
%line
%column
%type    Symbol

%{
    /** Lista pública — la GUI la usa para llenar la tabla de tokens */
    public List<Token> listaTokens = new ArrayList<>();

    /** Crea Symbol SIN valor semántico y lo registra */
    private Symbol mk(int tipo, String nombreTipo) {
        listaTokens.add(new Token(yytext(), nombreTipo, yyline + 1, ""));
        return new Symbol(tipo, yyline + 1, yycolumn + 1);
    }

    /** Crea Symbol CON valor semántico y lo registra */
    private Symbol mk(int tipo, String nombreTipo, Object valor) {
        listaTokens.add(new Token(yytext(), nombreTipo,
                                  yyline + 1, String.valueOf(valor)));
        return new Symbol(tipo, yyline + 1, yycolumn + 1, valor);
    }
%}

/* ── Macros ─────────────────────────────────────────────── */
LETRA      = [a-zA-Z_]
DIGITO     = [0-9]
ID         = {LETRA}({LETRA}|{DIGITO})*
ENTERO     = {DIGITO}+
DECIMAL    = {DIGITO}+"."{DIGITO}+
CADENA     = \"[^\"\n]*\"
ESPACIO    = [ \t\r\n]+
COMENTARIO = "//"[^\n]*

%%

/* ── Ignorar ─────────────────────────────────────────────── */
{COMENTARIO}    { /* ignorar comentarios */             }
{ESPACIO}       { /* ignorar espacios y saltos */       }

/* ── Palabras reservadas ─────────────────────────────────── */
"cuarto"        { return mk(sym.CUARTO,      "RESERVADA");    }
"media"         { return mk(sym.MEDIA,       "RESERVADA");    }
"mega"          { return mk(sym.MEGA,        "RESERVADA");    }

/* ── Asignación (=> antes de otros símbolos) ─────────────── */
"=>"            { return mk(sym.ASIGNAR,     "ASIGNACION");   }

/* ── Operadores Coronix ──────────────────────────────────── */
"@"             { return mk(sym.SUMA,        "OP_SUMA");      }
"~"             { return mk(sym.RESTA,       "OP_RESTA");     }
"#"             { return mk(sym.MULTIPLICAR, "OP_MULTI");     }
"%"             { return mk(sym.DIVIDIR,     "OP_DIV");       }

/* ── Fin de sentencia ────────────────────────────────────── */
"!!"            { return mk(sym.FIN,         "FIN_SENT");     }

/* ── Paréntesis ──────────────────────────────────────────── */
"("             { return mk(sym.LPAREN,      "LPAREN");       }
")"             { return mk(sym.RPAREN,      "RPAREN");       }

/* ── Literales  (DECIMAL antes que ENTERO) ───────────────── */
{DECIMAL}       {
                    double v = Double.parseDouble(yytext());
                    return mk(sym.LIT_DECIMAL, "DECIMAL", v);
                }
{ENTERO}        {
                    int v = Integer.parseInt(yytext());
                    return mk(sym.LIT_ENTERO, "ENTERO", v);
                }
{CADENA}        {
                    String v = yytext().substring(1, yytext().length() - 1);
                    return mk(sym.LIT_CADENA, "CADENA", v);
                }

/* ── Identificadores ─────────────────────────────────────── */
{ID}            {
                    String nombre = yytext();
                    if (nombre.length() > 10) {
                        throw new LexicalException(
                            "Error léxico en línea " + (yyline + 1) +
                            ": identificador '" + nombre +
                            "' supera el límite de 10 caracteres");
                    }
                    return mk(sym.ID, "IDENTIFICADOR", nombre);
                }

/* ── Error léxico ────────────────────────────────────────── */
[^]             {
                    throw new LexicalException(
                        "Error léxico en línea " + (yyline + 1) +
                        ", col " + (yycolumn + 1) +
                        ": carácter no reconocido -> '" + yytext() + "'");
                }
