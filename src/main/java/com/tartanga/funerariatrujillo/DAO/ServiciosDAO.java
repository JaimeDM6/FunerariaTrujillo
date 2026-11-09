package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Servicios;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ServiciosDAO {

    public List<Servicios> obtenerServicios() {
        List<Servicios> servicios = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("servicios");

            for (Document doc : collection.find()) {
                try {
                    Servicios servicio = new Servicios();
                    servicio.setId(doc.getObjectId("_id").toString());
                    servicio.setNombre(doc.getString("nombre"));
                    servicio.setDescripcion(doc.getString("descripcion"));
                    servicio.setPrecio(obtenerPrecio(doc));
                    servicios.add(servicio);
                } catch (Exception docEx) {
                    System.err.println("Error procesando el servicio con ID: " + doc.getObjectId("_id"));
                    docEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los servicios: " + e.getMessage());
            e.printStackTrace();
        }

        return servicios;
    }

    private Double obtenerPrecio(Document doc) {
        Object precioObject = doc.get("precio");

        if (precioObject instanceof Number) {
            Number precioNumber = (Number) precioObject;
            return precioNumber.doubleValue();
        }

        if (precioObject instanceof org.bson.types.Decimal128) {
            org.bson.types.Decimal128 precioDecimal = (org.bson.types.Decimal128) precioObject;
            return precioDecimal.doubleValue();
        }

        return 0.0;
    }

    public boolean actualizarServicio(Servicios servicio) {
        try {
            if (!ObjectId.isValid(servicio.getId())) {
                return false;
            }

            MongoCollection<Document> collection = MongoConfig.getCollection("servicios");
            Document filtro = new Document("_id", new ObjectId(servicio.getId()));
            Document cambios = new Document()
                    .append("nombre", servicio.getNombre())
                    .append("descripcion", servicio.getDescripcion())
                    .append("precio", servicio.getPrecio());

            long encontrados = collection.updateOne(filtro, new Document("$set", cambios)).getMatchedCount();
            return encontrados > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar el servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarServicio(Servicios servicio) {
        try {
            Document documento = new Document()
                    .append("nombre", servicio.getNombre())
                    .append("descripcion", servicio.getDescripcion())
                    .append("precio", servicio.getPrecio());

            MongoConfig.getCollection("servicios").insertOne(documento);
            ObjectId id = documento.getObjectId("_id");

            if (id == null) {
                return false;
            }

            servicio.setId(id.toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar el servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarServicio(String id) {
        try {
            if (!ObjectId.isValid(id)) {
                return false;
            }

            long eliminados = MongoConfig.getCollection("servicios")
                    .deleteOne(new Document("_id", new ObjectId(id)))
                    .getDeletedCount();
            return eliminados > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar el servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
