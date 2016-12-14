package com.vas.vassdk.bean;

public class VasOrderInfo
{

    private String goodsId;

    private String goodsName;

    private String goodsDesc;

    private String quantifier;

    private String cpOrderId;
    
    private String orderId;

    // private double price;

    private int count;

    private double amount;

    private String callbackUrl;

    private String extrasParams;

    private String externalParams;

    public String getGoodsId()
    {
        return goodsId;
    }

    public void setGoodsId(String goodsId)
    {
        this.goodsId = goodsId;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc()
    {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc)
    {
        this.goodsDesc = goodsDesc;
    }

    public String getQuantifier()
    {
        return quantifier;
    }

    public void setQuantifier(String quantifier)
    {
        this.quantifier = quantifier;
    }

    public String getCpOrderId()
    {
        return cpOrderId;
    }

    public void setCpOrderId(String cpOrderId)
    {
        this.cpOrderId = cpOrderId;
    }

    // public double getPrice()
    // {
    // return price;
    // }
    //
    // public void setPrice(double price)
    // {
    // this.price = price;
    // }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public String getCallbackUrl()
    {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    public String getExtrasParams()
    {
        return extrasParams;
    }

    public void setExtrasParams(String extrasParams)
    {
        this.extrasParams = extrasParams;
    }

    public String getExternalParams()
    {
        return externalParams;
    }

    public void setExternalParams(String externalParams)
    {
        this.externalParams = externalParams;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    

}
