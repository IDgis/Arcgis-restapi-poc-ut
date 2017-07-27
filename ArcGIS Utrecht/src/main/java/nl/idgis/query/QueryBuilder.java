package nl.idgis.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.esri.terraformer.formats.EsriJson;
import com.esri.terraformer.formats.GeoJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nl.idgis.QueryHandler;

@Component
public class QueryBuilder {
	
	private static final String AARDKUNDIG = "staging_data.\"222b66f4-b450-4871-8874-52170d56b1e8\"";
	private static final String VEENGEBIED = "staging_data.\"bddbf261-4401-47e0-a721-d832a44e0462\"";
	
	private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);
	
	@Autowired QueryHandler handler;

	/**
	 * Builds the json to return to ArcGIS so the results can be displayed on the map.
	 * 
	 * @param layerId - The layer number
	 * @return
	 */
	public String getJsonQueryResult(int layerId) {
		log.debug("Creating JSON Object...");
		String dbUrl = getDbUrl(layerId);
		
		JsonObject obj = new JsonObject();
		
		obj.addProperty("objectIdFieldName", "OBJECTID");
		obj.addProperty("globalIdFieldName", "");
		obj.addProperty("geometryType", "esriGeometryPolygon");
		obj.add("spatialReference", getSpatialReference());
		obj.add("fields", getFields());
		obj.add("features", getFeatures(dbUrl));
		
		return obj.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	private String getDbUrl(int layerId) {
		switch(layerId) {
		case 0:
			return AARDKUNDIG;
		case 1:
			return VEENGEBIED;
		default:
			return null;
		}
	}
	
	private JsonObject getSpatialReference() {
		log.debug("Getting spatialReference...");
		JsonObject obj = new JsonObject();
		
		obj.addProperty("wkid", 28992);
		obj.addProperty("latestWkid", 28992);
		
		return obj;
	}
	
	private JsonArray getFields() {
		log.debug("Getting fields...");
		JsonArray arr = new JsonArray();
		
		arr.add(getField());
		
		return arr;
	}
	
	private JsonObject getField() {
		log.debug("Getting field...");
		JsonObject obj = new JsonObject();
		
		obj.addProperty("name", "OBJECTID");
		obj.addProperty("type", "esriFieldTypeOID");
		obj.addProperty("alias", "OBJECTID");
		obj.addProperty("sqlType", "sqlTypeOther");
		obj.add("domain", null);
		obj.add("defaultValue", null);
		
		return obj;
	}
	
	private JsonArray getFeatures(String dbUrl) {
		log.debug("Getting features...");
		JsonArray arr = new JsonArray();
		
		List<String> geoJsons = handler.getGeoJsonsFromTable(dbUrl);
		int numObjects = geoJsons.size();
		
		if(numObjects > 0) {
			for(int i = 0; i < numObjects; i++) {
				arr.add(getFeature(geoJsons, i));
			}
		}
		
		return arr;
	}
	
	private JsonObject getFeature(List<String> geoJsons, int index) {
		JsonObject obj = new JsonObject();
		
		obj.add("attributes", getAttributes(index));
		
		String esriJson = getEsriJson(geoJsons.get(index));
		JsonParser parser = new JsonParser();
		
		obj.add("geometry", parser.parse(esriJson));
		
		return obj;
	}
	
	private JsonObject getAttributes(int index) {
		JsonObject obj = new JsonObject();
		
		obj.addProperty("OBJECTID", index + 1);
		
		return obj;
	}
	
	private String getEsriJson(String geoJson) {
		Terraformer t = new Terraformer();
		
		t.setDecoder(new GeoJson());
		EsriJson ej = new EsriJson();
		ej.setSpatialReference(28992);
		t.setEncoder(ej);
		
		String esriJson = "";
		try {
			esriJson = t.convert(geoJson);
		} catch(TerraformerException e) {
			log.error(e.getMessage(), e);
		}
		
		return esriJson;
	}
}