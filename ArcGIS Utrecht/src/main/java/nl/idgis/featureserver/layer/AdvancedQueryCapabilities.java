package nl.idgis.featureserver.layer;

public class AdvancedQueryCapabilities {

	private boolean supportsPagination;
	private boolean supportsQueryRelatedPagination;
	private boolean supportsQueryWithDistance;
	private boolean supportsReturningQueryExtent;
	private boolean supportsStatistics;
	private boolean supportsOrderBy;
	private boolean supportsDistinct;
	private boolean supportsQueryWithResultType;
	private boolean supportsSqlExpression;
	private boolean supportsAdvancedQueryRelated;
	private boolean supportsReturningGeometryCentroid;
	private boolean supportsReturningGeometryProperties;
	
	public AdvancedQueryCapabilities() {
		// Default all values to true for testing
		supportsPagination = true;
		supportsQueryRelatedPagination = true;
		supportsQueryWithDistance = true;
		supportsReturningQueryExtent = true;
		supportsStatistics = true;
		supportsOrderBy = true;
		supportsDistinct = true;
		supportsQueryWithResultType = true;
		supportsSqlExpression = true;
		supportsAdvancedQueryRelated = true;
		supportsReturningGeometryCentroid = true;
		supportsReturningGeometryProperties = true;
	}
	
	/////////////////////////////////////////////////////////
	////////////////////// GETTERS //////////////////////////
	/////////////////////////////////////////////////////////

	public boolean isSupportsPagination() {
		return supportsPagination;
	}

	public void setSupportsPagination(boolean supportsPagination) {
		this.supportsPagination = supportsPagination;
	}

	public boolean isSupportsQueryRelatedPagination() {
		return supportsQueryRelatedPagination;
	}

	public void setSupportsQueryRelatedPagination(boolean supportsQueryRelatedPagination) {
		this.supportsQueryRelatedPagination = supportsQueryRelatedPagination;
	}

	public boolean isSupportsQueryWithDistance() {
		return supportsQueryWithDistance;
	}

	public void setSupportsQueryWithDistance(boolean supportsQueryWithDistance) {
		this.supportsQueryWithDistance = supportsQueryWithDistance;
	}

	public boolean isSupportsReturningQueryExtent() {
		return supportsReturningQueryExtent;
	}

	public void setSupportsReturningQueryExtent(boolean supportsReturningQueryExtent) {
		this.supportsReturningQueryExtent = supportsReturningQueryExtent;
	}

	public boolean isSupportsStatistics() {
		return supportsStatistics;
	}

	public void setSupportsStatistics(boolean supportsStatistics) {
		this.supportsStatistics = supportsStatistics;
	}

	public boolean isSupportsOrderBy() {
		return supportsOrderBy;
	}

	public void setSupportsOrderBy(boolean supportsOrderBy) {
		this.supportsOrderBy = supportsOrderBy;
	}

	public boolean isSupportsDistinct() {
		return supportsDistinct;
	}

	public void setSupportsDistinct(boolean supportsDistinct) {
		this.supportsDistinct = supportsDistinct;
	}

	public boolean isSupportsQueryWithResultType() {
		return supportsQueryWithResultType;
	}

	public void setSupportsQueryWithResultType(boolean supportsQueryWithResultType) {
		this.supportsQueryWithResultType = supportsQueryWithResultType;
	}

	public boolean isSupportsSqlExpression() {
		return supportsSqlExpression;
	}

	public void setSupportsSqlExpression(boolean supportsSqlExpression) {
		this.supportsSqlExpression = supportsSqlExpression;
	}

	public boolean isSupportsAdvancedQueryRelated() {
		return supportsAdvancedQueryRelated;
	}

	public void setSupportsAdvancedQueryRelated(boolean supportsAdvancedQueryRelated) {
		this.supportsAdvancedQueryRelated = supportsAdvancedQueryRelated;
	}

	public boolean isSupportsReturningGeometryCentroid() {
		return supportsReturningGeometryCentroid;
	}

	public void setSupportsReturningGeometryCentroid(boolean supportsReturningGeometryCentroid) {
		this.supportsReturningGeometryCentroid = supportsReturningGeometryCentroid;
	}

	public boolean isSupportsReturningGeometryProperties() {
		return supportsReturningGeometryProperties;
	}

	public void setSupportsReturningGeometryProperties(boolean supportsReturningGeometryProperties) {
		this.supportsReturningGeometryProperties = supportsReturningGeometryProperties;
	}
}
