package com.soonsoft.uranus.core.common.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.soonsoft.uranus.core.common.beans.error.BeanPropertyException;
import com.soonsoft.uranus.core.error.argument.ArgumentNullException;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IForEach;

public class BeanAccessor<TObject> implements IForEach<BeanProperty<TObject, Object>> {
    
    private final Map<String, BeanProperty<TObject, Object>> propertyMap;

    public BeanAccessor() {
        this.propertyMap = new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(TObject instance, String propertyName) {
        if(instance == null) {
            throw new ArgumentNullException("instance");
        }
        if(propertyName == null || propertyName.length() == 0) {
            throw new ArgumentNullException("propertyName");
        }
        checkPropertyExists(propertyName);

        BeanProperty<TObject, Object> property = propertyMap.get(propertyName);
        return (T) property.get(instance);
    }

    public void set(TObject instance, String propertyName, Object value) {
        if(instance == null) {
            throw new ArgumentNullException("instance");
        }
        if(propertyName == null || propertyName.length() == 0) {
            throw new ArgumentNullException("propertyName");
        }
        checkPropertyExists(propertyName);

        BeanProperty<TObject, Object> property = propertyMap.get(propertyName);
        property.set(instance, value);
    }

    @Override
    public void forEach(Action3<BeanProperty<TObject, Object>, Integer, ForEachBehavior> action) {
        int i = 0;
        ForEachBehavior behavior = new ForEachBehavior();
        for(String name : propertyMap.keySet()) {
            BeanProperty<TObject, Object> property = propertyMap.get(name);
            if(action != null) {
                behavior.reset();
                action.apply(property, i, behavior);
                if(behavior.isContinue()) {
                    continue;
                }
                if(behavior.isBreak()) {
                    break;
                }
            }
            i++;
        }
    }

    public void add(BeanProperty<TObject, Object> beanProperty) {
        if(beanProperty == null) {
            throw new ArgumentNullException("beanProperty");
        }
        propertyMap.put(beanProperty.getPropertyName(), beanProperty);
    }

    private void checkPropertyExists(String propertyName) {
        if(!propertyMap.containsKey(propertyName)) {
            throw new BeanPropertyException("the propertyName [%s] is not exists.", propertyName);
        }
    }

}
