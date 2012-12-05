package com.ipoint.coursegenerator.core.elementparser.graphics;

import java.io.File;

import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.utils.FileUtils;

public class RasterGraphicsParser extends AbstractGraphicsParser {

    public static void parse(Object picture, String path, Element imgElement) {
	if (picture instanceof Picture) {
	    Picture pic = (Picture) picture;
	    if (pic.getMimeType().equals(IMAGE_PNG)) {		
		String url = "img" + File.separator + FileUtils.IMAGE_PREFIX + pic.hashCode() + ".png";
		FileUtils.savePNGImage(((Picture) picture).getRawContent(), 
			path + File.separator + url);
		imgElement.setAttribute("src", url);
	    }
	} else if (picture instanceof XWPFPicture) {
	    XWPFPicture pic = (XWPFPicture) picture;
	    if (pic.getPictureData().getPictureType() == Document.PICTURE_TYPE_PNG) {		
		String url = "img" + File.separator + FileUtils.IMAGE_PREFIX + pic.hashCode() + ".png";
		FileUtils.savePNGImage(((XWPFPicture) picture).getPictureData().getData(), 
			path + File.separator + url);
		imgElement.setAttribute("src", url);
    }
	}
}
}