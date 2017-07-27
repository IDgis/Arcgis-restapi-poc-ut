package nl.idgis.featureserver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

import nl.idgis.ErrorMessageHandler;
import nl.idgis.MetaDataHandler;

public class FeatureServerHandler implements MetaDataHandler {
	
	private static final Logger log = LoggerFactory.getLogger(FeatureServerHandler.class);

	public FeatureServerHandler() {
		super();
		log.debug("Creating FeatureServerHandler...");
	}
	
	public String getMetadata(String fileUrl) {
		log.info("Getting metadata for feature server...");
		String fileContent = null;
		try {
			fileContent = readFile(fileUrl);
		} catch(IOException e) {
			log.error(e.getMessage(), e);
			return ErrorMessageHandler.getErrorMessage(String.format("Could not open or read the file from: %s", fileUrl));
		}
		JsonParser parser = new JsonParser();
		
		return parser.parse(fileContent).toString();
	}
}
