package com.example.allgoods.model;

import com.example.allgoods.utils.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductModel {
    private int id;
    private String name;
    private List<String> images;
    private double price;
    private String description;
    private Category category;
    private String sellerId;
    private int quantity = 1;
    private boolean isFav = false;
    private Map<String, Integer> sizesQuantity = new HashMap<>();

    public ProductModel() {}



    // Constructor  Seller
    public ProductModel(String name, List<String> images, double price, String description,
                        Category category, String sellerId, Map<String, Integer> sizesQuantity) {
        this.name = name;
        this.images = images;
        this.price = price;
        this.description = description;
        this.category = category;
        this.sellerId = sellerId;
        this.sizesQuantity = sizesQuantity;
    }



    // 3. Constructor للـ Customer (Dummy Data & Cart)
    public ProductModel(int id, String name, List<String> images, double price, Category category) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.price = price;
        this.category = category;
    }

    public ProductModel(int id, String name, List<String> images, double price, Category category,boolean isFav ) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.price = price;
        this.category = category;
        this.isFav = isFav;
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Map<String, Integer> getSizesQuantity() { return sizesQuantity; }
    public void setSizesQuantity(Map<String, Integer> sizesQuantity) { this.sizesQuantity = sizesQuantity; }

    public boolean isFav() {return isFav;}

    public void setFav(boolean fav) {isFav = fav;}


    public double getPrice() {return price;}

    public void setPrice(double price) {this.price = price; }
}