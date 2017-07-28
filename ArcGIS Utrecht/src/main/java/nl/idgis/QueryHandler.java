package nl.idgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueryHandler {

	private static final Logger log = LoggerFactory.getLogger(QueryHandler.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * Queries the database to get all geometries and let Postgres return them as GeoJson
	 * 
	 * @param dbUrl - The table name
	 * @param extent - The extent in [xmin, ymin, xmax, ymax]
	 * @param outSR - The spatialRel for output
	 * @param resultOffset - The OFFSET
	 * @param resultRecordCount - The LIMIT
	 * @return The geometry in GeoJson
	 */
	public Map<String, Object> getDataFromTable(String dbUrl, String[] fields, String where, double[] extent, int outSR, int resultOffset, int resultRecordCount) {
		log.debug("Connecting to the database...");
		Map<String, Object> data = new HashMap<>();
		List<Object> list = null;
		
		String query = String.format("SELECT ST_AsGeoJson(\"SHAPE\") AS geoJsons, * FROM %s%s LIMIT %d OFFSET %d", 
				dbUrl, getWhereExtent(where, extent, outSR), resultRecordCount, resultOffset);
		log.debug("Query: " + query);
		
		try(Connection conn = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
																	   ResultSet.CONCUR_READ_ONLY)) {
			
			log.debug("Connected to the database, getting data...");
			ResultSet rs = statement.executeQuery();
			
			// Check for number of rows
			int numRows = 0;
			if(rs.last()) {
				numRows = rs.getRow();
				rs.beforeFirst();
			}
			
			for(int i = 0; i < fields.length; i++) {
				rs.beforeFirst();
				list = new ArrayList<>(numRows);
				while(rs.next()) {
					list.add(rs.getObject(fields[i]));
				}
				data.put(fields[i], list);
			}
			
			log.debug("Got the data from the database...");
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return data;
	}
	
	private String getWhereExtent(String where, double[] extent, int outSR) {
		if(extent.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(" WHERE ST_Intersects(\"SHAPE\", ST_MakeEnvelope(" + extent[0] + ", " + extent[1] + ", "
															   + extent[2] + ", " + extent[3] + ", " 
															   + outSR + "))");
		if(!"".equals(where)) {
			builder.append(" AND " + where);
		}
		
		return builder.toString();
	}
}
