package it.smartcommunitylab.bridge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.Profile;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
	@Query(value="{extId:?0}")
	Profile findByExtId(String extId);

}
