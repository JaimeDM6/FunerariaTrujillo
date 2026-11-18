package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Configuración de la conexión a MongoDB. Mantiene un único cliente compartido por toda la
 * aplicación.
 *
 * @author Aritz
 */
public class MongoConfig {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "FunerariaTrujillo";

    private static MongoClient mongoClient;

    /**
     * Devuelve una colección de la base de datos, creando antes el cliente de MongoDB si todavía no
     * existe.
     *
     * @param collectionName nombre de la colección
     * @return colección solicitada
     */
    public static synchronized MongoCollection<Document> getCollection(String collectionName) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
        }
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        return database.getCollection(collectionName);
    }
}