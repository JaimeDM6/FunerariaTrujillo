package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Asignacion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import com.tartanga.funerariatrujillo.Security.UserSession;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsignacionesDAO {

    private static final String COLLECTION_NAME = "asignacion";
    private static final String CARGO_ASIGNABLE = "TANATOPRACTOR";

    private final String usuarioId;
    private final Trabajador.Rol rol;

    public AsignacionesDAO() {
        this(UserSession.getCurrentUser());
    }

    public AsignacionesDAO(Trabajador usuario) {
        String id = null;
        Trabajador.Rol rolUsuario = null;

        if (usuario != null) {
            id = usuario.getId();
            rolUsuario = usuario.getRol();
        }

        this.usuarioId = id;
        this.rol = rolUsuario;
    }

    public boolean puedeGestionar() {
        return rol == Trabajador.Rol.ADMINISTRACION || rol == Trabajador.Rol.AGENTE_FUNERARIO;
    }

    public boolean puedeConsultar() {
        return puedeGestionar()
                || (rol == Trabajador.Rol.TANATOPRAXIA && esIdValido(usuarioId));
    }

    private boolean esIdValido(String id) {
        return id != null && ObjectId.isValid(id);
    }

    private boolean esEstadoValido(String estado) {
        return "Pendiente".equals(estado) || "En curso".equals(estado) || "Completado".equals(estado);
    }

    private String textoSinNulos(String texto) {
        String resultado = "";
        if (texto != null) {
            resultado = texto;
        }
        return resultado;
    }

    private MongoCollection<Document> coleccion() {
        return MongoConfig.getCollection(COLLECTION_NAME);
    }

    private Document filtroAcceso() {
        Document filtro = new Document();
        if (!puedeGestionar()) {
            filtro.append("id_trabajador", new ObjectId(usuarioId));
        }
        return filtro;
    }

    public List<String> obtenerDifuntos() {
        return obtenerReferencias("difunto", new Document());
    }

    public List<String> obtenerTrabajadores() {
        return obtenerReferencias("trabajador", new Document("cargo", CARGO_ASIGNABLE));
    }

    private List<String> obtenerReferencias(String coleccion, Document filtro) {
        List<String> referencias = new ArrayList<>();

        try {
            if (puedeGestionar()) {
                for (Document documento : MongoConfig.getCollection(coleccion).find(filtro)) {
                    ObjectId id = documento.getObjectId("_id");

                    if (id != null) {
                        referencias.add(id.toHexString() + " - " + nombreCompleto(documento));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener " + coleccion + ": " + e.getMessage());
        }

        return referencias;
    }

    private String nombreCompleto(Document documento) {
        return (textoSinNulos(documento.getString("nombre")) + " "
                + textoSinNulos(documento.getString("apellidos"))).trim();
    }

    public List<Asignacion> obtenerAsignaciones() {
        List<Asignacion> resultado = new ArrayList<>();

        try {
            if (puedeConsultar()) {
                List<Document> documentos = coleccion().find(filtroAcceso())
                        .sort(new Document("fecha_asignacion", -1))
                        .into(new ArrayList<Document>());

                List<ObjectId> idsDifuntos = new ArrayList<>();
                List<ObjectId> idsTrabajadores = new ArrayList<>();

                for (Document documento : documentos) {
                    idsDifuntos.add(documento.getObjectId("id_difunto"));
                    idsTrabajadores.add(documento.getObjectId("id_trabajador"));
                }

                Map<String, String> nombresDifuntos = obtenerNombres("difunto", idsDifuntos);
                Map<String, String> nombresTrabajadores = obtenerNombres("trabajador", idsTrabajadores);

                for (Document documento : documentos) {
                    Asignacion asignacion = crearAsignacion(documento);
                    asignacion.setDifuntoNombre(nombreOPredeterminado(
                            nombresDifuntos.get(asignacion.getIdDifunto()), "Difunto no disponible"));
                    asignacion.setTrabajadorNombre(nombreOPredeterminado(
                            nombresTrabajadores.get(asignacion.getIdTrabajador()), "Trabajador no disponible"));
                    resultado.add(asignacion);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron cargar los datos de asignaciones", e);
        }

        return resultado;
    }

    private String nombreOPredeterminado(String nombre, String predeterminado) {
        String resultado = predeterminado;
        if (nombre != null) {
            resultado = nombre;
        }
        return resultado;
    }

    private Map<String, String> obtenerNombres(String coleccion, List<ObjectId> ids) {
        Map<String, String> nombres = new HashMap<>();

        if (!ids.isEmpty()) {
            Document filtro = new Document("_id", new Document("$in", ids));

            for (Document documento : MongoConfig.getCollection(coleccion).find(filtro)) {
                nombres.put(documento.getObjectId("_id").toHexString(), nombreCompleto(documento));
            }
        }

        return nombres;
    }

    private Asignacion crearAsignacion(Document documento) {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(documento.getObjectId("_id").toHexString());
        asignacion.setIdDifunto(documento.getObjectId("id_difunto").toHexString());
        asignacion.setIdTrabajador(documento.getObjectId("id_trabajador").toHexString());
        asignacion.setEstado(documento.getString("estado"));
        asignacion.setFechaAsignacion(documento.getDate("fecha_asignacion"));
        asignacion.setFechaInicioTrabajo(documento.getDate("fecha_inicio_trabajo"));
        asignacion.setFechaFinalizacion(documento.getDate("fecha_finalizacion"));
        asignacion.setIndicacionesFamilia(textoSinNulos(documento.getString("indicaciones_familia")));

        Document seguimiento = documento.get("seguimiento", Document.class);

        if (seguimiento != null) {
            asignacion.setPasoIdentificacion(Boolean.TRUE.equals(seguimiento.getBoolean("paso_identificacion")));
            asignacion.setPasoConservacion(Boolean.TRUE.equals(seguimiento.getBoolean("paso_conservacion")));
            asignacion.setPasoEstetica(Boolean.TRUE.equals(seguimiento.getBoolean("paso_estetica")));
            asignacion.setObservaciones(textoSinNulos(seguimiento.getString("observaciones")));
            asignacion.setUltimaActualizacion(seguimiento.getDate("ultima_actualizacion"));
        }

        return asignacion;
    }

    public boolean insertarAsignacion(Asignacion asignacion) {
        boolean insertado = false;

        try {
            if (puedeGestionar() && esIdValido(asignacion.getIdDifunto()) && esIdValido(asignacion.getIdTrabajador())) {
                Date ahora = new Date();

                Document documento = new Document("id_difunto", new ObjectId(asignacion.getIdDifunto()))
                        .append("id_trabajador", new ObjectId(asignacion.getIdTrabajador()))
                        .append("estado", "Pendiente")
                        .append("fecha_asignacion", ahora)
                        .append("indicaciones_familia", textoSinNulos(asignacion.getIndicacionesFamilia()));

                coleccion().insertOne(documento);

                asignacion.setId(documento.getObjectId("_id").toHexString());
                asignacion.setEstado("Pendiente");
                asignacion.setFechaAsignacion(ahora);
                insertado = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insertado;
    }

    public boolean actualizarAsignacion(Asignacion asignacion) {
        boolean actualizado = false;

        try {
            if (puedeConsultar() && esIdValido(asignacion.getId()) && esEstadoValido(asignacion.getEstado())) {
                Document filtro = filtroAcceso().append("_id", new ObjectId(asignacion.getId()));
                Document anterior = coleccion().find(filtro).first();

                if (anterior != null) {
                    actualizado = guardarCambios(asignacion, filtro, anterior);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actualizado;
    }

    private boolean guardarCambios(Asignacion asignacion, Document filtro, Document anterior) {
        Date ahora = new Date();
        Date inicio = anterior.getDate("fecha_inicio_trabajo");
        Date fin = anterior.getDate("fecha_finalizacion");

        if ("Pendiente".equals(asignacion.getEstado())) {
            inicio = null;
        } else if (inicio == null) {
            inicio = ahora;
        }

        if ("Completado".equals(asignacion.getEstado())) {
            if (fin == null) {
                fin = ahora;
            }
        } else {
            fin = null;
        }

        Document cambios = new Document("estado", asignacion.getEstado())
                .append("seguimiento.paso_identificacion", asignacion.isPasoIdentificacion())
                .append("seguimiento.paso_conservacion", asignacion.isPasoConservacion())
                .append("seguimiento.paso_estetica", asignacion.isPasoEstetica())
                .append("seguimiento.observaciones", textoSinNulos(asignacion.getObservaciones()))
                .append("seguimiento.ultima_actualizacion", ahora);
        Document quitar = new Document();

        if (inicio == null) {
            quitar.append("fecha_inicio_trabajo", "");
        } else {
            cambios.append("fecha_inicio_trabajo", inicio);
        }

        if (fin == null) {
            quitar.append("fecha_finalizacion", "");
        } else {
            cambios.append("fecha_finalizacion", fin);
        }

        if (puedeGestionar() && esIdValido(asignacion.getIdDifunto()) && esIdValido(asignacion.getIdTrabajador())) {
            cambios.append("id_difunto", new ObjectId(asignacion.getIdDifunto()))
                    .append("id_trabajador", new ObjectId(asignacion.getIdTrabajador()))
                    .append("indicaciones_familia", textoSinNulos(asignacion.getIndicacionesFamilia()));
        }

        Document actualizacion = new Document("$set", cambios);

        if (!quitar.isEmpty()) {
            actualizacion.append("$unset", quitar);
        }

        boolean modificado = coleccion().updateOne(filtro, actualizacion).getMatchedCount() > 0;

        if (modificado) {
            asignacion.setFechaInicioTrabajo(inicio);
            asignacion.setFechaFinalizacion(fin);
            asignacion.setUltimaActualizacion(ahora);
        }

        return modificado;
    }

    public boolean eliminarAsignacion(String id) {
        boolean eliminado = false;

        try {
            if (puedeGestionar() && esIdValido(id)) {
                eliminado = coleccion().deleteOne(new Document("_id", new ObjectId(id)))
                        .getDeletedCount() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eliminado;
    }
}