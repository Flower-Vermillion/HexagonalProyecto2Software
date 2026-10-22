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
import co.edu.poli.sw2Hexagonal.dominio.modelo.chain.WeatherHandler;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Adaptador de entrada: controlador JavaFX del formulario de drones
 * ({@code formDrone.fxml}).
 * <p>
 * La vista tiene dos pestañas independientes:
 * </p>
 * <ul>
 *   <li><b>Drones</b>: CRUD de drones y patrón <b>Observer</b>. Permite
 *       suscribir y desuscribir observadores a los drones seleccionados y
 *       consultar el clima para notificarlos automáticamente. Las
 *       notificaciones del {@link DashboardObserver} se muestran en una
 *       ventana emergente.</li>
 *   <li><b>Clima de vuelo</b>: patrón <b>Chain of Responsibility</b>.
 *       Consulta el clima de una ciudad y evalúa la viabilidad de vuelo con
 *       {@link WeatherHandler}, sin notificar a ningún observador.</li>
 * </ul>
 * <p>
 * Las consultas a las APIs se ejecutan en un hilo aparte ({@link Task}) para
 * no congelar la interfaz.
 * </p>
 *
 * @see WeatherNotificationService
 * @see GetWeatherByCityCasoDeUso
 * @see WeatherHandler
 */
public class ControlFormDrone {

    // ===== CRUD =====

    /** Campo del id del dron. */
    @FXML private TextField txtId;
    /** Campo del serial del dron. */
    @FXML private TextField txtSerial;
    /** Campo del modelo del dron. */
    @FXML private TextField txtModelo;
    /** Campo del fabricante del dron. */
    @FXML private TextField txtFabricante;
    /** Campo del peso del dron. */
    @FXML private TextField txtPeso;
    /** Selector del tipo de dron (AGRICULTURA o VIGILANCIA). */
    @FXML private ComboBox<String> cmbTipo;

    /** Tabla con los drones registrados. Permite selección múltiple. */
    @FXML private TableView<Drone> tblDrones;
    /** Columna del id. */
    @FXML private TableColumn<Drone, String> colId;
    /** Columna del serial. */
    @FXML private TableColumn<Drone, String> colSerial;
    /** Columna del modelo. */
    @FXML private TableColumn<Drone, String> colModelo;
    /** Columna del fabricante. */
    @FXML private TableColumn<Drone, String> colFabricante;
    /** Columna del peso. */
    @FXML private TableColumn<Drone, Double> colPeso;
    /** Columna del tipo. */
    @FXML private TableColumn<Drone, String> colTipo;

    // ===== Observer =====

    /** Marca el {@link ConsoleObserver} para suscribir o desuscribir. */
    @FXML private CheckBox chkConsola;
    /** Marca el {@link LoggerObserver} para suscribir o desuscribir. */
    @FXML private CheckBox chkLogger;
    /** Marca el {@link FlightStatusObserver} para suscribir o desuscribir. */
    @FXML private CheckBox chkEstadoVuelo;
    /** Marca el {@link DashboardObserver} para suscribir o desuscribir. */
    @FXML private CheckBox chkDashboard;
    /** Muestra cuántos observadores tiene el dron seleccionado. */
    @FXML private Label lblObservadores;
    /** Ciudad cuyo clima se notifica a los observadores. */
    @FXML private TextField txtCiudadObserver;
    /** Botón que consulta el clima y notifica a los observadores. */
    @FXML private Button btnNotificarClima;

    // ===== Chain (Clima de vuelo) =====

    /** Ciudad cuyo clima se evalúa con la cadena. */
    @FXML private TextField txtCiudad;
    /** Botón que consulta el clima y evalúa la cadena. */
    @FXML private Button btnConsultarClima;
    /** Indicador de carga mientras se consulta la API. */
    @FXML private ProgressIndicator prgClima;
    /** Muestra la ciudad consultada. */
    @FXML private Label lblUbicacion;
    /** Muestra la temperatura. */
    @FXML private Label lblTemperatura;
    /** Muestra la humedad. */
    @FXML private Label lblHumedad;
    /** Muestra la velocidad del viento. */
    @FXML private Label lblViento;
    /** Muestra la presión atmosférica. */
    @FXML private Label lblPresion;
    /** Muestra la visibilidad. */
    @FXML private Label lblVisibilidad;
    /** Muestra si el clima es apto o no para volar. */
    @FXML private Label lblResultadoClima;
    /** Muestra el recorrido completo de la cadena de validaciones. */
    @FXML private TextArea txtDetalleCadena;

    // ===== Casos de uso CRUD =====

    /** Caso de uso para crear drones. */
    private CrearDroneCasosDeUso crearDrone;
    /** Caso de uso para buscar un dron por id. */
    private LeerUnoDroneCasosDeUso leerUnoDrone;
    /** Caso de uso para listar todos los drones. */
    private LeerTodosDroneCasosDeUso leerTodosDrone;
    /** Caso de uso para modificar drones. */
    private ModificarDroneCasosDeUso modificarDrone;
    /** Caso de uso para eliminar drones. */
    private EliminarDroneCasosDeUso eliminarDrone;

