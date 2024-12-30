package com.soonsoft.uranus.security.authorization.event;

import java.util.function.Consumer;

public interface IRoleChangedListener<T> {

    void addRoleChanged(Consumer<RoleChangedEvent<T>> eventHandler);

    void removeRoleChanged(Consumer<RoleChangedEvent<T>> eventHandler);


}