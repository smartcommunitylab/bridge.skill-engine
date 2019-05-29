package it.smartcommunitylab.bridge.controller;

import org.reflections.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.bridge.exception.EntityNotFoundException;
import it.smartcommunitylab.bridge.exception.StorageException;
import it.smartcommunitylab.bridge.model.JobOffer;

@RestController
public class JobOfferController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(JobOfferController.class);
	
	@GetMapping(value = "/api/joboffer")
	public JobOffer getJobOffer(@RequestParam String extUri) throws Exception {
		JobOffer jobOffer = jobOfferRepository.findByExtUri(extUri);
		if(jobOffer == null) {
			throw new EntityNotFoundException("entity not found");
		}
		logger.info("getJobOffer:{}", extUri);
		return jobOffer;
	}
	
	@PostMapping(value = "/api/joboffer")
	public JobOffer saveJobOffer(@RequestBody JobOffer jobOffer) throws Exception {
		if(Utils.isEmpty(jobOffer.getExtUri())) {
			throw new StorageException("extUri not present");
		}
		JobOffer jobOfferDb = jobOfferRepository.findByExtUri(jobOffer.getExtUri());
		if(jobOfferDb != null) {
			jobOffer.setId(jobOfferDb.getId());
		}
		jobOffer.setOccupationsLink(completeOccupationLink(jobOffer.getOccupations()));
		jobOfferRepository.save(jobOffer);
		logger.info("saveJobOffer:{}", jobOffer.getExtUri());
		return jobOffer;
	}
	
	@DeleteMapping(value = "/api/joboffer")
	public JobOffer deleteJobOffer(@RequestParam String extUri) throws Exception {
		JobOffer jobOffer = jobOfferRepository.findByExtUri(extUri);
		if(jobOffer == null) {
			throw new EntityNotFoundException("entity not found");
		}
		jobOfferRepository.delete(jobOffer);
		logger.info("deleteJobOffer:{}", extUri);
		return jobOffer;
	}
	
}
