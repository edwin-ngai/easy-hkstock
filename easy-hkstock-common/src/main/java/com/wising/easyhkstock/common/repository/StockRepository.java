package com.wising.easyhkstock.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wising.easyhkstock.common.domain.Stock;

public interface StockRepository extends MongoRepository<Stock, String> {
	
}
