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
import it.smartcommunitylab.bridge.model.Course;

@RestController
public class CourseController extends MainController {
	private static final transient Logger logger = LoggerFactory.getLogger(CourseController.class);
	
	@GetMapping(value = "/api/course")
	public Course getCourse(@RequestParam String extUri) throws Exception {
		Course course = courseRepository.findByExtUri(extUri);
		if(course == null) {
			throw new EntityNotFoundException("entity not found");
		}
		logger.info("getJobOffer:{}", extUri);
		return course;
	}
	
	@PostMapping(value = "/api/course")
	public Course saveCourse(@RequestBody Course course) throws Exception {
		if(Utils.isEmpty(course.getExtUri())) {
			throw new StorageException("extUri not present");
		}
		Course courseDb = courseRepository.findByExtUri(course.getExtUri());
		if(courseDb != null) {
			course.setId(courseDb.getId());
		}
		course.setSkillsLink(completeSkillLink(course.getSkills()));
		courseRepository.save(course);
		logger.info("saveCourse:{}", course.getExtUri());
		return course;
	}
	
	@DeleteMapping(value = "/api/course")
	public Course deleteCourse(@RequestParam String extUri) throws Exception {
		Course course = courseRepository.findByExtUri(extUri);
		if(course == null) {
			throw new EntityNotFoundException("entity not found");
		}
		courseRepository.delete(course);
		logger.info("deleteCourse:{}", extUri);
		return course;
	}
	
}
