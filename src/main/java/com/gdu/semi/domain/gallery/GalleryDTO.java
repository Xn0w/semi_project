package com.gdu.semi.domain.gallery;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GalleryDTO {

	private int galNo;
	private String id;
	private String galTitle;
	private String galContent;
	private Timestamp galCreateDate;
	private Timestamp galModifyDate;
	private int galHit;
	private String galIp;
	
}
