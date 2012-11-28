package com.ipoint.coursegenerator.server.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    final private static String directory = "tmp" + File.separator; 
    
    
    @RequestMapping(value = "/file/form", method = RequestMethod.POST)
    @ResponseBody
    public String handleFormUpload(@RequestParam MultipartFile sourceDocFile) {

        if (!sourceDocFile.isEmpty()) {
            try {
		byte[] bytes = sourceDocFile.getBytes();
		String uuid = java.util.UUID.randomUUID().toString();
		File tmpFodler = new File(directory);
		if (!tmpFodler.isDirectory() || !tmpFodler.exists()) {
		    tmpFodler.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(new File(directory 
			+ uuid));
		fos.write(bytes);
		fos.close();
		return uuid;
	    } catch (IOException e) {
		e.printStackTrace();
		return "error";
	    }
       } else {
           return "error";
       }
    }
}
