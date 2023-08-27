package com.soonsoft.uranus.core.common.attribute.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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

    public void notify(Collection<TKey> keys) {
        if(keys == null || keys.isEmpty()) {
            return;
        }

        Set<IUpdateDelegate> delegateList = new HashSet<>();
        for(TKey key : keys) {
            if(!delegateMap.containsKey(key)) {
                continue;
            }

            delegateList.addAll(delegateMap.get(key));
        }
        
        if(!delegateList.isEmpty()) {
            for(IUpdateDelegate delegate : delegateList) {
                delegate.update();
            }
        }
    }

    public void notify(TKey key) {
        List<TKey> keys = new ArrayList<>(1);
        keys.add(key);
        notify(keys);
    }

    public void setCurrent(IUpdateDelegate current) {
        currentDelegate = current;
    }
}
