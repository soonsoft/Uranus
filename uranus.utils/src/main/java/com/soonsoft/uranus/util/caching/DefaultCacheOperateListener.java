package com.soonsoft.uranus.util.caching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.functional.action.Action2;

public class DefaultCacheOperateListener<TKey, TValue> implements ICacheOperateListener<TKey, TValue> {

    public final static String Remove = "REMOVE_OPERATE";
    public final static String Insert = "INSERT_OPERATE";
    public final static String Update = "UPDATE_OPERATE";

    private final Map<String, Set<Action2<TKey, TValue>>> actionMap = new HashMap<>();

    @Override
    public void addListener(String type, Action2<TKey, TValue> cacheAction) {
        Guard.notNull(cacheAction, "the parameter cacheAction is required.");

        Set<Action2<TKey, TValue>> actionSet = actionMap.get(type);
        if(actionSet == null) {
            actionSet = new HashSet<>();
            actionMap.put(type, actionSet);
        }

        actionSet.add(cacheAction);
        
    }

    @Override
    public void removeListener(String type, Action2<TKey, TValue> cacheAction) {
        if(cacheAction != null) {
            Set<Action2<TKey, TValue>> actionSet = actionMap.get(type);
            if(actionSet != null) {
                actionSet.remove(cacheAction);
            }
        }
    }

    @Override
    public void emit(String type, TKey key, TValue value) {
        Set<Action2<TKey, TValue>> actionSet = actionMap.get(type);
        if(actionSet != null) {
            for(Action2<TKey, TValue> action : actionSet) {
                action.apply(key, value);
            }
        }
    }
    
}
