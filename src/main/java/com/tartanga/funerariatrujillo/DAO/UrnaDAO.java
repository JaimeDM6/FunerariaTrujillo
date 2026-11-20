package com.tartanga.funerariatrujillo.DAO;

import com.tartanga.funerariatrujillo.Objects.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la colección {@code urna} de MongoDB.
 *
 * @author Jaime
 */
public class UrnaDAO {

    /**
     * Obtiene todas las urnas de la base de datos.
     *
     * @return lista de urnas; vacía si se produce un error
     */
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

    /**
     * Actualiza los datos de una urna.
     *
     * @param u urna con los datos nuevos; su identificador indica el documento que se modifica
     * @return {@code true} si se actualizó correctamente
     */
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

    /**
     * Inserta una nueva urna.
     *
     * @param u urna que se inserta
     * @return {@code true} si se insertó correctamente
     */
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

    /**
     * Elimina una urna.
     *
     * @param id identificador de la urna
     * @return {@code true} si se eliminó correctamente
     */
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

    /**
     * Comprueba si ya existe otra urna con la misma capacidad y el mismo material, sin distinguir mayúsculas.
     *
     * @param capacidad capacidad en litros
     * @param material material de la urna
     * @param idExcluido identificador del registro que se ignora en la comprobación, o {@code null} si se trata de un alta
     * @return {@code true} si existe otra urna con esos datos
     */
    public boolean existeUrna(double capacidad, String material, String idExcluido) {
        boolean existe = false;

        try {
            for (Document documento : MongoConfig.getCollection("urna").find()) {
                String idDocumento = "";
                if (documento.getObjectId("_id") != null) {
                    idDocumento = documento.getObjectId("_id").toString();
                }
                Object capacidadDocumento = documento.get("capacidad");
                boolean mismaCapacidad = capacidadDocumento instanceof Number
                        && ((Number) capacidadDocumento).doubleValue() == capacidad;
                boolean mismoMaterial = documento.getString("material") != null
                        && documento.getString("material").trim().equalsIgnoreCase(material.trim());
                boolean esOtro = !idDocumento.equals(idExcluido);

                if (mismaCapacidad && mismoMaterial && esOtro) {
                    existe = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al comprobar duplicados: " + e.getMessage());
        }

        return existe;
    }
}
