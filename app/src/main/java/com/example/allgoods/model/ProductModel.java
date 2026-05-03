package com.example.allgoods.model;

import com.example.allgoods.utils.Category;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductModel implements Serializable {
    @Exclude
    private String id;
    private String name;
    private String image;
    private List<String> images = new ArrayList<>();
    private double price;
    private String description;
    private Category category;
    private String sellerId;
    private int quantity = 1;
    private boolean isFav = false;
    private Map<String, Integer> sizesQuantity = new HashMap<>();
    private String selectedSize = "";
    private float rating = 0.0f;
    private int reviewCount = 0;

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
    public ProductModel(String id, String name, String image, double price, Category category) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.images = new ArrayList<>();
        if (image != null && !image.isEmpty()) {
            this.images.add(image);
        }
        this.price = price;
        this.category = category;
    }

    public ProductModel(String id, String name, String image, double price, Category category,boolean isFav ) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.images = new ArrayList<>();
        if (image != null && !image.isEmpty()) {
            this.images.add(image);
        }
        this.price = price;
        this.category = category;
        this.isFav = isFav;
    }

    // --- Getters and Setters ---
    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

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

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public boolean isFav() {return isFav;}

    public void setFav(boolean fav) {isFav = fav;}

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
}