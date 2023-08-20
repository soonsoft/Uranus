package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.common.attribute.access.AttributeBag;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.notify.Dependency;

public class AttributeBagFactory {

    private Dependency<String> dependency;

    public AttributeBagFactory() {
        this(new Dependency<>());
    }

    public AttributeBagFactory(Dependency<String> dependency) {
        this.dependency = dependency;
    }

    public IAttributeBag createBag() {
        return createBag(new ArrayList<>());
    }

    public IAttributeBag createBag(List<AttributeData> attributeDataList) {
        return new AttributeBag(attributeDataList, dependency);
    }
    
}
