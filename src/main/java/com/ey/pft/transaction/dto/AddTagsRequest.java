package com.ey.pft.transaction.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AddTagsRequest {

	@NotEmpty
	private List<@Size(min = 1, max = 60) String> names;

	public AddTagsRequest() {
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}
}