import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Component;
import org.wololo.jts2geojson.GeoJSONWriter;

import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.formats.EsriJson;
import com.esri.terraformer.formats.GeoJson;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;

@Component
public class Test {
	
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://192.168.99.100:5432/publisher";
	static final String USER = "publisher";
	static final String PASS = "publisher";
	
	/**
	 * Needed libraries:<br><br>
	 * 
	 * compile group: 'com.vividsolutions', name: 'jts', version: '1.13'<br>
	 * compile group: 'org.wololo', name: 'jts2geojson', version: '0.10.0'<br>
	 * compile files('./includes/Terraformer-1.0.jar')
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void test(String[] args) throws Exception {
		
		String query = "SELECT st_asbinary(\"SHAPE\") AS geo FROM staging_data.\"222b66f4-b450-4871-8874-52170d56b1e8\" LIMIT 1";
		Connection conn = null;
		Statement stmt = null;
		
		byte[] wkb = null;
		
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				// Read WKB from database as bytes
				wkb = rs.getBytes("geo");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("WKB: " + wkb);
		System.out.println("EsriJson: " + convertWkbToEsriJson(wkb));
	}
	
	public static String convertWkbToEsriJson(byte[] wkb) throws Exception {
		// Convert WKB to Geometry
		WKBReader reader = new WKBReader();
		Geometry geom = reader.read(wkb);
		
		// Convert Geometry to GeoJson
		GeoJSONWriter writer = new GeoJSONWriter();
		org.wololo.geojson.Geometry jsonGeom = writer.write(geom);
		String geoJson = jsonGeom.toString();
		System.out.println("GeoJson: " + geoJson);
		
		// Convert GeoJson to EsriJson
		Terraformer t = new Terraformer();
		t.setDecoder(new GeoJson());
		EsriJson ej = new EsriJson();
		ej.setSpatialReference(28992);
		t.setEncoder(ej);
		return t.convert(geoJson);
	}
}
