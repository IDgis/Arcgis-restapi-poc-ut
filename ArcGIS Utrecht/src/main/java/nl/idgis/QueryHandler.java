package nl.idgis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHandler {

public static final Logger log = LoggerFactory.getLogger(QueryHandler.class);
	
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
		String url = "jdbc:postgresql://192.168.99.100:5432/<database_name>";
		Properties prop = new Properties();
		prop.put("user", userName);
		prop.put("password", password);
		return DriverManager.getConnection(url, prop);
	}
}
