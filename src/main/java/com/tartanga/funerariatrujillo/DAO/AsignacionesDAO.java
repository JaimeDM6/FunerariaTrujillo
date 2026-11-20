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

/**
 * Acceso a datos de las asignaciones de servicios. Combina las colecciones {@code asignacion},
 * {@code difunto}, {@code cliente} y {@code trabajador} para obtener los servicios pendientes, el
 * trabajo asignado y los tanatopractores, y guarda las asignaciones.
 *
 * @author Aritz
 */
public class AsignacionesDAO {

    /**
     * Asignación ampliada con los datos de presentación que necesitan las tablas y el panel lateral de
     * la vista de asignaciones.
     */
    public static class ResumenAsignacion extends Asignacion {

        private static final long serialVersionUID = 1L;

        private String codigoServicio;
        private String clienteInfo;
        private String trabajoSolicitado;
        private Date fechaLlegada;
        private String codigoTrabajador;
        private String trabajadorCargo;

        /**
         * Crea un resumen vacío con valores por defecto.
         */
        public ResumenAsignacion() {
            super();
            this.codigoServicio = "";
            this.clienteInfo = "-";
            this.trabajoSolicitado = "";
            this.fechaLlegada = null;
            this.codigoTrabajador = "";
            this.trabajadorCargo = "";
        }

        /**
         * Devuelve el código del servicio.
         *
         * @return El código del servicio
         */
        public String getCodigoServicio() {
            return codigoServicio;
        }

        /**
         * Establece el código del servicio.
         *
         * @param codigoServicio el código del servicio
         */
        public void setCodigoServicio(String codigoServicio) {
            this.codigoServicio = codigoServicio;
        }

        /**
         * Devuelve el cliente con su código.
         *
         * @return El cliente con su código
         */
        public String getClienteInfo() {
            return clienteInfo;
        }

        /**
         * Establece el cliente con su código.
         *
         * @param clienteInfo el cliente con su código
         */
        public void setClienteInfo(String clienteInfo) {
            this.clienteInfo = clienteInfo;
        }

        /**
         * Devuelve el trabajo solicitado.
         *
         * @return El trabajo solicitado
         */
        public String getTrabajoSolicitado() {
            return trabajoSolicitado;
        }

        /**
         * Establece el trabajo solicitado.
         *
         * @param trabajoSolicitado el trabajo solicitado
         */
        public void setTrabajoSolicitado(String trabajoSolicitado) {
            this.trabajoSolicitado = trabajoSolicitado;
        }

        /**
         * Devuelve la fecha de llegada del difunto.
         *
         * @return La fecha de llegada del difunto
         */
        public Date getFechaLlegada() {
            return copiarFecha(fechaLlegada);
        }

        /**
         * Establece la fecha de llegada del difunto.
         *
         * @param fechaLlegada la fecha de llegada del difunto
         */
        public void setFechaLlegada(Date fechaLlegada) {
            this.fechaLlegada = copiarFecha(fechaLlegada);
        }

        /**
         * Devuelve el código del trabajador.
         *
         * @return El código del trabajador
         */
        public String getCodigoTrabajador() {
            return codigoTrabajador;
        }

        /**
         * Establece el código del trabajador.
         *
         * @param codigoTrabajador el código del trabajador
         */
        public void setCodigoTrabajador(String codigoTrabajador) {
            this.codigoTrabajador = codigoTrabajador;
        }

        /**
         * Devuelve el cargo del trabajador.
         *
         * @return El cargo del trabajador
         */
        public String getTrabajadorCargo() {
            return trabajadorCargo;
        }

        /**
         * Establece el cargo del trabajador.
         *
         * @param trabajadorCargo el cargo del trabajador
         */
        public void setTrabajadorCargo(String trabajadorCargo) {
            this.trabajadorCargo = trabajadorCargo;
        }

        /**
         * Devuelve el responsable con su código y su cargo, o un texto indicando que no hay responsable.
         *
         * @return texto del responsable
         */
        public String getResponsableDetalle() {
            String resultado = "Sin responsable";

            if (getTrabajadorNombre() != null && !getTrabajadorNombre().isBlank()) {
                StringBuilder sb = new StringBuilder(getTrabajadorNombre());
                if (codigoTrabajador != null && !codigoTrabajador.isBlank()) {
                    sb.append(" · ").append(codigoTrabajador);
                }
                if (trabajadorCargo != null && !trabajadorCargo.isBlank()) {
                    sb.append(" · ").append(formatearCargo(trabajadorCargo));
                }
                resultado = sb.toString();
            }

            return resultado;
        }

