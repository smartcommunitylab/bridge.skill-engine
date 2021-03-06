package it.smartcommunitylab.bridge.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.Skill;

@Repository
public interface SkillRepository extends MongoRepository<Skill, String>, SkillRepositoryCustom {
	
	@Query(value="{$or:[{isEssentialForOccupation:{$in:?0}},{isOptionalForOccupation:{$in:?0}}]}")
	List<Skill> findByOccupation(String occupationUri);
	
	@Query(value="{uri:{$in:?0}}")
	List<Skill> findByIds(List<String> ids);

}
