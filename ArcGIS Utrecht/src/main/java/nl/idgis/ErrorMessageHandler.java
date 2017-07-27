package nl.idgis;

import com.google.gson.JsonObject;

public class ErrorMessageHandler {
	
	private ErrorMessageHandler() {}

	public static String getErrorMessage(String message) {
		JsonObject obj = new JsonObject();
		
		obj.addProperty("error", message);
		
		return obj.toString();
	}
}
