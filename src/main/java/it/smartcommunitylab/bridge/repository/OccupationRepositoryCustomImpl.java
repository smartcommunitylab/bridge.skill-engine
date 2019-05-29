package it.smartcommunitylab.bridge.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.smartcommunitylab.bridge.common.Const;
import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.model.Occupation;

public class OccupationRepositoryCustomImpl implements OccupationRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Occupation> findOccupation(boolean iscoGroup, String iscoCode, 
			List<String> hasEssentialSkill, List<String> hasOptionalSkill) {
		Criteria criteria = iscoGroup ? Criteria.where("conceptType").is(Const.CONCEPT_ISCO_GROUP) :
			Criteria.where("conceptType").is(Const.CONCEPT_OCCCUPATION);
		if((hasEssentialSkill != null) && (hasEssentialSkill.size() > 0)) {
			criteria = criteria.and("hasEssentialSkill").in(hasEssentialSkill);
		}
		if((hasOptionalSkill != null) && (hasOptionalSkill.size() > 0)) {
			criteria = criteria.and("hasOptionalSkill").in(hasOptionalSkill);
		}
		if(Utils.isNotEmpty(iscoCode)) {
			criteria = criteria.and("iscoCode").regex("^" + iscoCode);
		}
		Query query = new Query(criteria);
		query.with(new Sort(Direction.ASC, "preferredLabel.it"));
		return mongoTemplate.find(query, Occupation.class);
	}

}
