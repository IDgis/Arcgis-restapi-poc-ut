package nl.idgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
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
	
	
	public Map<String, Object> executePreparedStatementQuery(String query) {
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
	}
	
	/**
	 * Process all columns and map the key-value pairs.
	 * 
	 * @param rs - The found ResultSet
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Object> processColumns(ResultSet rs) throws SQLException {
		Map<String, Object> map = new LinkedHashMap<>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();
		for(int i = 1; i <= numColumns; i++) {
			map.put(rsmd.getColumnName(i), rs.getObject(i));
		}
		
		return map;
	}
}
