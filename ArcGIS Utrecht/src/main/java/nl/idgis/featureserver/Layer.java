package nl.idgis.featureserver;

import nl.idgis.QueryHandler;
import nl.idgis.featureserver.layer.AdvancedQueryCapabilities;
import nl.idgis.featureserver.layer.EditingInfo;

public class Layer {
	
	// VARIABLES
	private double currentVersion;
	private int id;
	private String name;
	private String type;
	private String serviceItemId;
	private String displayField;
	private String description;
	private String copyrightText;
	private boolean defaultVisibility;
	private EditingInfo editingInfo;
	private String[] relationships;
	private boolean isDataVersioned;
	private boolean supportsCalculate;
	private boolean supportsTruncate;
	private boolean supportsAttachmentsByUploadId;
	private boolean supportsRollbackOnFailureParameter;
	private boolean supportsStatistics;
	private boolean supportsAdvancedQueries;
	private boolean supportsValidateSql;
	private boolean supportsCoordinatesQuantization;
	private boolean supportsApplyEditsWithGlobalIds;
	private boolean supportsMultiScaleGeometry;
	private AdvancedQueryCapabilities advancedQueryCapabilities;
	private boolean useStandardizedQueries;
	private String geometryType;
	private int minScale;
	private int maxScale;
	private Object extent;
	
	
	public Layer() {
		currentVersion = 10.41;
		type = "Feature Layer";
		displayField = "LEGENDA";
		description = "";
		copyrightText = "";
		defaultVisibility = false;
		editingInfo = new EditingInfo();
		relationships = new String[]{};
		isDataVersioned = false;
		supportsCalculate = false;
		supportsTruncate = false;
		supportsAttachmentsByUploadId = false;
		supportsRollbackOnFailureParameter = false;
		supportsStatistics = false;
		supportsAdvancedQueries = false;
		supportsValidateSql = false;
		supportsCoordinatesQuantization = false;
		supportsApplyEditsWithGlobalIds = false;
		supportsMultiScaleGeometry = false;
		advancedQueryCapabilities = new AdvancedQueryCapabilities();
		useStandardizedQueries = false;
	}
	
	public Layer(int id, String name, String serviceItemId, String geometryType, int minScale, int maxScale, int outSR) {
		this();
		this.id = id;
		this.name = name;
		this.serviceItemId = serviceItemId;
		this.geometryType = geometryType;
		this.minScale = minScale;
		this.maxScale = maxScale;
		extent = new Object() {
			private double xmin = 136095.57119999826;
			private double ymin = 454560.44399999821;
			private double xmax = 141030.35200000182;
			private double ymax = 455956.7509999983;
			private Object spatialReference = new Object() {
				private int wkid = outSR;
				private int latestWkid = outSR;
				
				public int getWkid() {
					return wkid;
				}
				
				public int getLatestWkid() {
					return latestWkid;
				}
			};
			
			public Object getSpatialReference() {
				return spatialReference;
			}
			
			public double getXmin() {
				return xmin;
			}
			
			public double getYmin() {
				return ymin;
			}
			
			public double getXmax() {
				return xmax;
			}
			
			public double getYmax() {
				return ymax;
			}
		};
	}
	
	public Layer(QueryHandler handler) {
		this(handler.getId(), handler.getName(), handler.getServiceItemId(), handler.getGeometryType(), handler.getMinScale(), handler.getMaxScale(), handler.getOutSR());
	}
	
	
	/////////////////////////////////////////////////////////
	////////////////////// GETTERS //////////////////////////
	/////////////////////////////////////////////////////////
	
	// De getters worden gebruikt om alle variablen van het object automatisch om te zetten naar json
	// Als weergavenaam wordt alles voor de 1e hoofdletter verwijderd en de hoofdletter omgezet in kleine letter
	// getCurrentVersion() => currentVersion
	
	public double getCurrentVersion() {
		return currentVersion;
	}

	public String getType() {
		return type;
	}

	public String[] getRelationships() {
		return relationships;
	}

	public boolean isDefaultVisibility() {
		return defaultVisibility;
	}

	public EditingInfo getEditingInfo() {
		return editingInfo;
	}

	public boolean isIsDataVersioned() {
		return isDataVersioned;
	}

	public boolean isSupportsCalculate() {
		return supportsCalculate;
	}

	public boolean isSupportsTruncate() {
		return supportsTruncate;
	}

	public boolean isSupportsAttachmentsByUploadId() {
		return supportsAttachmentsByUploadId;
	}

	public boolean isSupportsRollbackOnFailureParameter() {
		return supportsRollbackOnFailureParameter;
	}

	public boolean isSupportsStatistics() {
		return supportsStatistics;
	}

	public boolean isSupportsAdvancedQueries() {
		return supportsAdvancedQueries;
	}

	public boolean isSupportsValidateSql() {
		return supportsValidateSql;
	}

	public boolean isSupportsCoordinatesQuantization() {
		return supportsCoordinatesQuantization;
	}

	public boolean isSupportsApplyEditsWithGlobalIds() {
		return supportsApplyEditsWithGlobalIds;
	}

	public boolean isSupportsMultiScaleGeometry() {
		return supportsMultiScaleGeometry;
	}

	public String getDisplayField() {
		return displayField;
	}

	public String getDescription() {
		return description;
	}

	public String getCopyrightText() {
		return copyrightText;
	}

	public AdvancedQueryCapabilities getAdvancedQueryCapabilities() {
		return advancedQueryCapabilities;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public boolean isUseStandardizedQueries() {
		return useStandardizedQueries;
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

	public int getMinScale() {
		return minScale;
	}

	public int getMaxScale() {
		return maxScale;
	}

	public Object getExtent() {
		return extent;
	}
}
