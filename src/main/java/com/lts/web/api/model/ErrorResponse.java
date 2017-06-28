package com.lts.web.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author veeru
 * 
 */
public class ErrorResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8882612816860608451L;
	private String code;
	private String message;
	private List<Cause> causes;

	public ErrorResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return this.code;
	}

	public void addCause(String value, String label) {
		if (this.getCauses() == null) {
			this.causes = new LinkedList<>();
		}
		this.causes.add(new Cause(value, label));
	}

	public String getMessage() {
		return this.message;
	}

	public List<Cause> getCauses() {
		return this.causes;
	}

	class Cause {
		private String value;
		private String label;

		Cause(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return this.value;
		}

		public String getLabel() {
			return this.label;
		}
	}
}
