package com.soonsoft.uranus.services.membership.dao;

public interface IMapperID {

    public final static String MAPPER_NAMESPACE = "uranus.membership";

    default String getStatement(String name) {
        return MAPPER_NAMESPACE + "." + name;
    }
    
}
