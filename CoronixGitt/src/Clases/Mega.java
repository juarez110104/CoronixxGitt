package Clases;

public class Mega {
    private String valor;

    public Mega(String valor) {
        this.valor = valor;
    }

    public String getValor() { return valor; }

    // Solo permite concatenación con @
    public Mega concatenar(Mega otro) {
        return new Mega(this.valor + otro.valor);
    }

    @Override
    public String toString() { return valor; }
}