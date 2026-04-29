package Clases;

public class Cuarto {
    private int valor;

    public Cuarto(int valor) {
        this.valor = valor;
    }

    public int getValor() { return valor; }

    public Cuarto sumar(Cuarto otro)        { return new Cuarto(this.valor + otro.valor); }
    public Cuarto restar(Cuarto otro)       { return new Cuarto(this.valor - otro.valor); }
    public Cuarto multiplicar(Cuarto otro)  { return new Cuarto(this.valor * otro.valor); }

    public Cuarto dividir(Cuarto otro) {
        if (otro.valor == 0)
            throw new ArithmeticException("❌ Error semántico: división entre cero");
        return new Cuarto(this.valor / otro.valor);
    }

    @Override
    public String toString() { return String.valueOf(valor); }
}