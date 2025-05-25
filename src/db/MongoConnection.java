package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {
    private static final String connectionString = "mongodb+srv://Tuptuus:zaqwsX12@rentalvehicles.wymugcu.mongodb.net/";
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    public static void init(){
        mongoClient = MongoClients.create(connectionString);
        mongoDatabase = mongoClient.getDatabase("Rental");
    }
    public static MongoCollection<Document> getCarsCollection(){
        return mongoDatabase.getCollection("Cars");
    }
    public static MongoCollection<Document> getBikesCollection(){
        return mongoDatabase.getCollection("Bikes");
    }
    public static MongoCollection<Document> getScooterCollection(){
        return mongoDatabase.getCollection("Scooter");
    }
    public static MongoCollection<Document> getRentedCollection(){
        return mongoDatabase.getCollection("Rented");
    }
    public static void printCollections(){
        System.out.println("Collections:");
        mongoDatabase.listCollectionNames().forEach(name->System.out.println(" * " + name));
    }
}
