package com.rpos.pos.data.local.entity;

public class CurrencyItem {

    private Integer id;
    private String currencyName;
    private String country;
    private String symbol;
    private String fractionName;
    private float fractionUnits;
    private float smallestFraction;
    private String numberFormat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getFractionName() {
        return fractionName;
    }

    public void setFractionName(String fractionName) {
        this.fractionName = fractionName;
    }

    public float getFractionUnits() {
        return fractionUnits;
    }

    public void setFractionUnits(float fractionUnits) {
        this.fractionUnits = fractionUnits;
    }

    public float getSmallestFraction() {
        return smallestFraction;
    }

    public void setSmallestFraction(float smallestFraction) {
        this.smallestFraction = smallestFraction;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public CurrencyItem() {
    }

    public CurrencyItem(Integer id, String currencyName, String country, String symbol, String fractionName, float fractionUnits, float smallestFraction, String numberFormat) {
        this.id = id;
        this.currencyName = currencyName;
        this.country = country;
        this.symbol = symbol;
        this.fractionName = fractionName;
        this.fractionUnits = fractionUnits;
        this.smallestFraction = smallestFraction;
        this.numberFormat = numberFormat;
    }
}
