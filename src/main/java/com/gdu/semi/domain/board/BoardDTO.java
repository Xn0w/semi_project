package com.gdu.semi.domain.board;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class BoardDTO {

	private int bdNo;
	private String id;
	private String bdTitle;
	private String bdContent;
	private Timestamp bdCreateDate;
	private Timestamp bdModifyDate;
	private int bdState;
	private int bdDepth;
	private int bdGroupNo;
	private int bdGroupOrder;
	private String bdIp;
	
	
}
