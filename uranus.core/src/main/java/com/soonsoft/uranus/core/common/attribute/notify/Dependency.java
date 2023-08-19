package com.soonsoft.uranus.core.common.attribute.notify;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Dependency<TKey> {
    private Map<TKey, Set<IUpdateDelegate>> delegateMap = new HashMap<>();
    private IUpdateDelegate currentDelegate;

    public void depend(TKey key) {
        Set<IUpdateDelegate> delegateList = null;
        if(delegateMap.containsKey(key)) {
            delegateList = delegateMap.get(key);
        } else {
            delegateList = new LinkedHashSet<>();
            delegateMap.put(key, delegateList);
        }

        if(currentDelegate != null) {
            delegateList.add(currentDelegate);
        }
    }

    public void notify(TKey key) {
        if(!delegateMap.containsKey(key)) {
            return;
        }

        Set<IUpdateDelegate> delegateList = delegateMap.get(key);
        if(!delegateList.isEmpty()) {
            for(IUpdateDelegate delegate : delegateList) {
                delegate.update();
            }
        }
    }

    public void setCurrent(IUpdateDelegate current) {
        currentDelegate = current;
    }
}
