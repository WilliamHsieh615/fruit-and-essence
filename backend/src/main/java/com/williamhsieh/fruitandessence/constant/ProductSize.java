package com.williamhsieh.fruitandessence.constant;

public enum ProductSize {

    SMALL_300ML("300ml", 10),     // 10 fl oz
    MEDIUM_700ML("700ml", 24),    // 24 fl oz
    LARGE_1900ML("1900ml", 64);

    private final String label;  // 公制容量
    private final int fluidOunce; // 美制容量

    ProductSize(String label, int fluidOunce) {
        this.label = label;
        this.fluidOunce = fluidOunce;
    }

    public String getLabel() {
        return label;
    }

    public int getFluidOunce() {
        return fluidOunce;
    }
}
