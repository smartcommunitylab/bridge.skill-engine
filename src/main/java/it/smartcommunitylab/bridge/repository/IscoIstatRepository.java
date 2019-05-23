package it.smartcommunitylab.bridge.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.bridge.model.IscoIstat;

@Repository
public interface IscoIstatRepository extends MongoRepository<IscoIstat, String> {
	
	@Query(value="{iscoCode:{$regex:?0}}")
	List<IscoIstat> findByIscoCode(String iscoCode);
	
	@Query(value="{istatCode:{$regex:?0}}")
	List<IscoIstat> findByIstatCode(String istatCode);
	
	@Query(value="{istatCode:?0}")
	IscoIstat findByCompleteIstatCode(String istatCode);	
	
	@Query(value="{istatName:?0}")
	IscoIstat findByCompleteIstatName(String istatName);

}
