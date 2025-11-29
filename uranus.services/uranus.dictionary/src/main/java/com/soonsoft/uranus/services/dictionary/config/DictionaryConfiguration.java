package com.soonsoft.uranus.services.dictionary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.services.dictionary.DictionaryManager;
import com.soonsoft.uranus.services.dictionary.dao.DictionaryGroupDAO;
import com.soonsoft.uranus.services.dictionary.dao.DictionaryItemDAO;
import com.soonsoft.uranus.services.dictionary.service.DictionaryManagementService;
import com.soonsoft.uranus.services.dictionary.service.DictionaryService;

@Configuration
public class DictionaryConfiguration {

    @Bean
    public DictionaryManager dictionaryManager(IDatabaseAccess<?> databaseAccess) {
        DictionaryGroupDAO groupDAO = new DictionaryGroupDAO(databaseAccess);
        DictionaryItemDAO itemDAO = new DictionaryItemDAO(databaseAccess);
        DictionaryService dictionaryService = new DictionaryService(groupDAO, itemDAO);
        DictionaryManagementService dictionaryManagementService = new DictionaryManagementService(groupDAO, itemDAO);
        return new DictionaryManager(dictionaryService, dictionaryManagementService);
    }

}
