package it.smartcommunitylab.bridge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.Course;

@Repository
public interface CourseRepository extends MongoRepository<Course, String>, CourseRepositoryCustom {
	@Query(value="{extUri:?0}")
	Course findByExtUri(String extUri);

}
