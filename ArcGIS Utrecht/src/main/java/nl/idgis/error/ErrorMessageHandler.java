package nl.idgis.error;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageHandler {
	
	private ErrorMessageHandler() {}

	public static Map<String, Object> getErrorMessage(String message) {
		Map<String, Object> errorMessage = new HashMap<>();
		errorMessage.put("error", message);
		return errorMessage;
	}
}
