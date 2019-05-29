package it.smartcommunitylab.bridge.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.Occupation;

@Repository
public interface OccupationRepository extends MongoRepository<Occupation, String>, OccupationRepositoryCustom {
	
	@Query(value="{$or:[{hasEssentialSkill:{$in:?0}},{hasOptionalSkill:{$in:?0}}]}")
	List<Occupation> findBySkill(List<String> skills);
	
	@Query(value="{hasEssentialSkill:{$in:?0}}")
	List<Occupation> findByEssentialSkill(List<String> skills);
	
	@Query(value="{hasOptionalSkill:{$in:?0}}")
	List<Occupation> findByOptionalSkill(List<String> skills);
	
	@Query(value="{totalSkill:{$all:?0}}")
	List<Occupation> findByMandatorySkill(List<String> skills);

	@Query(value="{iscoCode:{$regex:?0}}")
	List<Occupation> findByIscoCode(String iscoCode);
	
	@Query(value="{uri:{$in:?0}}")
	List<Occupation> findByIds(List<String> ids);
	

}
