package model;

public class Car extends Vehicle {
    private String licensePlate;
    public Car(String id, String brand, String model, double priceperhour, String status, String color, int year, String licensePlate) {
        super(id, brand, model, priceperhour, status, color, year);
        this.licensePlate = licensePlate;
    }

    @Override
    public double calculatePrice(int hours) {
        return hours * priceperhour;
    }

    @Override
    public String toString() {
        return "[Car] " + super.toString();
    }
    public String getLicensePlate(){
        return licensePlate;
    }
}
