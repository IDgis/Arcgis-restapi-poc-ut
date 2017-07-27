package nl.idgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	 * @return The geometry in GeoJson
	 */
	public List<String> getGeoJsonsFromTable(String dbUrl) {
		log.debug("Getting GeoJsons from database...");
		String query = String.format("SELECT ST_AsGeoJson(\"SHAPE\") AS geo FROM %s", dbUrl);
		
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
			
			List<String> list = new ArrayList<>(numRows);
			while(rs.next()) {
				list.add(rs.getString("geo"));
			}
			log.debug("Got the GeoJsons, returning the list...");
			return list;
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return new ArrayList<>();
	}
}
