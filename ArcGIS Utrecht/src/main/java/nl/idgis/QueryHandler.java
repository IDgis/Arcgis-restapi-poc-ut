package nl.idgis;

public class QueryHandler {

	private String query;
	
	private int id;
	private String name;
	private String serviceItemId;
	private String geometryType;
	private int minScale;
	private int maxScale;
	private int outSR;
	
	public QueryHandler(String query) {
		this.query = query;
	}
	
	public void executeQuery(int outSR) {
		id = 0;
		name = "Meetnet";
		serviceItemId = "570d1dbbbe834ed89ee88c02e2d42cde";
		geometryType = "esriGeometryPolyline";
		minScale = 0;
		maxScale = 0;
		this.outSR = outSR;
	}

	public String getQuery() {
		return query;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getServiceItemId() {
		return serviceItemId;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public int getMinScale() {
		return minScale;
	}

	public int getMaxScale() {
		return maxScale;
	}
	
	public int getOutSR() {
		return outSR;
	}
}
