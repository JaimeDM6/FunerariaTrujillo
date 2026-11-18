package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tartanga.funerariatrujillo.Objects.Asignacion;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AsignacionesDAO {

    /**
     * Extensión de Asignacion con los datos de presentación necesarios para las
     * tablas y el panel lateral de Asignación de servicios.
     */
    public static class ResumenAsignacion extends Asignacion {

        private static final long serialVersionUID = 1L;

        private String codigoServicio;
        private String clienteInfo;
        private String trabajoSolicitado;
        private Date fechaLlegada;
        private String codigoTrabajador;
        private String trabajadorCargo;

        public ResumenAsignacion() {
            super();
            this.codigoServicio = "";
            this.clienteInfo = "-";
            this.trabajoSolicitado = "";
            this.fechaLlegada = null;
            this.codigoTrabajador = "";
            this.trabajadorCargo = "";
        }

        public String getCodigoServicio() {
            return codigoServicio;
        }

        public void setCodigoServicio(String codigoServicio) {
            this.codigoServicio = codigoServicio;
        }

        public String getClienteInfo() {
            return clienteInfo;
        }

        public void setClienteInfo(String clienteInfo) {
            this.clienteInfo = clienteInfo;
        }

        public String getTrabajoSolicitado() {
            return trabajoSolicitado;
        }

        public void setTrabajoSolicitado(String trabajoSolicitado) {
            this.trabajoSolicitado = trabajoSolicitado;
        }

        public Date getFechaLlegada() {
            return fechaLlegada != null ? new Date(fechaLlegada.getTime()) : null;
        }

        public void setFechaLlegada(Date fechaLlegada) {
            this.fechaLlegada = fechaLlegada != null ? new Date(fechaLlegada.getTime()) : null;
        }

        public String getCodigoTrabajador() {
            return codigoTrabajador;
        }

        public void setCodigoTrabajador(String codigoTrabajador) {
            this.codigoTrabajador = codigoTrabajador;
        }

        public String getTrabajadorCargo() {
            return trabajadorCargo;
        }

        public void setTrabajadorCargo(String trabajadorCargo) {
            this.trabajadorCargo = trabajadorCargo;
        }

        public String getResponsableDetalle() {
            if (getTrabajadorNombre() == null || getTrabajadorNombre().isBlank()) {
                return "Sin responsable";
            }
            StringBuilder sb = new StringBuilder(getTrabajadorNombre());
            if (codigoTrabajador != null && !codigoTrabajador.isBlank()) {
                sb.append(" · ").append(codigoTrabajador);
            }
            if (trabajadorCargo != null && !trabajadorCargo.isBlank()) {
                sb.append(" · ").append(formatearCargo(trabajadorCargo));
            }
            return sb.toString();
        }
    }

    /**
     * Representa un tanatopractor seleccionable en el ComboBox y en el panel de
     * carga.
     */
    public static class TanatopractorResumen {

        private final String id;
        private final String nombreCompleto;
        private final String codigo;
        private final String cargo;

        public TanatopractorResumen(String id, String nombreCompleto, String codigo, String cargo) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
            this.codigo = codigo;
            this.cargo = cargo;
        }

        public String getId() {
            return id;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getCargo() {
            return cargo;
        }

        @Override
        public String toString() {
            return nombreCompleto + " · " + codigo + " · " + formatearCargo(cargo);
        }
    }

    /**
     * Obtiene los servicios pendientes de asignar: 1) Documentos en
     * 'asignacion' sin 'id_trabajador'. 2) Difuntos registrados que aún no
     * tienen ninguna asignación asociada.
     *
     * @return
     */
    public List<ResumenAsignacion> obtenerPendientesDeAsignar() {
        List<ResumenAsignacion> pendientes = new ArrayList<>();
        try {
            MongoCollection<Document> asigColl = MongoConfig.getCollection("asignacion");
            MongoCollection<Document> difuntoColl = MongoConfig.getCollection("difunto");
            MongoCollection<Document> clienteColl = MongoConfig.getCollection("cliente");

            Map<ObjectId, Document> clientesMap = cargarMapaPorId(clienteColl);
            Map<ObjectId, Document> difuntosMap = cargarMapaPorId(difuntoColl);
            Set<ObjectId> difuntosConAsignacion = new HashSet<>();

            for (Document docAsig : asigColl.find()) {
                ObjectId idDifunto = docAsig.getObjectId("id_difunto");
                ObjectId idTrabajador = docAsig.getObjectId("id_trabajador");

                if (idDifunto != null) {
                    difuntosConAsignacion.add(idDifunto);
                }

                if (idTrabajador == null && idDifunto != null) {
                    Document docDifunto = difuntosMap.get(idDifunto);
                    if (docDifunto != null) {
                        ResumenAsignacion item = construirDesdeDifunto(docDifunto, clientesMap);
                        item.setId(docAsig.getObjectId("_id").toString());
                        item.setEstado(docAsig.getString("estado") != null ? docAsig.getString("estado") : "Pendiente");
                        item.setIndicacionesFamilia(docAsig.getString("indicaciones_familia"));
                        pendientes.add(item);
                    }
                }
            }

            for (Map.Entry<ObjectId, Document> entry : difuntosMap.entrySet()) {
                if (!difuntosConAsignacion.contains(entry.getKey())) {
                    pendientes.add(construirDesdeDifunto(entry.getValue(), clientesMap));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener servicios pendientes de asignar: " + e.getMessage());
        }
        return pendientes;
    }

    /**
     * Obtiene todas las asignaciones que ya tienen un trabajador responsable
     * asignado.
     *
     * @return
     */
    public List<ResumenAsignacion> obtenerTrabajoAsignado() {
        List<ResumenAsignacion> asignados = new ArrayList<>();
        try {
            MongoCollection<Document> asigColl = MongoConfig.getCollection("asignacion");
            MongoCollection<Document> difuntoColl = MongoConfig.getCollection("difunto");
            MongoCollection<Document> clienteColl = MongoConfig.getCollection("cliente");
            MongoCollection<Document> trabColl = MongoConfig.getCollection("trabajador");

            Map<ObjectId, Document> clientesMap = cargarMapaPorId(clienteColl);
            Map<ObjectId, Document> difuntosMap = cargarMapaPorId(difuntoColl);
            Map<ObjectId, Document> trabMap = cargarMapaPorId(trabColl);

            for (Document docAsig : asigColl.find()) {
                ObjectId idTrabajador = docAsig.getObjectId("id_trabajador");
                ObjectId idDifunto = docAsig.getObjectId("id_difunto");

                if (idTrabajador == null || idDifunto == null) {
                    continue;
                }

                Document docDifunto = difuntosMap.get(idDifunto);
                ResumenAsignacion item = docDifunto != null
                        ? construirDesdeDifunto(docDifunto, clientesMap)
                        : new ResumenAsignacion();

                item.setId(docAsig.getObjectId("_id").toString());
                item.setIdDifunto(idDifunto.toString());
                item.setIdTrabajador(idTrabajador.toString());
                item.setEstado(docAsig.getString("estado") != null ? docAsig.getString("estado") : "Pendiente");
                item.setFechaAsignacion(docAsig.getDate("fecha_asignacion"));
                item.setFechaInicioTrabajo(docAsig.getDate("fecha_inicio_trabajo"));
                item.setFechaFinalizacion(docAsig.getDate("fecha_finalizacion"));
                item.setIndicacionesFamilia(docAsig.getString("indicaciones_familia"));

                Document seg = (Document) docAsig.get("seguimiento");
                if (seg != null) {
                    item.setPasoIdentificacion(seg.getBoolean("paso_identificacion", false));
                    item.setPasoConservacion(seg.getBoolean("paso_conservacion", false));
                    item.setPasoEstetica(seg.getBoolean("paso_estetica", false));
                    item.setObservaciones(seg.getString("observaciones"));
                    item.setUltimaActualizacion(seg.getDate("ultima_actualizacion"));
                }

                Document docTrab = trabMap.get(idTrabajador);
                if (docTrab != null) {
                    String nombre = texto(docTrab.getString("nombre"));
                    String apellidos = texto(docTrab.getString("apellidos"));
                    item.setTrabajadorNombre((nombre + " " + apellidos).trim());
                    item.setCodigoTrabajador(formatearCodigo("TR", idTrabajador.toString()));
                    item.setTrabajadorCargo(docTrab.getString("cargo"));
                }

                asignados.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener trabajo asignado: " + e.getMessage());
        }
        return asignados;
    }

    /**
     * Obtiene el listado de trabajadores con cargo TANATOPRACTOR (o rol
     * TANATOPRAXIA).
     *
     * @return
     */
    public List<TanatopractorResumen> obtenerTanatopractores() {
        List<TanatopractorResumen> lista = new ArrayList<>();
        try {
            MongoCollection<Document> trabColl = MongoConfig.getCollection("trabajador");
            for (Document doc : trabColl.find(Filters.or(
                    Filters.eq("cargo", "TANATOPRACTOR"),
                    Filters.eq("rol", "TANATOPRAXIA")))) {

                ObjectId idObj = doc.getObjectId("_id");
                String id = idObj != null ? idObj.toString() : "";
                String nombreCompleto = (texto(doc.getString("nombre")) + " " + texto(doc.getString("apellidos"))).trim();
                String codigo = formatearCodigo("TR", id);
                String cargo = doc.getString("cargo") != null ? doc.getString("cargo") : "TANATOPRACTOR";

                lista.add(new TanatopractorResumen(id, nombreCompleto, codigo, cargo));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener tanatopractores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inserta o actualiza la asignación en MongoDB vinculando el difunto al
     * tanatopractor elegido.
     *
     * @param pendiente
     * @param tanatopractor
     * @return
     */
    public boolean confirmarAsignacion(ResumenAsignacion pendiente, TanatopractorResumen tanatopractor) {
        if (pendiente == null || tanatopractor == null) {
            return false;
        }
        try {
            MongoCollection<Document> asigColl = MongoConfig.getCollection("asignacion");
            Date ahora = new Date();
            ObjectId idTrabObj = new ObjectId(tanatopractor.getId());

            if (pendiente.getId() != null && ObjectId.isValid(pendiente.getId())) {
                Document filtro = new Document("_id", new ObjectId(pendiente.getId()));
                Document actualizacion = new Document("$set", new Document()
                        .append("id_trabajador", idTrabObj)
                        .append("estado", "Pendiente")
                        .append("fecha_asignacion", ahora));
                asigColl.updateOne(filtro, actualizacion);
            } else {
                Document seguimientoInicial = new Document()
                        .append("paso_identificacion", false)
                        .append("paso_conservacion", false)
                        .append("paso_estetica", false)
                        .append("observaciones", "")
                        .append("ultima_actualizacion", ahora);

                Document nuevoDoc = new Document()
                        .append("id_difunto", new ObjectId(pendiente.getIdDifunto()))
                        .append("id_trabajador", idTrabObj)
                        .append("estado", "Pendiente")
                        .append("fecha_asignacion", ahora)
                        .append("indicaciones_familia", pendiente.getIndicacionesFamilia() != null
                                ? pendiente.getIndicacionesFamilia() : "")
                        .append("seguimiento", seguimientoInicial);

                asigColl.insertOne(nuevoDoc);
                pendiente.setId(nuevoDoc.getObjectId("_id").toString());
            }

            pendiente.setIdTrabajador(tanatopractor.getId());
            pendiente.setTrabajadorNombre(tanatopractor.getNombreCompleto());
            pendiente.setCodigoTrabajador(tanatopractor.getCodigo());
            pendiente.setTrabajadorCargo(tanatopractor.getCargo());
            pendiente.setEstado("Pendiente");
            pendiente.setFechaAsignacion(ahora);
            return true;
        } catch (Exception e) {
            System.err.println("Error al confirmar asignación: " + e.getMessage());
            return false;
        }
    }

    private ResumenAsignacion construirDesdeDifunto(Document docDifunto, Map<ObjectId, Document> clientesMap) {
        ResumenAsignacion item = new ResumenAsignacion();
        ObjectId idDifObj = docDifunto.getObjectId("_id");
        String idDifunto = idDifObj != null ? idDifObj.toString() : "";

        item.setIdDifunto(idDifunto);
        item.setCodigoServicio(formatearCodigo("SV", idDifunto));
        item.setDifuntoNombre((texto(docDifunto.getString("nombre")) + " " + texto(docDifunto.getString("apellidos"))).trim());
        item.setTrabajoSolicitado(formatearServicio(docDifunto.getString("servicio")));
        item.setFechaLlegada(docDifunto.getDate("fecha_muerte"));
        item.setEstado("Pendiente");

        ObjectId idClienteObj = docDifunto.getObjectId("id_cliente");
        if (idClienteObj != null && clientesMap.containsKey(idClienteObj)) {
            Document docCliente = clientesMap.get(idClienteObj);
            String nombreCliente = (texto(docCliente.getString("nombre")) + " " + texto(docCliente.getString("apellidos"))).trim();
            item.setClienteInfo(nombreCliente + " · " + formatearCodigo("CL", idClienteObj.toString()));
        } else {
            item.setClienteInfo("Sin cliente asociado");
        }

        return item;
    }

    private Map<ObjectId, Document> cargarMapaPorId(MongoCollection<Document> collection) {
        Map<ObjectId, Document> map = new HashMap<>();
        for (Document doc : collection.find()) {
            ObjectId id = doc.getObjectId("_id");
            if (id != null) {
                map.put(id, doc);
            }
        }
        return map;
    }

    private static String formatearCodigo(String prefijo, String id) {
        if (id == null || id.isBlank()) {
            return prefijo + "-0000";
        }
        String sufijo = id.length() >= 4 ? id.substring(id.length() - 4).toUpperCase() : id.toUpperCase();
        return prefijo + "-" + sufijo;
    }

    private static String formatearServicio(String servicio) {
        if (servicio == null || servicio.isBlank()) {
            return "Preparación general";
        }
        String lower = servicio.trim().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String formatearCargo(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            return "";
        }
        String limpio = cargo.replace("_", " ").toLowerCase();
        return Character.toUpperCase(limpio.charAt(0)) + limpio.substring(1);
    }

    private static String texto(String valor) {
        return valor != null ? valor : "";
    }
}
