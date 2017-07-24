package nl.idgis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilder {

	private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);
	
	private static final String TABLE_NAME = "publisher.constants";
	
	
	public QueryBuilder() {
		// Empty constructor
	}
	
	public String createValidQuery(Map<String, Object> params) {
		// Create a StringBuilder to create a query
		log.debug("Building query...");
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		
		// Check for outFields to select the columns to return
		String[] outFields = (String[])params.get("outFields");
		if(outFields.length > 0) {
			log.debug("'outFields' attributes found...");
			builder.append(processOutFields(outFields));
		}
		
		// Get the table name
		log.debug("Getting table name...");
		builder.append(TABLE_NAME + " ");
		
		// Check for where parameter
		String where = (String)params.get("where");
		if(!"".equals(where)) {
			log.debug("'where' attributes found...");
		}
		
		log.debug(String.format("QueryString: %s", builder.toString()));
		return builder.toString();
	}
	
	/**
	 * Go through the String[] to get all columns to filter from the table
	 * @param outFields - The column names. A * for all columns
	 * @return
	 */
	private String processOutFields(String[] outFields) {
		if("*".equals(outFields[0])) {
			return "* FROM ";
		}
		StringBuilder builder = new StringBuilder();
		for(String s : outFields) {
			builder.append(s + ", ");
		}
		// Remove the final comma
		builder.deleteCharAt(builder.length() - 2);
		builder.append("FROM ");
		
		return builder.toString();
	}
}
