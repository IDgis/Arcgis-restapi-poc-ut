package nl.idgis.error;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorPageHandler implements ErrorController {

	private static final String PATH = "/error";
	
	@RequestMapping(PATH)
	public String error() {
		return "<h1>You entered an invalid URL!</h1><br><br>"
				+ "Please extends your URL with /rest/services/{serviceName}/FeatureServer";
	}
	
	@Override
	public String getErrorPath() {
		return PATH;
	}
}
