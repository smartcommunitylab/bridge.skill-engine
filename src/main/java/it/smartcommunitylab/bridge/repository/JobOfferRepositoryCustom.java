package it.smartcommunitylab.bridge.repository;

import java.util.List;

import it.smartcommunitylab.bridge.model.JobOffer;

public interface JobOfferRepositoryCustom {
	List<JobOffer> findByLocation(double lat, double lng, double distance, 
			String iscoCode);
}
