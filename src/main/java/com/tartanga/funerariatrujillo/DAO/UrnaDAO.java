package com.tartanga.funerariatrujillo.DAO;

import com.tartanga.funerariatrujillo.Objects.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UrnaDAO {

    public List<Urna> obtenerUrnas() {
        List<Urna> urnas = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("urna");

            for (Document doc : collection.find()) {
                try {
                    Urna u = new Urna();

                    u.setId(doc.getObjectId("_id").toString());
                    u.setMaterial(doc.getString("material"));

                    Object capObj = doc.get("capacidad");
                    if (capObj instanceof Number number) {
                        u.setCapacidad(number.doubleValue());
                    } else if (capObj instanceof org.bson.types.Decimal128 decimal128) {
                        u.setCapacidad(decimal128.doubleValue());
                    } else {
                        u.setCapacidad(0.0);
                    }

                    Object precioObj = doc.get("precio");
                    if (precioObj instanceof Number number) {
                        u.setPrecio(number.doubleValue());
                    } else if (precioObj instanceof org.bson.types.Decimal128 decimal128) {
                        u.setPrecio(decimal128.doubleValue());
                    } else {
                        u.setPrecio(0.0);
                    }

                    urnas.add(u);
                } catch (Exception docEx) {
                    System.err.println("Error procesando la urna con ID: " + doc.getObjectId("_id"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener las urnas: " + e.getMessage());
        }

        return urnas;
    }

    public boolean actualizarUrna(Urna u) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("urna");

            Document filtro = new Document("_id", new org.bson.types.ObjectId(u.getId()));

            Document updateDocs = new Document("$set", new Document()
                    .append("capacidad", u.getCapacidad())
                    .append("material", u.getMaterial())
                    .append("precio", u.getPrecio())
            );

            collection.updateOne(filtro, updateDocs);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar la urna: " + e.getMessage());
            return false;
        }
    }

    public boolean insertarUrna(Urna u) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("urna");

            Document doc = new Document()
                    .append("capacidad", u.getCapacidad())
                    .append("material", u.getMaterial())
                    .append("precio", u.getPrecio());

            collection.insertOne(doc);

            u.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar la urna: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUrna(String id) {
        try {
            Document filtro = new Document("_id", new org.bson.types.ObjectId(id));
            MongoConfig.getCollection("urna").deleteOne(filtro);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar la urna: " + e.getMessage());
            return false;
        }
    }
}
