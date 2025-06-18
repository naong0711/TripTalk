package org.kosa.tripTalk.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
	
	private int page = 1;
	private int size = 10;
	private String sort = "id,desc";
	
	public Pageable toPageable() {
		String [] sortParams = sort.split(",");
		String sortFiled = sortParams[0];
		
		Sort.Direction direction = sortParams.length > 1
							? Sort.Direction.fromString(sortParams[1])
							: Sort.Direction.DESC;
		
		return PageRequest.of(page -1, size, Sort.by(direction, sortFiled));
	}
}
