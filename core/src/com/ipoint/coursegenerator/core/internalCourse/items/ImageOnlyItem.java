package com.ipoint.coursegenerator.core.internalCourse.items;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ipoint.coursegenerator.core.elementparser.graphics.RasterGraphicsParser;
import com.ipoint.coursegenerator.core.elementparser.graphics.VectorGraphicsParser;
import com.ipoint.coursegenerator.core.internalCourse.blocks.HyperlinkBlock;
import com.ipoint.coursegenerator.core.internalCourse.blocks.TextBlock;

/**
 * Item for {@link TextBlock}. This item includes picture data
 * 
 * @see TextBlock
 * @see HyperlinkBlock
 * @author Kalashnikov Vladislav
 *
 */
public class ImageOnlyItem extends AbstractTextItem {

	private XWPFPictureData value;

	/**
	 * Create image as Block Item
	 * 
	 * @param imageData
	 *            Image of block. There cannot be null
	 */
	public ImageOnlyItem(XWPFPictureData imageData) {
		if (!this.setValue(imageData)) {
			// TODO:exception
		}
	}

	public XWPFPictureData getValue() {
		return value;
	}

	/**
	 * Set value of item
	 * 
	 * @param imageData
	 *            Image of block
	 * @return If successful then true
	 */
	public boolean setValue(XWPFPictureData imageData) {
		if (imageData == null) {
			return false;
		} else {
			this.value = imageData;
			return true;
		}
	}

	@Override
	public Element toHtml(Document creatorTag, boolean isHyperlink) {
		Element img = creatorTag.createElement("img");
		
		if (this.getValue().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG
	    		|| this.getValue().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG
	    		|| this.getValue().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP
	    		|| this.getValue().getPictureType() == org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF) {
	    	//RasterGraphicsParser.parse(this.getValue(), path, imgElement);
	    } else {
	    	//VectorGraphicsParser.parse(this.getValue(), path, imgElement);
	    }
		
		return img;
	}

}
