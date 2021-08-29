package com.soonsoft.uranus.api.controller.example;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.soonsoft.uranus.api.model.vo.ProductInfo;
import com.soonsoft.uranus.api.model.vo.ProductVO;
import com.soonsoft.uranus.web.mvc.model.PagingList;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/product")
public class ProductController {

    @GetMapping("/list")
    public PagingList<String> getProductList(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        List<String> list = new ArrayList<>();
        list.add("iPhone 11");
        list.add("iPhone 11 Pro");
        list.add("iPhone 11 Pro MAX");
        list.add("iPhone 12");
        list.add("iPhone 12 Pro");
        list.add("iPhone 12 Pro MAX");

        return new PagingList<>(list);
    }

    @PostMapping("/add")
    public void addProduct(@Valid @RequestBody ProductInfo product) {
    }

    @PutMapping("/edit")
    public void editProduct(@Valid @RequestBody ProductVO product) {
    }

    @DeleteMapping("/remove")
    public void editProduct(String productId) {
    }

    @RequestMapping(value = "/check", method = RequestMethod.HEAD)
    public void checkProductId(String productId) {
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
    }
    
}
