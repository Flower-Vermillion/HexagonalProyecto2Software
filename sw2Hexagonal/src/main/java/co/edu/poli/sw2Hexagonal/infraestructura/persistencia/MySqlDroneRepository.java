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
 * Adaptador de salida — implementa {@link DroneRepository} sobre MySQL (JDBC).
 * <p>
 * Utiliza la conexión administrada por {@link ConexionBD} y trabaja sobre la
 * tabla {@code drone}, con las columnas {@code id}, {@code serial},
 * {@code modelo}, {@code fabricante}, {@code peso} y {@code tipo}.
 * </p>
 *
 * @see ConexionBD
 */
public class MySqlDroneRepository implements DroneRepository {

    /** Conexión JDBC compartida obtenida de {@link ConexionBD}. */
    private final Connection conexion;

    /**
     * Crea el repositorio usando la conexión única de {@link ConexionBD}.
     */
    public MySqlDroneRepository() {
        this.conexion = ConexionBD
                .getInstancia()
                .getConexion();
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException si falla la inserción en la tabla {@code drone}
     */
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

    /**
     * {@inheritDoc}
     *
     * @throws SQLException si falla la consulta
     */
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

    /**
     * {@inheritDoc}
     *
     * @throws SQLException si falla la consulta
     */
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

    /**
     * {@inheritDoc}
     *
     * @throws SQLException si falla la actualización o no existe un dron
     *                      con el id indicado
     */
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

    /**
     * {@inheritDoc}
     *
     * @throws SQLException si falla la eliminación o no existe un dron
     *                      con el id indicado
     */
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
     * Construye un {@link Drone} a partir de la fila actual del
     * {@link ResultSet}. Como {@code Drone} es abstracta, se usa
     * {@link DroneBase}, que conserva el tipo guardado en la tabla.
     * Cuando se usen las subclases concretas, conviene reemplazarlo por un
     * {@code switch} sobre el tipo ("AGRICULTURA" / "VIGILANCIA") que cree
     * la subclase correspondiente.
     *
     * @param rs resultado posicionado en la fila a convertir
     * @return dron con los datos de la fila
     * @throws SQLException si falla la lectura de alguna columna
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

    /**
     * Implementación concreta y mínima de {@link Drone} usada al leer desde
     * la base de datos. Conserva el tipo almacenado en la columna
     * {@code tipo}.
     */
    public static class DroneBase extends Drone {

        /** Tipo del dron tal como está guardado en la base de datos. */
        private final String tipo;

        /**
         * Crea un dron base con el tipo indicado.
         *
         * @param tipo tipo del dron ({@code "AGRICULTURA"} o {@code "VIGILANCIA"})
         */
        DroneBase(String tipo) {
            this.tipo = tipo;
        }

        /**
         * Retorna el tipo guardado en la base de datos.
         *
         * @return tipo del dron
         */
        @Override
        public String getTipo() {
            return tipo;
        }
    }
}
