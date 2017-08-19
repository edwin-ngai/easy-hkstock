package com.wising.easyhkstock.ccass.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;

public interface SnapshotDetailRepository extends MongoRepository<SnapshotDetail, String> {

}