        /**
         * Devuelve una copia de una fecha para no compartir el objeto mutable.
         *
         * @param fecha fecha que se copia, que puede ser nula
         * @return copia de la fecha, o {@code null} si era nula
         */
        private static Date copiarFecha(Date fecha) {
            Date copia = null;
            if (fecha != null) {
                copia = new Date(fecha.getTime());
            }
            return copia;
        }
    }

    /**
     * Tanatopractor que se puede elegir en el desplegable y que se muestra en el panel de carga de
     * trabajo.
     */
    public static class TanatopractorResumen {

        private final String id;
        private final String nombreCompleto;
        private final String codigo;
        private final String cargo;

        /**
         * Crea el resumen de un tanatopractor.
         *
         * @param id identificador del trabajador
         * @param nombreCompleto nombre completo
         * @param codigo código del trabajador
         * @param cargo cargo del trabajador
         */
        public TanatopractorResumen(String id, String nombreCompleto, String codigo, String cargo) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
            this.codigo = codigo;
            this.cargo = cargo;
        }

        /**
         * Devuelve el identificador del trabajador.
         *
         * @return El identificador del trabajador
         */
        public String getId() {
            return id;
        }

        /**
         * Devuelve el nombre completo.
         *
         * @return El nombre completo
         */
        public String getNombreCompleto() {
            return nombreCompleto;
        }

        /**
         * Devuelve el código del trabajador.
         *
         * @return El código del trabajador
         */
        public String getCodigo() {
            return codigo;
        }

        /**
         * Devuelve el cargo.
         *
         * @return El cargo
         */
        public String getCargo() {
            return cargo;
        }

