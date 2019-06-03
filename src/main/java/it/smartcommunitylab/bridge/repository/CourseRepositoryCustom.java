package it.smartcommunitylab.bridge.repository;

import java.util.List;

import it.smartcommunitylab.bridge.model.Course;

public interface CourseRepositoryCustom {
	List<Course> findByLocation(double lat, double lng, double distance, 
			List<String> skills);
}