    // ===== Clima =====

    /**
     * Caso de uso que obtiene el clima de una ciudad (geocodificación y luego
     * clima actual). Lo comparten el Observer y el Chain.
     */
    private final GetWeatherByCityCasoDeUso getWeatherByCity = new GetWeatherByCityCasoDeUso(
            new GeoApiAdapter(new GeoApiClient()),
            new OpenWeatherAdapter(new OpenWeatherClient()));

    /** Servicio que gestiona las suscripciones y notificaciones del Observer. */
    private final WeatherNotificationService weatherService =
            new WeatherNotificationService(getWeatherByCity);

    /** Observador que imprime en consola. */
    private final ConsoleObserver consoleObserver = new ConsoleObserver();
    /** Observador que escribe en el log. */
    private final LoggerObserver loggerObserver = new LoggerObserver();
    /** Observador que guarda el último clima de cada dron. */
    private final FlightStatusObserver flightStatusObserver = new FlightStatusObserver();
    /** Observador que envía las notificaciones a la ventana emergente. */
    private DashboardObserver dashboardObserver;

    /** Ventana emergente de notificaciones; se crea la primera vez que se usa. */
    private Stage ventanaNotificaciones;
    /** Lista de notificaciones que se muestra en la ventana emergente. */
    private final ListView<String> listaNotificaciones = new ListView<>();

    /**
     * Inicializa la vista después de cargar el FXML: configura el combo de
     * tipos, las columnas de la tabla, el listener de selección y el
     * patrón Observer.
     */
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

