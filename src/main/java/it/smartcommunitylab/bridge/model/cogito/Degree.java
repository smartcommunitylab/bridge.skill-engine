package it.smartcommunitylab.bridge.model.cogito;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Degree {
	@JsonProperty("LEVEL")
	private String LEVEL;
	
	@JsonProperty("SUBJECT")
	private String SUBJECT;
	
	@JsonProperty("YEAR")
	private String YEAR;
	
	@JsonProperty("SCORE")
	private String SCORE;
	
	@JsonProperty("DESCRIPTION")
	private String DESCRIPTION;
	
	@JsonProperty("PATENTE")
	private String PATENTE;
	
	@JsonProperty("FORMATION")
	private String FORMATION;
	
	public String getLEVEL() {
		return LEVEL;
	}
	public void setLEVEL(String lEVEL) {
		LEVEL = lEVEL;
	}
	public String getSUBJECT() {
		return SUBJECT;
	}
	public void setSUBJECT(String sUBJECT) {
		SUBJECT = sUBJECT;
	}
	public String getYEAR() {
		return YEAR;
	}
	public void setYEAR(String yEAR) {
		YEAR = yEAR;
	}
	public String getSCORE() {
		return SCORE;
	}
	public void setSCORE(String sCORE) {
		SCORE = sCORE;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getPATENTE() {
		return PATENTE;
	}
	public void setPATENTE(String pATENTE) {
		PATENTE = pATENTE;
	}
	public String getFORMATION() {
		return FORMATION;
	}
	public void setFORMATION(String fORMATION) {
		FORMATION = fORMATION;
	}

}
