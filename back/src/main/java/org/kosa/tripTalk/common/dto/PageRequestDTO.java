package org.kosa.tripTalk.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
	
	private int page = 1;
	private int size = 10;
	private String sort = "id,desc";
	
	public Pageable toPageable() {
		if(sort == null || sort.isBlank())
			return PageRequest.of(page -1, size);
		
		String [] sortParams = sort.split(",");
		String sortFiled = sortParams[0];
		
		Sort.Direction direction = sortParams.length > 1
							? Sort.Direction.fromString(sortParams[1])
							: Sort.Direction.DESC;
		
		return PageRequest.of(page -1, size, Sort.by(direction, sortFiled));
	}
}
