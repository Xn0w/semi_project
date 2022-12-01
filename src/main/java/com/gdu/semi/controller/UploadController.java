package com.gdu.semi.controller;

import java.util.Map;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.semi.service.UploadService;

import oracle.jdbc.proxy.annotation.Post;

@Controller
public class UploadController {

	@Autowired
	private UploadService uploadService;
	
	@GetMapping("/upload/list")
	public String list(HttpServletRequest request, Model model){
		uploadService.getUploadList(request, model);
		return "/upload/list";
	}
	
	@GetMapping("/upload/write")
	public String requiredLogin_write() {
		return "upload/write";
	}

	@PostMapping("/upload/add")
	public void requiredLogin_add(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
		uploadService.save(multipartRequest, response);
	}
	
	@GetMapping("/upload/increase/hit")
	public String increaseHit(@RequestParam(value="upNo", required = false, defaultValue = "0") int upNo) {
		int result = uploadService.increaseHit(upNo);
		if(result > 0) {
			return "redirect:/upload/detail?upNo=" + upNo;
		} else {
			return "redirect:/upload/list";
		}
	}

	@GetMapping("/upload/detail")
	public String detail(@RequestParam(value="upNo", required = false, defaultValue = "0") int upNo, Model model) {
		uploadService.getUploadByNo(upNo, model);
		return "upload/detail";
	}
	
	@ResponseBody
	@PostMapping(value="/upload/attach/remove",produces = "application/json")
	public Map<String, Object> attachRemove(@RequestParam("upNo") int upNo, @RequestParam("attachNo") int attachNo) {
		return uploadService.removeAttachByAttachNo(attachNo);
	}
	
//   @ResponseBody
//   @GetMapping(value="/comment/remove", produces = "application/json")
//   public Map<String, Object> remove(@RequestParam("commentNo") int commentNo){
//      return commentService.removeComment(commentNo);
//   }
	
	
	@PostMapping("/upload/remove")
	public void requiredLogin_remove(HttpServletRequest request, HttpServletResponse response) {
		uploadService.removeUpload(request, response);
	}
	
	@PostMapping("/upload/edit")
	public String requiredLogin_edit(@RequestParam(value="upNo") int upNo, Model model) {
		uploadService.getUploadByNo(upNo, model);
		return "upload/edit";
	}

	@PostMapping("/upload/modify")
	public void requiredLogin_modify(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
		uploadService.modifyUpload(multipartRequest, response);
	}
	
	@ResponseBody
	@GetMapping("/upload/download")
	public ResponseEntity<Resource> requiredLogin_download(@RequestHeader("User-Agent") String userAgent, @RequestParam("attachNo") int attachNo, HttpServletRequest request){
		return uploadService.download(userAgent, attachNo, request);
	}
	
	@GetMapping("/upload/downloadAll")
	public ResponseEntity<Resource> requiredLogin_downloadAll(@RequestHeader("User-Agent") String userAgent, @RequestParam("upNo") int upNo){
		return uploadService.downloadAll(userAgent, upNo);
	}
}

