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
	 * Queries the database to get all data speciefied by the field array and stores them in Lists in a HashMap.
	 * 
	 * @param dbUrl - The table name
	 * @param fields - All column names to get from the database
	 * @param extent - The extent in [xmin, ymin, xmax, ymax]
	 * @param outSR - The spatialRel for output
	 * @param resultOffset - The OFFSET
	 * @param resultRecordCount - The LIMIT
	 * @return All data specified in the fields array.
	 */
	public Map<String, List<String>> getDataFromTable(String dbUrl, String[] fields, String where, double[] extent, String outFields, int outSR, int resultOffset, int resultRecordCount) {
		log.debug("Connecting to the database...");
		Map<String, List<String>> data = new HashMap<>();
		List<String> list = null;
		
		String query = String.format("SELECT ST_AsGeoJson(\"SHAPE\") AS geoJsons, %s FROM %s%s LIMIT %d OFFSET %d", 
				getOutFields(outFields), dbUrl, getWhereExtent(where, extent, outSR), resultRecordCount, resultOffset);
		log.debug("Query: " + query);
		log.debug(jdbcTemplate.getDataSource().toString());
		
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
					list.add(rs.getString(fields[i]));
				}
				data.put(fields[i], list);
			}
			
			log.debug("Got the data from the database...");
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return data;
	}
	
	private String getOutFields(String outFields) {
		if("*".equals(outFields)) {
			return outFields;
		}
		
		String[] fields = outFields.split(",");
		StringBuilder builder = new StringBuilder();
		builder.append("\"" + fields[0] + "\"");
		
		if(fields.length > 1) {
			for(int i = 1; i < fields.length; i++) {
				builder.append(",\"" + fields[i] + "\"");
			}
		}
		
		
		return builder.toString();
	}
	
	/**
	 * Gets the geometries within the given bounding box and extends with the where clause
	 * 
	 * @param where - The WHERE clause
	 * @param extent - The bounding box in which the geometries to get
	 * @param outSR - The spatialRel
	 * @return Returns the WHERE string
	 */
	private String getWhereExtent(String where, double[] extent, int outSR) {
		if(extent.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(" WHERE ST_Intersects(\"SHAPE\", ST_MakeEnvelope(" + extent[0] + ", " + extent[1] + ", "
															   + extent[2] + ", " + extent[3] + ", " 
															   + outSR + "))");
		if(!"".equals(where)) {
			builder.append(" AND ");
			builder.append(parseWhere(where));
		}
		
		return builder.toString();
	}
	
	private String parseWhere(String where) {
		log.debug("Where: " + where);
		
		if(where.indexOf('(') != -1) {
			int i = where.indexOf('(');
			while(i != -1) {
				where = where.replace(where.substring(i + 1, where.indexOf(' ', i)), "\"" + where.substring(i + 1, where.indexOf(' ', i)) + "\"");
				i = where.indexOf('(', i + 1);
			}
			return where;
		}
		
		return where.replace(where.substring(0, where.indexOf(' ')), "\"" + where.substring(0, where.indexOf(' ')) + "\"");
	}
}
