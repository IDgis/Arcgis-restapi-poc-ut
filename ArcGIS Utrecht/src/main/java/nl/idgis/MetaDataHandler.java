package nl.idgis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import nl.idgis.error.ErrorMessageHandler;

public abstract class MetaDataHandler {
	
	private static final Logger log = LoggerFactory.getLogger(MetaDataHandler.class);

	public MetaDataHandler() {
		log.debug("Creating MetaDataHandler...");
		// Empty constructor
	}
	
	public Map<String, Object> getMetadata(String fileUrl) {
		log.info("Getting metadata...");
		String fileContent = null;
		try {
			fileContent = readFile(fileUrl);
		} catch(IOException e) {
			log.error(e.getMessage());
			return ErrorMessageHandler.getErrorMessage(String.format("Could not open or read the file from: %s", fileUrl));
		}
		JsonParser parser = JsonParserFactory.getJsonParser();
		
		return parser.parseMap(fileContent);
	}
	
	private String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, Charset.defaultCharset());
	}
}
