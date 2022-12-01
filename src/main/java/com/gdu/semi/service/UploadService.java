package com.gdu.semi.service;

import java.util.Map;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface UploadService {

	//public List<UploadDTO> getUploadList();
	public void getUploadList(HttpServletRequest request, Model model);
	
	public void getUploadByNo(int upNo, Model model);
	
	public void save(MultipartHttpServletRequest multipartReuqest, HttpServletResponse response);
	
	public int increaseHit(int upHit);	
	
	public void removeUpload(HttpServletRequest multipartRequest, HttpServletResponse response);
	public Map<String, Object> removeAttachByAttachNo(int attachNo);
	
	public void modifyUpload(MultipartHttpServletRequest multipartRequest, HttpServletResponse response);
	
	public ResponseEntity<Resource> download(String userAgent,int attachNo, HttpServletRequest request);
	public ResponseEntity<Resource> downloadAll(String userAgent, int upNo);
	
	
}
