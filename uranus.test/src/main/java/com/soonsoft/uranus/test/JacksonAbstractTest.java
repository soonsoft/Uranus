package com.soonsoft.uranus.test;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonAbstractTest {

    public static void main(String[] args) throws Exception {
        NormalOrder normalOrder = new NormalOrder();
        normalOrder.setOrderName("普通订单");
        normalOrder.setOrderNo("123");
        normalOrder.setOrderType(Byte.valueOf("1"));

        Result result = new Result();
        result.setOrder(normalOrder);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        System.out.println("JSON数据：" + json);

        Result resp = objectMapper.readValue(json, new TypeReference<Result>() {});
        System.out.println(resp.getOrder().getClass().getName());
    }

    //@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "orderType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = NormalOrder.class, name = "1"),
        @JsonSubTypes.Type(value = SubscriptionOrder.class, name = "2")
    })
    public static abstract class BaseOrder {
        private String orderNo;
        private Byte orderType;

        public String getOrderNo() {
            return orderNo;
        }
        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
        public Byte getOrderType() {
            return orderType;
        }
        public void setOrderType(Byte orderType) {
            this.orderType = orderType;
        }
    }

    //@JsonTypeName("normal")
    public static class NormalOrder extends BaseOrder {
        private String orderName;

        public String getOrderName() {
            return orderName;
        }
        public void setOrderName(String orderName) {
            this.orderName = orderName;
        }
    }

    //@JsonTypeName("subscription")
    public static class SubscriptionOrder extends BaseOrder {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class Result {
        private BaseOrder order;

        public BaseOrder getOrder() {
            return order;
        }
        public void setOrder(BaseOrder order) {
            this.order = order;
        }
    }
    
}
