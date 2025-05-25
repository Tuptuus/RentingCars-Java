// src/db/VehicleRepository.java
package db;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.eq;
import model.Bike;
import model.Car;
import model.Scooter;
import model.Vehicle;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VehicleRepository {

    private static MongoCollection<Document> carsCol()    { return MongoConnection.getCarsCollection(); }
    private static MongoCollection<Document> bikesCol()   { return MongoConnection.getBikesCollection(); }
    private static MongoCollection<Document> scootersCol(){ return MongoConnection.getScooterCollection(); }
    private static MongoCollection<Document> rentedCol()  { return MongoConnection.getRentedCollection(); }

    /** wszystkie auta oprócz RENTED */
    public static List<Car> findAllCars() {
        return mapDocuments(carsCol().find(), "CAR", false);
    }
    /** wszystkie rowery oprócz RENTED */
    public static List<Bike> findAllBikes() {
        return mapDocuments(bikesCol().find(), "BIKE", false);
    }
    /** wszystkie skutery oprócz RENTED */
    public static List<Scooter> findAllScooters() {
        return mapDocuments(scootersCol().find(), "SCOOTER", false);
    }
    /** wszystkie pojazdy o statusie RENTED */
    public static List<Vehicle> findAllRented() {
        List<Vehicle> rented = new ArrayList<>();
        rented.addAll(mapDocuments(carsCol().find(),     "CAR",     true));
        rented.addAll(mapDocuments(bikesCol().find(),    "BIKE",    true));
        rented.addAll(mapDocuments(scootersCol().find(), "SCOOTER", true));
        return rented;
    }

    /**
     * Oznacza pojazd jako wypożyczony (status=RENTED + daty).
     */
    public static boolean rentVehicle(Vehicle v, Date from, Date to, double discountPercent) {
        MongoCollection<Document> col = getCollectionFor(v);
        String id = v.getId();
        UpdateResult res = col.updateOne(
                eq("_id", id),
                Updates.combine(
                        Updates.set("status", "RENTED"),
                        Updates.set("rentedFrom", from),
                        Updates.set("rentedTo", to),
                        Updates.set("discountPercent", discountPercent)
                )
        );
        return res.getModifiedCount() == 1;
    }

    /**
     * Anuluje wypożyczenie (status=AVAILABLE + usunięcie dat).
     */
    public static boolean cancelRental(Vehicle v) {
        MongoCollection<Document> col = getCollectionFor(v);
        String id = v.getId();
        UpdateResult res = col.updateOne(
                eq("_id", id),
                Updates.combine(
                        Updates.set("status", "AVAILABLE"),
                        Updates.unset("rentedFrom"),
                        Updates.unset("rentedTo"),
                        Updates.unset("discountPercent")
                )
        );
        return res.getModifiedCount() == 1;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Vehicle> List<T> mapDocuments(FindIterable<Document> docs,
                                                            String type,
                                                            boolean onlyRented) {
        List<Vehicle> result = new ArrayList<>();
        for (Document doc : docs) {
            String status = doc.getString("status");
            boolean isRented = "RENTED".equalsIgnoreCase(status);
            if (onlyRented ^ isRented) continue;

            String id       = doc.get("_id").toString();
            String brand    = doc.getString("brand");
            String model    = doc.getString("model");
            Object priceObj = doc.containsKey("basePricePerHour")
                    ? doc.get("basePricePerHour")
                    : doc.get("priceperhour");
            double price    = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : 0.0;
            String color    = doc.getString("color");
            Integer year    = doc.getInteger("year");
            Date rentedFrom = doc.getDate("rentedFrom");
            Date rentedTo   = doc.getDate("rentedTo");
            double discount = doc.containsKey("discountPercent")
                    ? doc.getDouble("discountPercent")
                    : 0.0;

            Vehicle v;
            switch (type) {
                case "CAR":
                    v = new Car(id, brand, model, price, status, color, year, doc.getString("licensePlate"));
                    break;
                case "BIKE":
                    v = new Bike(id, brand, model, price, status, color, year, doc.getString("serialNumber"));
                    break;
                case "SCOOTER":
                    v = new Scooter(id, brand, model, price, status, color, year, doc.getString("serialNumber"));
                    break;
                default:
                    continue;
            }
            v.setRentedFrom(rentedFrom);
            v.setRentedTo(rentedTo);
            v.setDiscountPercent(discount);

            result.add(v);
        }
        return (List<T>) result;
    }

    /**
     * Zwraca kolekcję MongoDB odpowiednią do typu pojazdu.
     */
    private static MongoCollection<Document> getCollectionFor(Vehicle v) {
        if (v instanceof Car) {
            return carsCol();
        } else if (v instanceof Bike) {
            return bikesCol();
        } else if (v instanceof Scooter) {
            return scootersCol();
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ pojazdu: " + v.getClass());
        }
    }
}
