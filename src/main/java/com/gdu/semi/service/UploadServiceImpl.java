package com.gdu.semi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.semi.domain.upload.AttachDTO;
import com.gdu.semi.domain.upload.UploadDTO;
import com.gdu.semi.domain.user.UserDTO;
import com.gdu.semi.mapper.UploadMapper;
import com.gdu.semi.mapper.UserMapper;
import com.gdu.semi.util.MyFileUtil;
import com.gdu.semi.util.PageUtil;
import com.gdu.semi.util.SecurityUtil;

import lombok.AllArgsConstructor;



@AllArgsConstructor
@Service
public class UploadServiceImpl implements UploadService {

	private UploadMapper uploadMapper;
	private UserMapper userMapper;
	private MyFileUtil myFileUtil;
	private SecurityUtil securityUtil;
	private PageUtil pageUtil;
	
	/*
	 * @Override public List<UploadDTO> getUploadList() { return
	 * uploadMapper.selectUploadList(); }
	 */
	
	@Override
	public void getUploadList(HttpServletRequest request, Model model) {
		
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt1.orElse("1"));		
		int totalRecord = uploadMapper.selectAllUploadCount();
		
		pageUtil.setPageUtil(page, totalRecord);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());		
		map.put("end", pageUtil.getEnd());		
		List<UploadDTO> uploadList = uploadMapper.selectUploadList(map);
		
		model.addAttribute("uploadList", uploadList);
		model.addAttribute("paging", pageUtil.getPaging(request.getContextPath() + "/upload/list"));
		model.addAttribute("beginNo", totalRecord - (page -1) * pageUtil.getRecordPerPage());

	}
	
	@Transactional
	@Override
	public void save(MultipartHttpServletRequest multipartReuqest, HttpServletResponse response) {
		
		String title = multipartReuqest.getParameter("title");
		String content = multipartReuqest.getParameter("content");
		String content2 = content.replaceAll("[</p>]", "");
		
		// id
		HttpSession session = multipartReuqest.getSession();
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser");
		String id = loginUser.getId();
				
		UploadDTO upload = UploadDTO.builder()
				.id(id)
				.upTitle(securityUtil.preventXSS(title))
				.upContent(securityUtil.preventXSS(content2))
				.upIp(multipartReuqest.getRemoteAddr())
				.build();
		
		int uploadResult =  uploadMapper.insertUpload(upload);
		System.out.println("????????? ?????????: "+  uploadResult);
		
		List<MultipartFile> files = multipartReuqest.getFiles("files");
		System.out.println("?????? ????????? : " +files.size());
		
		int attachResult;
		if(files.get(0).getSize() == 0) {
			attachResult = 1;	
		} else {
			attachResult = 0;
		}
		
		
		// ????????? ?????? ?????? ?????? (????????? ??????)
		long mpf = 0;
		for(MultipartFile multipartFile : files) {
			
			try {
				
				if(multipartFile != null && multipartFile.isEmpty() == false) {
					
					// ?????? ??????
					String origin = multipartFile.getOriginalFilename();
					origin = origin.substring(origin.lastIndexOf("\\") +1);
				
					// ????????? ??????
					String filesystem = myFileUtil.getFilename(origin);
					
					// ????????? ??????
					String path = myFileUtil.getTodayPath();
					
					// ????????? ?????? ?????????
					File dir = new File(path);
					if(dir.exists() == false) {
						dir.mkdirs();
					}
					
					// ????????? File ??????
					File file = new File(dir, filesystem); // ??????, ????????? ??????


					// ???????????? ????????? ??????(????????? ??????)
					multipartFile.transferTo(file);
					
					AttachDTO attach = AttachDTO.builder()
							.path(path)
							.origin(origin)
							.filesystem(filesystem)
							.upNo(upload.getUpNo())
							.build();
					
					
					
					System.out.println("????????????: " + multipartFile.getSize());
					mpf = multipartFile.getSize();
					// DB??? AttachDTO ??????
					attachResult += uploadMapper.insertAttach(attach);

					
				}
				System.out.println("attachResult :" + attachResult);
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		} // for
		
		// ??????
		
		try {
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			 if(uploadResult > 0 && mpf == 0) {
				out.println("<script>");
				out.println("alert('?????????????????? ?????? ????????? ???????????? ???????????? ????????????.');");
				out.println("location.href='" + multipartReuqest.getContextPath() + "/upload/list';");
				out.println("</script>");
			} else if(uploadResult > 0 || attachResult == files.size()) {  
				uploadMapper.updatePoint(id);
			    Map<String, Object> map = new HashMap<String, Object>();
			    map.put("id", id);
				UserDTO loginUser1 = userMapper.selectUserByMap(map);
				multipartReuqest.getSession().setAttribute("loginUser", loginUser1);
				out.println("<script>");
				out.println("alert('????????? ??????!');");
				out.println("location.href='" + multipartReuqest.getContextPath() + "/upload/list';");
				out.println("</script>");			
			} else {
				out.println("<script>");
				out.println("alert('????????? ??????????????????.');");
				out.println("history.back();");
				out.println("</script>");
			}
			out.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	// ????????? ??????
	@Override
	public int increaseHit(int upHit) {
		return uploadMapper.updateHit(upHit);
	}
	
	@Override
	public void getUploadByNo(int upNo, Model model) {
		model.addAttribute("upload", uploadMapper.selectUploadByNo(upNo));
		model.addAttribute("attachList", uploadMapper.selectAttachList(upNo));		
	}
	

	
	@Override
	public void removeUpload(HttpServletRequest multipartRequest, HttpServletResponse response) {
		int upNo = Integer.parseInt(multipartRequest.getParameter("upNo"));
		List<AttachDTO> attachList = uploadMapper.selectAttachList(upNo);
		
		int result =  uploadMapper.deleteUpload(upNo);
		
		if(result > 0) {
			if(attachList != null && attachList.isEmpty() == false) {
				
				for(AttachDTO attach : attachList) {
					
					File file = new File(attach.getPath(), attach.getFilesystem());
					
					if(file.exists()) {
						file.delete();
					}
				}
			}
		}
		
		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			if(result > 0) {
				out.println("<script>");
				out.println("alert('?????? ???????????????.');");
				out.println("location.href='" + multipartRequest.getContextPath() + "/upload/list'");
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("alert('?????? ??????????????????.');");
				out.println("history.back();");
				out.println("</script>");
			}
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Map<String, Object> removeAttachByAttachNo(int attachNo) {
		// ????????? Attach ?????? ????????????
		AttachDTO attach = uploadMapper.selectAttachByNo(attachNo);
		Map<String, Object> map = new HashMap<String, Object>();
		
		// DB?????? ??????
		int result = uploadMapper.deleteAttach(attachNo);
		
		// ?????? ?????? ??????
		if(result > 0) {
			
			// ?????? ????????? File ????????? ??????
			File file = new File(attach.getPath(), attach.getFilesystem());
			
			// ??????
			if(file.exists()) { // ????????? ???????????? ??????
				file.delete();
			}
			
		}
		
		map.put("result", result);
		
		return map;
	}
	
	@Transactional
	@Override
	public void modifyUpload(MultipartHttpServletRequest multipartRequest, HttpServletResponse response) {
		
		int upNo = Integer.parseInt(multipartRequest.getParameter("upNo"));
		String title = multipartRequest.getParameter("title");
		String content = multipartRequest.getParameter("content");
		
		UploadDTO upload = UploadDTO.builder()
						.upNo(upNo)
						.upTitle(title)
						.upContent(content)
						.build();
		
		int uploadResult = uploadMapper.updateUpload(upload);
		
		List<MultipartFile> files = multipartRequest.getFiles("files");
		
		
		int attachResult;
		if(files.get(0).getSize() == 0) {  
			attachResult = 1;
		} else {
			attachResult = 0;
		}
		
		// ????????? ?????? ?????? ??????(????????? ??????)
		for(MultipartFile multipartFile : files) {
			
			try {
				
				// ????????? ????????? ??????
				if(multipartFile != null && multipartFile.isEmpty() == false) {  // ??? ??? ?????????
					
					// ?????? ??????
					String origin = multipartFile.getOriginalFilename();
					origin = origin.substring(origin.lastIndexOf("\\") + 1);  // IE??? origin??? ?????? ????????? ????????? ???????????? ???????????? ???
					
					// ????????? ??????
					String filesystem = myFileUtil.getFilename(origin);
					
					// ????????? ??????
					String path = myFileUtil.getTodayPath();
					
					// ????????? ?????? ?????????
					File dir = new File(path);
					if(dir.exists() == false) {
						dir.mkdirs();
					}
					
					// ????????? File ??????
					File file = new File(dir, filesystem);
					
					// ???????????? ????????? ??????(????????? ??????)
					multipartFile.transferTo(file);
					
					// AttachDTO ??????
					AttachDTO attach = AttachDTO.builder()
							.path(path)
							.origin(origin)
							.filesystem(filesystem)
							.upNo(upNo)
							.build();
					
					// DB??? AttachDTO ??????
					attachResult += uploadMapper.insertAttach(attach);
					
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		
		} // for
		
		try {
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			if(uploadResult > 0 && attachResult == files.size()) {
				out.println("<script>");
				out.println("alert('?????? ???????????????.');");
				out.println("location.href='" + multipartRequest.getContextPath() + "/upload/detail?upNo=" + upNo + "'");
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("alert('?????? ??????????????????.');");
				out.println("history.back();");
				out.println("</script>");
			}
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		
	}
	
	@Override
	public ResponseEntity<Resource> download(String userAgent, int attachNo, HttpServletRequest request) {
		
		AttachDTO attach = uploadMapper.selectAttachByNo(attachNo);
		File file = new File(attach.getPath(), attach.getFilesystem());
		
		Resource resource = new FileSystemResource(file);
		
		if(resource.exists() == false) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
		
		uploadMapper.updateDownloadCnt(attachNo);
		
		HttpSession session = request.getSession();
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser"); 
		String id = loginUser.getId();
		uploadMapper.downloadPoint(id);
			
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("id", id);
		UserDTO loginUser1 = userMapper.selectUserByMap(map);
		request.getSession().setAttribute("loginUser", loginUser1);
		
		
		String origin = attach.getOrigin();
		try {
			// IE (userAgent)??? "Trident"??? ???????????? ??????
			if(userAgent.contains("Trident")) {
				origin = URLEncoder.encode(origin, "UTF-8").replaceAll("\\+", "");
			}
			
			else if(userAgent.contains("Edg")) {
				origin = URLEncoder.encode(origin,"UTF-8");
			}
			else {
				origin = new String(origin.getBytes("UTF-8"), "ISO-8859-1");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename=" + origin);
		header.add("Content-Length", file.length() + "");
		
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
		
	
	}	
	
	@Override
	public ResponseEntity<Resource> downloadAll(String userAgent, int upNo) {
		
		List<AttachDTO> attachList = uploadMapper.selectAttachList(upNo);
		
		FileOutputStream fileOutputStream = null;
		ZipOutputStream zipOutputStream = null;
		FileInputStream fileInputStream = null;
		
		String tmpPath = "storage" + File.separator + "temp";
		
		File tmpDir = new File(tmpPath);
		if(tmpDir.exists() == false) {
			tmpDir.mkdirs();
		}
		
		String tmpName = System.currentTimeMillis() + ".zip";
		
		try {
			
			fileOutputStream = new FileOutputStream(new File(tmpPath, tmpName));
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			
			// ????????? ????????? ??????
			if(attachList != null && attachList.isEmpty() == false) {
				
				// ???????????? ??????
				for(AttachDTO attach : attachList) {
					
					// zip ????????? ?????? ?????? ??????
					ZipEntry zipEntry = new ZipEntry(attach.getOrigin());
					zipOutputStream.putNextEntry(zipEntry);
					
					fileInputStream = new FileInputStream(new File(attach.getPath(), attach.getFilesystem()));
					byte[] buffer = new byte[1024];
					int length;
					while((length = fileInputStream.read(buffer)) != -1) {
						zipOutputStream.write(buffer, 0, length);
					}
					zipOutputStream.closeEntry();
					fileInputStream.close();
					// ??? ?????? ?????? ?????? ???????????? ?????? ??????
					uploadMapper.updateDownloadCnt(attach.getAttachNo());
				}
				
				zipOutputStream.close();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// ????????? Resource
		File file = new File(tmpPath, tmpName);
		Resource resource = new FileSystemResource(file);
		
		// Resource??? ????????? ?????? (??????????????? ????????? ??????)
		if(resource.exists() == false) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
		
		// ???????????? ?????? ?????????
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename=" + tmpName);  // ??????????????? zip???????????? ?????????????????? ?????? ????????? ????????? ??????
		header.add("Content-Length", file.length() + "");
		
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	

	
}
