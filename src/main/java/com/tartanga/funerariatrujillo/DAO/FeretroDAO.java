package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class FeretroDAO {

    public List<Feretro> obtenerFeretros() {
        List<Feretro> resultado = new ArrayList<>();
        MongoCollection<Document> collection = MongoConfig.getCollection("feretros");
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
            // El controlador mostrará el error en lugar de una tabla vacía.
            throw new RuntimeException("No se pudieron cargar los datos de feretros", e);
        }
        return resultado;
    }

    private Document crearDocumento(Feretro objeto) {
        return new Document()
                .append("medidas", objeto.getMedidas())
                .append("material", objeto.getMaterial())
                .append("peso", objeto.getPeso())
                .append("precio", objeto.getPrecio());
    }

    public boolean insertarFeretro(Feretro objeto) {
        try {
            Document doc = crearDocumento(objeto);
            MongoConfig.getCollection("feretros").insertOne(doc);
            ObjectId id = doc.getObjectId("_id");
            if (id == null) return false;
            objeto.setId(id.toHexString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarFeretro(Feretro objeto) {
        try {
            if (objeto.getId() == null || !ObjectId.isValid(objeto.getId())) return false;
            Document filtro = new Document("_id", new ObjectId(objeto.getId()));
            Document cambios = new Document("$set", crearDocumento(objeto));
            return MongoConfig.getCollection("feretros")
                    .updateOne(filtro, cambios).getMatchedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarFeretro(String id) {
        try {
            if (id == null || !ObjectId.isValid(id)) return false;
            return MongoConfig.getCollection("feretros")
                    .deleteOne(new Document("_id", new ObjectId(id)))
                    .getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private double obtenerNumero(Document doc, String campo) {
        Object valor = doc.get(campo);
        if (valor instanceof Number) return ((Number) valor).doubleValue();
        if (valor instanceof org.bson.types.Decimal128) {
            return ((org.bson.types.Decimal128) valor).doubleValue();
        }
        if (valor == null) return 0.0;
        throw new IllegalArgumentException("El campo " + campo + " no es numérico");
    }
}
