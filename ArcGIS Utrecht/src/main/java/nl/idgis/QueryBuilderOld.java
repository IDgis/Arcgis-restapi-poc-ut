package nl.idgis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilderOld {

	private static final Logger log = LoggerFactory.getLogger(QueryBuilderOld.class);
	
	private static final String AARDKUNDIG = "staging_data.\"222b66f4-b450-4871-8874-52170d56b1e8\"";
	private static final String VEENGEBIED = "staging_data.\"bddbf261-4401-47e0-a721-d832a44e0462\"";
	
	/**
	 * Create a SQL query String that can be executed by the database.
	 * 
	 * @param layerId - The layerId to get the tableName from. 0 - AARDKUNDIG, 1 - VEENGEBIED
	 * @param outFields - Fields to filter, an asterisk if all results should be returned
	 * @param where - The where condition to filter
	 * @param resultOffset - The OFFSET
	 * @param resultRecordCount - The LIMIT
	 * @return a SQL String
	 */
	public String createPreparedStatement(int layerId, String outFields, String where, int resultOffset, int resultRecordCount) {
		String tableName = getTableName(layerId);
		
		// Create a StringBuilder to create a query
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		
		// Check for outFields to select the columns to return
		log.debug(String.format("outFields: %s", outFields));
		builder.append(processOutFieldsParameter(outFields));
		builder.append(String.format("FROM %s ", tableName));
		
		// Check for where parameter
		log.debug(String.format("where: %s", where));
		builder.append(processWhereParameter(where));
		
		// Check for offset and limit
		log.debug(String.format("resultOffset: %d, resultRecordCount: %d", resultOffset, resultRecordCount));
		builder.append(processOffsetAndRecordCount(resultOffset, resultRecordCount));
		
		return builder.toString();
	}
	
	/**
	 * Creates a query to create a temporary table to later filter columns out of it.
	 * 
	 * @param outFields - The fields to filter
	 * @return A SQL query String
	 */
	/*public String createTempTableQuery(String outFields) {
		// Create a StringBuilder to create a query
		StringBuilder builder = new StringBuilder();
		
		builder.append("SELECT ");
		builder.append(processOutFieldsParameter(outFields));
		builder.append("INTO tempTable ");
		builder.append(String.format("FROM %s ", AARDKUNDIG));
		
		return builder.toString();
	}*/
	
	/**
	 * Return the tableName for the given layerId.
	 * 0 - Aardkundige waarden
	 * 1 - Bodemkaart Veengebied
	 * 
	 * @param layerId - The given layerId
	 * @return the name of the table
	 */
	private String getTableName(int layerId) {
		switch(layerId) {
		case 0:
			return AARDKUNDIG;
		default:
			return VEENGEBIED;
		}
	}
	
	/**
	 * Go through the String and seperate at the comma to get all columns to filter from the table
	 * 
	 * @param outFields - The column names. A * for all columns
	 * @return Part of the query String
	 */
	private String processOutFieldsParameter(String outFields) {
		String[] outFieldsArray = outFields.split(",");
		if("*".equals(outFieldsArray[0])) {
			return "* ";
		}
		StringBuilder builder = new StringBuilder();
		for(String s : outFieldsArray) {
			builder.append(s + ", ");
		}
		// Remove the final comma
		builder.deleteCharAt(builder.length() - 2);
		
		return builder.toString();
	}
	
	/**
	 * Go through the String and check if there is a where clause in it. If so, append to the main query.
	 * 
	 * @param where - The where condition
	 * @return Part of the query String
	 */
	private String processWhereParameter(String where) {
		if("".equals(where)) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("WHERE ");
		builder.append(where + " ");
		
		return builder.toString();
	}
	
	/**
	 * Check if there are a LIMIT and OFFSET given. If so, append them to the main query.
	 * 
	 * @param resultOffset - the OFFSET
	 * @param resultRecordCount - the LIMIT
	 * @return Part of the query String
	 */
	private String processOffsetAndRecordCount(int resultOffset, int resultRecordCount) {
		StringBuilder builder = new StringBuilder();
		builder.append("");
		if(resultRecordCount > 0) {
			builder.append(String.format("LIMIT %d ", resultRecordCount));
		}
		if(resultOffset > 0) {
			builder.append(String.format("OFFSET %d ", resultOffset));
		}
		
		return builder.toString();
	}
}
