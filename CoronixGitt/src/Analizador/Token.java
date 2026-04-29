package Analizador;

public class Token {
    public String lexema, tipo;
    public int linea;
    public String valor;

    public Token(String l,String t,int ln,String v){
        lexema=l;tipo=t;linea=ln;valor=v;
    }
}