package nl.idgis.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.idgis.ErrorMessageHandler;
import nl.idgis.MetaDataHandler;
import nl.idgis.QueryHandler;
import nl.idgis.featurelayer.FeatureLayerHandler;
import nl.idgis.featureserver.FeatureServerHandler;
import nl.idgis.query.QueryBuilder;

@RestController
@RequestMapping("/ArcGIS/rest")
public class Controller {
	
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	
	private static final String FORMAT_ERROR_MESSAGE = "Invalid format type. Can only return JSON!";
	
	@Autowired
	private QueryBuilder builder;
	
	@Autowired
	private QueryHandler handler;
	
	/**
	 * The ServerInfo resource provides general information about the server (e.g. current version of the server), 
	 * and provides information on whether the server is secured using token based authentication; and the token 
	 * services url (if token based authentication is used).
	 * 
	 * @param formatType
	 * @return
	 */
	@RequestMapping("/info")
	public Map<String, Object> getServerInfo(
			@RequestParam(value="f", defaultValue="json") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			log.warn(FORMAT_ERROR_MESSAGE);
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		Map<String, Object> returnMap = new LinkedHashMap<>();
		returnMap.put("currentVersion", 10.51);
		returnMap.put("owningSystemUrl", "http://localhost:8090");
		
		Map<String, Object> authInfo = new LinkedHashMap<>();
		authInfo.put("isTokenBasedSecurity", false);
		returnMap.put("authInfo", authInfo);
		
		return returnMap;
	}

	/**
	 * This mapping gets the metadata for the FeatureServer. If an invalid format type is given, it will give
	 * an error message in json.
	 * @param serviceName - The service name
	 * @param formatType - The return type for the metadata. Only json is available.
	 * @return The metadata of the FeatureServer in JSON.
	 */
	@RequestMapping("/services/{serviceName}/FeatureServer")
	public Map<String, Object> getFeatureServerMetadata(
			@PathVariable String serviceName,
			@RequestParam(value="f", defaultValue="json") String formatType) {
		
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
	@RequestMapping("/services/{serviceName}/FeatureServer/{layerId}")
	public Map<String, Object> getFeatureLayerMetadata(
			@PathVariable String serviceName,
			@PathVariable int layerId,
			@RequestParam(value="f", defaultValue="json") String formatType) {
		
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
	@RequestMapping("/services/{serviceName}/FeatureServer/{layerId}/query")
	public Map<String, Object> getQueryResult(
			@PathVariable String serviceName,
			@PathVariable int layerId,
			@RequestParam(value="f", defaultValue="json") String formatType,
			@RequestParam(value="where", defaultValue="") String where,
			@RequestParam(value="returnGeometry", defaultValue="true") boolean returnGeometry,
			@RequestParam(value="spatialRel", defaultValue="esriSpatialRelIntersects") String spatialRel,
			@RequestParam(value="outFields", defaultValue="*") String outFields,
			@RequestParam(value="outSR", defaultValue="0") long outSR,
			@RequestParam(value="resultOffset", defaultValue="0") int resultOffset,
			@RequestParam(value="resultRecordCount", defaultValue="0") int resultRecordCount,
			@RequestParam(value="quantizationParameters", defaultValue="") String quantizationParameters) {
		
		// Check for required fields to be present
		if(!"json".equalsIgnoreCase(formatType)) {
			log.warn(FORMAT_ERROR_MESSAGE);
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		return builder.getJsonQueryResult(layerId);
		
		// Check if a geometry should be returned. Else exclude the column from the result
		/*if(!returnGeometry) {
			log.debug("Creating prepared statement from attributes without geometry...");
			// Get the data into a temp table
			String query = builder.createTempTableQuery(outFields);
			handler.executePreparedStatement(query);
			
			// Drop the geometry column
			handler.executePreparedStatement("ALTER TABLE tempTable DROP COLUMN geometry");
			
			// Get the results and drop the temp table
			query = builder.createPreparedStatement("*", where, resultOffset, resultRecordCount);
			Map<String, Object> result = handler.getQueryResult(query);
			handler.executePreparedStatement("DROP TABLE tempTable");
			
			return result;
		}
		
		log.debug("Creating prepared statement from attributes...");
		String query = builder.createPreparedStatement(layerId, outFields, where, resultOffset, resultRecordCount);
		
		log.debug("Got a valid query. Querying to database...");
		return handler.getQueryResult(query);*/
	}
}
