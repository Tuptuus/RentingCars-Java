// src/model/Vehicle.java
package model;

import java.util.Date;
import java.time.Duration;

public abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected double priceperhour;
    protected String status;
    protected String color;
    protected int year;
    protected Date rentedFrom;
    protected Date rentedTo;
    protected double discountPercent;      // <--- nowe pole

    public Vehicle(String id,
                   String brand,
                   String model,
                   double priceperhour,
                   String status,
                   String color,
                   int year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.priceperhour = priceperhour;
        this.status = status;
        this.color = color;
        this.year = year;
        this.discountPercent = 0.0;
    }

    /**
     * Oblicza cenę na podstawie liczby godzin.
     * Implementowane przez podklasy.
     */
    public abstract double calculatePrice(int hours);

    /**
     * Oblicza cenę na podstawie okresu i zniżki.
     */
    public double calculatePrice(Date from, Date to, double discountPercent) {
        long hours = Duration.between(from.toInstant(), to.toInstant()).toHours();
        if (hours < 0) hours = 0;
        double full = calculatePrice((int) hours);
        return full * (1 - discountPercent / 100.0);
    }

    // --- getter/setter dla zniżki ---
    public double getDiscountPercent() {
        return discountPercent;
    }
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    // pozostałe gettery/settery
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getPricePerHour() { return priceperhour; }
    public String getStatus() { return status; }
    public String getColor() { return color; }
    public int getYear() { return year; }
    public Date getRentedFrom() { return rentedFrom; }
    public Date getRentedTo() { return rentedTo; }
    public void setRentedFrom(Date rentedFrom) { this.rentedFrom = rentedFrom; }
    public void setRentedTo(Date rentedTo)     { this.rentedTo   = rentedTo;   }

    @Override
    public String toString() {
        return String.format("%s %s (%s) %.2f zł/h [%s]",
                brand, model, id, priceperhour, status);
    }
}
