package com.soonsoft.uranus.api.model.vo;

import org.hibernate.validator.constraints.URL;

public class ProductDetail {

    @URL(message = "商品URL格式不正确")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
