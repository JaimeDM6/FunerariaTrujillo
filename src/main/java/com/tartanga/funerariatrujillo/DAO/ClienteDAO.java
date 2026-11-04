package com.tartanga.funerariatrujillo.DAO;

import com.tartanga.funerariatrujillo.Objects.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "FunerariaTrujillo";
    private static final String COLLECTION_NAME = "cliente";

    public List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            for (Document doc : collection.find()) {
                try {
                    Cliente c = new Cliente();

                    c.setId(doc.getObjectId("_id").toString());
                    c.setNombre(doc.getString("nombre"));
                    c.setApellidos(doc.getString("apellidos"));
                    c.setDni(doc.getString("dni"));
                    c.setCorreoElectronico(doc.getString("correo_electronico"));
                    c.setTelefono(doc.getString("telefono"));
                    c.setFechaRegistro(doc.getDate("fecha_registro"));

                    Document dirDoc = (Document) doc.get("direccion");
                    if (dirDoc != null) {
                        Direccion dir = new Direccion();
                        dir.setCalle(dirDoc.getString("calle"));
                        dir.setCiudad(dirDoc.getString("ciudad"));

                        Object cpObj = dirDoc.get("codigo_postal");
                        if (cpObj != null && !cpObj.toString().isEmpty()) {
                            dir.setCodigoPostal(Integer.parseInt(cpObj.toString()));
                        }

                        c.setDireccion(dir);
                    }

                    clientes.add(c);
                } catch (NumberFormatException docEx) {
                    System.err.println("Error procesando el cliente con ID: " + doc.getObjectId("_id"));
                    docEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error general de conexión a MongoDB.");
            e.printStackTrace();
        }

        return clientes;
    }

    public boolean actualizarCliente(Cliente c) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document filtro = new Document("_id", new org.bson.types.ObjectId(c.getId()));

            Document updateDocs = new Document("$set", new Document()
                    .append("nombre", c.getNombre())
                    .append("apellidos", c.getApellidos())
                    .append("dni", c.getDni())
                    .append("telefono", c.getTelefono())
                    .append("correo_electronico", c.getCorreoElectronico())
            );

            collection.updateOne(filtro, updateDocs);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente en MongoDB.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarCliente(Cliente c) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document dirDoc = new Document()
                    .append("calle", c.getDireccion().getCalle())
                    .append("ciudad", c.getDireccion().getCiudad())
                    .append("codigo_postal", String.valueOf(c.getDireccion().getCodigoPostal()));

            Document doc = new Document()
                    .append("nombre", c.getNombre())
                    .append("apellidos", c.getApellidos())
                    .append("dni", c.getDni())
                    .append("telefono", c.getTelefono())
                    .append("correo_electronico", c.getCorreoElectronico())
                    .append("fecha_registro", c.getFechaRegistro())
                    .append("direccion", dirDoc);

            collection.insertOne(doc);

            c.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar nuevo cliente en MongoDB.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarCliente(String id) {
        try (MongoClient mongoClient = MongoClients.create(URI)) {
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document filtro = new Document("_id", new org.bson.types.ObjectId(id));
            collection.deleteOne(filtro);

            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar el cliente en MongoDB.");
            e.printStackTrace();
            return false;
        }
    }
}
