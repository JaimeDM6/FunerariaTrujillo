package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.tartanga.funerariatrujillo.Objects.Sala;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la colección {@code sala} de MongoDB.
 *
 * @author Aritz
 */
public class SalasDAO {

    private static final String COLLECTION_NAME = "sala";

    /**
     * Obtiene todas las salas de la base de datos.
     *
     * @return lista de salas; vacía si se produce un error
     */
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

    /**
     * Lee la capacidad de una sala de su documento.
     *
     * @param doc documento de la sala
     * @return capacidad de la sala, o 0 si no existe o no es numérica
     */
    private int obtenerCapacidad(Document doc) {
        Object valor = doc.get("capacidad");
        int resultado = 0;

        if (valor instanceof Number) {
            resultado = ((Number) valor).intValue();
        }

        return resultado;
    }

    /**
     * Convierte una sala en el documento que se guarda en MongoDB.
     *
     * @param objeto sala que se convierte
     * @return documento con los datos de la sala
     */
    private Document crearDocumento(Sala objeto) {
        return new Document()
                .append("nombre", objeto.getNombre())
                .append("descripcion", objeto.getDescripcion())
                .append("capacidad", objeto.getCapacidad());
    }

    /**
     * Inserta una nueva sala.
     *
     * @param objeto sala que se inserta
     * @return {@code true} si se insertó correctamente
     */
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

    /**
     * Actualiza los datos de una sala.
     *
     * @param objeto sala con los datos nuevos; su identificador indica el documento que se modifica
     * @return {@code true} si se actualizó correctamente
     */
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

    /**
     * Elimina una sala.
     *
     * @param id identificador de la sala
     * @return {@code true} si se eliminó correctamente
     */
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

    /**
     * Comprueba si ya existe otra sala con el mismo nombre, sin distinguir mayúsculas.
     *
     * @param nombre nombre que se comprueba
     * @param idExcluido identificador del registro que se ignora en la comprobación, o {@code null} si se trata de un alta
     * @return {@code true} si existe otra sala con ese nombre
     */
    public boolean existeSala(String nombre, String idExcluido) {
        boolean existe = false;

        try {
            for (Document documento : MongoConfig.getCollection(COLLECTION_NAME).find()) {
                String idDocumento = "";
                if (documento.getObjectId("_id") != null) {
                    idDocumento = documento.getObjectId("_id").toString();
                }
                boolean mismoNombre = documento.getString("nombre") != null
                        && documento.getString("nombre").trim().equalsIgnoreCase(nombre.trim());
                boolean esOtro = !idDocumento.equals(idExcluido);

                if (mismoNombre && esOtro) {
                    existe = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al comprobar duplicados: " + e.getMessage());
        }

        return existe;
    }
}