package com.example.calorieguide.Utils;

import java.util.List;

public class foodModel {
    private String label;
    private List<String> weightLabels;
    private List <Integer> weights;
    private double energyKcal;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double fiber;
    public foodModel() {}

    public foodModel(String label, List<String> weightLabels, List <Integer> weights, double energyKcal, double protein, double fat, double carbohydrates, double fiber) {
        this.label = label;
        this.weightLabels = weightLabels;
        this.weights = weights;
        this.energyKcal = energyKcal;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
    }



    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getWeightLabels() {
        return weightLabels;
    }

    public void setWeightLabels(List<String> weightLabels) {
        this.weightLabels = weightLabels;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public void setWeights(List<Integer> weights) {
        this.weights = weights;
    }

    public double getEnergyKcal() {
        return energyKcal;
    }

    public void setEnergyKcal(double energyKcal) {
        this.energyKcal = energyKcal;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }
}
