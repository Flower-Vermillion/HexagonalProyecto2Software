package co.edu.poli.sw2Hexagonal.infraestructura.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import co.edu.poli.sw2Hexagonal.aplicacion.observer.ConsoleObserver;
import co.edu.poli.sw2Hexagonal.aplicacion.observer.DashboardObserver;
import co.edu.poli.sw2Hexagonal.aplicacion.observer.FlightStatusObserver;
import co.edu.poli.sw2Hexagonal.aplicacion.observer.LoggerObserver;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.CrearDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.EliminarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerTodosDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerUnoDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.ModificarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.weather.WeatherNotificationService;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.WeatherObserver;
import co.edu.poli.sw2Hexagonal.infraestructura.weather.GeoApiAdapter;
import co.edu.poli.sw2Hexagonal.infraestructura.weather.GeoApiClient;
import co.edu.poli.sw2Hexagonal.infraestructura.weather.OpenWeatherAdapter;
import co.edu.poli.sw2Hexagonal.infraestructura.weather.OpenWeatherClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Adaptador de entrada — controlador del formulario de drones.
 * Invoca los casos de uso sin conocer su implementación ni la base de datos.
 */
public class ControlFormDrone {

    @FXML private TextField txtId;
    @FXML private TextField txtSerial;
    @FXML private TextField txtModelo;
    @FXML private TextField txtFabricante;
    @FXML private TextField txtPeso;
    @FXML private ComboBox<String> cmbTipo;

    @FXML private TableView<Drone> tblDrones;
    @FXML private TableColumn<Drone, String> colId;
    @FXML private TableColumn<Drone, String> colSerial;
    @FXML private TableColumn<Drone, String> colModelo;
    @FXML private TableColumn<Drone, String> colFabricante;
    @FXML private TableColumn<Drone, Double> colPeso;
    @FXML private TableColumn<Drone, String> colTipo;

    // ===== Clima (Observer) =====
    @FXML private CheckBox chkConsola;
    @FXML private CheckBox chkLogger;
    @FXML private CheckBox chkEstadoVuelo;
    @FXML private CheckBox chkDashboard;
    @FXML private Label lblObservadores;
    @FXML private TextField txtCiudad;
    @FXML private Button btnConsultarClima;
    @FXML private Label lblTemperatura;
    @FXML private Label lblHumedad;
    @FXML private Label lblViento;
    @FXML private Label lblPresion;
    @FXML private Label lblVisibilidad;

    private CrearDroneCasosDeUso crearDrone;
    private LeerUnoDroneCasosDeUso leerUnoDrone;
    private LeerTodosDroneCasosDeUso leerTodosDrone;
    private ModificarDroneCasosDeUso modificarDrone;
    private EliminarDroneCasosDeUso eliminarDrone;

    private final WeatherNotificationService weatherService = new WeatherNotificationService(
            new GetWeatherByCityCasoDeUso(
                    new GeoApiAdapter(new GeoApiClient()),
                    new OpenWeatherAdapter(new OpenWeatherClient())));

    private final ConsoleObserver consoleObserver = new ConsoleObserver();
    private final LoggerObserver loggerObserver = new LoggerObserver();
    private final FlightStatusObserver flightStatusObserver = new FlightStatusObserver();
    private DashboardObserver dashboardObserver;

    // Ventana emergente de notificaciones
    private Stage ventanaNotificaciones;
    private final ListView<String> listaNotificaciones = new ListView<>();

