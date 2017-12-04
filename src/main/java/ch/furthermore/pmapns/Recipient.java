package ch.furthermore.pmapns;

import java.util.LinkedList;
import java.util.List;

public class Recipient {
	private String certId;
	private List<String> tokens = new LinkedList<String>();

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
}