        /**
         * Devuelve el texto que se muestra en el desplegable: nombre, código y cargo.
         *
         * @return texto del tanatopractor
         */
        @Override
        public String toString() {
            return nombreCompleto + " · " + codigo + " · " + formatearCargo(cargo);
        }
    }

    /**
     * Obtiene los servicios pendientes de asignar: las asignaciones sin trabajador y los difuntos que
     * todavía no tienen ninguna asignación.
     *
     * @return lista de servicios pendientes; vacía si se produce un error
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
                        item.setEstado(estadoPorDefecto(docAsig.getString("estado")));
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
     * Obtiene las asignaciones que ya tienen trabajador responsable. Las asignaciones cuyo difunto ya
     * no existe se ignoran.
     *
     * @return lista de asignaciones; vacía si se produce un error
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

                if (idTrabajador != null && idDifunto != null) {
                    Document docDifunto = difuntosMap.get(idDifunto);

                    if (docDifunto != null) {
                        ResumenAsignacion item = construirDesdeDifunto(docDifunto, clientesMap);

                        item.setId(docAsig.getObjectId("_id").toString());
                        item.setIdDifunto(idDifunto.toString());
                        item.setIdTrabajador(idTrabajador.toString());
                        item.setEstado(estadoPorDefecto(docAsig.getString("estado")));
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
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener trabajo asignado: " + e.getMessage());
        }
        return asignados;
    }

    /**
     * Obtiene los trabajadores con cargo de tanatopractor o con rol de tanatopraxia.
     *
     * @return lista de tanatopractores; vacía si se produce un error
     */
    public List<TanatopractorResumen> obtenerTanatopractores() {
        List<TanatopractorResumen> lista = new ArrayList<>();
        try {
            MongoCollection<Document> trabColl = MongoConfig.getCollection("trabajador");
            for (Document doc : trabColl.find(Filters.or(
                    Filters.eq("cargo", "TANATOPRACTOR"),
                    Filters.eq("rol", "TANATOPRAXIA")))) {

                ObjectId idObj = doc.getObjectId("_id");
                String id = "";
                if (idObj != null) {
                    id = idObj.toString();
                }
                String nombreCompleto = (texto(doc.getString("nombre")) + " " + texto(doc.getString("apellidos"))).trim();
                String codigo = formatearCodigo("TR", id);
                String cargo = doc.getString("cargo");
                if (cargo == null) {
                    cargo = "TANATOPRACTOR";
                }

                lista.add(new TanatopractorResumen(id, nombreCompleto, codigo, cargo));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener tanatopractores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Guarda la asignación de un servicio a un tanatopractor. Actualiza la asignación existente o crea
     * una nueva con el seguimiento inicial, y actualiza el resumen recibido.
     *
     * @param pendiente servicio pendiente que se asigna
     * @param tanatopractor tanatopractor elegido
     * @return {@code true} si se guardó correctamente
     */
    public boolean confirmarAsignacion(ResumenAsignacion pendiente, TanatopractorResumen tanatopractor) {
        boolean guardada = false;

        if (pendiente != null && tanatopractor != null) {
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
                            .append("indicaciones_familia", texto(pendiente.getIndicacionesFamilia()))
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
                guardada = true;
            } catch (Exception e) {
                System.err.println("Error al confirmar asignación: " + e.getMessage());
            }
        }

        return guardada;
    }

    /**
     * Crea el resumen de un servicio a partir del documento de un difunto y de su cliente.
     *
     * @param docDifunto documento del difunto
     * @param clientesMap documentos de clientes indexados por identificador
     * @return resumen con los datos del difunto y del cliente
     */
    private ResumenAsignacion construirDesdeDifunto(Document docDifunto, Map<ObjectId, Document> clientesMap) {
        ResumenAsignacion item = new ResumenAsignacion();
        ObjectId idDifObj = docDifunto.getObjectId("_id");
        String idDifunto = "";
        if (idDifObj != null) {
            idDifunto = idDifObj.toString();
        }

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

    /**
     * Carga todos los documentos de una colección indexados por su identificador.
     *
     * @param collection colección que se carga
     * @return mapa de documentos por identificador
     */
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

    /**
     * Crea un código con un prefijo y los cuatro últimos caracteres del identificador, por ejemplo
     * {@code SV-A1B2}.
     *
     * @param prefijo prefijo del código
     * @param id identificador del que se toma el sufijo
     * @return código en mayúsculas
     */
    private static String formatearCodigo(String prefijo, String id) {
        String resultado = prefijo + "-0000";

        if (id != null && !id.isBlank()) {
            String sufijo = id.toUpperCase();
            if (id.length() >= 4) {
                sufijo = id.substring(id.length() - 4).toUpperCase();
            }
            resultado = prefijo + "-" + sufijo;
        }

        return resultado;
    }

    /**
     * Da formato al tipo de servicio: primera letra en mayúscula y el resto en minúscula.
     *
     * @param servicio tipo de servicio, que puede ser nulo
     * @return tipo de servicio, o {@code Preparación general} si no hay
     */
    private static String formatearServicio(String servicio) {
        String resultado = "Preparación general";

        if (servicio != null && !servicio.isBlank()) {
            String lower = servicio.trim().toLowerCase();
            resultado = Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        }

        return resultado;
    }

    /**
     * Da formato a un cargo: sustituye los guiones bajos por espacios y deja solo la primera letra en
     * mayúscula.
     *
     * @param cargo cargo, que puede ser nulo
     * @return cargo con formato, o una cadena vacía si no hay
     */
    private static String formatearCargo(String cargo) {
        String resultado = "";

        if (cargo != null && !cargo.isBlank()) {
            String limpio = cargo.replace("_", " ").toLowerCase();
            resultado = Character.toUpperCase(limpio.charAt(0)) + limpio.substring(1);
        }

        return resultado;
    }

    /**
     * Convierte un texto nulo en una cadena vacía.
     *
     * @param valor texto original, que puede ser nulo
     * @return el mismo texto, o una cadena vacía si era nulo
     */
    private static String texto(String valor) {
        String resultado = "";
        if (valor != null) {
            resultado = valor;
        }
        return resultado;
    }

    /**
     * Devuelve el estado indicado o, si no hay, {@code Pendiente}.
     *
     * @param estado estado leído de la base de datos, que puede ser nulo
     * @return estado de la asignación
     */
    private static String estadoPorDefecto(String estado) {
        String resultado = "Pendiente";
        if (estado != null) {
            resultado = estado;
        }
        return resultado;
    }
}
