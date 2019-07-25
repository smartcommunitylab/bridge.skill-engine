package it.smartcommunitylab.bridge.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import it.smartcommunitylab.bridge.common.Utils;
import it.smartcommunitylab.bridge.model.JobOffer;

public class JobOfferRepositoryCustomImpl implements JobOfferRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void init() {
		mongoTemplate.indexOps(JobOffer.class).ensureIndex(new GeospatialIndex("geocoding"));
	}
	
	@Override
	public List<JobOffer> findByLocation(double lat, double lng, double distance, String iscoCode) {
		List<JobOffer> result = new ArrayList<>();
		Point location = new Point(lng, lat);
		Distance geoDistance = new Distance(distance, Metrics.KILOMETERS);
		NearQuery query = NearQuery.near(location).maxDistance(geoDistance);
		Criteria criteria = Criteria.where("expirationDate").gte(new Date());
		if(Utils.isNotEmpty(iscoCode)) {
			iscoCode = iscoCode.length() > 3 ? iscoCode.substring(0, 3) : iscoCode;
			criteria = criteria.and("iscoCode").regex("^" + iscoCode);
		}
		query = query.query(Query.query(criteria));
		GeoResults<JobOffer> geoNear = mongoTemplate.geoNear(query, JobOffer.class);
		Iterator<GeoResult<JobOffer>> iterator = geoNear.iterator();
		while(iterator.hasNext()) {
			GeoResult<JobOffer> geoResult = iterator.next();
			result.add(geoResult.getContent());
		}
		return result;
	}

}
