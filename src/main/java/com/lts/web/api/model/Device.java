package com.lts.web.api.model;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {
	private Integer id;
	@NotBlank
	private String description;
	@Min(value = 1)
	private Integer quota;
	@NotBlank
	private String department;

	public Device(@JsonProperty("description") String description,
			@JsonProperty("quota") Integer quota,
			@JsonProperty("department") String department) {
		this.description = description;
		this.quota = quota;
		this.department = department;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void merge(Device other) {
		this.setId(other.getId());
		this.setDescription(other.getDescription());
		this.setQuota(other.getQuota());
		this.setDepartment(other.getDepartment());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getQuota() {
		return quota;
	}

	public void setQuota(Integer quota) {
		this.quota = quota;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
