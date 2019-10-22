package it.smartcommunitylab.bridge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.cogito.CogitoProfile;

@Repository
public interface CogitoProfileRepository extends MongoRepository<CogitoProfile, String> {
	@Query(value="{filename:?0}")
	CogitoProfile findByFilename(String filename);
}
