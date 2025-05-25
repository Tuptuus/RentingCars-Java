package model;

import java.util.Date;

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

    public Vehicle(String id, String brand, String model, double priceperhour, String status, String color, int year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.priceperhour = priceperhour;
        this.status = status;
        this.color = color;
        this.year = year;
    }
    public String getId() {
        return id;
    }
    public String getBrand() {
        return brand;
    }
    public String getModel() {
        return model;
    }
    public double getPricePerHour() {
        return priceperhour;
    }
    public String getStatus() {
        return status;
    }
    public String getColor() {
        return color;
    }
    public int getYear() {
        return year;
    }
    public Date getRentedFrom() {
        return rentedFrom;
    }
    public Date getRentedTo() {
        return rentedTo;
    }
    public void setRentedFrom(Date rentedFrom) {
        this.rentedFrom = rentedFrom;
    }
    public void setRentedTo(Date rentedTo) {
        this.rentedTo = rentedTo;
    }
    public abstract double calculatePrice(int hours);
    public String toString() {
        return String.format("%s %s (%s) %.2f zł/h [%s]", brand, model, id, priceperhour, status);
    }
}
