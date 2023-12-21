package com.example.exam;

public class FoodItem {
    String name;
    int Kcal;
    int protein;
    int resId;

    public FoodItem(String name, int Kcal) {
        this.name = name;
        this.Kcal = Kcal;
    }

    public FoodItem(String name, int Kcal, int protetin, int resId) {
        this.name = name;
        this.Kcal = Kcal;
        this.protein = protetin;
        this.resId = resId;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getKcal() {
        return Kcal;
    }

    public void setKcal(int kcal) {
        this.Kcal = kcal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
