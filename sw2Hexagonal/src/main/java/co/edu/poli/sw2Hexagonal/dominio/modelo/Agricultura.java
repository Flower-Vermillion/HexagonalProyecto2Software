package co.edu.poli.sw2Hexagonal.dominio.modelo;

public class Agricultura extends Drone {

    private double capacidadTanque;

    public Agricultura() {
        super();
    }

    public Agricultura(String id, String serial, String modelo,
                        String fabricante, double peso,
                        double capacidadTanque) {
        super(id, serial, modelo, fabricante, peso);
        this.capacidadTanque = capacidadTanque;
    }

    public double getCapacidadTanque() {
        return capacidadTanque;
    }

    public void setCapacidadTanque(double capacidadTanque) {
        this.capacidadTanque = capacidadTanque;
    }

    @Override
    public String getTipo() {
        return "AGRICULTURA";
    }

    @Override
    public String toString() {
        return "Agricultura{" +
                "id='" + id + '\'' +
                ", serial=" + serial +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", peso=" + peso +
                ", capacidadTanque=" + capacidadTanque +
                '}';
    }
}
