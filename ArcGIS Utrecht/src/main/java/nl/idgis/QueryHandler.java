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
	public Map<String, Object> getDataFromTable(String dbUrl, double[] extent, int outSR, int resultOffset, int resultRecordCount) {
		log.debug("Connecting to the database...");
		Map<String, Object> data = new HashMap<>();
		List<String> geoJsons = null;
		
		String query = String.format("SELECT ST_AsGeoJson(\"SHAPE\") AS geo FROM %s%s LIMIT %d OFFSET %d", 
				dbUrl, getWhereExtent(extent, outSR), resultRecordCount, resultOffset);
		
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
			
			geoJsons = new ArrayList<>(numRows);
			while(rs.next()) {
				geoJsons.add(rs.getString("geo"));
			}
			log.debug("Got the data from the database...");
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		data.put("geoJsons", geoJsons);
		
		return data;
	}
	
	private String getWhereExtent(double[] extent, int outSR) {
		if(extent.length == 0) {
			return "";
		}
		return "WHERE ST_Overlaps(\"SHAPE\", ST_MakeEnvelope(" + extent[0] + ", " + extent[1] + ", "
															   + extent[2] + ", " + extent[3] + ", " 
															   + outSR + "))";
	}
}
