package nl.idgis;

import java.util.Map;

public class QueryParser {

	private Map<String, Object> queryParams;
	private String query;
	
	public QueryParser(Map<String, Object> params) {
		queryParams = params;
	}
	
	public void createValidQuery() {
		
	}
	
	public String getQuery() {
		return query;
	}
}
