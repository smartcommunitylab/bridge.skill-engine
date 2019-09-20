package it.smartcommunitylab.bridge.model;

import java.util.ArrayList;
import java.util.List;

public class CourseResult {
	private String title;
	private List<Course> courses = new ArrayList<>();
	private int matching;
	private int coverage;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Course> getCourses() {
		return courses;
	}
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	public int getMatching() {
		return matching;
	}
	public void setMatching(int matching) {
		this.matching = matching;
	}
	public int getCoverage() {
		return coverage;
	}
	public void setCoverage(int coverage) {
		this.coverage = coverage;
	}
}
