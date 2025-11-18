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
	public DictionaryGroupDAO dictionaryGroupDAO(IDatabaseAccess<?> databaseAccess) {
		return new DictionaryGroupDAO(databaseAccess);
	}

	@Bean
	public DictionaryItemDAO dictionaryItemDAO(IDatabaseAccess<?> databaseAccess) {
		return new DictionaryItemDAO(databaseAccess);
	}

	@Bean
	public DictionaryManagementService dictionaryManagementService(DictionaryGroupDAO groupDAO, DictionaryItemDAO itemDAO) {
		return new DictionaryManagementService(groupDAO, itemDAO);
	}

	@Bean
	public DictionaryService dictionaryService(DictionaryGroupDAO groupDAO, DictionaryItemDAO itemDAO) {
		return new DictionaryService(groupDAO, itemDAO);
	}

	@Bean
	public DictionaryManager dictionaryManager(DictionaryService dictionaryService) {
		return new DictionaryManager(dictionaryService);
	}

}
