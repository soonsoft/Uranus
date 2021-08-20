package com.soonsoft.uranus.api.model.req;

import javax.validation.constraints.NotBlank;


public class ProductReq {

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @NotBlank(message = "商品编号不能为空")
    private String productId;

    public ProductReq() {

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    
}
