package nl.idgis.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.esri.terraformer.formats.EsriJson;
import com.esri.terraformer.formats.GeoJson;

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
	public Map<String, Object> getJsonQueryResult(int layerId) {
		log.debug("Creating JSON Object...");
		String dbUrl = getDbUrl(layerId);
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("objectIdFieldName", "OBJECTID");
		map.put("globalIdFieldName", "");
		map.put("geometryType", "esriGeometryPolygon");
		map.put("spatialReference", getSpatialReference());
		map.put("fields", getFields());
		map.put("features", getFeatures(dbUrl));
		
		return map;
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
	
	private Map<String, Object> getSpatialReference() {
		log.debug("Getting spatialReference...");
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("wkid", 28992);
		map.put("latestWkid", 28992);
		
		return map;
	}
	
	private List<Object> getFields() {
		log.debug("Getting fields...");
		List<Object> list = new ArrayList<>();
		
		list.add(getField());
		
		return list;
	}
	
	private Map<String, Object> getField() {
		log.debug("Getting field...");
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("name", "OBJECTID");
		map.put("type", "esriFieldTypeOID");
		map.put("alias", "OBJECTID");
		map.put("sqlType", "sqlTypeOther");
		map.put("domain", null);
		map.put("defaultValue", null);
		
		return map;
	}
	
	// SELECT SELECT ST_AsGeoJson("SHAPE") FROM {URL}
	private List<Object> getFeatures(String dbUrl) {
		log.debug("Getting features...");
		//int numObjects = handler.getNumDbRows(dbUrl);
		int numObjects = 15; //TODO remove hardcoded number
		List<Object> list = new ArrayList<>();
		
		if(numObjects > 0) {
			for(int i = 1; i <= numObjects; i++) {
				list.add(getFeature(dbUrl, i));
			}
		}
		
		return list;
	}
	
	private Map<String, Object> getFeature(String dbUrl, int index) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("attributes", getAttributes(index));
		
		JsonParser parser = JsonParserFactory.getJsonParser();
		map.put("geometry", parser.parseMap(getGeoJson(dbUrl, index)));
		
		return map;
	}
	
	private Map<String, Object> getAttributes(int index) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("OBJECTID", index);
		
		return map;
	}
	
	private String getGeoJson(String dbUrl, int index) {
		Terraformer t = new Terraformer();
		
		t.setDecoder(new GeoJson());
		EsriJson ej = new EsriJson();
		ej.setSpatialReference(28992);
		t.setEncoder(ej);
		
		String geoJson = handler.getGeoJsonGeometry(dbUrl, index);
		String esriJson;
		try {
			esriJson = t.convert(geoJson);
			return esriJson;
		} catch (TerraformerException e) {
			log.error(e.getMessage(), e);
		}
		
		return "";
	}
}
