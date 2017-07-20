package nl.idgis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLQuery {
	
	public static final Logger log = LoggerFactory.getLogger(SQLQuery.class);
	
	public static void executeQuery() {
		Map<String, Object> map = new HashMap<>();
		
		try(Connection con = getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT * FROM book WHERE unit_price > 47.7")) {
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int numColumns = rsmd.getColumnCount();
				for(int i = 1; i <= numColumns; i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(i));
				}
			}
		} catch (SQLException e) {
			log.equals(e.getMessage());
		}
	}
	
	private static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://<host>:<port>/<database_name>";
		Properties prop = new Properties();
		prop.put("user", "test");
		prop.put("password", "test");
		return DriverManager.getConnection(url, prop);
	}
}
