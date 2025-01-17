/**
 * ProductRegistryError
 * 
 * This class is used to represent the error message that is returned when a product is not found in the product registry.
 */
public record ProductRegistryError implements ProductRegistryMessage (String code, String message) {
    @Override
    public String toString() {
        return "ProductRegistryError{" +
                "errorcode='" + code + '\'' +
                ", errormessage='" + message + '\'' +
                '}';
    }
}