package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.functional.func.Func1;

public class ComputedAttribute<TValue> extends Attribute<TValue> {

    private Func1<StructDataAccessor, TValue> computedFn;

    public ComputedAttribute(String entityName, String propertyName, 
            IAttributeConvertor<TValue> valueConvertor, Func1<StructDataAccessor, TValue> computedFn) {
        super(entityName, propertyName, PropertyType.ComputedProperty, valueConvertor);

        Guard.notNull(computedFn, "the arguments computedFn is required.");
        this.computedFn = computedFn;
    }

    public Func1<StructDataAccessor, TValue> getComputedFn() {
        return computedFn;
    }
    
}
