package ru.example.forkjoinframework;

public class Item {
    private final String name;
    private final boolean isAggregated;

    public Item(String name, boolean isAggregated) {
        this.name = name;
        this.isAggregated = isAggregated;
    }

    public String getName() {
        return name;
    }

    public boolean isAggregated() {
        return isAggregated;
    }
}