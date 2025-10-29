package com.soonsoft.uranus.core.utils.identity.guid;

import java.util.UUID;

import com.soonsoft.uranus.core.utils.identity.IdentityGenerator;

public class GuidGenerator implements IdentityGenerator<String> {

    @Override
    public String newID() {
        return UUID.randomUUID().toString();
    }
    
}