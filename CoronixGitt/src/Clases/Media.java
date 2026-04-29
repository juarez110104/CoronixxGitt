package Clases;

public class Media {
    private double valor;

    public Media(double valor) {
        this.valor = valor;
    }

    public double getValor() { return valor; }

    public Media sumar(Media otro)        { return new Media(this.valor + otro.valor); }
    public Media restar(Media otro)       { return new Media(this.valor - otro.valor); }
    public Media multiplicar(Media otro)  { return new Media(this.valor * otro.valor); }

    public Media dividir(Media otro) {
        if (otro.valor == 0)
            throw new ArithmeticException("❌ Error semántico: división entre cero");
        return new Media(this.valor / otro.valor);
    }

    @Override
    public String toString() { return String.valueOf(valor); }
}