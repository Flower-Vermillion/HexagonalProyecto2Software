package co.edu.poli.sw2Hexagonal.infraestructura.ui;

import java.util.List;
import java.util.Optional;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.CrearDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.EliminarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerTodosDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerUnoDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.ModificarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.weather.GetWeatherByCityCasoDeUso;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import co.edu.poli.sw2Hexagonal.dominio.modelo.weather.Weather;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private CrearDroneCasosDeUso crearDrone;
    private LeerUnoDroneCasosDeUso leerUnoDrone;
    private LeerTodosDroneCasosDeUso leerTodosDrone;
    private ModificarDroneCasosDeUso modificarDrone;
    private EliminarDroneCasosDeUso eliminarDrone;

    // ---------- Pestaña "Clima de vuelo" ----------
    @FXML private TextField txtCiudad;
    @FXML private Button btnConsultarClima;
    @FXML private ProgressIndicator prgClima;
    @FXML private Label lblUbicacion;
    @FXML private Label lblTemperatura;
    @FXML private Label lblHumedad;
    @FXML private Label lblViento;
    @FXML private Label lblPresion;
    @FXML private Label lblVisibilidad;
    @FXML private Label lblResultadoClima;
    @FXML private TextArea txtDetalleCadena;

    private GetWeatherByCityCasoDeUso consultarClima;

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
                });
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

    // ---------- Clima de vuelo ----------

    /**
     * Inyecta el caso de uso de clima desde App.
     *
     * @param consultarClima caso de uso que geocodifica, consulta y evalúa el clima
     */
    public void setClimaCasoDeUso(GetWeatherByCityCasoDeUso consultarClima) {
        this.consultarClima = consultarClima;
    }

    /**
     * Consulta el clima en un hilo aparte para no congelar la ventana mientras
     * responden las APIs. Al terminar se muestra el resultado de la cadena.
     */
    @FXML
    private void onConsultarClima() {
        if (consultarClima == null) {
            mostrar(AlertType.ERROR, "El servicio de clima no está configurado.");
            return;
        }

        String ciudad = txtCiudad.getText();

        Task<Weather> tarea = new Task<>() {
            @Override
            protected Weather call() throws Exception {
                return consultarClima.ejecutar(ciudad);
            }
        };

        tarea.setOnSucceeded(e -> {
            ocupado(false);
            mostrarClima(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            ocupado(false);
            Throwable error = tarea.getException();
            mostrar(AlertType.ERROR, error == null ? "Error desconocido." : error.getMessage());
        });

        ocupado(true);
        Thread hilo = new Thread(tarea, "consulta-clima");
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Muestra el clima consultado y el resultado de la cadena de validaciones:
     * las reglas aprobadas y, si el clima no es apto, la regla en la que se
     * detuvo la cadena con su motivo.
     */
    private void mostrarClima(Weather clima) {
        lblUbicacion.setText(clima.getNombreCompleto());
        lblTemperatura.setText(formato(clima.getTemperatura(), "°C"));
        lblHumedad.setText(formato(clima.getHumedad(), "%"));
        lblViento.setText(formato(clima.getVelocidadViento(), "km/h"));
        lblPresion.setText(formato(clima.getPresion(), "hPa"));
        lblVisibilidad.setText(formato(clima.getVisibilidad(), "km"));

        lblResultadoClima.setText(clima.getMensajeEvaluacion());
        lblResultadoClima.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: "
                + (clima.isApto() ? "#1b7f3b" : "#b3261e") + ";");

        StringBuilder detalle = new StringBuilder("Cadena de validaciones:\n");
        for (String regla : clima.getValidacionesAprobadas()) {
            detalle.append("  ✔ ").append(regla).append('\n');
        }
        if (!clima.isApto() && clima.getReglaIncumplida() != null) {
            detalle.append("  ✘ ").append(clima.getReglaIncumplida())
                   .append(" — ").append(clima.getMotivoRechazo()).append('\n')
                   .append("  (la cadena se detuvo en esta regla)\n");
        }
        detalle.append('\n').append(clima.getMensajeEvaluacion());
        txtDetalleCadena.setText(detalle.toString());
    }

    private void ocupado(boolean ocupado) {
        btnConsultarClima.setDisable(ocupado);
        txtCiudad.setDisable(ocupado);
        prgClima.setVisible(ocupado);
    }

    private static String formato(double valor, String unidad) {
        return Double.isNaN(valor) ? "no disponible" : String.format("%.1f %s", valor, unidad);
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