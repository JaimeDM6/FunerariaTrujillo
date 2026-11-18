package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.tartanga.funerariatrujillo.Objects.Sala;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class SalasDAO {

    private static final String COLLECTION_NAME = "sala";

    public List<Sala> obtenerSalas() {
        List<Sala> resultado = new ArrayList<>();
        MongoCollection<Document> collection = MongoConfig.getCollection(COLLECTION_NAME);

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Sala objeto = new Sala();
                objeto.setId(doc.getObjectId("_id").toHexString());
                objeto.setNombre(doc.getString("nombre"));
                objeto.setDescripcion(doc.getString("descripcion"));
                objeto.setCapacidad(obtenerCapacidad(doc));
                resultado.add(objeto);
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los datos de salas", e);
        }

        return resultado;
    }

    private int obtenerCapacidad(Document doc) {
        Object valor = doc.get("capacidad");
        int resultado = 0;

        if (valor instanceof Number) {
            resultado = ((Number) valor).intValue();
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
        boolean insertado = false;

        try {
            Document doc = crearDocumento(objeto);
            MongoConfig.getCollection(COLLECTION_NAME).insertOne(doc);
            ObjectId id = doc.getObjectId("_id");

            if (id != null) {
                objeto.setId(id.toHexString());
                insertado = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insertado;
    }

    public boolean actualizarSala(Sala objeto) {
        boolean actualizado = false;

        try {
            if (objeto.getId() != null && ObjectId.isValid(objeto.getId())) {
                Document filtro = new Document("_id", new ObjectId(objeto.getId()));
                Document cambios = new Document("$set", crearDocumento(objeto));
                actualizado = MongoConfig.getCollection(COLLECTION_NAME)
                        .updateOne(filtro, cambios).getMatchedCount() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actualizado;
    }

    public boolean eliminarSala(String id) {
        boolean eliminado = false;

        try {
            if (id != null && ObjectId.isValid(id)) {
                eliminado = MongoConfig.getCollection(COLLECTION_NAME)
                        .deleteOne(new Document("_id", new ObjectId(id)))
                        .getDeletedCount() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eliminado;
    }
}