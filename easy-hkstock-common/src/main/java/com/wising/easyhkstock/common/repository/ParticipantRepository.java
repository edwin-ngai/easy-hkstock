package com.wising.easyhkstock.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wising.easyhkstock.common.domain.Participant;

public interface ParticipantRepository extends MongoRepository<Participant, String> {
	
}
