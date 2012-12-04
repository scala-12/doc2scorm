package com.ipoint.coursegenerator.core.elementparser.graphics;

import java.io.File;

import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.utils.FileUtils;
import com.ipoint.coursegenerator.core.utils.ImageFormatConverter;

public class VectorGraphicsParser extends AbstractGraphicsParser {
    
    public static void parse(Object picture, String path, Element imgElement) {
	if (picture instanceof Picture) {
	    Picture pic = (Picture) picture;
	    if (pic.getMimeType().equals(IMAGE_WMF)) {		
		String url = "img" + File.separator + FileUtils.IMAGE_PREFIX + pic.hashCode() + ".png";
		FileUtils.savePNGImage(ImageFormatConverter.transcodeWMFtoPNG(pic.getRawContent()), 
			path + File.separator + url);
		imgElement.setAttribute("src", url);
	    }
	}
    }
}
