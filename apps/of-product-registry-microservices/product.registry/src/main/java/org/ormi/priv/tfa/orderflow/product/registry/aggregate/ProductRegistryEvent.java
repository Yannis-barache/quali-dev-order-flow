/**
* Base class for product registry events.
*/
@JsonTypeInfo(
 // the type field is used to determine the concrete type
  use = JsonTypeInfo.Id.NAME,
  // the type field is included in the JSON
  include = JsonTypeInfo.As.PROPERTY,
  // the type field is named "type"
  property = "type"
)
// the concrete types are annotated with @JsonTypeName
@JsonSubTypes({
  @JsonSubTypes.Type(value = ProductRegistered.class, name = "ProductRegistered"),
  @JsonSubTypes.Type(value = ProductUpdated.class, name = "ProductUpdated"),
  @JsonSubTypes.Type(value = ProductRemoved.class, name = "ProductRemoved"),
  @JsonSubTypes.Type(value = ProductRegistryError.class, name = "ProductRegistryError")
})

public sealed interface ProductRegistryEvent permits ProductRegistered, ProductRemoved, ProductUpdated, ProductRegistryError {
}