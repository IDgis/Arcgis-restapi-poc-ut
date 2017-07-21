package nl.idgis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
			@RequestParam(value="f", defaultValue="") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		// TODO create json file and add URL
		MetaDataHandler metaDataHandler = new FeatureServerHandler();
		log.info(String.format("Getting metadata for serviceName: %s", serviceName));
		return metaDataHandler.getMetadata("examples/featureserver.json");
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
			@RequestParam(value="f", defaultValue="") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		//TODO create json file and add URL
		String jsonUrl = null;
		switch(layerId) {
		case 0:
			jsonUrl = "<path-to-layerId-0>";
			break;
		case 1:
			jsonUrl = "<path-to-layerId-1>";
			break;
		default:
			return ErrorMessageHandler.getErrorMessage("Invalid layer id. Please enter 0 or 1");
		}
		
		MetaDataHandler metaDataHandler = new FeatureLayerHandler();
		log.info(String.format("Getting metadata for serviceName: %s and layerId: %d", serviceName, layerId));
		return metaDataHandler.getMetadata(jsonUrl);
	}
	
	/**
	 * This mapping gets the metadata for the query with the given parameters.
	 * @param serviceName - The service name
	 * @param layerId - The layer id for the given service name. Only layers 0 and 1 are available.
	 * @param formatType - The return type for the metadata. Only json is available.
	 * @return The metadata for the specified query in JSON
	 */
	@RequestMapping("/{serviceName}/FeatureServer/{layerId}/query")
	public Map<String, Object> getQueryResult(
			@PathVariable String serviceName,
			@PathVariable int layerId,
			@RequestParam(value="f", defaultValue="") String formatType) {
		
		if(!"json".equalsIgnoreCase(formatType)) {
			return ErrorMessageHandler.getErrorMessage(FORMAT_ERROR_MESSAGE);
		}
		
		//TODO implement method
		log.info(String.format("Getting query metadata for serviceName: %s and layerId: %d", serviceName, layerId));
		return null;
	}
}
