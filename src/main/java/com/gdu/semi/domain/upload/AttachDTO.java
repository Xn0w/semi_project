package com.gdu.semi.domain.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachDTO {
	private int attachNo;
	private int upNo;
	private String path;
	private String origin;
	private String filesystem;
	private int downloadCnt;
	
}
