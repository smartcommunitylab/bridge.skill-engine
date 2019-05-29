package it.smartcommunitylab.bridge.repository;

import java.util.List;

import it.smartcommunitylab.bridge.model.Skill;

public interface SkillRepositoryCustom {
	List<Skill> findSkill(boolean skillGroup, 
			List<String> isEssentialForOccupation, List<String> isOptionalForOccupation);
}
