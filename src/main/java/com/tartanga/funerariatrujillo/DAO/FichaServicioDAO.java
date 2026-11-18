package com.tartanga.funerariatrujillo.DAO;

import com.tartanga.funerariatrujillo.Objects.Servicio;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import static com.mongodb.client.model.Filters.eq;

/**
 * Acceso a datos de la ficha de servicio. Reúne en un {@link Servicio} la información de las
 * colecciones {@code asignacion}, {@code trabajador}, {@code difunto}, {@code cliente}, {@code
 * feretro}, {@code urna} y {@code servicios}, y guarda el seguimiento del trabajo.
 *
 * @author Jaime
 */
public class FichaServicioDAO {

    /**
     * Obtiene la ficha de una asignación reuniendo los datos del trabajador, del difunto, del cliente
     * y de lo contratado.
     *
     * @param idAsignacion identificador de la asignación
     * @return ficha del servicio, o {@code null} si no existe o se produce un error
     */
    public Servicio obtenerServicioPorId(String idAsignacion) {
        try {
            MongoCollection<Document> asigColl = MongoConfig.getCollection("asignacion");
            Document docAsig = asigColl.find(eq("_id", new ObjectId(idAsignacion))).first();

            if (docAsig != null) {
                Servicio s = new Servicio();
                s.setIdAsignacion(docAsig.getObjectId("_id").toString());
                s.setIdDifunto(docAsig.getObjectId("id_difunto").toString());
                s.setIdTrabajador(docAsig.getObjectId("id_trabajador").toString());

                s.setEstado(docAsig.getString("estado"));
                s.setFechaAsignacion(docAsig.getDate("fecha_asignacion"));
                s.setFechaInicioTrabajo(docAsig.getDate("fecha_inicio_trabajo"));
                s.setFechaFinalizacion(docAsig.getDate("fecha_finalizacion"));
                s.setIndicacionesFamilia(docAsig.getString("indicaciones_familia"));

                Document seg = (Document) docAsig.get("seguimiento");
                if (seg != null) {
                    s.setPasoIdentificacion(seg.getBoolean("paso_identificacion", false));
                    s.setPasoConservacion(seg.getBoolean("paso_conservacion", false));
                    s.setPasoEstetica(seg.getBoolean("paso_estetica", false));
                    s.setObservaciones(seg.getString("observaciones"));
                    s.setUltimaActualizacion(seg.getDate("ultima_actualizacion"));
                }

                MongoCollection<Document> trabColl = MongoConfig.getCollection("trabajador");
                Document docTrab = trabColl.find(eq("_id", new ObjectId(s.getIdTrabajador()))).first();
                if (docTrab != null) {
                    s.setTrabajadorNombre(docTrab.getString("nombre") + " " + docTrab.getString("apellidos"));
                    s.setTrabajadorCargo(docTrab.getString("cargo"));
                }

                MongoCollection<Document> difuntoColl = MongoConfig.getCollection("difunto");
                Document docDifunto = difuntoColl.find(eq("_id", new ObjectId(s.getIdDifunto()))).first();
                if (docDifunto != null) {
                    s.setDifuntoNombre(docDifunto.getString("nombre") + " " + docDifunto.getString("apellidos"));
                    s.setDifuntoNacimiento(docDifunto.getDate("fecha_nacimiento"));
                    s.setDifuntoFallecimiento(docDifunto.getDate("fecha_muerte"));
                    s.setProcedencia(docDifunto.getString("procedencia"));
                    s.setCausaMuerte(docDifunto.getString("causa_de_muerte"));
                    s.setTipoServicio(docDifunto.getString("servicio"));
                    s.setTituloServicio("SV-" + s.getIdDifunto().substring(s.getIdDifunto().length() - 4).toUpperCase());

                    if (docDifunto.getObjectId("id_cliente") != null) {
                        MongoCollection<Document> clienteColl = MongoConfig.getCollection("cliente");
                        Document docCliente = clienteColl.find(eq("_id", docDifunto.getObjectId("id_cliente"))).first();
                        if (docCliente != null) {
                            s.setClienteNombre(docCliente.getString("nombre") + " " + docCliente.getString("apellidos"));
                            s.setClienteTelefono(docCliente.getString("telefono"));
                            s.setClienteCorreo(docCliente.getString("correo_electronico"));
                        }
                        s.setClienteRelacion(docDifunto.getString("relacion_cliente"));
                    }

                    if (docDifunto.getObjectId("id_feretro") != null) {
                        MongoCollection<Document> feretroColl = MongoConfig.getCollection("feretro");
                        Document docFeretro = feretroColl.find(eq("_id", docDifunto.getObjectId("id_feretro"))).first();
                        if (docFeretro != null) {
                            s.setFeretroInfo(docFeretro.getString("material") + " (" + docFeretro.getString("medidas") + ")");
                        }
                    } else {
                        s.setFeretroInfo("No aplica");
                    }

                    if (docDifunto.getObjectId("id_urna") != null) {
                        MongoCollection<Document> urnaColl = MongoConfig.getCollection("urna");
                        Document docUrna = urnaColl.find(eq("_id", docDifunto.getObjectId("id_urna"))).first();
                        if (docUrna != null) {
                            s.setUrnaInfo(docUrna.getString("material") + " (" + docUrna.get("capacidad") + "L)");
                        }
                    } else {
                        s.setUrnaInfo("No aplica");
                    }

                    List<ObjectId> idServiciosList = docDifunto.getList("lista_servicios", ObjectId.class);
                    List<String> nombresServicios = new ArrayList<>();
                    if (idServiciosList != null && !idServiciosList.isEmpty()) {
                        MongoCollection<Document> servColl = MongoConfig.getCollection("servicios");
                        for (ObjectId servId : idServiciosList) {
                            Document servDoc = servColl.find(eq("_id", servId)).first();
                            if (servDoc != null) {
                                nombresServicios.add(servDoc.getString("nombre"));
                            }
                        }
                    }
                    if (nombresServicios.isEmpty()) {
                        nombresServicios.add("Ninguno");
                    }
                    s.setListaServicios(nombresServicios);
                }

                return s;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el servicio por ID completo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Guarda el estado y el seguimiento de una asignación.
     *
     * @param s ficha con el estado y el seguimiento nuevos
     * @return {@code true} si se guardó correctamente
     */
    public boolean actualizarSeguimientoServicio(Servicio s) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("asignacion");
            Document filtro = new Document("_id", new ObjectId(s.getIdAsignacion()));

            Document seguimientoDoc = new Document()
                    .append("paso_identificacion", s.isPasoIdentificacion())
                    .append("paso_conservacion", s.isPasoConservacion())
                    .append("paso_estetica", s.isPasoEstetica())
                    .append("observaciones", s.getObservaciones())
                    .append("ultima_actualizacion", new Date());

            Document updateDocs = new Document("$set", new Document()
                    .append("estado", s.getEstado())
                    .append("seguimiento", seguimientoDoc)
            );

            if (s.getFechaInicioTrabajo() != null) {
                ((Document) updateDocs.get("$set")).append("fecha_inicio_trabajo", s.getFechaInicioTrabajo());
            }
            if (s.getFechaFinalizacion() != null) {
                ((Document) updateDocs.get("$set")).append("fecha_finalizacion", s.getFechaFinalizacion());
            }

            collection.updateOne(filtro, updateDocs);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar seguimiento del servicio: " + e.getMessage());
            return false;
        }
    }
}
