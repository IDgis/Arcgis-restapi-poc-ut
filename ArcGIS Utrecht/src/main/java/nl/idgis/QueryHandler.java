package nl.idgis;

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
	
	
	public Map<String, Object> executeQuery(String query) {
		log.debug(String.format("Entered query: %s", query));
		
		// execute a prepared statement
		return jdbcTemplate.queryForMap(query);
	}
}
