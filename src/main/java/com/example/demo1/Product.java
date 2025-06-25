package com.example.demo1;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty price;
    private final StringProperty imagePath;

    public Product(int id, String name, int price, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleIntegerProperty(price);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public int getPrice() { return price.get(); }
    public String getImagePath() { return imagePath.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty priceProperty() { return price; }
    public StringProperty imagePathProperty() { return imagePath; }
}
