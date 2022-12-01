package com.gdu.semi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.semi.domain.upload.AttachDTO;
import com.gdu.semi.domain.upload.UploadDTO;
import com.gdu.semi.domain.user.UserDTO;

@Mapper
public interface UploadMapper {

	public UserDTO selectUserByMap(Map<String, Object> map);
	public int selectAllUploadCount();
	public List<UploadDTO> selectUploadList(Map<String , Object> map);
	public int insertUpload(UploadDTO upload);
	public int insertAttach(AttachDTO attach);
	public int deleteUpload(int upNo);
	public int deleteAttach(int attachNo);
	public int updateHit(int upNo);
	public UploadDTO selectUploadByNo(int upNo);
	public List<AttachDTO> selectAttachList(int upNo);
	public AttachDTO selectAttachByNo(int attachNo);
	public int updateUpload(UploadDTO upload);
	public int updateDownloadCnt(int attachNo);
	public int updatePoint(String id);
	public int downloadPoint(String id);
	public int selectPoint();
}
