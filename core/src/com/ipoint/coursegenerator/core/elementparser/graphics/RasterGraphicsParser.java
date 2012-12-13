package com.ipoint.coursegenerator.core.elementparser.graphics;

import java.io.File;

import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.utils.FileWork;

public class RasterGraphicsParser extends AbstractGraphicsParser {

    public static void parse(Object picture, String path, Element imgElement) {
	if (picture instanceof Picture) {
	    Picture pic = (Picture) picture;
	    if (pic.getMimeType().equals(IMAGE_PNG)) {
		String url = "img" + File.separator + FileWork.IMAGE_PREFIX
			+ pic.hashCode() + ".png";
		FileWork.savePNGImage(((Picture) picture).getRawContent(), path
			+ File.separator + url);
		imgElement.setAttribute("src",
			url.replace(File.separatorChar, '/'));
	    }
	} else if (picture instanceof XWPFPicture) {
	    XWPFPicture pic = (XWPFPicture) picture;
	    String url = null;
	    switch (pic.getPictureData().getPictureType()) {
	    case Document.PICTURE_TYPE_PNG:
		url = "img" + File.separator + FileWork.IMAGE_PREFIX
			+ pic.hashCode() + ".png";
		break;
	    case Document.PICTURE_TYPE_BMP:
		url = "img" + File.separator + FileWork.IMAGE_PREFIX
			+ pic.hashCode() + ".bmp";
		break;
	    case Document.PICTURE_TYPE_GIF:
		url = "img" + File.separator + FileWork.IMAGE_PREFIX
			+ pic.hashCode() + ".gif";
		break;
	    case Document.PICTURE_TYPE_JPEG:
		url = "img" + File.separator + FileWork.IMAGE_PREFIX
			+ pic.hashCode() + ".jpg";
		break;
	    }
	    if (url != null) {
		FileWork.savePNGImage(((XWPFPicture) picture).getPictureData()
			.getData(), path + File.separator + url);
		imgElement.setAttribute("src", url.replace(File.separatorChar, '/'));
	    }
	}
    }
}