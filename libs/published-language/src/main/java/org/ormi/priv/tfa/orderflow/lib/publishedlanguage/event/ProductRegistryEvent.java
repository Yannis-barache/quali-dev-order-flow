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
    @JsonSubTypes.Type(value = ProductRegistered.class, name = "ProductRegistered"),
    @JsonSubTypes.Type(value = ProductUpdated.class, name = "ProductUpdated"),
    @JsonSubTypes.Type(value = ProductRemoved.class, name = "ProductRemoved")
})
public sealed interface ProductRegistryEvent extends ProductRegistryMessage permits ProductRegistered, ProductRemoved, ProductUpdated {
}
