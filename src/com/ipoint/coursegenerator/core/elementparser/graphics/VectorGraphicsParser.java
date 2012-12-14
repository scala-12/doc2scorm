package com.ipoint.coursegenerator.core.elementparser.graphics;

import java.io.File;

import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.utils.FileWork;
import com.ipoint.coursegenerator.core.utils.ImageFormatConverter;

public class VectorGraphicsParser extends AbstractGraphicsParser {

    public static void parse(Picture picture, String path, Element imgElement) {
	if (picture.getMimeType().equals(IMAGE_WMF)) {
	    String url = "img" + File.separator + FileWork.IMAGE_PREFIX
		    + picture.hashCode() + ".png";
	    System.out.println(picture.getHeight() + "x" + picture.getWidth());
	    FileWork.savePNGImage(ImageFormatConverter
		    .transcodeWMFtoPNG(picture.getRawContent()), path
		    + File.separator + url);
	    imgElement
		    .setAttribute("src", url.replace(File.separatorChar, '/'));
	}
    }

    public static void parse(XWPFPictureData picture, String path,
	    Element imgElement) {
	XWPFPictureData pic = (XWPFPictureData) picture;
	String url = "img" + File.separator + FileWork.IMAGE_PREFIX
		+ pic.hashCode() + ".png";
	FileWork.savePNGImage(
		ImageFormatConverter.transcodeWMFtoPNG(pic.getData()), path
			+ File.separator + url);
	imgElement.setAttribute("src", url.replace(File.separatorChar, '/'));
    }
}
