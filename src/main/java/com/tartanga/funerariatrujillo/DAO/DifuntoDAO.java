package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Difunto;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import com.tartanga.funerariatrujillo.Objects.Urna;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifuntoDAO {

    private static final String COLLECTION_NAME = "difunto";

    public List<Difunto> obtenerDifuntos() {
        List<Difunto> difuntos = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection(COLLECTION_NAME);
            Map<String, Document> feretros = cargarDocumentosPorId("feretro");
            Map<String, Document> urnas = cargarDocumentosPorId("urna");

            for (Document doc : collection.find()) {
                try {
                    Difunto difunto = convertirDocumento(doc, feretros, urnas);
                    difuntos.add(difunto);
                } catch (Exception docEx) {
                    System.err.println("Error procesando el documento de difunto: " + doc);
                    docEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los difuntos: " + e.getMessage());
            e.printStackTrace();
        }

        return difuntos;
    }

    private Difunto convertirDocumento(Document doc, Map<String, Document> feretros, Map<String, Document> urnas) {
        Difunto difunto = new Difunto();

        difunto.setId(doc.getObjectId("_id").toString());
        difunto.setId_cliente(obtenerIdComoTexto(doc, "id_cliente"));
        difunto.setId_feretro(obtenerIdComoTexto(doc, "id_feretro"));
        difunto.setId_urna(obtenerIdComoTexto(doc, "id_urna"));
        difunto.setNombre(doc.getString("nombre"));
        difunto.setApellidos(doc.getString("apellidos"));
        difunto.setFecha_nacimiento(doc.getDate("fecha_nacimiento"));
        difunto.setFecha_muerte(doc.getDate("fecha_muerte"));

        String causa = doc.getString("causa_de_muerte");
        if (causa == null) {
            causa = doc.getString("causa_muerte");
        }
        difunto.setCausa_muerte(causa);
        difunto.setServicio(convertirServicio(doc.getString("servicio")));
        difunto.setFeretro(obtenerFeretro(difunto.getId_feretro(), feretros));
        difunto.setUrna(obtenerUrna(difunto.getId_urna(), urnas));

        return difunto;
    }

    private String obtenerIdComoTexto(Document doc, String campo) {
        Object valor = doc.get(campo);

        if (valor instanceof ObjectId) {
            ObjectId objectId = (ObjectId) valor;
            return objectId.toString();
        }

        if (valor != null) {
            return valor.toString();
        }

        return "";
    }

    private Difunto.Servicio convertirServicio(String valor) {
        if (valor != null && valor.equalsIgnoreCase("Cremación")) {
            return Difunto.Servicio.CREMACIÓN;
        }

        return Difunto.Servicio.ENTIERRO;
    }

    private Feretro obtenerFeretro(String id, Map<String, Document> documentos) {
        Feretro feretro = new Feretro();

        if (id == null || id.isEmpty()) {
            return feretro;
        }

        Document doc = documentos.get(id);
        if (doc != null) {
            feretro.setMedidas(doc.getString("medidas"));
            feretro.setMaterial(doc.getString("material"));
            feretro.setPeso(obtenerDouble(doc.get("peso")));
            feretro.setPrecio(obtenerDouble(doc.get("precio")));
        }

        return feretro;
    }

    private Urna obtenerUrna(String id, Map<String, Document> documentos) {
        Urna urna = new Urna();

        if (id == null || id.isEmpty()) {
            return urna;
        }

        Document doc = documentos.get(id);
        if (doc != null) {
            urna.setId(id);
            urna.setCapacidad(obtenerDouble(doc.get("capacidad")));
            urna.setMaterial(doc.getString("material"));
            urna.setPrecio(obtenerDouble(doc.get("precio")));
        }

        return urna;
    }

    private Double obtenerDouble(Object valor) {
        if (valor instanceof Number) {
            Number numero = (Number) valor;
            return numero.doubleValue();
        }

        if (valor instanceof org.bson.types.Decimal128) {
            org.bson.types.Decimal128 decimal = (org.bson.types.Decimal128) valor;
            return decimal.doubleValue();
        }

        return 0.0;
    }

    private Map<String, Document> cargarDocumentosPorId(String coleccion) {
        Map<String, Document> documentos = new HashMap<>();

        for (Document documento : MongoConfig.getCollection(coleccion).find()) {
            ObjectId id = documento.getObjectId("_id");
            if (id != null) {
                documentos.put(id.toString(), documento);
            }
        }

        return documentos;
    }

    public List<String> obtenerMaterialesFeretros() {
        return obtenerMateriales("feretro");
    }

    public List<String> obtenerMaterialesUrnas() {
        return obtenerMateriales("urna");
    }

    public List<String> obtenerClientes() {
        List<String> clientes = new ArrayList<>();

        try {
            for (Document documento : MongoConfig.getCollection("cliente").find()) {
                ObjectId id = documento.getObjectId("_id");
                String nombre = documento.getString("nombre");
                String apellidos = documento.getString("apellidos");

                if (id != null) {
                    clientes.add(id.toString() + " - " + nombre + " " + apellidos);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }

        return clientes;
    }

    public String obtenerIdPorMaterial(String coleccion, String material) {
        if (material == null || material.isEmpty()) {
            return "";
        }

        for (Document documento : MongoConfig.getCollection(coleccion)
                .find(new Document("material", material))) {
            ObjectId id = documento.getObjectId("_id");
            if (id != null) {
                return id.toString();
            }
        }

        return "";
    }

    private List<String> obtenerMateriales(String coleccion) {
        List<String> materiales = new ArrayList<>();

        try {
            for (Document documento : MongoConfig.getCollection(coleccion).find()) {
                String material = documento.getString("material");
                if (material != null && !materiales.contains(material)) {
                    materiales.add(material);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener materiales de " + coleccion + ": " + e.getMessage());
        }

        return materiales;
    }

    public boolean insertarDifunto(Difunto difunto) {
        try {
            if (!esObjectIdValido(difunto.getId_cliente())
                    || !esObjectIdValido(difunto.getId_feretro())) {
                System.err.println("No se puede insertar el difunto: faltan id_cliente o id_feretro válidos.");
                return false;
            }

            Document documento = new Document()
                    .append("id_cliente", new ObjectId(difunto.getId_cliente()))
                    .append("nombre", difunto.getNombre())
                    .append("apellidos", difunto.getApellidos())
                    .append("fecha_nacimiento", difunto.getFecha_nacimiento())
                    .append("fecha_muerte", difunto.getFecha_muerte())
                    .append("causa_de_muerte", difunto.getCausa_muerte())
                    .append("servicio", obtenerNombreServicio(difunto.getServicio()))
                    .append("id_feretro", new ObjectId(difunto.getId_feretro()));

            if (esObjectIdValido(difunto.getId_urna())) {
                documento.append("id_urna", new ObjectId(difunto.getId_urna()));
            }

            MongoConfig.getCollection(COLLECTION_NAME).insertOne(documento);
            difunto.setId(documento.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar el difunto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarDifunto(Difunto difunto) {
        try {
            if (!esObjectIdValido(difunto.getId())) {
                System.err.println("No se puede actualizar el difunto: el id no es válido.");
                return false;
            }

            Document cambios = new Document()
                    .append("nombre", difunto.getNombre())
                    .append("apellidos", difunto.getApellidos())
                    .append("fecha_nacimiento", difunto.getFecha_nacimiento())
                    .append("fecha_muerte", difunto.getFecha_muerte())
                    .append("causa_de_muerte", difunto.getCausa_muerte())
                    .append("servicio", obtenerNombreServicio(difunto.getServicio()));

            if (esObjectIdValido(difunto.getId_cliente())) {
                cambios.append("id_cliente", new ObjectId(difunto.getId_cliente()));
            }

            if (esObjectIdValido(difunto.getId_feretro())) {
                cambios.append("id_feretro", new ObjectId(difunto.getId_feretro()));
            }

            if (esObjectIdValido(difunto.getId_urna())) {
                cambios.append("id_urna", new ObjectId(difunto.getId_urna()));
            }

            long modificados = MongoConfig.getCollection(COLLECTION_NAME)
                    .updateOne(new Document("_id", new ObjectId(difunto.getId())), new Document("$set", cambios))
                    .getMatchedCount();

            actualizarMaterialRelacionado(difunto);
            return modificados > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar el difunto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarMaterialRelacionado(Difunto difunto) {
        if (esObjectIdValido(difunto.getId_feretro()) && difunto.getFeretro() != null) {
            Document cambiosFeretro = new Document()
                    .append("material", difunto.getFeretro().getMaterial());
            MongoConfig.getCollection("feretro").updateOne(
                    new Document("_id", new ObjectId(difunto.getId_feretro())),
                    new Document("$set", cambiosFeretro));
        }

        if (esObjectIdValido(difunto.getId_urna()) && difunto.getUrna() != null) {
            Document cambiosUrna = new Document()
                    .append("material", difunto.getUrna().getMaterial());
            MongoConfig.getCollection("urna").updateOne(
                    new Document("_id", new ObjectId(difunto.getId_urna())),
                    new Document("$set", cambiosUrna));
        }
    }

    public boolean eliminarDifunto(String id) {
        try {
            if (!esObjectIdValido(id)) {
                return false;
            }

            long eliminados = MongoConfig.getCollection(COLLECTION_NAME)
                    .deleteOne(new Document("_id", new ObjectId(id)))
                    .getDeletedCount();
            return eliminados > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar el difunto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String obtenerNombreServicio(Difunto.Servicio servicio) {
        if (servicio == Difunto.Servicio.CREMACIÓN) {
            return "CREMACIÓN";
        }

        return "ENTIERRO";
    }

    private boolean esObjectIdValido(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        return ObjectId.isValid(id);
    }
}
