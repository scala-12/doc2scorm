package com.ipoint.coursegenerator.server.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import com.ipoint.coursegenerator.server.UploadFileName;

@Controller
public class FileUploadController implements ServletContextAware {

	private ServletContext servletContext;

	@RequestMapping(value = "/file/form", method = RequestMethod.POST)
	public ResponseEntity<UploadFileName> handleFormUpload(@RequestParam MultipartFile sourceDocFile) {
		String directory = servletContext.getRealPath(File.separator + "tmp") + File.separator;
		if (!sourceDocFile.isEmpty()) {
			try {
				byte[] bytes = sourceDocFile.getBytes();
				String uuid = java.util.UUID.randomUUID().toString();
				File tmpFodler = new File(directory);
				if (!tmpFodler.isDirectory() || !tmpFodler.exists()) {
					tmpFodler.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(new File(directory + uuid));
				fos.write(bytes);
				fos.close();
				UploadFileName ufn = new UploadFileName();
				ufn.setSourceFileName(sourceDocFile.getOriginalFilename());
				ufn.setUuidFileName(uuid);
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<UploadFileName>(ufn, responseHeaders, HttpStatus.CREATED);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
