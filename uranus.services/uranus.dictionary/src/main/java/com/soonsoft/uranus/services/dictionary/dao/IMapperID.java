package com.soonsoft.uranus.services.dictionary.dao;

public interface IMapperID {

    public final static String MAPPER_NAMESPACE = "uranus.dictionary";

    default String getStatement(String name) {
        return MAPPER_NAMESPACE + "." + name;
    }

}
