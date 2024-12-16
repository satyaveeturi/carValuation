package com.carvaluation;

public class CarDetails {
    private String registration;
    private String manufacturer;
    private String model;
    private String year;

    public CarDetails(String registration, String manufacturer, String model, String year) {
        this.registration = registration;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
    }

    public String getRegistration() { return registration; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getYear() { return year; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", registration, manufacturer, model, year);
    }

    public boolean matches(CarDetails other) {
        if (other == null) return false;
        return this.manufacturer.equalsIgnoreCase(other.manufacturer)
            && this.model.equalsIgnoreCase(other.model)
            && this.year.equals(other.year);
    }
}