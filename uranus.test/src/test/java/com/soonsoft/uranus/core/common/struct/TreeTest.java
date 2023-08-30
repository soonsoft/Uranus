package com.soonsoft.uranus.core.common.struct;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.soonsoft.uranus.core.common.struct.tree.TreeRoot;

public class TreeTest {

    @Test
    public void test_Tree() {
        List<Element> list = new ArrayList<>();
        list.add(new Element("1", null));
        list.add(new Element("2", null));
        list.add(new Element("3", null));

        list.add(new Element("11", "1"));
        list.add(new Element("12", "1"));
        list.add(new Element("13", "1"));

        list.add(new Element("111", "11"));
        list.add(new Element("121", "12"));
        
        list.add(new Element("21", "2"));
        list.add(new Element("22", "2"));

        TreeRoot<Element> tree = new TreeRoot<>(e -> e.getKey(), e -> e.getParentKey());
        tree.load(list);
        
        tree.deepEach(e -> System.out.println(e.key));

    }

    static class Element {
        private String key;
        private String parentKey;

        public Element(String key, String parentKey) {
            this.key = key;
            this.parentKey = parentKey;
        }

        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }

        public String getParentKey() {
            return parentKey;
        }
        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }
    }
    
}
