package co.edu.poli.sw2Hexagonal.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class ConexionBD {

    private static ConexionBD instancia;

    private Connection conexion;

    private final String url;
    private final String usuario;
    private final String password;

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

    public static ConexionBD getInstancia() {

        if (instancia == null) {
            instancia = new ConexionBD();
        }

        return instancia;
    }

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

    public Connection getConexion() {
        return conexion;
    }

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