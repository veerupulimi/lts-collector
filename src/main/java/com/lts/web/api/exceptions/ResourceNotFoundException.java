package com.lts.web.api.exceptions;

public final class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public String getCode() {
		return "RESOURCE_NOT_FOUND";
	}
}