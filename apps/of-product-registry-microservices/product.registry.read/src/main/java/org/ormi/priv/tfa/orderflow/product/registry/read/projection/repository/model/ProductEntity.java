package org.ormi.priv.tfa.orderflow.product.registry.read.projection.repository.model;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "products")
public class ProductEntity {
  public ObjectId id;
  public String productId;
  public String name;
  public String description;
  public long version;
  public long updatedAt;
  public long registeredAt;
}
