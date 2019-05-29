package it.smartcommunitylab.bridge.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import it.smartcommunitylab.bridge.model.Skill;

public interface SkillRepositoryCustom {
	List<Skill> findSkill(boolean skillGroup, List<String> isEssentialForOccupation, 
			List<String> isOptionalForOccupation, Pageable pageable);
}
