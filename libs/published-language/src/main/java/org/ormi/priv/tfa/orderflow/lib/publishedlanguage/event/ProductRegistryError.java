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