package controladores;

import modelo.ProductoEcologico;
import modelo.ProductoLimpieza;
import modelo.SerializadorProductos;
import modelo.RepositorioProductos;
import modelo.ProductoQuimico;
import excepciones.ProductoDuplicadoException;
import excepciones.ProductoVencidoException;
import excepciones.ProductoInvalidoException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class ControladorProductos {

    // Tabla y columnas
    @FXML private TableView<ProductoLimpieza> tableProducts;
    @FXML private TableColumn<ProductoLimpieza, String> colTipo;
    @FXML private TableColumn<ProductoLimpieza, String> colNombre;
    @FXML private TableColumn<ProductoLimpieza, String> colConcentracion;
    @FXML private TableColumn<ProductoLimpieza, LocalDate> colVencimiento;
    @FXML private TableColumn<ProductoLimpieza, String> colDetalle;

    // Campos comunes
    @FXML private TextField txtNombre;
    @FXML private TextField txtConcentracion;
    @FXML private DatePicker dpVencimiento;

    // Campos específicos
    @FXML private ChoiceBox<String> elegirTipo;
    @FXML private TextField txtSpecific;       

    // Botones
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnBorrar;
    @FXML private Button btnExportar;
    @FXML private CheckBox chkFilterSoon;

    // Estado
    private RepositorioProductos repository;
    private ObservableList<ProductoLimpieza> observableProducts;
    private final SerializadorProductos serializer = new SerializadorProductos();

    // Rutas de archivos
    private final String PRODUCTOS_FILE = "products.json";
    private final String VENCIMIENTO_FILE = "expiring_soon.json";

    @FXML
    public void initialize() {
        repository = new RepositorioProductos();
        observableProducts = FXCollections.observableArrayList();

        // Configurar ChoiceBox
        elegirTipo.getItems().add("QUIMICO");
        elegirTipo.getItems().add("ECOLOGICO");
        elegirTipo.setValue("QUIMICO");

        // Configurar columnas
        colTipo.setCellValueFactory(new PropertyValueFactory<ProductoLimpieza, String>("type"));
        colNombre.setCellValueFactory(new PropertyValueFactory<ProductoLimpieza, String>("nombre"));
        colConcentracion.setCellValueFactory(new PropertyValueFactory<ProductoLimpieza, String>("concentracion"));
        colVencimiento.setCellValueFactory(new PropertyValueFactory<ProductoLimpieza, LocalDate>("fechaVencimiento"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<ProductoLimpieza, String>("specificDetail"));

        tableProducts.setItems(observableProducts);

        // Cargar datos desde archivo
        loadData();

        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });
    }

    private void fillForm(ProductoLimpieza p) {
        txtNombre.setText(p.getNombre());
        txtConcentracion.setText(p.getConcentracion());
        dpVencimiento.setValue(p.getFechaVencimiento());
        elegirTipo.setValue(p.getType());
        txtSpecific.setText(p.getSpecificDetail());
    }

    private void refreshTable() {
        observableProducts.setAll(repository.getAll());
        if (chkFilterSoon.isSelected()) {
            applySoonFilter();
        }
    }

    @FXML
    private void onAgregar() {
        try {
            ProductoLimpieza newProduct = buildFromForm();
            repository.add(newProduct);
            refreshTable();
            clearForm();
            showInfo("Producto agregado correctamente.");
            saveData();
        } catch (ProductoInvalidoException e) {
            showError(e.getMessage());
        } catch (ProductoVencidoException e) {
            showError(e.getMessage());
        } catch (ProductoDuplicadoException e) {
            showError(e.getMessage());
        } 
    }

    @FXML
    private void onActualizar() {
        int index = tableProducts.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Seleccione un producto para modificar.");
            return;
        }
        try {
            ProductoLimpieza updated = buildFromForm();
            repository.update(index, updated);
            refreshTable();
            showInfo("Producto actualizado correctamente.");
            saveData();
        } catch (ProductoInvalidoException e) {
            showError(e.getMessage());
        } catch (ProductoVencidoException e) {
            showError(e.getMessage());
        } catch (ProductoDuplicadoException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error inesperado al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void onBorrar() {
        int index = tableProducts.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showError("Seleccione un producto para eliminar.");
            return;
        }
        try {
            repository.remove(index);
            refreshTable();
            clearForm();
            showInfo("Producto eliminado correctamente.");
            saveData();
        } catch (ProductoInvalidoException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error inesperado al eliminar: " + e.getMessage());
        }
    }

    @FXML
    private void onFilterSoon() {
        if (chkFilterSoon.isSelected()) {
            applySoonFilter();
        } else {
            observableProducts.setAll(repository.getAll());
        }
    }

    private void applySoonFilter() {
        ArrayList<ProductoLimpieza> soon = repository.getExpiringWithinDays(60);
        observableProducts.setAll(soon);
    }

    @FXML
    private void onExportar() {
        try {
            ArrayList<ProductoLimpieza> soon = repository.getExpiringWithinDays(60);
            serializer.saveToFile(VENCIMIENTO_FILE, soon);
            showInfo("Productos próximos a vencer exportados a " + VENCIMIENTO_FILE);
        } catch (Exception e) {
            showError("Error al exportar: " + e.getMessage());
        }
    }

    private ProductoLimpieza buildFromForm()
            throws ProductoInvalidoException, ProductoVencidoException {
        String name = txtNombre.getText();
        String concentration = txtConcentracion.getText();
        LocalDate expiry = dpVencimiento.getValue();
        String type = elegirTipo.getValue();
        String specific = txtSpecific.getText();

        if (type == null) {
            throw new ProductoInvalidoException("Seleccione un tipo de producto.");
        }

        if ("QUIMICO".equals(type)) {
            return new ProductoQuimico(name, concentration, expiry, specific);
        } else {
            return new ProductoEcologico(name, concentration, expiry, specific);
        }
    }

    private void clearForm() {
        txtNombre.clear();
        txtConcentracion.clear();
        dpVencimiento.setValue(null);
        elegirTipo.setValue("QUIMICO");
        txtSpecific.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operación no realizada");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadData() {
        try {
            ArrayList<ProductoLimpieza> loaded = serializer.loadFromFile(PRODUCTOS_FILE);
            for (int i = 0; i < loaded.size(); i++) {
                repository.add(loaded.get(i));
            }
            refreshTable();
        } catch (Exception e) {
        }
    }

    public void saveData() {
        try {
            serializer.saveToFile(PRODUCTOS_FILE, repository.getAll());
        } catch (Exception e) {
            showError("No se pudo guardar el archivo: " + e.getMessage());
        }
    }
}