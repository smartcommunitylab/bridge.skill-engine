package it.smartcommunitylab.bridge.model.cogito;

import java.util.ArrayList;
import java.util.List;

public class Training {
	private String organization;
	private List<String> trainings = new ArrayList<String>();
	private List<String> stages = new ArrayList<String>();
	
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public List<String> getTrainings() {
		return trainings;
	}
	public void setTrainings(List<String> trainings) {
		this.trainings = trainings;
	}
	public List<String> getStages() {
		return stages;
	}
	public void setStages(List<String> stages) {
		this.stages = stages;
	}
}
