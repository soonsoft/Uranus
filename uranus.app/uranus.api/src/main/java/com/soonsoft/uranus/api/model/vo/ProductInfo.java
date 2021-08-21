package com.soonsoft.uranus.api.model.vo;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;


public class ProductInfo {

    @NotBlank(message = "商品名称不能为空")
    @Length(max = 50, message = "商品名称不能超过50个字符")
    private String productName;

    @Valid
    private ProductDetail productDetail;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductDetail getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }
    
    
}
