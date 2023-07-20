package org.julie;

class CurrencyInfo {
    private String name;
    private float rate;

    public CurrencyInfo(String name, float rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public float getRate() {
        return rate;
    }
}
