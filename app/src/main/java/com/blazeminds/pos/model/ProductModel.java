package com.blazeminds.pos.model;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.text.UStringsKt;

public class ProductModel {

    private String prod_id,prod_name,prod_price,brand_id;
    private int prod_qty=0;
    private String prev_prod_qty;
    private String Discount;
    private String TradePrice,isTaxMrp,is_taxable,tax_mrp,mrp_price,tradeOffer,schemeValue,schemeTxt,tax1,tax2,tax3,taxf1,taxf2,taxf3;





    public ProductModel(String prod_id, String prod_name, String prod_price) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_price = prod_price;
    }

    public ProductModel() {
    }

    public String getIs_taxable() {
        return is_taxable;
    }

    public void setIs_taxable(String is_taxable) {
        this.is_taxable = is_taxable;
    }

    public String getSchemeValue() {
        return schemeValue;
    }

    public void setSchemeValue(String schemeValue) {
        this.schemeValue = schemeValue;
    }

    public String getSchemeTxt() {
        return schemeTxt;
    }

    public void setSchemeTxt(String schemeTxt) {
        this.schemeTxt = schemeTxt;
    }

    public String getIsTaxMrp() {
        return isTaxMrp;
    }

    public void setIsTaxMrp(String isTaxMrp) {
        this.isTaxMrp = isTaxMrp;
    }

    public String getTax_mrp() {
        return tax_mrp;
    }

    public void setTax_mrp(String tax_mrp) {
        this.tax_mrp = tax_mrp;
    }

    public String getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(String mrp_price) {
        this.mrp_price = mrp_price;
    }

    public String getTradeOffer() {
        return tradeOffer;
    }

    public void setTradeOffer(String tradeOffer) {
        this.tradeOffer = tradeOffer;
    }



    public String getTax1() {
        return tax1;
    }

    public void setTax1(String tax1) {
        this.tax1 = tax1;
    }

    public String getTax2() {
        return tax2;
    }

    public void setTax2(String tax2) {
        this.tax2 = tax2;
    }

    public String getTax3() {
        return tax3;
    }

    public void setTax3(String tax3) {
        this.tax3 = tax3;
    }

    public String getTaxf1() {
        return taxf1;
    }

    public void setTaxf1(String taxf1) {
        this.taxf1 = taxf1;
    }

    public String getTaxf2() {
        return taxf2;
    }

    public void setTaxf2(String taxf2) {
        this.taxf2 = taxf2;
    }

    public String getTaxf3() {
        return taxf3;
    }

    public void setTaxf3(String taxf3) {
        this.taxf3 = taxf3;
    }

    public String getTradePrice() {
        return TradePrice;
    }

    public void setTradePrice(String tradePrice) {
        TradePrice = tradePrice;
    }
    public ProductModel(String prod_id, String prod_name, String prod_price, String brand_id) {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.prod_price = prod_price;
        this.brand_id = brand_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getProd_id() {
        return prod_id;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name() {
        return prod_name;
    }

    public void setProd_name(String prod_name) {
        this.prod_name = prod_name;
    }

    public String getProd_price() {
        return prod_price;
    }

    public void setProd_price(String prod_price) {
        this.prod_price = prod_price;
    }

    public int getProd_qty() {
        return prod_qty;
    }

    public void setProd_qty(int prod_qty) {
        this.prod_qty = prod_qty;
    }

    public String getPrev_prod_qty() {
        return prev_prod_qty;
    }

    public void setPrev_prod_qty(String prev_prod_qty) {
        this.prev_prod_qty = prev_prod_qty;
    }
}

