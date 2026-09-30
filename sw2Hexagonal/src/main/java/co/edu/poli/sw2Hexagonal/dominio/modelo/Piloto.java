package co.edu.poli.sw2Hexagonal.dominio.modelo;

public class Piloto {

    private int id;
    private String nombre;
    private String experiencia;
    private int telefono;

    public Piloto() {
    }

    public Piloto(int id, String nombre, String experiencia, int telefono) {
        this.id = id;
        this.nombre = nombre;
        this.experiencia = experiencia;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Piloto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", telefono=" + telefono +
                '}';
    }
}