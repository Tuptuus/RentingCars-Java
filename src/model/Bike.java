package model;

public class Bike extends Vehicle {
    private String serialNumber;
    public Bike(String id, String brand, String model, double priceperhour, String status, String color, int year, String serialNumber) {
        super(id, brand, model, priceperhour, status, color, year);
        this.serialNumber = serialNumber;
    }
    @Override
    public double calculatePrice(int hours) {
        return priceperhour * hours;
    }
    @Override
    public String toString() {
        return "[Bike] " + super.toString();
    }
    public String getSerialNumber(){
        return serialNumber;
    }
}
