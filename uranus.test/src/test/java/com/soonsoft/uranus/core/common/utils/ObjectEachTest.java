package com.soonsoft.uranus.core.common.utils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;

public class ObjectEachTest {
    
    //#region 类型定义

    public static enum Enum1 {
        A,
        B,
        C,
        ;
    }

    public static class Base {
        private String nationality;
        private String ai;
        private String nameEn;
        private String[] certTypes;
        private AddressInfo addressInfo;
        public String getNationality() {
            return nationality;
        }
        public void setNationality(String nationality) {
            this.nationality = nationality;
        }
        public String getAi() {
            return ai;
        }
        public void setAi(String ai) {
            this.ai = ai;
        }
        public String getNameEn() {
            return nameEn;
        }
        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }
        public String[] getCertTypes() {
            return certTypes;
        }
        public void setCertTypes(String[] certTypes) {
            this.certTypes = certTypes;
        }
        public AddressInfo getAddressInfo() {
            return addressInfo;
        }
        public void setAddressInfo(AddressInfo addressInfo) {
            this.addressInfo = addressInfo;
        }
    }

    public static class AddressInfo {
        private String residencyStatus;
        private List<Address> addressList;
        private AddressInfo self;
        public String getResidencyStatus() {
            return residencyStatus;
        }
        public void setResidencyStatus(String residencyStatus) {
            this.residencyStatus = residencyStatus;
        }
        public List<Address> getAddressList() {
            return addressList;
        }
        public void setAddressList(List<Address> addressList) {
            this.addressList = addressList;
        }
        public AddressInfo getSelf() {
            return self;
        }
        public void setSelf(AddressInfo self) {
            this.self = self;
        }
    }

    public static class Address {
        private String addressType;
        private String country;
        public String getAddressType() {
            return addressType;
        }
        public void setAddressType(String addressType) {
            this.addressType = addressType;
        }
        public String getCountry() {
            return country;
        }
        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Taxation {
        private List<String> taxationType;
        public List<String> getTaxationType() {
            return taxationType;
        }
        public void setTaxationType(List<String> taxationType) {
            this.taxationType = taxationType;
        }
    }

    //#endregion

    private List<ObjectEach.EachNode> visitedNodes;

    private AtomicInteger visitCount;

    @BeforeEach
    void setUp() {
        visitedNodes = new ArrayList<>();
        visitCount = new AtomicInteger(0);
    }

    //#region 基本单元测试

    @Test
    void testForEachWithSimpleString() {
        String testString = "hello";
        ObjectEach.of(testString).each(createCollectingAction());
        
        assertEquals(1, visitCount.get());
        assertEquals(testString, visitedNodes.get(0).getValue());
        assertFalse(visitedNodes.get(0).isComplexNode());
    }

    @Test
    void testForEachWithList() {
        List<String> testList = List.of("a", "b", "c");
        ObjectEach.of(testList).each(createCollectingAction());
        
        assertEquals(3, visitCount.get()); // 3 elements
        assertTrue(!visitedNodes.get(0).isComplexNode());
    }

    @Test
    void testForEachWithMap() {
        Map<String, Integer> testMap = Map.of("one", 1, "two", 2);
        ObjectEach.of(testMap).each(createCollectingAction());
        
        assertTrue(visitCount.get() == 2);
        assertTrue(!visitedNodes.get(0).isComplexNode());
    }

    @Test
    void testDeepEachWithNullAction() {
        ObjectEach.of("test").deepEach(null);
        assertEquals(0, visitCount.get());
    }

    @Test
    void testDeepEachWithMixedTypes() {
        Object[] mixedArray = {"string", 42, true, List.of("nested")};
        ObjectEach.of(mixedArray).deepEach(createCollectingAction());
        
        assertTrue(visitCount.get() == 5); // Array + 4 elements + nested list element
    }

    @Test
    void testOfWithComplexObject() {
        Map<String, Object> testMap = Map.of("key", "value");
        ObjectEach objectEach = ObjectEach.of(testMap);
        assertNotNull(objectEach);
    }

    @Test
    void testForEachWithNullTarget() {
        ObjectEach.of(null).each(createCollectingAction());
        assertEquals(0, visitCount.get());
    }

    @Test
    void testDeepEachWithNestedList() {
        List<Object> nestedList = List.of("a", List.of("b", "c"), "d");
        ObjectEach.of(nestedList).deepEach(createCollectingAction());
        
        assertTrue(visitCount.get() == 5); // inner list + all elements
    }

    @Test
    void testForEachAndDeepEachComparison() {
        List<Object> nested = List.of("a", List.of("b", "c"));
        
        visitCount.set(0);
        ObjectEach.of(nested).each(createCollectingAction());
        int forEachCount = visitCount.get();
        
        visitCount.set(0);
        ObjectEach.of(nested).deepEach(createCollectingAction());
        int deepEachCount = visitCount.get();
        
        assertTrue(deepEachCount > forEachCount, 
            "deepEach should visit more nodes than forEach for nested structures");
    }

    private Action1<ObjectEach.EachNode> createCollectingAction() {
        return node -> {
            visitedNodes.add(node);
            visitCount.incrementAndGet();
        };
    }

    @Test
    void testForEachWithArray() {
        int[] testArray = {1, 2, 3};
        ObjectEach.of(testArray).each(createCollectingAction());
        
        assertEquals(3, visitCount.get()); // 3 elements
        assertTrue(!visitedNodes.get(0).isComplexNode()); // 根节点不会被迭代到
    }

    @Test
    void testDeepEachWithNullTarget() {
        ObjectEach.of(null).deepEach(createCollectingAction());
        assertEquals(0, visitCount.get());
    }

    @Test
    void testDeepEachWithSimpleValue() {
        String testString = "simple";
        ObjectEach.of(testString).deepEach(createCollectingAction());
        
        assertEquals(1, visitCount.get());
        assertEquals(testString, visitedNodes.get(0).getValue());
    }

    @Test
    void testForEachWithNullAction() {
        ObjectEach.of("test").each(null);
        assertEquals(0, visitCount.get());
    }

    @Test
    void testForEachWithInteger() {
        Integer testInt = 42;
        ObjectEach.of(testInt).each(createCollectingAction());
        
        assertEquals(1, visitCount.get());
        assertEquals(testInt, visitedNodes.get(0).getValue());
    }

    @Test
    void testDeepEachWithComplexStructure() {
        Map<String, Object> complex = Map.of(
            "users", List.of(
                Map.of("name", "Alice", "scores", new int[]{90, 85}),
                Map.of("name", "Bob", "scores", new int[]{78, 92})
            ),
            "metadata", Map.of("version", 1.0)
        );
        
        ObjectEach.of(complex).deepEach(createCollectingAction());
        
        assertTrue(visitCount.get() > 10); // Should visit all nested elements
    }

    @Test
    void testDeepEachWithEmptyCollections() {
        Map<String, Object> testData = Map.of(
            "emptyList", Collections.emptyList(),
            "emptyMap", Collections.emptyMap(),
            "value", "test"
        );
        
        ObjectEach.of(testData).deepEach(createCollectingAction());
        
        assertTrue(visitCount.get() == 3); // Outer map + 3 entries
    }

    @Test
    void testNodeProperties() {
        Map<String, String> testMap = Map.of("key", "value");
        ObjectEach.of(testMap).deepEach(node -> {
            assertNotNull(node.getKey());
            assertNotNull(node.getValue());
            assertNotNull(node.getParentPath());
            assertNotNull(node.isComplexNode());
        });
    }

    @Test
    void testOfWithNull() {
        ObjectEach objectEach = ObjectEach.of(null);
        assertNotNull(objectEach);
    }

    @Test
    void testOfWithString() {
        String testString = "test";
        ObjectEach objectEach = ObjectEach.of(testString);
        assertNotNull(objectEach);
    }

    @Test
    void testDeepEachWithNestedMap() {
        Map<String, Object> nestedMap = Map.of(
                "name", "test",
                "details", Map.of("age", 25, "active", true)
        );
        ObjectEach.of(nestedMap).deepEach(createCollectingAction());

        assertTrue(visitCount.get() == 4); // Outer map + inner map + all key-value pairs
    }

    @Test
    public void test_forEach_simple() {
        Integer value = 1;
        ObjectEach.of(value).each(node -> {
            assertTrue(node instanceof ObjectEach.EachNode);
            assertTrue(value.equals(node.getValue()));
        });

        ObjectEach.of(null).each(node -> {
            assertTrue(node instanceof ObjectEach.EachNode);
            assertTrue(node.getValue() == null);
        });
    }

    @Test
    public void test_forEach_enum() {
        Enum1 e = Enum1.A;
        ObjectEach.of(e).each(node -> {
            assertTrue(node instanceof ObjectEach.EnumNode);
            assertTrue(node.getValue() == e);
        });
    }

    @Test
    public void test_deepEach_loop_reference() {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map1.put("ref", map2);
        map2.put("ref", map1);

        //System.out.println(System.identityHashCode(map1));

        ObjectEach.of(map1).deepEach(createCollectingAction());
        assertTrue(visitedNodes.size() == 1);
    }

    //#endregion 基本单元测试结束

    //#region Singpass相关功能测试

    @Test
    public void test_forEach_map() {
        Map<String, String> map = new HashMap<>();
        map.put("gid", "1");
        map.put("accountType", "101000001");

        Map<String, String> mapCopy = new HashMap<>();
        ObjectEach.of(map).each(node -> {
            mapCopy.put(node.getKey(), (String)node.getValue());
        });

        assertTrue(mapCopy.size() == map.size());
        assertTrue(map.toString().equals(mapCopy.toString()));
    }

    @Test
    public void test_deepEach_pojo() {
        Base base = new Base();
        base.setAi("AI");
        base.setNameEn("Jack");
        base.setCertTypes(new String[]{ "1", "2", "5", "7" });
        base.setNationality("119000156");

        base.setAddressInfo(new AddressInfo());
        base.getAddressInfo().setResidencyStatus("Enabled");
        base.getAddressInfo().setAddressList(new ArrayList<>());

        Address address = new Address();
        address.setAddressType("addrType1");
        address.setCountry("CN");
        base.getAddressInfo().getAddressList().add(address);

        address = new Address();
        address.setAddressType("addrType2");
        address.setCountry("HK");
        base.getAddressInfo().getAddressList().add(address);

        Map<String, Object> fromFront = new HashMap<>();
        fromFront.put("ai", "user");
        fromFront.put("nameEn", "user");

        Map<String, Object> stateTree = new HashMap<>();
        ObjectEach.of(base).deepEach(node -> {
            if(node.isComplexNode()) {
                System.out.println(StringUtils.format("obj: {0}", node.getKey()));
                return;
            }

            System.out.println(StringUtils.format("%s: %s", node.getKey(), node.getValue()));

            String key = node.getKey();
            List<ObjectEach.EachNode> path = node.getParentPath();
            String frontState = getValue(fromFront, path, key);
            String state = frontState != null ? frontState : "singpass";
            setValue(stateTree, path, key, state);
        });

        System.out.println("=================================");
        ObjectEach.of(stateTree).deepEach(node -> {
            if(node.isComplexNode()) {
                System.out.println(StringUtils.format("obj: {0}", node.getKey()));
                return;
            }
            System.out.println(StringUtils.format("%s: %s", node.getKey(), node.getValue()));
        });
    }

    @Test
    public void test_deepEach_cycleReference() {
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setResidencyStatus("Status1");
        addressInfo.setAddressList(new ArrayList<>());
        Address address = new Address();
        address.setAddressType("133123445");
        addressInfo.getAddressList().add(address);
        addressInfo.setSelf(addressInfo);

        ObjectEach.of(addressInfo).deepEach(node -> {
            if(node.isComplexNode()) {
                System.out.println(StringUtils.format("obj: {0}", node.getKey()));
                return;
            }
            System.out.println(StringUtils.format("%s: %s", node.getKey(), node.getValue()));
        });
    }

    @SuppressWarnings("unchecked")
    public static void setValue(Map<String, Object> tree, List<ObjectEach.EachNode> path, String key, Object value) {
        Map<String, Object> item = tree;
        for(ObjectEach.EachNode node : path) {
            String parentKey = node.getKey();
            if(item.containsKey(parentKey)) {
                item = (Map<String, Object>) item.get(parentKey);
            } else {
                item.put(parentKey, new HashMap<>());
                item = (Map<String, Object>) item.get(parentKey);
            }
        }
        item.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static String getValue(Map<String, Object> map, List<ObjectEach.EachNode> path, String key) {
        Map<String, Object> item = map;
        for(ObjectEach.EachNode node : path) {
            String parentKey = node.getKey();
            if(item.containsKey(parentKey)) {
                item = (Map<String, Object>) item.get(parentKey);
            } else {
                return null;
            }
        }
        return item != null ? (String) item.get(key) : null;
    }

    //#endregion

}
