package nl.idgis;

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
	
	
	public List<Map<String, Object>> executeTemplateQuery(String query) {
		log.debug(String.format("Entered query: %s", query));
		
		// execute a prepared statement
		return jdbcTemplate.queryForList(query);
	}
	
	/*
	public static Map<String, Object> executeQuery(String query, String userName, String password) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		try(Connection con = getConnection(userName, password);
			PreparedStatement stmt = con.prepareStatement(query)) {
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int numColumns = rsmd.getColumnCount();
				for(int i = 1; i <= numColumns; i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(i));
				}
			}
			
			rs.close();
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
		return map;
	}
	
	private static Connection getConnection(String userName, String password) throws SQLException {
		//String url = "jdbc:postgresql://<host>:<port>/<database_name>";
		String url = "jdbc:postgresql://192.168.99.100:5432/publisher";
		Properties prop = new Properties();
		prop.put("user", userName);
		prop.put("password", password);
		return DriverManager.getConnection(url, prop);
	}
	*/
}
