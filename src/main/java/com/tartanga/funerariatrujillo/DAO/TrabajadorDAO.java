package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Cargo;
import com.tartanga.funerariatrujillo.Objects.Trabajador.Rol;
import org.mindrot.jbcrypt.BCrypt;

public class TrabajadorDAO {

    private static final String COLLECTION_NAME = "trabajador";

    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection(COLLECTION_NAME);

            for (Document doc : collection.find()) {
                try {
                    Trabajador t = new Trabajador();

                    t.setId(doc.getObjectId("_id").toString());
                    t.setNombre(doc.getString("nombre"));
                    t.setApellidos(doc.getString("apellidos"));
                    t.setDni(doc.getString("dni"));
                    t.setCorreo_electronico(doc.getString("correo_electronico"));
                    t.setUsuario(doc.getString("usuario"));
                    t.setPassword(doc.getString("password"));

                    Document dirDoc = (Document) doc.get("direccion");
                    if (dirDoc != null) {
                        Direccion dir = new Direccion();
                        dir.setCalle(dirDoc.getString("calle"));
                        dir.setCiudad(dirDoc.getString("ciudad"));

                        Object cpObj = dirDoc.get("codigo_postal");
                        if (cpObj != null && !cpObj.toString().isEmpty()) {
                            dir.setCodigoPostal(Integer.parseInt(cpObj.toString()));
                        }
                        t.setDireccion(dir);
                    }

                    t.setNumero_ss(doc.getString("numero_ss"));

                    Document bancoDoc = (Document) doc.get("datos_bancarios");
                    if (bancoDoc != null) {
                        t.setIban(bancoDoc.getString("iban"));
                    } else {
                        t.setIban(doc.getString("iban"));
                    }

                    Object salarioObj = doc.get("salario");
                    if (salarioObj instanceof Double) {
                        t.setSalario((Double) salarioObj);
                    } else if (salarioObj instanceof Integer) {
                        t.setSalario(((Integer) salarioObj).doubleValue());
                    }

                    String cargoStr = doc.getString("cargo");
                    if (cargoStr != null) {
                        try {
                            String cargoNormalizado = cargoStr.toUpperCase().replace(" ", "_");
                            t.setCargo(Cargo.valueOf(cargoNormalizado));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Cargo no reconocido: " + cargoStr);
                        }
                    }

                    String rolStr = doc.getString("rol");
                    if (rolStr != null) {
                        try {
                            t.setRol(Rol.valueOf(rolStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Rol no reconocido: " + rolStr);
                        }
                    }

                    t.setTipo_jornada(doc.getString("tipo_jornada"));

                    trabajadores.add(t);
                } catch (NumberFormatException docEx) {
                    System.err.println("Error procesando el trabajador con ID: " + doc.getObjectId("_id"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los trabajadores: " + e.getMessage());
        }

        return trabajadores;
    }

    public boolean actualizarTrabajador(Trabajador t) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("trabajador");

            Document filtro = new Document("_id", new org.bson.types.ObjectId(t.getId()));

            Document updateDocs = new Document("$set", new Document()
                    .append("nombre", t.getNombre())
                    .append("apellidos", t.getApellidos())
                    .append("dni", t.getDni())
                    .append("correo_electronico", t.getCorreo_electronico())
                    .append("numero_ss", t.getNumero_ss())
                    .append("iban", t.getIban())
                    .append("salario", t.getSalario())
                    .append("cargo", t.getCargo())
                    .append("tipo_jornada", t.getTipo_jornada())
            );

            collection.updateOne(filtro, updateDocs);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar el trabajador: " + e.getMessage());
            return false;
        }
    }

    public boolean insertarTrabajador(Trabajador t) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("trabajador");

            Document dirDoc = new Document()
                    .append("calle", t.getDireccion().getCalle())
                    .append("ciudad", t.getDireccion().getCiudad())
                    .append("codigo_postal", String.valueOf(t.getDireccion().getCodigoPostal()));

            Document doc = new Document()
                    .append("nombre", t.getNombre())
                    .append("apellidos", t.getApellidos())
                    .append("dni", t.getDni())
                    .append("correo_electronico", t.getCorreo_electronico())
                    .append("direccion", dirDoc)
                    .append("numero_ss", t.getNumero_ss())
                    .append("iban", t.getIban())
                    .append("salario", t.getSalario())
                    .append("cargo", t.getCargo())
                    .append("tipo_jornada", t.getTipo_jornada());

            collection.insertOne(doc);

            t.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar el trabajador: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarTrabajador(String id) {
        try {
            Document filtro = new Document("_id", new org.bson.types.ObjectId(id));
            MongoConfig.getCollection("trabajador").deleteOne(filtro);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar el trabajador: " + e.getMessage());
            return false;
        }
    }

    public boolean existeUsuario(String usuario) {
        try {
            Document doc = MongoConfig.getCollection(COLLECTION_NAME)
                    .find(eq("usuario", usuario.trim()))
                    .first();
            return doc != null;
        } catch (Exception e) {
            System.err.println("Error comprobando disponibilidad del usuario: " + e.getMessage());
            return true;
        }
    }

    public Optional<Trabajador> autenticar(String usuario, char[] password) {
        if (usuario == null || usuario.isBlank() || password == null || password.length == 0) {
            return Optional.empty();
        }

        try {
            Document doc = MongoConfig.getCollection(COLLECTION_NAME)
                    .find(eq("usuario", usuario.trim()))
                    .first();

            if (doc == null) {
                return Optional.empty();
            }

            String passwordHash = doc.getString("password");

            if (passwordHash == null || passwordHash.isEmpty()) {
                return Optional.empty();
            }

            boolean valid = false;
            try {
                valid = BCrypt.checkpw(new String(password), passwordHash);
            } catch (Exception e) {
                System.err.println("Error al verificar el hash BCrypt: " + e.getMessage());
            }

            if (valid) {
                Trabajador t = new Trabajador();

                t.setId(doc.getObjectId("_id").toString());
                t.setNombre(doc.getString("nombre"));
                t.setApellidos(doc.getString("apellidos"));
                t.setDni(doc.getString("dni"));
                t.setCorreo_electronico(doc.getString("correo_electronico"));
                t.setUsuario(doc.getString("usuario"));
                t.setPassword(doc.getString("password"));

                Document dirDoc = (Document) doc.get("direccion");
                if (dirDoc != null) {
                    Direccion dir = new Direccion();
                    dir.setCalle(dirDoc.getString("calle"));
                    dir.setCiudad(dirDoc.getString("ciudad"));

                    Object cpObj = dirDoc.get("codigo_postal");
                    if (cpObj != null && !cpObj.toString().isEmpty()) {
                        dir.setCodigoPostal(Integer.parseInt(cpObj.toString()));
                    }
                    t.setDireccion(dir);
                }

                t.setNumero_ss(doc.getString("numero_ss"));

                Document bancoDoc = (Document) doc.get("datos_bancarios");
                if (bancoDoc != null) {
                    t.setIban(bancoDoc.getString("iban"));
                } else {
                    t.setIban(doc.getString("iban"));
                }

                Object salarioObj = doc.get("salario");
                if (salarioObj instanceof Double) {
                    t.setSalario((Double) salarioObj);
                } else if (salarioObj instanceof Integer) {
                    t.setSalario(((Integer) salarioObj).doubleValue());
                }

                String cargoStr = doc.getString("cargo");
                if (cargoStr != null) {
                    try {
                        String cargoNormalizado = cargoStr.toUpperCase().replace(" ", "_");
                        t.setCargo(Cargo.valueOf(cargoNormalizado));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Cargo no reconocido: " + cargoStr);
                    }
                }

                String rolStr = doc.getString("rol");
                if (rolStr != null) {
                    try {
                        t.setRol(Rol.valueOf(rolStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Rol no reconocido: " + rolStr);
                    }
                }

                t.setTipo_jornada(doc.getString("tipo_jornada"));

                return Optional.of(t);
            } else {
                return Optional.empty();
            }

        } catch (NumberFormatException exception) {
            System.err.println("Error al autenticar el trabajador: " + exception.getMessage());
            return Optional.empty();
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
