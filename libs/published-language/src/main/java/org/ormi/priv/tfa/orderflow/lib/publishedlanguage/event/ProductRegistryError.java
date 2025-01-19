
package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event;
/**
 * ProductRegistryError
 * 
 * This class is used to represent the error message that is returned when a product is not found in the product registry.
 */
public class ProductRegistryError extends RuntimeException implements ProductRegistryMessage(String code, String message) {
    @Override
    public String toString() {
        return "ProductRegistryError{" +
                "errorcode='" + code + '\'' +
                ", errormessage='" + message + '\'' +
                '}';
    }
}