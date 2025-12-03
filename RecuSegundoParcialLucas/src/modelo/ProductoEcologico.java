package modelo;

import excepciones.ProductoInvalidoException;
import excepciones.ProductoVencidoException;

import java.time.LocalDate;

public class ProductoEcologico extends ProductoLimpieza {

    private String etiquetaEco;

    public ProductoEcologico(String name, String concentration, LocalDate expiryDate, String ecoLabel)
            throws ProductoInvalidoException, ProductoVencidoException {
        super(name, concentration, expiryDate);
        setEtiquetaEco(ecoLabel);
    }

    public String getEtiquetaEco() {
        return etiquetaEco;
    }

    public void setEtiquetaEco(String etiquetaEco) throws ProductoInvalidoException {
        if (etiquetaEco == null || etiquetaEco.trim().isEmpty()) {
            throw new ProductoInvalidoException("La etiqueta ecológica no puede estar vacía.");
        }
        this.etiquetaEco = etiquetaEco.trim();
    }

    @Override
    public String getType() {
        return "ECOLOGICO";
    }

    @Override
    public String getSpecificDetail() {
        return getEtiquetaEco();
    }

    @Override
    public void setSpecificDetail(String value) throws ProductoInvalidoException {
        setEtiquetaEco(value);
    }
}