package com.gdu.semi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.gdu.semi.service.GalleryService;
import com.gdu.semi.service.UploadService;

@Controller
public class GalleryController {

	@Autowired
	private GalleryService galleryService;
	
	@GetMapping("/gallery/main")
	public String board() {
		return "/gallery/gallery";
	}
	

	
}




