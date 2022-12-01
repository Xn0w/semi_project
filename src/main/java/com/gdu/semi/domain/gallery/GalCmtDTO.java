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
public class GalCmtDTO {

	private int cmtNo;
	private int galNo;
	private String id;
	private String cmtContent;
	private Timestamp cmtCreateDate;
	private Timestamp cmtModifyDate;

}
