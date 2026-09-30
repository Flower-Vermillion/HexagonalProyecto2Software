package co.edu.poli.sw2Hexagonal.dominio.modelo;

public abstract class Drone {

    protected String id;
    protected String serial;
    protected String modelo;
    protected String fabricante;
    protected double peso;

    public Drone() {
    }

    public Drone(String id, String serial, String modelo,
                 String fabricante, double peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Cada subclase concreta indica su tipo ("AGRICULTURA" o "VIGILANCIA").
     * Esto se usa en el DAO/Controller para decidir a qué tabla
     * adicional escribir o leer, sin necesidad de encadenar instanceof.
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Drone{" +
                "id='" + id + '\'' +
                ", serial=" + serial +
                ", modelo='" + modelo + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", peso=" + peso +
                ", tipo=" + getTipo() +
                '}';
    }
}