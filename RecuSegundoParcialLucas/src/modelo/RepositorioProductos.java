package modelo;

import excepciones.ProductoDuplicadoException;
import excepciones.ProductoInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;

public class RepositorioProductos {

    private final ArrayList<ProductoLimpieza> products;

    public RepositorioProductos() {
        this.products = new ArrayList<ProductoLimpieza>();
    }

    public ArrayList<ProductoLimpieza> getAll() {
        return products;
    }

    public void add(ProductoLimpieza producto) throws ProductoDuplicadoException {
        if (isDuplicate(producto)) {
            throw new ProductoDuplicadoException("Producto repetido: mismo nombre, concentración y fecha.");
        }
        products.add(producto);
    }

    public void update(int index, ProductoLimpieza updatedProduct) throws ProductoInvalidoException, ProductoDuplicadoException {
        if (index < 0 || index >= products.size()) {
            throw new ProductoInvalidoException("Índice fuera de rango.");
        }
        for (int i = 0; i < products.size(); i++) {
            if (i == index) continue;
            ProductoLimpieza other = products.get(i);
            if (equalsByIdentity(updatedProduct, other)) {
                throw new ProductoDuplicadoException("La actualización genera un producto duplicado.");
            }
        }
        products.set(index, updatedProduct);
    }

    public void remove(int index) throws ProductoInvalidoException {
        if (index < 0 || index >= products.size()) {
            throw new ProductoInvalidoException("Índice fuera de rango.");
        }
        products.remove(index);
    }

    public ArrayList<ProductoLimpieza> getExpiringWithinDays(int days) {
        ArrayList<ProductoLimpieza> soon = new ArrayList<ProductoLimpieza>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(days);
        for (int i = 0; i < products.size(); i++) {
            ProductoLimpieza p = products.get(i);
            LocalDate exp = p.getFechaVencimiento();
            if ((exp.isAfter(today) || exp.isEqual(today)) && (exp.isBefore(limit) || exp.isEqual(limit))) {
                soon.add(p);
            }
        }
        return soon;
    }

    private boolean isDuplicate(ProductoLimpieza candidate) {
        for (int i = 0; i < products.size(); i++) {
            if (equalsByIdentity(candidate, products.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsByIdentity(ProductoLimpieza a, ProductoLimpieza b) {
        return a.getNombre().equalsIgnoreCase(b.getNombre())
                && a.getConcentracion().equalsIgnoreCase(b.getConcentracion())
                && a.getFechaVencimiento().isEqual(b.getFechaVencimiento());
    }
}