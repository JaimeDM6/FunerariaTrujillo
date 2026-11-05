package com.tartanga.funerariatrujillo.DAO;

import com.tartanga.funerariatrujillo.Objects.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();

        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("cliente");

            for (Document doc : collection.find()) {
                try {
                    Cliente c = new Cliente();

                    c.setId(doc.getObjectId("_id").toString());
                    c.setNombre(doc.getString("nombre"));
                    c.setApellidos(doc.getString("apellidos"));
                    c.setDni(doc.getString("dni"));
                    c.setCorreo_electronico(doc.getString("correo_electronico"));
                    c.setTelefono(doc.getString("telefono"));
                    c.setFecha_registro(doc.getDate("fecha_registro"));

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
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los clientes: " + e.getMessage());
        }

        return clientes;
    }

    public boolean actualizarCliente(Cliente c) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("cliente");

            Document filtro = new Document("_id", new org.bson.types.ObjectId(c.getId()));

            Document updateDocs = new Document("$set", new Document()
                    .append("nombre", c.getNombre())
                    .append("apellidos", c.getApellidos())
                    .append("dni", c.getDni())
                    .append("telefono", c.getTelefono())
                    .append("correo_electronico", c.getCorreo_electronico())
            );

            collection.updateOne(filtro, updateDocs);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar el cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean insertarCliente(Cliente c) {
        try {
            MongoCollection<Document> collection = MongoConfig.getCollection("cliente");

            Document dirDoc = new Document()
                    .append("calle", c.getDireccion().getCalle())
                    .append("ciudad", c.getDireccion().getCiudad())
                    .append("codigo_postal", String.valueOf(c.getDireccion().getCodigoPostal()));

            Document doc = new Document()
                    .append("nombre", c.getNombre())
                    .append("apellidos", c.getApellidos())
                    .append("dni", c.getDni())
                    .append("telefono", c.getTelefono())
                    .append("correo_electronico", c.getCorreo_electronico())
                    .append("fecha_registro", c.getFecha_registro())
                    .append("direccion", dirDoc);

            collection.insertOne(doc);

            c.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error al insertar el cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCliente(String id) {
        try {
            Document filtro = new Document("_id", new org.bson.types.ObjectId(id));
            MongoConfig.getCollection("cliente").deleteOne(filtro);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar el cliente: " + e.getMessage());
            return false;
        }
    }
}
