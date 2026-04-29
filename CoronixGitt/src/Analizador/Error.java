package Analizador;

public class Error {

    public enum TipoError { LEXICO, SINTACTICO, SEMANTICO }

    private final TipoError tipo;
    private final int       linea;
    private final String    mensaje;

    public Error(TipoError tipo, int linea, String mensaje) {
        this.tipo    = tipo;
        this.linea   = linea;
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return String.format("❌ Error %s en línea %d: %s",
                tipo.name(), linea, mensaje);
    }

    public TipoError getTipo()   { return tipo;    }
    public int       getLinea()  { return linea;   }
    public String    getMensaje(){ return mensaje;  }
}