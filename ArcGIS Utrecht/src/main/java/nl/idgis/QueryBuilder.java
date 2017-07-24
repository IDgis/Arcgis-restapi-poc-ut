package nl.idgis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryBuilder {

	private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);
	
	private static final String TABLE_NAME1 = "constants";
	private static final String TABLE_NAME2 = "data_source";
	private static final String TABLE_NAME3 = "source_dataset";
	
	public String createPreparedStatement(String outFields, String where) {
		// Create a StringBuilder to create a query
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		
		// Check for outFields to select the columns to return
		log.debug(String.format("outFields: %s", outFields));
		builder.append(processOutFieldsParameter(outFields));
		builder.append(String.format("FROM %s ", TABLE_NAME3));
		
		// Check for where parameter
		log.debug(String.format("where: %s", where));
		builder.append(processWhereParameter(where));
		
		return builder.toString();
	}
	
	/**
	 * Go through the String and seperate at the comma to get all columns to filter from the table
	 * @param outFields - The column names. A * for all columns
	 * @return
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
	
	private String processWhereParameter(String where) {
		if("".equals(where)) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("WHERE ");
		builder.append(where + " ");
		
		return builder.toString();
	}
}
