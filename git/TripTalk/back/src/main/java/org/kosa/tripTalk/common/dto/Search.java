package org.kosa.tripTalk.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Search {
	private String searchKey;
	private String searchValue;
	
	public boolean isValid() {
		return searchKey != null && !searchKey.isBlank()
				&& searchValue != null && !searchValue.isBlank();
	}
}
