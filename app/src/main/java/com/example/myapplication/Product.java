package com.example.myapplication;

public class Product  {
    private String product_name;
    private String price;
    private String check;
    private String photo;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Product(String product_name, String price, String check, String photo) {
        this.product_name = product_name;
        this.price = price;
        this.check = check;
        this.photo = photo;
    }
}

