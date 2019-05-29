package it.smartcommunitylab.bridge.repository;

import java.util.List;

import it.smartcommunitylab.bridge.model.Occupation;

public interface OccupationRepositoryCustom {
	List<Occupation> findOccupation(boolean iscoGroup, String iscoCode,
			List<String> hasEssentialSkill, List<String> hasOptionalSkill);
}
