package com.soonsoft.uranus.api.model.vo;

import javax.validation.constraints.NotBlank;

public class ProductVO extends ProductInfo {

    @NotBlank(message = "商品编号不能为空")
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    
}
