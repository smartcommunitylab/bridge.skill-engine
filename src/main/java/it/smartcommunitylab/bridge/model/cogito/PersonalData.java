package it.smartcommunitylab.bridge.model.cogito;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonalData {
	@JsonProperty("FIRST_NAME")
	private String FIRST_NAME;
	
	@JsonProperty("LAST_NAME")
	private String LAST_NAME;
	
	@JsonProperty("SEX")
	private String SEX;
	
	@JsonProperty("BIRTH_DATE")
	private String BIRTH_DATE;
	
	@JsonProperty("DOMICILE_CITY")
	private String DOMICILE_CITY;
	
	@JsonProperty("DOMICILE_PROVINCE")
	private String DOMICILE_PROVINCE;
	
	@JsonProperty("RESIDENCE_CITY")
	private String RESIDENCE_CITY;
	
	@JsonProperty("RESIDENCE_PROVINCE")
	private String RESIDENCE_PROVINCE;
	
	@JsonProperty("MOBILE_PHONE")
	private String MOBILE_PHONE;
	
	@JsonProperty("NAZIONALITA")
	private String NAZIONALITA;
	
	@JsonProperty("CODICE_FISCALE")
	private String CODICE_FISCALE;
	
	@JsonProperty("STATO_CIVILE")
	private String STATO_CIVILE;
	
	@JsonProperty("E_MAIL")
	private String E_MAIL;
	
	@JsonProperty("PARTITA_IVA")
	private String PARTITA_IVA;
	
	@JsonProperty("PERMESSO_SOGGIORNO")
	private String PERMESSO_SOGGIORNO;
	
	public String getFIRST_NAME() {
		return FIRST_NAME;
	}
	public void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
	}
	public String getLAST_NAME() {
		return LAST_NAME;
	}
	public void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getBIRTH_DATE() {
		return BIRTH_DATE;
	}
	public void setBIRTH_DATE(String bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}
	public String getDOMICILE_CITY() {
		return DOMICILE_CITY;
	}
	public void setDOMICILE_CITY(String dOMICILE_CITY) {
		DOMICILE_CITY = dOMICILE_CITY;
	}
	public String getDOMICILE_PROVINCE() {
		return DOMICILE_PROVINCE;
	}
	public void setDOMICILE_PROVINCE(String dOMICILE_PROVINCE) {
		DOMICILE_PROVINCE = dOMICILE_PROVINCE;
	}
	public String getRESIDENCE_CITY() {
		return RESIDENCE_CITY;
	}
	public void setRESIDENCE_CITY(String rESIDENCE_CITY) {
		RESIDENCE_CITY = rESIDENCE_CITY;
	}
	public String getRESIDENCE_PROVINCE() {
		return RESIDENCE_PROVINCE;
	}
	public void setRESIDENCE_PROVINCE(String rESIDENCE_PROVINCE) {
		RESIDENCE_PROVINCE = rESIDENCE_PROVINCE;
	}
	public String getMOBILE_PHONE() {
		return MOBILE_PHONE;
	}
	public void setMOBILE_PHONE(String mOBILE_PHONE) {
		MOBILE_PHONE = mOBILE_PHONE;
	}
	public String getNAZIONALITA() {
		return NAZIONALITA;
	}
	public void setNAZIONALITA(String nAZIONALITA) {
		NAZIONALITA = nAZIONALITA;
	}
	public String getCODICE_FISCALE() {
		return CODICE_FISCALE;
	}
	public void setCODICE_FISCALE(String cODICE_FISCALE) {
		CODICE_FISCALE = cODICE_FISCALE;
	}
	public String getSTATO_CIVILE() {
		return STATO_CIVILE;
	}
	public void setSTATO_CIVILE(String sTATO_CIVILE) {
		STATO_CIVILE = sTATO_CIVILE;
	}
	public String getE_MAIL() {
		return E_MAIL;
	}
	public void setE_MAIL(String e_MAIL) {
		E_MAIL = e_MAIL;
	}
	public String getPARTITA_IVA() {
		return PARTITA_IVA;
	}
	public void setPARTITA_IVA(String pARTITA_IVA) {
		PARTITA_IVA = pARTITA_IVA;
	}
	public String getPERMESSO_SOGGIORNO() {
		return PERMESSO_SOGGIORNO;
	}
	public void setPERMESSO_SOGGIORNO(String pERMESSO_SOGGIORNO) {
		PERMESSO_SOGGIORNO = pERMESSO_SOGGIORNO;
	}
}
