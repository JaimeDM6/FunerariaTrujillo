package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la colección {@code feretro} de MongoDB.
 *
 * @author Aritz
 */
public class FeretroDAO {

    private static final String COLLECTION_NAME = "feretro";

    /**
     * Obtiene todos los féretros de la base de datos.
     *
     * @return lista de féretros; vacía si se produce un error
     */
    public List<Feretro> obtenerFeretros() {
        List<Feretro> resultado = new ArrayList<>();
        MongoCollection<Document> collection = MongoConfig.getCollection(COLLECTION_NAME);

        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Feretro objeto = new Feretro();
                objeto.setId(doc.getObjectId("_id").toHexString());
                objeto.setMedidas(doc.getString("medidas"));
                objeto.setMaterial(doc.getString("material"));
                objeto.setPeso(obtenerNumero(doc, "peso"));
                objeto.setPrecio(obtenerNumero(doc, "precio"));
                resultado.add(objeto);
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los datos de feretros", e);
        }

        return resultado;
    }

    /**
     * Convierte un féretro en el documento que se guarda en MongoDB.
     *
     * @param objeto féretro que se convierte
     * @return documento con los datos del féretro
     */
    private Document crearDocumento(Feretro objeto) {
        return new Document()
                .append("medidas", objeto.getMedidas())
                .append("material", objeto.getMaterial())
                .append("peso", objeto.getPeso())
                .append("precio", objeto.getPrecio());
    }

    /**
     * Inserta un nuevo féretro.
     *
     * @param objeto féretro que se inserta
     * @return {@code true} si se insertó correctamente
     */
    public boolean insertarFeretro(Feretro objeto) {
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

    /**
     * Actualiza los datos de un féretro.
     *
     * @param objeto féretro con los datos nuevos; su identificador indica el documento que se modifica
     * @return {@code true} si se actualizó correctamente
     */
    public boolean actualizarFeretro(Feretro objeto) {
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

    /**
     * Elimina un féretro.
     *
     * @param id identificador del féretro
     * @return {@code true} si se eliminó correctamente
     */
    public boolean eliminarFeretro(String id) {
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

    /**
     * Lee un campo numérico de un documento, sea cual sea el tipo numérico con el que se guardó.
     *
     * @param doc documento de MongoDB
     * @param campo nombre del campo
     * @return valor del campo, o 0 si no existe o no es numérico
     */
    private double obtenerNumero(Document doc, String campo) {
        Object valor = doc.get(campo);
        double resultado = 0.0;

        if (valor instanceof Number) {
            resultado = ((Number) valor).doubleValue();
        } else if (valor instanceof org.bson.types.Decimal128) {
            resultado = ((org.bson.types.Decimal128) valor).doubleValue();
        } else if (valor != null) {
            throw new IllegalArgumentException("El campo " + campo + " no es numérico");
        }

        return resultado;
    }
}