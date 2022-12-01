package com.gdu.semi.domain.gallery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LikedDTO {

	private String id;
	private int galNo;
}
