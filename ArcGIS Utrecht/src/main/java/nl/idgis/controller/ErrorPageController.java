package nl.idgis.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorPageController implements ErrorController {

	private static final String PATH = "/error";
	
	@RequestMapping(PATH)
	public Map<String, Object> error() {
		Map<String, Object> retVal = new HashMap<>();
		retVal.put("error", "Something went wrong!! Use URL: /ArcGIS/rest/services/Bodemlagen/FeatureServer");
		return retVal;
	}
	
	@Override
	public String getErrorPath() {
		return PATH;
	}
}
