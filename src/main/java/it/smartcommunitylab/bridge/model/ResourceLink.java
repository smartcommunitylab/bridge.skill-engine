package it.smartcommunitylab.bridge.model;

import java.util.HashMap;
import java.util.Map;

public class ResourceLink {
		private Map<String, String> preferredLabel = new HashMap<>();
		private String uri;
		
		public Map<String, String> getPreferredLabel() {
			return preferredLabel;
		}
		public void setPreferredLabel(Map<String, String> preferredLabel) {
			this.preferredLabel = preferredLabel;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		
}
