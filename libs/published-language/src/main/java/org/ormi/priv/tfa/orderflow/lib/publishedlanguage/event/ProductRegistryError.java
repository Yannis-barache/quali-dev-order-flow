package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event;

public final class ProductRegistryError extends RuntimeException implements ProductRegistryMessage {
    private String errorcode;
    private String errormessage;

    public ProductRegistryError() {
        this.errorcode = "404";
        this.errormessage ="Product not found in the product registry";
    }

    @Override
    public String toString() {
        return "ProductRegistryError{" +
                "errorcode='" + errorcode + '\'' +
                ", errormessage='" + errormessage + '\'' +
                '}';
    }

    public String getErrorcode() {
        return errorcode;
    }

    public String getMessage() {
        return errormessage;
    }
}

/**
 * ProductRegistryError
 *
 * This class is used to represent the error message that is returned when a product is not found in the product registry.
 */

/*


public class ProductRegistryError extends RuntimeException implements ProductRegistryMessage(String code, String message) {
@Override
public String toString() {
    return "ProductRegistryError{" +
            "errorcode='" + code + '\'' +
            ", errormessage='" + message + '\'' +
            '}';
}
}
*/