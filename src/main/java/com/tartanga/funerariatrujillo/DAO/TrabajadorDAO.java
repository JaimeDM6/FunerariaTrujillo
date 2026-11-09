package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoCollection;
import com.tartanga.funerariatrujillo.Objects.Direccion;
import com.tartanga.funerariatrujillo.Objects.Trabajador;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TrabajadorDAO {

    private static final String COLLECTION_NAME = "trabajador";

    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection(COLLECTION_NAME);
            for (Document documento : collection.find()) {
                try {
                    trabajadores.add(convertirDocumento(documento));
                } catch (Exception documentoEx) {
                    System.err.println("Error procesando un trabajador: " + documentoEx.getMessage());
                    documentoEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los trabajadores: " + e.getMessage());
            e.printStackTrace();
        }

        return trabajadores;
    }

    private Trabajador convertirDocumento(Document documento) {
        Trabajador trabajador = new Trabajador();
        ObjectId id = documento.getObjectId("_id");

        if (id != null) {
            trabajador.setId(id.toString());
        }

        trabajador.setNombre(documento.getString("nombre"));
        trabajador.setApellidos(documento.getString("apellidos"));
        trabajador.setDni(documento.getString("dni"));
        trabajador.setCorreo_electronico(documento.getString("correo_electronico"));
        trabajador.setNumero_ss(documento.getString("numero_ss"));
        trabajador.setCargo(convertirCargo(documento.getString("cargo")));
        trabajador.setSalario(obtenerDouble(documento.get("salario")));
        trabajador.setTipo_jornada(documento.getString("tipo_jornada"));
        trabajador.setDireccion(convertirDireccion(documento.get("direccion")));

        Document datosBancarios = obtenerDocumento(documento.get("datos_bancarios"));
        if (datosBancarios != null) {
            trabajador.setIban(datosBancarios.getString("iban"));
        }

        return trabajador;
    }

    private Direccion convertirDireccion(Object valor) {
        Direccion direccion = new Direccion();
        Document documento = obtenerDocumento(valor);

        if (documento == null) {
            return direccion;
        }

        direccion.setCalle(documento.getString("calle"));
        direccion.setCiudad(documento.getString("ciudad"));
        direccion.setCodigoPostal(convertirCodigoPostal(documento.getString("codigo_postal")));
        return direccion;
    }

    private Document obtenerDocumento(Object valor) {
        if (valor instanceof Document) {
            return (Document) valor;
        }
        return null;
    }

    private int convertirCodigoPostal(String valor) {
        try {
            if (valor == null || valor.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
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

    private Trabajador.Cargo convertirCargo(String valor) {
        if ("Tanatopractor".equalsIgnoreCase(valor)) {
            return Trabajador.Cargo.TANATOPRACTOR;
        }
        if ("Agente Funerario".equalsIgnoreCase(valor)) {
            return Trabajador.Cargo.AGENTE_FUNERARIO;
        }
        if ("Recepcionista".equalsIgnoreCase(valor)) {
            return Trabajador.Cargo.RECEPCIONISTA;
        }
        if ("Director".equalsIgnoreCase(valor)) {
            return Trabajador.Cargo.DIRECTOR;
        }
        return Trabajador.Cargo.CONDUCTOR;
    }

    private String obtenerNombreCargo(Trabajador.Cargo cargo) {
        if (cargo == Trabajador.Cargo.TANATOPRACTOR) {
            return "Tanatopractor";
        }
        if (cargo == Trabajador.Cargo.AGENTE_FUNERARIO) {
            return "Agente Funerario";
        }
        if (cargo == Trabajador.Cargo.RECEPCIONISTA) {
            return "Recepcionista";
        }
        if (cargo == Trabajador.Cargo.DIRECTOR) {
            return "Director";
        }
        return "Conductor";
    }

    private Document crearDireccion(Trabajador trabajador) {
        Direccion direccion = trabajador.getDireccion();
        return new Document()
                .append("calle", direccion.getCalle())
                .append("ciudad", direccion.getCiudad())
                .append("codigo_postal", String.format("%05d", direccion.getCodigoPostal()));
    }

    private Document crearDatosBancarios(Trabajador trabajador) {
        return new Document()
                .append("iban", trabajador.getIban())
                .append("banco", "");
    }

    private Document crearDocumento(Trabajador trabajador) {
        return new Document()
                .append("nombre", trabajador.getNombre())
                .append("apellidos", trabajador.getApellidos())
                .append("dni", trabajador.getDni())
                .append("correo_electronico", trabajador.getCorreo_electronico())
                .append("direccion", crearDireccion(trabajador))
                .append("numero_ss", trabajador.getNumero_ss())
                .append("datos_bancarios", crearDatosBancarios(trabajador))
                .append("cargo", obtenerNombreCargo(trabajador.getCargo()))
                .append("salario", trabajador.getSalario())
                .append("tipo_jornada", trabajador.getTipo_jornada());
    }

    public boolean insertarTrabajador(Trabajador trabajador) {
        try {
            Document documento = crearDocumento(trabajador);
            MongoConfig.getCollection(COLLECTION_NAME).insertOne(documento);
            trabajador.setId(documento.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar el trabajador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarTrabajador(Trabajador trabajador) {
        try {
            if (!ObjectId.isValid(trabajador.getId())) {
                return false;
            }

            Document cambios = crearDocumento(trabajador);
            long encontrados = MongoConfig.getCollection(COLLECTION_NAME)
                    .updateOne(new Document("_id", new ObjectId(trabajador.getId())), new Document("$set", cambios))
                    .getMatchedCount();
            return encontrados > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar el trabajador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarTrabajador(String id) {
        try {
            if (!ObjectId.isValid(id)) {
                return false;
            }

            long eliminados = MongoConfig.getCollection(COLLECTION_NAME)
                    .deleteOne(new Document("_id", new ObjectId(id)))
                    .getDeletedCount();
            return eliminados > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar el trabajador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