        inicializarObserver();
    }

    /**
     * Inyecta los casos de uso desde {@code App} (composición de dependencias).
     *
     * @param crear     caso de uso para crear drones
     * @param leerUno   caso de uso para buscar un dron por id
     * @param leerTodos caso de uso para listar todos los drones
     * @param modificar caso de uso para modificar drones
     * @param eliminar  caso de uso para eliminar drones
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
     * Carga la tabla con los drones registrados. Se llama desde {@code App}
     * después de inyectar los casos de uso.
     */
    public void cargarDrones() {
        cargarTabla();
    }

    // ================= CRUD =================

    /**
     * Crea un dron con los datos del formulario y recarga la tabla.
     */
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

    /**
     * Busca un dron por el id escrito en el formulario y muestra sus datos.
     */
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

    /**
     * Modifica el dron con los datos del formulario y recarga la tabla.
     */
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

    /**
     * Pide confirmación y elimina el dron cuyo id está en el formulario.
     */
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

    /**
     * Limpia el formulario y la selección de la tabla.
     */
    @FXML
    private void onLimpiar() {
        limpiarFormulario();
    }

    // ================= Observer =================

    /**
     * Configura el patrón Observer: crea el {@link DashboardObserver}
     * conectado a la ventana emergente y habilita la selección múltiple en
     * la tabla.
     */
    private void inicializarObserver() {
        dashboardObserver = new DashboardObserver(
                texto -> Platform.runLater(() -> mostrarNotificacion(texto)));

        tblDrones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Obtiene los observadores marcados en los checkboxes.
     *
     * @return lista con los observadores seleccionados, posiblemente vacía
     */
    private List<WeatherObserver> observersSeleccionados() {
        List<WeatherObserver> lista = new ArrayList<>();
        if (chkConsola.isSelected()) lista.add(consoleObserver);
        if (chkLogger.isSelected()) lista.add(loggerObserver);
        if (chkEstadoVuelo.isSelected()) lista.add(flightStatusObserver);
        if (chkDashboard.isSelected()) lista.add(dashboardObserver);
        return lista;
    }

    /**
     * Suscribe los observadores marcados a todos los drones seleccionados en
     * la tabla.
     */
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

    /**
     * Desuscribe los observadores marcados de todos los drones seleccionados
     * en la tabla.
     */
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

    /**
     * Consulta el clima de la ciudad escrita y notifica automáticamente a los
     * observadores suscritos a cada dron de la tabla. No usa el Chain of
     * Responsibility.
     */
    @FXML
    private void onNotificarClima() {
        String ciudad = txtCiudadObserver.getText().trim();
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
        tarea.setOnSucceeded(e -> btnNotificarClima.setDisable(false));
        tarea.setOnFailed(e -> {
            btnNotificarClima.setDisable(false);
            mostrar(AlertType.ERROR, tarea.getException().getMessage());
        });

        btnNotificarClima.setDisable(true);
        ejecutar(tarea);
    }

    /**
     * Agrega una notificación a la ventana emergente y la muestra. La ventana
     * se crea la primera vez, no es modal y se vuelve a abrir si el usuario
     * la cerró.
     *
     * @param texto resumen enviado por el {@link DashboardObserver}
     */
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

    /**
     * Actualiza la etiqueta que muestra cuántos observadores tiene el dron
     * seleccionado.
     */
    private void actualizarLabelObservadores() {
        Drone d = tblDrones.getSelectionModel().getSelectedItem();
        if (d == null || d.getId() == null) {
            lblObservadores.setText("Seleccione un dron");
            return;
        }
        lblObservadores.setText("Dron " + d.getId() + ": "
                + weatherService.getObservadores(d).size() + " observer(s)");
    }

    // ================= Chain of Responsibility =================

    /**
     * Consulta el clima de la ciudad escrita y evalúa la viabilidad de vuelo
     * con la cadena de {@link WeatherHandler}. No notifica a ningún
     * observador.
     */
    @FXML
    private void onConsultarClima() {
        String ciudad = txtCiudad.getText().trim();
        if (ciudad.isEmpty()) {
            mostrar(AlertType.WARNING, "Escriba una ciudad.");
            return;
        }

        Task<Weather> tarea = new Task<>() {
            @Override
            protected Weather call() {
                return getWeatherByCity.ejecutar(ciudad);
            }
        };
        tarea.setOnSucceeded(e -> {
            terminarConsultaCadena();
            mostrarResultadoCadena(ciudad, tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            terminarConsultaCadena();
            mostrar(AlertType.ERROR, tarea.getException().getMessage());
        });

        btnConsultarClima.setDisable(true);
        prgClima.setVisible(true);
        lblResultadoClima.setText("");
        txtDetalleCadena.clear();
        ejecutar(tarea);
    }

    /**
     * Rehabilita el botón y oculta el indicador de carga al terminar la
     * consulta de la cadena.
     */
    private void terminarConsultaCadena() {
        btnConsultarClima.setDisable(false);
        prgClima.setVisible(false);
    }

    /**
     * Muestra los cinco aspectos climáticos, evalúa la cadena y presenta el
     * resultado (apto o no apto) con el detalle de cada regla.
     *
     * @param ciudad ciudad consultada
     * @param clima  clima obtenido de la API
     */
    private void mostrarResultadoCadena(String ciudad, Weather clima) {
        lblUbicacion.setText(ciudad);
        lblTemperatura.setText(String.format("%.1f °C", clima.getTemperatura()));
        lblHumedad.setText(String.format("%.0f %%", clima.getHumedad()));
        lblViento.setText(String.format("%.1f km/h", clima.getVelocidadViento()));
        lblPresion.setText(String.format("%.1f hPa", clima.getPresion()));
        lblVisibilidad.setText(String.format("%.1f km", clima.getVisibilidad() / 1000.0));

        WeatherHandler.Resultado resultado = WeatherHandler.evaluar(clima);
        if (resultado.isApto()) {
            lblResultadoClima.setText("✔ APTO PARA VOLAR");
            lblResultadoClima.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        } else {
            lblResultadoClima.setText("✘ NO APTO PARA VOLAR — " + resultado.getReglaIncumplida());
            lblResultadoClima.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #c62828;");
        }
        txtDetalleCadena.setText(resultado.getDetalle());
    }

    // ---------- Métodos auxiliares ----------

    /**
     * Ejecuta una tarea en un hilo daemon para no bloquear la interfaz.
     *
     * @param tarea tarea a ejecutar
     */
    private void ejecutar(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Recarga la tabla con todos los drones de la base de datos.
     */
    private void cargarTabla() {
        try {
            List<Drone> drones = leerTodosDrone.leerTodos();
            tblDrones.setItems(FXCollections.observableArrayList(drones));
        } catch (Exception e) {
            mostrar(AlertType.ERROR, e.getMessage());
        }
    }

    /**
     * Construye un {@link Drone} con los datos del formulario. Como
     * {@code Drone} es abstracta, se usa una subclase anónima que devuelve el
     * tipo elegido.
     *
     * @return dron con los datos del formulario
     * @throws Exception si no se eligió el tipo o el peso no es un número válido
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

    /**
     * Muestra en el formulario los datos de un dron.
     *
     * @param drone dron a mostrar
     */
    private void mostrarEnFormulario(Drone drone) {
        txtId.setText(drone.getId());
        txtSerial.setText(drone.getSerial());
        txtModelo.setText(drone.getModelo());
        txtFabricante.setText(drone.getFabricante());
        txtPeso.setText(String.valueOf(drone.getPeso()));
        cmbTipo.setValue(drone.getTipo());
    }

    /**
     * Limpia todos los campos del formulario y la selección de la tabla.
     */
    private void limpiarFormulario() {
        txtId.clear();
        txtSerial.clear();
        txtModelo.clear();
        txtFabricante.clear();
        txtPeso.clear();
        cmbTipo.setValue(null);
        tblDrones.getSelectionModel().clearSelection();
    }

    /**
     * Muestra un mensaje en una alerta sin encabezado.
     *
     * @param tipo    tipo de alerta (información, advertencia, error)
     * @param mensaje texto a mostrar
     */
    private void mostrar(AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}