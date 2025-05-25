package model;

public class Scooter extends Vehicle {
    private String serialNumber;
    public Scooter(String id, String brand, String model, double priceperhour, String status, String color, int year, String serialNumber) {
        super(id, brand, model, priceperhour, status, color, year);
        this.serialNumber = serialNumber;
    }
    @Override
    public double calculatePrice(int hours) {
        return priceperhour * hours;
    }
    @Override
    public String toString() {
        return "[Scooter] " + super.toString();
    }
    public String getSerialNumber(){
        return serialNumber;
    }
}
