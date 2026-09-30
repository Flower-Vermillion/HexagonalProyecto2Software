package co.edu.poli.sw2Hexagonal.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.salida.DroneRepository;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;

/**
 * Adaptador de salida — implementa DroneRepository sobre MySQL (JDBC).
 */
public class MySqlDroneRepository implements DroneRepository {

    private final Connection conexion;

    public MySqlDroneRepository() {
        this.conexion = ConexionBD
                .getInstancia()
                .getConexion();
    }

    @Override
    public void guardar(Drone drone) throws SQLException {

        String sql = """
                INSERT INTO drone
                (id, serial, modelo, fabricante, peso, tipo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, drone.getId());
            ps.setString(2, drone.getSerial());
            ps.setString(3, drone.getModelo());
            ps.setString(4, drone.getFabricante());
            ps.setDouble(5, drone.getPeso());
            ps.setString(6, drone.getTipo());

            ps.executeUpdate();
        }
    }

    @Override
    public Drone buscarPorId(String id) throws SQLException {

        String sql = """
                SELECT id, serial, modelo, fabricante, peso, tipo
                FROM drone
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirDrone(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Drone> buscarTodos() throws SQLException {

        List<Drone> drones = new ArrayList<>();

        String sql = """
                SELECT id, serial, modelo, fabricante, peso, tipo
                FROM drone
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                drones.add(convertirDrone(rs));
            }
        }

        return drones;
    }

    @Override
    public void actualizar(Drone drone) throws SQLException {

        String sql = """
                UPDATE drone
                SET serial = ?,
                    modelo = ?,
                    fabricante = ?,
                    peso = ?,
                    tipo = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, drone.getSerial());
            ps.setString(2, drone.getModelo());
            ps.setString(3, drone.getFabricante());
            ps.setDouble(4, drone.getPeso());
            ps.setString(5, drone.getTipo());
            ps.setString(6, drone.getId());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No existe un dron con el id " + drone.getId() + ".");
            }
        }
    }

    @Override
    public void eliminar(String id) throws SQLException {

        String sql = """
                DELETE FROM drone
                WHERE id = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, id);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No existe un dron con el id " + id + ".");
            }
        }
    }

    /**
     * Construye un Drone a partir de una fila. Como Drone es abstracta,
     * se usa DroneBase, que conserva el tipo guardado en la tabla.
     * Cuando tengas las subclases concretas, reemplaza esto por un
     * switch sobre el tipo ("AGRICULTURA" / "VIGILANCIA") que cree
     * la subclase correspondiente.
     */
    private Drone convertirDrone(ResultSet rs) throws SQLException {

        DroneBase drone = new DroneBase(rs.getString("tipo"));

        drone.setId(rs.getString("id"));
        drone.setSerial(rs.getString("serial"));
        drone.setModelo(rs.getString("modelo"));
        drone.setFabricante(rs.getString("fabricante"));
        drone.setPeso(rs.getDouble("peso"));

        return drone;
    }

    public static class DroneBase extends Drone {

        private final String tipo;

        DroneBase(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public String getTipo() {
            return tipo;
        }
    }
}