package org.ormi.priv.tfa.orderflow.product.registry.aggregate;
// This class is used to return error messages to the client
public class ProductRegistryError {

    // The error message
    private final String message;

    // Constructor
    public ProductRegistryError(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}