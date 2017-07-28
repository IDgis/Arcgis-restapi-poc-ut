package nl.idgis.query;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.esri.terraformer.formats.EsriJson;
import com.esri.terraformer.formats.GeoJson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nl.idgis.QueryHandler;

@Component
public class QueryBuilder {
	
	private static final String AARDKUNDIG = "staging_data.\"222b66f4-b450-4871-8874-52170d56b1e8\"";
	private static final String VEENGEBIED = "staging_data.\"bddbf261-4401-47e0-a721-d832a44e0462\"";
	
	private static final String[] aardkundigFields = {"INSPIREID", "PERCENTAGEUNDERDESIGNATION", "URL1", "URL2", "GEBIEDSOMS", "FUNCTIE", "geoJsons"};
	private static final String[] veengebiedFields = {"CODE", "OMSCHRIJVI", "geoJsons"};
	
	private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);
	
	@Autowired QueryHandler handler;

	/**
	 * Builds the json to return to ArcGIS so the results can be displayed on the map.
	 * 
	 * @param layerId - The layer number
	 * @return
	 */
	@Cacheable("data")
	@SuppressWarnings("unchecked")
	public String getJsonQueryResult(int layerId, String where, boolean returnGeometry, String geometry, int outSR, int resultOffset, int resultRecordCount) {
		log.debug("Generating data...");
		String dbUrl = getDbUrl(layerId);
		String[] fields = getFieldsToGet(layerId);
		double[] extent = getExtentFromGeometry(geometry);
		Map<String, Object> data = handler.getDataFromTable(dbUrl, fields, where, extent, outSR, resultOffset, resultRecordCount);
		
		List<String> geoJsons = (List<String>)data.get("geoJsons");
		
		JsonObject obj = new JsonObject();
		
		obj.addProperty("objectIdFieldName", "OBJECTID");
		obj.addProperty("globalIdFieldName", "");
		obj.addProperty("geometryType", "esriGeometryPolygon");
		obj.add("spatialReference", getSpatialReference());
		obj.add("fields", getFields(layerId));
		obj.add("features", getFeatures(geoJsons, returnGeometry));
		
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
	
	private String[] getFieldsToGet(int layerId) {
		switch(layerId) {
		case 0:
			return aardkundigFields;
		case 1:
			return veengebiedFields;
		default:
			return null;
		}
	}
	
	private double[] getExtentFromGeometry(String geometry) {
		if("".equals(geometry)) {
			return new double[0];
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(geometry);
		
		double xmin = element.getAsJsonObject().get("xmin").getAsDouble();
		double ymin = element.getAsJsonObject().get("ymin").getAsDouble();
		double xmax = element.getAsJsonObject().get("xmax").getAsDouble();
		double ymax = element.getAsJsonObject().get("ymax").getAsDouble();
		
		return new double[]{ xmin, ymin, xmax, ymax };
	}
	
	private JsonObject getSpatialReference() {
		log.debug("Getting spatialReference...");
		JsonObject obj = new JsonObject();
		
		obj.addProperty("wkid", 28992);
		obj.addProperty("latestWkid", 28992);
		
		return obj;
	}
	
	private JsonArray getFields(int layerId) {
		log.debug("Getting fields...");
		
		if(layerId == 0) {
			return getAardkundigeFields();
		}
		else {
			return getVeengebiedFields();
		}
	}
	
	private JsonArray getAardkundigeFields() {
		JsonArray arr = new JsonArray();
		
		JsonObject objectId = new JsonObject();
		objectId.addProperty("name", "OBJECTID");
		objectId.addProperty("type", "esriFieldTypeOID");
		objectId.addProperty("alias", "OBJECTID");
		objectId.addProperty("sqlType", "sqlTypeOther");
		objectId.add("domain", null);
		objectId.add("defaultValue", null);
		
		JsonObject inspireId = new JsonObject();
		inspireId.addProperty("name", "INSPIREID");
		inspireId.addProperty("type", "esriFieldTypeString");
		inspireId.addProperty("alias", "INSPIREID");
		inspireId.addProperty("sqlType", "sqlTypeOther");
		inspireId.addProperty("length", 200);
		inspireId.add("domain", null);
		inspireId.add("defaultValue", null);
		
		JsonObject percentage = new JsonObject();
		percentage.addProperty("name", "PERCENTAGEUNDERDESIGNATION");
		percentage.addProperty("type", "esriFieldTypeInteger");
		percentage.addProperty("alias", "PERCENTAGEUNDERDESIGNATION");
		percentage.addProperty("sqlType", "sqlTypeOther");
		percentage.add("domain", null);
		percentage.add("defaultValue", null);
		
		JsonObject url1 = new JsonObject();
		url1.addProperty("name", "URL1");
		url1.addProperty("type", "esriFieldTypeString");
		url1.addProperty("alias", "URL1");
		url1.addProperty("sqlType", "sqlTypeOther");
		url1.addProperty("length", 200);
		url1.add("domain", null);
		url1.add("defaultValue", null);
		
		JsonObject url2 = new JsonObject();
		url2.addProperty("name", "URL2");
		url2.addProperty("type", "esriFieldTypeString");
		url2.addProperty("alias", "URL2");
		url2.addProperty("sqlType", "sqlTypeOther");
		url2.addProperty("length", 200);
		url2.add("domain", null);
		url2.add("defaultValue", null);
		
		JsonObject omschrijving = new JsonObject();
		omschrijving.addProperty("name", "GEBIEDSOMS");
		omschrijving.addProperty("type", "esriFieldTypeString");
		omschrijving.addProperty("alias", "GEBIEDSOMS");
		omschrijving.addProperty("sqlType", "sqlTypeOther");
		omschrijving.addProperty("length", 800);
		omschrijving.add("domain", null);
		omschrijving.add("defaultValue", null);
		
		JsonObject functie = new JsonObject();
		functie.addProperty("name", "FUNCTIE");
		functie.addProperty("type", "esriFieldTypeString");
		functie.addProperty("alias", "FUNCTIE");
		functie.addProperty("sqlType", "sqlTypeOther");
		functie.addProperty("length", 100);
		functie.add("domain", null);
		functie.add("defaultValue", null);
		
		arr.add(objectId);
		arr.add(inspireId);
		arr.add(percentage);
		arr.add(url1);
		arr.add(url2);
		arr.add(omschrijving);
		arr.add(functie);
		return arr;
	}
	
	private JsonArray getVeengebiedFields() {
		JsonArray arr = new JsonArray();
		
		JsonObject code = new JsonObject();
		code.addProperty("name", "CODE");
		code.addProperty("type", "esriFieldTypeString");
		code.addProperty("alias", "CODE");
		code.addProperty("sqlType", "sqlTypeOther");
		code.addProperty("length", 80);
		code.add("domain", null);
		code.add("defaultValue", null);
		
		JsonObject omschrijving = new JsonObject();
		omschrijving.addProperty("name", "OMSCHRIJVI");
		omschrijving.addProperty("type", "esriFieldTypeString");
		omschrijving.addProperty("alias", "OMSCHRIJVI");
		omschrijving.addProperty("sqlType", "sqlTypeOther");
		omschrijving.addProperty("length", 200);
		omschrijving.add("domain", null);
		omschrijving.add("defaultValue", null);
		
		return arr;
	}
	
	private JsonArray getFeatures(List<String> geoJsons, boolean returnGeometry) {
		log.debug("Getting features...");
		JsonArray arr = new JsonArray();
		
		int numObjects = geoJsons.size();
		log.debug(String.format("%d results found...", numObjects));
		
		if(numObjects > 0) {
			for(int i = 0; i < numObjects; i++) {
				arr.add(getFeature(geoJsons, i, returnGeometry));
			}
		}
		
		log.debug("All features found, returning the array...");
		return arr;
	}
	
	@Cacheable("esriJson")
	private JsonObject getFeature(List<String> geoJsons, int index, boolean returnGeometry) {
		JsonObject obj = new JsonObject();
		
		obj.add("attributes", getAttributes(index));
		
		String esriJson = getEsriJson(geoJsons.get(index));
		JsonParser parser = new JsonParser();
		
		if(returnGeometry) {
			obj.add("geometry", parser.parse(esriJson));
		}
		
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