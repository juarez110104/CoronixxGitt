package Clases;

public class Cuarto {
    private int valor;

    public Cuarto(int valor) { this.valor = valor; }

    public int getValor() { return valor; }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}