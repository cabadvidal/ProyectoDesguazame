/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

/**
 *
 * @author Charlie
 */
public class CarPart {
    
    private int id;
    private String description;
    private int weight;
    private double price;
    private String reference;
    private String year;
    private String[] imagen;
    private String categories;
    private String sale;
    private String model;
    private String brand;
    private String company;

    public CarPart(int id, String description, int weight, double price, String reference, String year, String[] imagen, String categories, String sale, String model, String brand, String company) {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.price = price;
        this.reference = reference;
        this.year = year;
        this.imagen = imagen;
        this.categories = categories;
        this.sale = sale;
        this.model = model;
        this.brand = brand;
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String[] getImagen() {
        return imagen;
    }

    public void setImagen(String[] imagen) {
        this.imagen = imagen;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

        
}
