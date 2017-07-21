package nl.idgis;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.idgis.error.ErrorMessageHandler;
import nl.idgis.featurelayer.FeatureLayerHandler;
import nl.idgis.featureserver.FeatureServerHandler;

@RestController
@RequestMapping("/rest/services")
public class Controller {
	
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	
	private static final String FORMAT_ERROR_MESSAGE = "Invalid format type. Can only return JSON!";

	/**
	 * This mapping gets the metadata for the FeatureServer. If an invalid format type is given, it will give
	 * an error message in json.
	 * @param serviceName - The service name
	 * @param formatType - The return type for the metadata. Only json is available.
	 * @return The metadata of the FeatureServer in JSON.
	 */
	@RequestMapping("/{serviceName}/FeatureServer")
	public Map<String, Object> getFeatureServerMetadata(
			@PathVariable String serviceName,
			@RequestParam(value="f", defaultValue="html") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			log.warn(FORMAT_ERROR_MESSAGE);
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		MetaDataHandler metaDataHandler = new FeatureServerHandler();
		log.debug(String.format("Getting metadata for serviceName: %s", serviceName));
		return metaDataHandler.getMetadata("./examples/featureserver.json");
	}
	
	/**
	 * This mapping gets the metadata for the FeatureLayer. If an invalid format type is given, it will give
	 * an error message in json.
	 * @param serviceName - The service name
	 * @param layerId - The layer id for the given service name. Only layers 0 and 1 are available.
	 * @param formatType - The return type for the metadata. Only json is available.
	 * @return The metadata of the FeatureLayer in JSON.
	 */
	@RequestMapping("/{serviceName}/FeatureServer/{layerId}")
	public Map<String, Object> getFeatureLayerMetadata(
			@PathVariable String serviceName,
			@PathVariable int layerId,
			@RequestParam(value="f", defaultValue="html") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			log.warn(FORMAT_ERROR_MESSAGE);
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		String jsonUrl = null;
		switch(layerId) {
		case 0:
			jsonUrl = "./examples/featureLayer0.json";
			break;
		case 1:
			jsonUrl = "./examples/featureLayer1.json";
			break;
		default:
			return ErrorMessageHandler.getErrorMessage("Invalid layer id. Please enter 0 or 1");
		}
		
		MetaDataHandler metaDataHandler = new FeatureLayerHandler();
		log.debug(String.format("Getting metadata for serviceName: %s and layerId: %d", serviceName, layerId));
		return metaDataHandler.getMetadata(jsonUrl);
	}
	
	/**
	 * This mapping gets the metadata for the query with the given parameters.
	 * @param serviceName - The service name
	 * @param layerId - The layer id for the given service name. Only layers 0 and 1 are available.
	 * @param formatType - The response format. The default response format is html.
	 * @param where - A where clause for the query filter. Any legal SQL where clause operating on the fields in the layer is allowed.
	 * @param returnGeometry - If true, the resultset includes the geometry associated with each result. The default is true.
	 * @param spatialRel - The spatial relationship to be applied on the input geometry while performing the query. 
	 * 		The supported spatial relationships include intersects, contains, envelope intersects, within, etc. The default 
	 * 		spatial relationship is intersects (esriSpatialRelIntersects).
	 * @param outFields -  The list of fields to be included in the returned resultset. This list is a comma delimited 
	 * 		list of field names. If you specify the shape field in the list of return fields, it is ignored. To request geometry, 
	 * 		set returnGeometry to true. You can also specify the wildcard "*" as the value of this parameter. In this case, the 
	 * 		query results include all the field values. 
	 * @param outSR - The spatial reference of the returned geometry. 
	 * @param resultOffset - Description: This option can be used for fetching query results by skipping the specified number 
	 * 		of records and starting from the next record (that is, resultOffset + 1th). The default is 0. This parameter only 
	 * 		applies if supportsPagination is true. You can use this option to fetch records that are beyond maxRecordCount. 
	 * 		For example, if maxRecordCount is 1000, you can get the next 100 records by setting resultOffset=1000 and 
	 * 		resultRecordCount = 100, so the query results will return the results in the range of 1001 to 1100.
	 * @param resultRecordCount - Description: This option can be used for fetching query results up to the resultRecordCount specified. 
	 * 		When resultOffset is specified but this parameter is not, the map service defaults it to maxRecordCount. 
	 * 		The maximum value for this parameter is the value of the layer's maxRecordCount property. This parameter only applies if 
	 * 		supportsPagination is true. Example: resultRecordCount=10 to fetch up to 10 records
	 * @param quantizationParameters -  Used to project the geometry onto a virtual grid, likely representing pixels on the screen. This parameter 
	 * 		only applies if supportsCoordinatesQuantization is true. 
	 * 		<br>extent -  An extent defining the quantization grid bounds. Its SpatialReference matches the input geometry spatial reference 
	 * 		if one is specified for the query. Otherwise, the extent will be in the layer's spatial reference.
	 * 		<br>mode - Geometry coordinates are optimized for viewing and displaying of data. Value: view
	 * 		<br>originPosition - Integer coordinates will be returned relative to the origin position defined by this property value. 
	 * 		Default is upperLeft origin position. Values: upperLeft | lowerLeft
	 * 		<br>tolerance - The tolerance is the size of one pixel in the outSpatialReference units, this number is used to convert the 
	 * 		coordinates to integers by building a grid with resolution matching the tolerance. Each coordinate is then snapped to one pixel on the grid. 
	 * 		Consecutive coordinates snapped to the same pixel are removed to reduce the overall response size. 
	 * 		If the tolerance is not specified, the maxAllowableOffset is used.
	 * @return The metadata for the specified query in JSON
	 */
	@RequestMapping("/{serviceName}/FeatureServer/{layerId}/query")
	public Map<String, Object> getQueryResult(
			@PathVariable String serviceName,
			@PathVariable int layerId,
			@RequestParam(value="f", defaultValue="html") String formatType,
			@RequestParam(value="where", defaultValue="") String where,
			@RequestParam(value="returnGeometry", defaultValue="true") boolean returnGeometry,
			@RequestParam(value="spatialRel", defaultValue="esriSpatialRelIntersects") String spatialRel,
			@RequestParam(value="outFields", defaultValue="") String[] outFields,
			@RequestParam(value="outSR", defaultValue="") long outSR,
			@RequestParam(value="resultOffset", defaultValue="0") int resultOffset,
			@RequestParam(value="resultRecordCount", defaultValue="") int resultRecordCount,
			@RequestParam(value="quantizationParameters", defaultValue="") Object quantizationParameters) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			log.warn(FORMAT_ERROR_MESSAGE);
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		// Map all attributes to create a query to the database
		log.debug("Mapping all attributes from URL...");
		Map<String, Object> params = new HashMap<>();
		params.put("where", where);
		params.put("returnGeometry", returnGeometry);
		params.put("spatialRel", spatialRel);
		params.put("outFields", outFields);
		params.put("outSR", outSR);
		params.put("resultOffset", resultOffset);
		params.put("resultRecordCount", resultRecordCount);
		params.put("quantizationParameters", quantizationParameters);
		
		// Create a valid query String from the specified parameters
		log.debug("Parsing attributes to create a valifd query...");
		QueryParser parser = new QueryParser(params);
		parser.createValidQuery();
		String query = parser.getQuery();
		log.debug("Got a valid query. Sending query to database...");
		
		// Post the query to the database to get all properties and return them in json;
		return QueryHandler.executeQuery(query, "user", "password");
	}
}
