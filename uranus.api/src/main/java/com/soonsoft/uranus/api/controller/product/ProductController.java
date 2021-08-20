package com.soonsoft.uranus.api.controller.product;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.web.mvc.model.PagingList;
import com.soonsoft.uranus.web.mvc.model.RequestData;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/product")
public class ProductController {

    @GetMapping("/list")
    public PagingList<String> getProductList() {
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
    public void addProduct(@RequestBody RequestData parameter) {
    }

    @PutMapping("/edit")
    public void editProduct(@RequestBody RequestData parameter) {
    }

    @DeleteMapping("/remove")
    public void editProduct(String productId) {
    }

    @RequestMapping(value = "/check", method = RequestMethod.HEAD)
    public void checkProductId(String productId) {
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
    }
    
}
