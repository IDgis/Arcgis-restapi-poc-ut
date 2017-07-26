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
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Gets the total number of rows available in the given table.
	 * 
	 * @param dbUrl - The table name
	 * @return number of rows
	 */
	public int getNumDbRows(String dbUrl) {
		log.debug("Getting numDbRows...");
		String query = String.format("SELECT count(*) FROM %s", dbUrl);
		log.debug(String.format("Query: %s", query));
		
		try(Connection conn = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
																	   ResultSet.CONCUR_READ_ONLY)) {
			log.debug("Executing statement...");
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				log.debug(String.format("numRows: %d", rs.getInt(1)));
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return 0;
	}
	
	/**
	 * Queries the database for the SHAPE column and converts it to GeoJson.
	 * 
	 * @param dbUrl - The table name
	 * @param index - The row number
	 * @return - The geometry in GeoJson
	 */
	public String getGeoJsonGeometry(String dbUrl, int index) {
		String query = String.format("SELECT ST_AsGeoJson(\"SHAPE\") AS geo FROM %s LIMIT 15", dbUrl); // TODO remove limit
		
		try(Connection conn = jdbcTemplate.getDataSource().getConnection();
				PreparedStatement statement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
																		   ResultSet.CONCUR_READ_ONLY)) {
			
			ResultSet rs = statement.executeQuery();
			if(rs.absolute(index)) {
				return rs.getString("geo");
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Returns the mapped results from the given query as key-value pairs.
	 * 
	 * @param query - The query to execute
	 * @return the mapped results of the query
	 */
	/*public Map<String, Object> getQueryResult(String query) {
		Map<String, Object> retMap = new LinkedHashMap<>();
		log.debug(String.format("Query: %s", query));
		log.debug("executing statement...");
		
		try(Connection connection = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, 
																			 ResultSet.CONCUR_READ_ONLY)) {
			
			log.debug(statement.toString());
			
			ResultSet rSet = statement.executeQuery();
			
			// Check for number of rows
			int numRows = 0;
			if(rSet.last()) {
				numRows = rSet.getRow();
				rSet.beforeFirst();
			}
			
			// if only 1 row, create a Map and directly return it
			if(numRows <= 1 && rSet.next()) {
				return processColumns(rSet);
			}
			
			// Multiple rows found, map all rows in different objects and return them
			int num = 1;
			while(rSet.next()) {
				retMap.put(Integer.toString(num++), processColumns(rSet));
			}
		} catch(SQLException e) {
			log.error(e.getMessage());
			retMap.put("error", "SQLException");
			return retMap;
		}
		
		return retMap;
	}*/
	
	/**
	 * Process all columns and map the key-value pairs.
	 * 
	 * @param rs - The found ResultSet
	 * @return
	 * @throws SQLException
	 */
	/*private Map<String, Object> processColumns(ResultSet rs) throws SQLException {
		Map<String, Object> map = new LinkedHashMap<>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();
		for(int i = 1; i <= numColumns; i++) {
			if(rs.getObject(i) instanceof String) {
				// Check if some text is already made-up as json
				if(((String)rs.getObject(i)).startsWith("{\"") && ((String)rs.getObject(i)).endsWith("\"}")) {
					log.debug("JSON type found " + rs.getObject(i));
					JsonParser parser = JsonParserFactory.getJsonParser();
					map.put(rsmd.getColumnName(i), parser.parseMap((String)rs.getObject(i)));
					continue;
				}
			}
			map.put(rsmd.getColumnName(i), rs.getObject(i));
		}
		
		return map;
	}*/
	
	/**
	 * Executes the prepared statement.
	 * 
	 * @param query - The query to execute
	 */
	/*public void executePreparedStatement(String query) {
		log.debug(String.format("Query: %s", query));
		
		try(Connection conn = jdbcTemplate.getDataSource().getConnection();
			PreparedStatement stmt = conn.prepareStatement(query)) {
			
			stmt.execute();
			
		} catch(SQLException e) {
			log.error(e.getMessage());
		}
	}*/
}
