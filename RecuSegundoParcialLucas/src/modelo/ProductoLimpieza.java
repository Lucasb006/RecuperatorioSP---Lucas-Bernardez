package modelo;

import excepciones.ProductoInvalidoException;
import excepciones.ProductoVencidoException;

import java.time.LocalDate;

public abstract class ProductoLimpieza {

    private String nombre;
    private String concentracion;
    private LocalDate fechaVencimiento;

    protected ProductoLimpieza(String nombre, String concentracion, LocalDate fechaVencimiento)
            throws ProductoInvalidoException, ProductoVencidoException {
        setNombre(nombre);
        setConcentracion(concentracion);
        setFechaVencimiento(fechaVencimiento);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws ProductoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ProductoInvalidoException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) throws ProductoInvalidoException {
        if (concentracion == null || concentracion.trim().isEmpty()) {
            throw new ProductoInvalidoException("La concentración no puede estar vacía.");
        }
        this.concentracion = concentracion.trim();
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) throws ProductoInvalidoException, ProductoVencidoException {
        if (fechaVencimiento == null) {
            throw new ProductoInvalidoException("La fecha de vencimiento es requerida.");
        }
        LocalDate today = LocalDate.now();
        if (!fechaVencimiento.isAfter(today)) {
            throw new ProductoVencidoException("No se puede cargar un producto ya vencido.");
        }
        this.fechaVencimiento = fechaVencimiento;
    }

    public abstract String getType();

    public abstract String getSpecificDetail();

    public abstract void setSpecificDetail(String value) throws ProductoInvalidoException;
}