    @FXML
    private void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList("AGRICULTURA", "VIGILANCIA"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Al seleccionar una fila, se llena el formulario
        tblDrones.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        mostrarEnFormulario(seleccionado);
                    }
                    actualizarLabelObservadores();
                });

        inicializarClima();
    }

    /**
     * Inyecta los casos de uso desde App (composición de dependencias).
     */
    public void setUseCases(CrearDroneCasosDeUso crear,
                            LeerUnoDroneCasosDeUso leerUno,
                            LeerTodosDroneCasosDeUso leerTodos,
                            ModificarDroneCasosDeUso modificar,
                            EliminarDroneCasosDeUso eliminar) {
        this.crearDrone = crear;
        this.leerUnoDrone = leerUno;
        this.leerTodosDrone = leerTodos;
        this.modificarDrone = modificar;
        this.eliminarDrone = eliminar;
    }

    /**
     * Carga la tabla con los drones registrados.
     */
    public void cargarDrones() {
        cargarTabla();
    }

    @FXML
    private void onCrear() {
        try {
            crearDrone.crear(construirDesdeFormulario());
            mostrar(AlertType.INFORMATION, "Dron creado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void onBuscar() {
        try {
            Drone drone = leerUnoDrone.leerUno(txtId.getText());
            if (drone == null) {
                mostrar(AlertType.INFORMATION, "No se encontró un dron con ese id.");
                return;
            }
            mostrarEnFormulario(drone);
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void onModificar() {
        try {
            modificarDrone.modificar(construirDesdeFormulario());
            mostrar(AlertType.INFORMATION, "Dron actualizado correctamente.");
            cargarTabla();
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        String id = txtId.getText();

        Alert confirmacion = new Alert(AlertType.CONFIRMATION,
                "¿Eliminar el dron con id " + id + "?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }

        try {
            eliminarDrone.eliminar(id);
            mostrar(AlertType.INFORMATION, "Dron eliminado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    // ================= Clima (Observer) =================

    private void inicializarClima() {
        dashboardObserver = new DashboardObserver(
                texto -> Platform.runLater(() -> mostrarNotificacion(texto)));

        tblDrones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /** Observers marcados en los checkboxes. */
    private List<WeatherObserver> observersSeleccionados() {
        List<WeatherObserver> lista = new ArrayList<>();
        if (chkConsola.isSelected()) lista.add(consoleObserver);
        if (chkLogger.isSelected()) lista.add(loggerObserver);
        if (chkEstadoVuelo.isSelected()) lista.add(flightStatusObserver);
        if (chkDashboard.isSelected()) lista.add(dashboardObserver);
        return lista;
    }

    @FXML
    private void onSuscribir() {
        List<Drone> drones = new ArrayList<>(tblDrones.getSelectionModel().getSelectedItems());
        if (drones.isEmpty()) {
            mostrar(AlertType.WARNING, "Seleccione al menos un dron en la tabla.");
            return;
        }
        List<WeatherObserver> observers = observersSeleccionados();
        if (observers.isEmpty()) {
            mostrar(AlertType.WARNING, "Marque al menos un observer.");
            return;
        }
        for (Drone d : drones) {
            weatherService.suscribir(d, observers.toArray(new WeatherObserver[0]));
        }
        actualizarLabelObservadores();
    }

    @FXML
    private void onDesuscribir() {
        List<Drone> drones = new ArrayList<>(tblDrones.getSelectionModel().getSelectedItems());
        if (drones.isEmpty()) {
            mostrar(AlertType.WARNING, "Seleccione al menos un dron en la tabla.");
            return;
        }
        for (Drone d : drones) {
            for (WeatherObserver o : observersSeleccionados()) {
                weatherService.desuscribir(d, o);
            }
        }
        actualizarLabelObservadores();
    }

    @FXML
    private void onConsultarClima() {
        String ciudad = txtCiudad.getText().trim();
        if (ciudad.isEmpty()) {
            mostrar(AlertType.WARNING, "Escriba una ciudad.");
            return;
        }
        List<Drone> drones = new ArrayList<>(tblDrones.getItems());

        Task<Weather> tarea = new Task<>() {
            @Override
            protected Weather call() {
                return weatherService.actualizarClima(ciudad, drones);
            }
        };
        tarea.setOnSucceeded(e -> {
            mostrarClima(tarea.getValue());
            btnConsultarClima.setDisable(false);
        });
        tarea.setOnFailed(e -> {
            btnConsultarClima.setDisable(false);
            mostrar(AlertType.ERROR, tarea.getException().getMessage());
        });

        btnConsultarClima.setDisable(true);
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarClima(Weather clima) {
        lblTemperatura.setText(String.format("%.1f °C", clima.getTemperatura()));
        lblHumedad.setText(String.format("%.0f %%", clima.getHumedad()));
        lblViento.setText(String.format("%.1f km/h", clima.getVelocidadViento()));
        lblPresion.setText(String.format("%.1f hPa", clima.getPresion()));
        lblVisibilidad.setText(String.format("%.0f m", clima.getVisibilidad()));
    }

    /** Muestra cada notificación del DashboardObserver en una ventana emergente. */
    private void mostrarNotificacion(String texto) {
        listaNotificaciones.getItems().add(0, texto);

        if (ventanaNotificaciones == null) {
            Button btnLimpiar = new Button("Limpiar");
            btnLimpiar.setOnAction(e -> listaNotificaciones.getItems().clear());

            VBox raiz = new VBox(8,
                    new Label("Notificaciones recibidas (DashboardObserver)"),
                    listaNotificaciones,
                    btnLimpiar);
            raiz.setPadding(new Insets(12));
            VBox.setVgrow(listaNotificaciones, Priority.ALWAYS);

            ventanaNotificaciones = new Stage();
            ventanaNotificaciones.setTitle("Notificaciones de clima");
            ventanaNotificaciones.initOwner(tblDrones.getScene().getWindow());
            ventanaNotificaciones.setScene(new Scene(raiz, 600, 320));
        }

        if (!ventanaNotificaciones.isShowing()) {
            ventanaNotificaciones.show();
        }
        ventanaNotificaciones.toFront();
    }

    private void actualizarLabelObservadores() {
        Drone d = tblDrones.getSelectionModel().getSelectedItem();
        if (d == null || d.getId() == null) {
            lblObservadores.setText("Seleccione un dron");
            return;
        }
        lblObservadores.setText("Dron " + d.getId() + ": "
                + weatherService.getObservadores(d).size() + " observer(s)");
    }

    // ---------- Métodos auxiliares ----------

    private void cargarTabla() {
        try {
            List<Drone> drones = leerTodosDrone.leerTodos();
            tblDrones.setItems(FXCollections.observableArrayList(drones));
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    /**
     * Construye un Drone con los datos del formulario. Como Drone es abstracta,
     * se usa una subclase anónima que devuelve el tipo elegido. Cuando tengas
     * las subclases concretas, reemplaza esto por un switch sobre el tipo.
     */
    private Drone construirDesdeFormulario() throws Exception {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            throw new Exception("Selecciona el tipo de dron.");
        }

        double peso;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new Exception("El peso debe ser un número válido.");
        }

        Drone drone = new Drone() {
            @Override
            public String getTipo() {
                return tipo;
            }
        };

        drone.setId(txtId.getText().trim());
        drone.setSerial(txtSerial.getText().trim());
        drone.setModelo(txtModelo.getText().trim());
        drone.setFabricante(txtFabricante.getText().trim());
        drone.setPeso(peso);

        return drone;
    }

    private void mostrarEnFormulario(Drone drone) {
        txtId.setText(drone.getId());
        txtSerial.setText(drone.getSerial());
        txtModelo.setText(drone.getModelo());
        txtFabricante.setText(drone.getFabricante());
        txtPeso.setText(String.valueOf(drone.getPeso()));
        cmbTipo.setValue(drone.getTipo());
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtSerial.clear();
        txtModelo.clear();
        txtFabricante.clear();
        txtPeso.clear();
        cmbTipo.setValue(null);
        tblDrones.getSelectionModel().clearSelection();
    }

    private void mostrar(AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}