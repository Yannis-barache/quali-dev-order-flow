package org.ormi.priv.tfa.orderflow.lib.publishedlanguage.command;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for all product registry commands.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = RegisterProduct.class, name = "RegisterProduct"),
    @JsonSubTypes.Type(value = UpdateProduct.class, name = "UpdateProduct"),
    @JsonSubTypes.Type(value = RemoveProduct.class, name = "RemoveProduct")
})
public sealed interface ProductRegistryCommand extends Serializable permits RegisterProduct, UpdateProduct, RemoveProduct {
}
