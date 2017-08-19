package com.wising.easyhkstock.ccass.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wising.easyhkstock.ccass.domain.SnapshotSummary;

public interface SnapshotSummaryRepository extends MongoRepository<SnapshotSummary, String> {

}
