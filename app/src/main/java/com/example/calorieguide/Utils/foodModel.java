package com.example.calorieguide.Utils;

import java.util.List;

public class foodModel {
    private String label;
    private List<String> weightLabels;
    private List <Integer> weights;
    public foodModel() {}

    public foodModel(String label, List<String> weightLabels, List <Integer> weights) {
        this.label = label;
        this.weightLabels = weightLabels;
        this.weights = weights;
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
}
