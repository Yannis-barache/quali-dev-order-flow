package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for product registry events.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProductRegisteryError.class, name = "ProductRegisteryError"),
    @JsonSubTypes.Type(value = ProductRegistryEvent.class, name = "ProductRegistryEvent"),
})


public sealed interface ProductRegistryMessage permits ProductRegisteryError, ProductRegistryError,
    ProductRegistryEvent {
}
