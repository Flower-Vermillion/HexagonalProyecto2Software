package co.edu.poli.sw2Hexagonal.vista;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.CrearDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.EliminarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerTodosDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerUnoDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.ModificarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.servicio.CrearDroneServicio;
import co.edu.poli.sw2Hexagonal.aplicacion.servicio.EliminarDroneServicio;
import co.edu.poli.sw2Hexagonal.aplicacion.servicio.LeerTodosDroneServicio;
import co.edu.poli.sw2Hexagonal.aplicacion.servicio.LeerUnoDroneServicio;
import co.edu.poli.sw2Hexagonal.aplicacion.servicio.ModificarDroneServicio;
import co.edu.poli.sw2Hexagonal.infraestructura.persistencia.MySqlDroneRepository;
import co.edu.poli.sw2Hexagonal.infraestructura.ui.ControlFormDrone;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX.
 * <p>
 * Responsabilidad adicional en la arquitectura hexagonal: es el único lugar
 * del sistema que conoce todas las capas y realiza la composición (wiring)
 * manual de dependencias:
 * </p>
 * <pre>
 *   Adaptador de salida    →  Servicios de aplicación   →  Adaptador de entrada (UI)
 *   MySqlDroneRepository   →  CrearDroneServicio        →  ControlFormDrone
 *                          →  LeerUnoDroneServicio
 *                          →  LeerTodosDroneServicio
 *                          →  ModificarDroneServicio
 *                          →  EliminarDroneServicio
 * </pre>
 *
 * @see ControlFormDrone
 * @see MySqlDroneRepository
 */
public class App extends Application {

    /**
     * Crea el adaptador de salida, los servicios de aplicación y la vista, los
     * conecta entre sí y muestra la ventana principal.
     *
     * @param stage ventana principal proporcionada por JavaFX
     * @throws Exception si no se puede cargar el archivo FXML de la vista
     */
    @Override
    public void start(Stage stage) throws Exception {

        // 1. Crear el adaptador de salida (infraestructura de persistencia)
        MySqlDroneRepository droneRepo = new MySqlDroneRepository();

        // 2. Crear servicios de aplicación inyectando el repositorio
        CrearDroneCasosDeUso     crearUC     = new CrearDroneServicio(droneRepo);
        LeerUnoDroneCasosDeUso   leerUnoUC   = new LeerUnoDroneServicio(droneRepo);
        LeerTodosDroneCasosDeUso leerTodosUC = new LeerTodosDroneServicio(droneRepo);
        ModificarDroneCasosDeUso modificarUC = new ModificarDroneServicio(droneRepo);
        EliminarDroneCasosDeUso  eliminarUC  = new EliminarDroneServicio(droneRepo);

        // 3. Cargar la vista FXML y obtener el controlador
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/poli/sw2Hexagonal/vista/formDrone.fxml"));
        VBox root = loader.load();

        // 4. Inyectar use cases en el adaptador de entrada (UI)
        ControlFormDrone controlador = loader.getController();
        controlador.setUseCases(crearUC, leerUnoUC, leerTodosUC, modificarUC, eliminarUC);
        controlador.cargarDrones();

        // 5. Mostrar la ventana
        stage.setTitle("Gestión de Drones");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Método principal. Lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        launch();
    }
}
