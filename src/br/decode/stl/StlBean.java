package br.decode.stl;


public class StlBean {

	private float vertices[];

	private float normais[];

	private Integer qtdTriangles;

	private Float centroX;

	private Float centroY;

	private Float centroZ;

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public float[] getNormais() {
		return normais;
	}

	public void setNormais(float[] normais) {
		this.normais = normais;
	}

	public Integer getQtdTriangles() {
		return qtdTriangles;
	}

	public void setQtdTriangles(Integer qtdTriangles) {
		this.qtdTriangles = qtdTriangles;
	}

	public Float getCentroX() {
		return centroX;
	}

	public void setCentroX(Float centroX) {
		this.centroX = centroX;
	}

	public Float getCentroY() {
		return centroY;
	}

	public void setCentroY(Float centroY) {
		this.centroY = centroY;
	}

	public Float getCentroZ() {
		return centroZ;
	}

	public void setCentroZ(Float centroZ) {
		this.centroZ = centroZ;
	}

	
}
