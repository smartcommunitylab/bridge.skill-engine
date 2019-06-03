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

import it.smartcommunitylab.bridge.model.Course;

public class CourseRepositoryCustomImpl implements CourseRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void init() {
		mongoTemplate.indexOps(Course.class).ensureIndex(new GeospatialIndex("geocoding"));
	}
	
	@Override
	public List<Course> findByLocation(double lat, double lng, double distance, List<String> skills) {
		List<Course> result = new ArrayList<>();
		Point location = new Point(lng, lat);
		Distance geoDistance = new Distance(distance, Metrics.KILOMETERS);
		NearQuery query = NearQuery.near(location).maxDistance(geoDistance);
		Criteria criteria = Criteria.where("dateFrom").gte(new Date());
		if((skills != null) && (skills.size() > 0)) {
			criteria = criteria.and("skills").in(skills);
		}
		query = query.query(Query.query(criteria));
		GeoResults<Course> geoNear = mongoTemplate.geoNear(query, Course.class);
		Iterator<GeoResult<Course>> iterator = geoNear.iterator();
		while (iterator.hasNext()) {
			GeoResult<Course> geoResult = iterator.next();
			result.add(geoResult.getContent());
		}
		return result;
	}

}
