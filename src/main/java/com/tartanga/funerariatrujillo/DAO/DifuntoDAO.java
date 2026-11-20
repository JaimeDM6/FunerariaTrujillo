package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Difunto;
import com.tartanga.funerariatrujillo.Objects.Feretro;
import com.tartanga.funerariatrujillo.Objects.Urna;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a datos de la colección {@code difunto} de MongoDB. Resuelve también el féretro y la urna
 * asociados a cada difunto y ofrece las listas de clientes y materiales para los formularios.
 *
 * @author Iker
 */
public class DifuntoDAO {

    private static final String COLLECTION_NAME = "difunto";

    /**
     * Obtiene todos los difuntos de la base de datos con su féretro y su urna.
     *
     * @return lista de difuntos; vacía si se produce un error
     */
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

    /**
     * Convierte un documento de MongoDB en un difunto, incluidos su féretro y su urna.
     *
     * @param doc documento del difunto
     * @param feretros documentos de féretros indexados por identificador
     * @param urnas documentos de urnas indexados por identificador
     * @return difunto con los datos del documento
     */
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

    /**
     * Lee un campo identificador de un documento como texto.
     *
     * @param doc documento de MongoDB
     * @param campo nombre del campo
     * @return identificador como texto, o una cadena vacía si no existe
     */
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

    /**
     * Convierte el texto guardado en la base de datos en un tipo de servicio.
     *
     * @param valor texto del servicio
     * @return cremación si el texto es {@code Cremación}; en otro caso, entierro
     */
    private Difunto.Servicio convertirServicio(String valor) {
        if (valor != null && valor.equalsIgnoreCase("Cremación")) {
            return Difunto.Servicio.CREMACIÓN;
        }

        return Difunto.Servicio.ENTIERRO;
    }

    /**
     * Construye el féretro de un difunto a partir de los documentos cargados.
     *
     * @param id identificador del féretro
     * @param documentos documentos de féretros indexados por identificador
     * @return féretro con sus datos, o vacío si no se encuentra
     */
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

    /**
     * Construye la urna de un difunto a partir de los documentos cargados.
     *
     * @param id identificador de la urna
     * @param documentos documentos de urnas indexados por identificador
     * @return urna con sus datos, o vacía si no se encuentra
     */
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

    /**
     * Convierte un valor numérico de MongoDB en un {@link Double}.
     *
     * @param valor valor leído de la base de datos
     * @return valor numérico, o {@code null} si no se puede convertir
     */
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

    /**
     * Carga todos los documentos de una colección indexados por su identificador.
     *
     * @param coleccion nombre de la colección
     * @return mapa de documentos por identificador en texto
     */
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

    /**
     * Obtiene los materiales distintos de los féretros.
     *
     * @return lista de materiales
     */
    public List<String> obtenerMaterialesFeretros() {
        return obtenerMateriales("feretro");
    }

    /**
     * Obtiene los materiales distintos de las urnas.
     *
     * @return lista de materiales
     */
    public List<String> obtenerMaterialesUrnas() {
        return obtenerMateriales("urna");
    }

    /**
     * Obtiene los clientes para elegir el responsable de un difunto.
     *
     * @return lista de textos con el formato {@code identificador - nombre apellidos}
     */
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

    /**
     * Busca el identificador del primer documento de una colección con el material indicado.
     *
     * @param coleccion nombre de la colección
     * @param material material que se busca
     * @return identificador encontrado, o una cadena vacía si no hay coincidencia
     */
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

    /**
     * Obtiene los materiales distintos de una colección.
     *
     * @param coleccion nombre de la colección
     * @return lista de materiales sin repetir
     */
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

    /**
     * Inserta un nuevo difunto.
     *
     * @param difunto difunto que se inserta
     * @return {@code true} si se insertó correctamente
     */
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

    /**
     * Actualiza los datos de un difunto y el material de su féretro y de su urna.
     *
     * @param difunto difunto con los datos nuevos; su identificador indica el documento que se
     *         modifica
     * @return {@code true} si se actualizó correctamente
     */
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

    /**
     * Actualiza el material del féretro y de la urna asociados a un difunto.
     *
     * @param difunto difunto cuyos materiales se actualizan
     */
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

    /**
     * Elimina un difunto.
     *
     * @param id identificador del difunto
     * @return {@code true} si se eliminó algún documento
     */
    public boolean eliminarDifunto(String id) {
        boolean eliminado = false;

        try {
            if (esObjectIdValido(id)) {
                ObjectId idDifunto = new ObjectId(id);

                long eliminados = MongoConfig.getCollection(COLLECTION_NAME)
                        .deleteOne(new Document("_id", idDifunto))
                        .getDeletedCount();

                if (eliminados > 0) {
                    MongoConfig.getCollection("asignacion")
                            .deleteMany(new Document("id_difunto", idDifunto));
                    eliminado = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el difunto: " + e.getMessage());
            e.printStackTrace();
        }

        return eliminado;
    }

    /**
     * Comprueba si ya existe un difunto con el mismo nombre y apellidos (sin distinguir
     * mayúsculas) y las mismas fechas de nacimiento y fallecimiento.
     *
     * @param nombre nombre del difunto
     * @param apellidos apellidos del difunto
     * @param nacimiento fecha de nacimiento
     * @param muerte fecha de fallecimiento
     * @param idExcluido identificador del difunto que se ignora en la comprobación, o {@code null}
     * si se trata de un alta
     * @return {@code true} si existe otro difunto con esos datos
     */
    public boolean existeDifunto(String nombre, String apellidos, Date nacimiento, Date muerte, String idExcluido) {
        boolean existe = false;

        try {
            Document filtro = new Document("fecha_nacimiento", nacimiento)
                    .append("fecha_muerte", muerte);

            for (Document documento : MongoConfig.getCollection(COLLECTION_NAME).find(filtro)) {
                String nombreDocumento = documento.getString("nombre");
                String apellidosDocumento = documento.getString("apellidos");
                ObjectId idDocumento = documento.getObjectId("_id");

                boolean mismoNombre = nombreDocumento != null
                        && nombreDocumento.trim().equalsIgnoreCase(nombre.trim());
                boolean mismosApellidos = apellidosDocumento != null
                        && apellidosDocumento.trim().equalsIgnoreCase(apellidos.trim());
                boolean esOtroDifunto = idDocumento == null || !idDocumento.toString().equals(idExcluido);

                if (mismoNombre && mismosApellidos && esOtroDifunto) {
                    existe = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al comprobar si el difunto ya existe: " + e.getMessage());
        }

        return existe;
    }

    /**
     * Obtiene el texto con el que se guarda un tipo de servicio en la base de datos.
     *
     * @param servicio tipo de servicio
     * @return {@code CREMACIÓN} o {@code ENTIERRO}
     */
    private String obtenerNombreServicio(Difunto.Servicio servicio) {
        if (servicio == Difunto.Servicio.CREMACIÓN) {
            return "CREMACIÓN";
        }

        return "ENTIERRO";
    }

    /**
     * Comprueba que un texto sea un identificador válido de MongoDB.
     *
     * @param id texto que se comprueba
     * @return {@code true} si es un identificador válido
     */
    private boolean esObjectIdValido(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        return ObjectId.isValid(id);
    }
}
