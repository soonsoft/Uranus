package com.soonsoft.uranus.data.service.mybatis.mapper;

import com.soonsoft.uranus.data.service.meta.TableInfo;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;

public interface ISQLMapper {

    final static String DOT = ".";

    MappedStatement add(MapperBuilderAssistant builderAssistant, Class<?> poClass, TableInfo tableInfo);
    
}
