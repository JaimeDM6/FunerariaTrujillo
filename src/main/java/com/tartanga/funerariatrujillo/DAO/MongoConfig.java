package com.tartanga.funerariatrujillo.DAO;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConfig {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "FunerariaTrujillo";

    public static MongoCollection<Document> getCollection(String collectionName) {
        MongoClient mongoClient = MongoClients.create(URI);
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        return database.getCollection(collectionName);
    }
}
