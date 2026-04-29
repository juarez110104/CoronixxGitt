package Analizador;

public class Error {
    public String tipo;
    public int linea;
    public String mensaje;

    public Error(String t,int l,String m){
        tipo=t;linea=l;mensaje=m;
    }
}