package co.edu.poli.sw2Hexagonal.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Gestiona la conexión a la base de datos MySQL mediante el patrón Singleton.
 * <p>
 * Los parámetros de conexión se leen desde un archivo {@code .env} ubicado en
 * la raíz del proyecto. Si el archivo no existe o falta alguna variable, se
 * usan valores por defecto pensados para un entorno de desarrollo local:
 * </p>
 * <ul>
 *   <li>{@code DB_URL}: {@code jdbc:mysql://localhost:3306/drones?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}</li>
 *   <li>{@code DB_USER}: {@code root}</li>
 *   <li>{@code DB_PASSWORD}: cadena vacía</li>
 * </ul>
 * <p>
 * Esta clase pertenece a la capa de infraestructura y es utilizada por el
 * adaptador de salida {@link MySqlDroneRepository}.
 * </p>
 *
 * @see MySqlDroneRepository
 */
public class ConexionBD {

    /** Única instancia de la clase (Singleton). */
    private static ConexionBD instancia;

    /** Conexión JDBC activa; puede ser {@code null} si la conexión falló. */
    private Connection conexion;

    /** URL JDBC de la base de datos. */
    private final String url;

    /** Usuario de la base de datos. */
    private final String usuario;

    /** Contraseña del usuario de la base de datos. */
    private final String password;

    /**
     * Constructor privado. Lee la configuración del archivo {@code .env}
     * (si existe) y establece la conexión con la base de datos.
     */
    private ConexionBD() {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        url = dotenv.get(
                "DB_URL",
                "jdbc:mysql://localhost:3306/drones?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
        );

        usuario = dotenv.get("DB_USER", "root");
        password = dotenv.get("DB_PASSWORD", "");

        conectar();
    }

    /**
     * Retorna la única instancia de {@code ConexionBD}, creándola (y
     * abriendo la conexión) la primera vez que se invoca.
     *
     * @return instancia única de la clase
     */
    public static ConexionBD getInstancia() {

        if (instancia == null) {
            instancia = new ConexionBD();
        }

        return instancia;
    }

    /**
     * Abre la conexión JDBC con los parámetros configurados. Si ocurre un
     * error, lo informa por consola y deja la conexión en {@code null}.
     */
    private void conectar() {

        try {

            conexion = DriverManager.getConnection(
                    url,
                    usuario,
                    password
            );

            System.out.println("Conexión a MySQL establecida correctamente.");

        } catch (SQLException e) {

            System.out.println("Error al conectar con MySQL:");
            System.out.println(e.getMessage());

        }
    }

    /**
     * Retorna la conexión JDBC activa.
     *
     * @return la conexión a la base de datos, o {@code null} si no fue
     *         posible establecerla
     */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Cierra la conexión con la base de datos, si está abierta. Los errores
     * al cerrar se informan por consola.
     */
    public void cerrarConexion() {

        if (conexion != null) {

            try {

                conexion.close();

                System.out.println("Conexión cerrada correctamente.");

            } catch (SQLException e) {

                System.out.println("Error al cerrar la conexión:");
                System.out.println(e.getMessage());

            }
        }
    }
}
