package co.edu.poli.sw2Hexagonal.infraestructura.ui;

import java.util.List;
import java.util.Optional;

import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.CrearDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.EliminarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerTodosDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.LeerUnoDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.aplicacion.puerto.entrada.ModificarDroneCasosDeUso;
import co.edu.poli.sw2Hexagonal.dominio.modelo.Drone;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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