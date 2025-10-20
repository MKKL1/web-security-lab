package zielonka.chmury.products.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String s) {
        super(s);
    }
}
