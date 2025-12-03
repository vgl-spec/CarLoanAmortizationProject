package com.vismera.models;

/**
 * Model class representing a car available for loan financing.
 * @author Vismerá Inc.
 */
public class Car {
    private int id;
    private String make;
    private String model;
    private int year;
    private String category; // Sports Car, Luxury Sedan, Luxury SUV
    private String color;
    private int mpg;
    private double price;
    private String imagePath;

    public Car() {
    }

    public Car(int id, String make, String model, int year, String category, 
               String color, int mpg, double price, String imagePath) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.category = category;
        this.color = color;
        this.mpg = mpg;
        this.price = price;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getMpg() { return mpg; }
    public void setMpg(int mpg) { this.mpg = mpg; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getFullName() {
        return make + " " + model;
    }

    @Override
    public String toString() {
        return year + " " + make + " " + model;
    }
}
