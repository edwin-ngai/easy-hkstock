package com.wising.easyhkstock.common.task;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.data.mongodb.repository.MongoRepository;

public class MongoDispatcher<T, ID extends Serializable> implements DataDispatcher<T> {

	private MongoRepository<T, ID> repository;
	
	public MongoDispatcher(MongoRepository<T, ID> repository) {
		Validate.notNull(repository);
		this.repository = repository;
	}
	
	@Override
	public void dispatch(List<T> data) {
		Validate.notNull(data);
		repository.save(data);
	}
}
