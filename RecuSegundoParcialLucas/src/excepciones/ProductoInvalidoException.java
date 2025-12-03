package excepciones;

public class ProductoInvalidoException extends Exception {
    public ProductoInvalidoException(String message) {
        super(message);
    }
}