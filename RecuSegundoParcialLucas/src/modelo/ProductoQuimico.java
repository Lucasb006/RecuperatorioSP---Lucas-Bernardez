package modelo;

import excepciones.ProductoInvalidoException;
import excepciones.ProductoVencidoException;

import java.time.LocalDate;

public class ProductoQuimico extends ProductoLimpieza {

    private String warning;

    public ProductoQuimico(String nombre, String concentracion, LocalDate fechaVencimiento, String warning)
            throws ProductoInvalidoException, ProductoVencidoException {
        super(nombre, concentracion, fechaVencimiento);
        setWarning(warning);
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) throws ProductoInvalidoException {
        if (warning == null || warning.trim().isEmpty()) {
            throw new ProductoInvalidoException("La advertencia no puede estar vacía.");
        }
        this.warning = warning.trim();
    }

    @Override
    public String getType() {
        return "QUIMICO";
    }

    @Override
    public String getSpecificDetail() {
        return getWarning();
    }

    @Override
    public void setSpecificDetail(String value) throws ProductoInvalidoException {
        setWarning(value);
    }
}