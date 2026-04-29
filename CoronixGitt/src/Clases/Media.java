package Clases;

/**
 * Media — tipo decimal (double) del lenguaje Coronix.
 */
public class Media {
    private double valor;

    public Media(double valor) { this.valor = valor; }

    public double getValor()        { return valor; }
    public void   setValor(double v){ this.valor = v; }

    public Media sumar(Media o)       { return new Media(valor + o.valor); }
    public Media restar(Media o)      { return new Media(valor - o.valor); }
    public Media multiplicar(Media o) { return new Media(valor * o.valor); }
    public Media dividir(Media o) {
        if (o.valor == 0) throw new ArithmeticException("División entre cero");
        return new Media(valor / o.valor);
    }

    @Override public String toString() { return String.valueOf(valor); }
}