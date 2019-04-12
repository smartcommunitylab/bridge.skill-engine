package it.smartcommunitylab.bridge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.JobOffer;

@Repository
public interface JobOfferRepository extends MongoRepository<JobOffer, String> {
	@Query(value="{extUri:?0}")
	JobOffer findByExtUri(String extUri);

}
