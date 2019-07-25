package it.smartcommunitylab.bridge.model.cogito;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Language {
	@JsonProperty("LANGUAGE")
	private String LANGUAGE;
	
	@JsonProperty("DIMENSION")
	private String DIMENSION;
	
	@JsonProperty("LEVEL")
	private String LEVEL;
	
	@JsonProperty("CEFR_LEVELS")
	private String CEFR_LEVELS;
	
	public String getLANGUAGE() {
		return LANGUAGE;
	}
	public void setLANGUAGE(String lANGUAGE) {
		LANGUAGE = lANGUAGE;
	}
	public String getDIMENSION() {
		return DIMENSION;
	}
	public void setDIMENSION(String dIMENSION) {
		DIMENSION = dIMENSION;
	}
	public String getLEVEL() {
		return LEVEL;
	}
	public void setLEVEL(String lEVEL) {
		LEVEL = lEVEL;
	}
	public String getCEFR_LEVELS() {
		return CEFR_LEVELS;
	}
	public void setCEFR_LEVELS(String cEFR_LEVELS) {
		CEFR_LEVELS = cEFR_LEVELS;
	}

}
