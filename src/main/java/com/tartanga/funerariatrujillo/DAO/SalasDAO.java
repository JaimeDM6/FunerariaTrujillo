package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.tartanga.funerariatrujillo.Objects.Sala;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class SalasDAO {

    public List<Sala> obtenerSalas() {
        List<Sala> resultado = new ArrayList<>();
        MongoCollection<Document> collection = MongoConfig.getCollection("salas");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Sala objeto = new Sala();
                objeto.setId(doc.getObjectId("_id").toHexString());
                objeto.setNombre(doc.getString("nombre"));
                objeto.setDescripcion(doc.getString("descripcion"));
                objeto.setCapacidad(doc.getInteger("capacidad", 0));
                resultado.add(objeto);
            }
        } catch (Exception e) {
            // El controlador mostrará el error en lugar de una tabla vacía.
            throw new RuntimeException("No se pudieron cargar los datos de salas", e);
        }
        return resultado;
    }

    private Document crearDocumento(Sala objeto) {
        return new Document()
                .append("nombre", objeto.getNombre())
                .append("descripcion", objeto.getDescripcion())
                .append("capacidad", objeto.getCapacidad());
    }

    public boolean insertarSala(Sala objeto) {
        try {
            Document doc = crearDocumento(objeto);
            MongoConfig.getCollection("salas").insertOne(doc);
            ObjectId id = doc.getObjectId("_id");
            if (id == null) return false;
            objeto.setId(id.toHexString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarSala(Sala objeto) {
        try {
            if (objeto.getId() == null || !ObjectId.isValid(objeto.getId())) return false;
            Document filtro = new Document("_id", new ObjectId(objeto.getId()));
            Document cambios = new Document("$set", crearDocumento(objeto));
            return MongoConfig.getCollection("salas")
                    .updateOne(filtro, cambios).getMatchedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarSala(String id) {
        try {
            if (id == null || !ObjectId.isValid(id)) return false;
            return MongoConfig.getCollection("salas")
                    .deleteOne(new Document("_id", new ObjectId(id)))
                    .getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
