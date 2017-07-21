package nl.idgis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.idgis.featureserver.Layer;

// Geeft aan dat deze class een Controller is waarin urls ge-mapt worden
@RestController
public class ControllerTest {
	
	private static final Logger log = LoggerFactory.getLogger(ControllerTest.class);

	// @RequestParam als bijvoorbeeld: localhost:8080?name=test&version=1.0
	@RequestMapping("/")
	public Map<Object, Object> testMessage(@RequestParam(value="name", defaultValue="ArcGIS REST-API") Object appName,
							  @RequestParam(value="version", defaultValue="0.1.0") Object version) {
		
		// Creeer een Map die alle json objecten bevat
		Map<Object, Object> result = new LinkedHashMap<>();
		result.put(appName, version);
		result.put("Boolean", true);
		
		// Een list wordt terug gegeven als een Array
		List<Object> list = new ArrayList<>();
		list.add("Test");
		list.add("One");
		list.add("Two");
		result.put("ArrayList", list);
		
		// Een Array wordt terug gegeven als een Array
		int[] numbers = new int[]{5, 6, 7};
		result.put("Array", numbers);
		
		// Een Object wordt terug gegeven als een Object
		result.put("Anonymous Object", new Object() {
			private String info = "info String";
			@SuppressWarnings("unused")
			public String getInfo() { return info; }
		});
		
		// Een Map wordt terug gegeven als een Object
		Map<Object, Object> mapping = new LinkedHashMap<>();
		mapping.put("One", 1);
		mapping.put("Two", 2);
		List<Object> num = new ArrayList<>();
		num.add(3);
		num.add(4);
		mapping.put("continued", num);
		result.put("LinkedHashMap", mapping);
		
		return result;
	}
	
	/**
	 * Example: http://localhost:8080/query?f=json&where=test&outSR=28992
	 * 
	 * @param formatType - The response format. The default response format is json.
	 * @param where - A where clause for the query filter. Any legal SQL where clause operating on the fields in the layer is allowed.
	 * @param returnGeometry - If true, the resultset includes the geometry associated with each result. The default is true. 
	 * @param spatialRel - The spatial relationship to be applied on the input geometry while performing the query. 
	 * 			The supported spatial relationships include intersects, contains, envelope intersects, within, etc. 
	 * 			The default spatial relationship is intersects (esriSpatialRelIntersects).
	 * @param outFields -  The list of fields to be included in the returned resultset. This list is a comma delimited list of field names. 
	 * 			If you specify the shape field in the list of return fields, it is ignored. To request geometry, set returnGeometry to true.
	 * @param outSR - The spatial reference of the returned geometry.
	 * @return A Json from the values found in the query
	 */
	@RequestMapping("/services/{serviceName}/FeatureServer/{featureLayerId}/query")
	public Layer getQuery(@RequestParam(value="f", defaultValue="json") String formatType,
						  @RequestParam(value="where", defaultValue="") String where,
						  @RequestParam(value="returnGeometry", defaultValue="true") boolean returnGeometry,
						  @RequestParam(value="spatialRel", defaultValue="esriSpatialRelIntersects") String spatialRel,
						  @RequestParam(value="outFields", defaultValue="") String outFields,
						  @RequestParam(value="outSR", defaultValue="28992") int outSR) {
		
		log.info("Initializing query, checking parameters...");
		
		// First check if the return type is valid
		if(!"json".equalsIgnoreCase(formatType)) {
			return null;
		}
		
		// Check if the where query is present and execute it
		if(!"".equals(where)) {

			QueryHandler queryHandler = new QueryHandler(where);
			queryHandler.executeQuery(outSR);
			
			log.info("Query was successfull. Returning Layer in JSON...");
			return new Layer(queryHandler);
		}
		
		log.info("No where variable found. Returning default test values...");
		
		return new Layer(0, "Meetnet", "570d1dbbbe834ed89ee88c02e2d42cde", "esriGeometryPolyline", 0, 0, outSR);
	}
	
	// @PathVariable pakt de variabelen tussen {} uit de url
	@RequestMapping("/services/{serviceName}/FeatureServer/{featureLayerId}")
	public void getVal(@PathVariable String serviceName, @PathVariable String featureLayerId) {
		log.info(String.format("serviceName: %s, featureLayerId: %s", serviceName, featureLayerId));
	}
}
