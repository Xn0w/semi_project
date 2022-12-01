package com.gdu.semi.domain.upload;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadDTO {
	private int upNo;
	private String id;
	private String upTitle;
	private String upContent;
	private Timestamp upCreateDate;
	private Timestamp upModifyDate;
	private int upHit;
	private String upIp;
	private int attachCnt;
	private String name;
	private int point;
}
