package com.soonsoft.uranus.core.common.attribute.access;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActionCommandPackage implements Iterable<ActionCommand> {

    private Map<String, ActionCommand> actionCommandMap;

    public ActionCommandPackage() {
        actionCommandMap = new LinkedHashMap<>();
    }

    public int size() {
        return actionCommandMap.size();
    }

    public boolean isEmpty() {
        return actionCommandMap.isEmpty();
    }

    public boolean contains(ActionCommand cmd) {
        if(cmd != null) {
            return actionCommandMap.containsValue(cmd);
        }
        return false;
    }

    @Override
    public Iterator<ActionCommand> iterator() {
        return actionCommandMap.values().iterator();
    }

    public boolean add(ActionCommand command) {
        if(command == null) {
            return false;
        }

        ActionCommand oldCommand = actionCommandMap.get(command.getKey());
        if(oldCommand == null) {
            actionCommandMap.put(command.getKey(), command);
            return true;
        }

        if(oldCommand != null && command.getActionType().isPriority(oldCommand.getActionType())) {
            actionCommandMap.put(command.getKey(), command);
            return true;
        }

        return false;
    }

    public boolean remove(ActionCommand cmd) {
        if(cmd != null) {
            String key = cmd.getKey();
            return actionCommandMap.remove(key, cmd);
        }
        return false;
    }

    public void clear() {
        actionCommandMap.clear();
    }
    
}
