package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.FormulaOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.ImageOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.TextOptionItem;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.MathInfo;

import schemasMicrosoftComVml.CTImageData;
import schemasMicrosoftComVml.CTShape;

/**
 * Parsing paragraph which includes only text and images
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextParser extends AbstractParser {

	/**
	 * Return text block with formula
	 * 
	 * @param mathInfo
	 *            Info about MathML formulas
	 * @param paragraphFlag
	 *            Flag that formula is paragraph
	 * @return Text block with formula
	 */
	public static TextBlock parse(MathInfo mathInfo, boolean paragraphFlag) {
		return new TextBlock(new FormulaOptionItem(mathInfo.read(), paragraphFlag));
	}
	
	private static final int EMU_TO_PX_COEF = 9525;

	/**
	 * Return Text block there are includes text and images
	 * 
	 * @param runs
	 * @return
	 */
	public static TextBlock parse(List<XWPFRun> runs) {
		ArrayList<AbstractContentItem<?>> blockItems = null;

		if (runs != null) {
			if (!runs.isEmpty()) {
				// image
				blockItems = new ArrayList<>();

				for (XWPFRun run : runs) {
					if (run.toString().isEmpty()) {
						// this run is not simple text
						XWPFPictureData pictureData = null;
						String picStyle = null;
						boolean isWrap = true;

						if (!run.getEmbeddedPictures().isEmpty()) {
							// TODO: Why is it? Maybe, it's for EMF?
							picStyle = "";
							if (!run.getCTR().getDrawingArray(0).getAnchorList().isEmpty()) {
								// square, Through, Tight, TopAndBottom
								isWrap = !run.getCTR().getDrawingArray(0).getAnchorArray(0).isSetWrapNone();
								if (!isWrap) {
									picStyle = picStyle + "z-index:"
											+ ((run.getCTR().getDrawingArray(0).getAnchorArray(0).getBehindDoc()) ? "-"
													: "")
											+ "1;";
								}
							}

							pictureData = run.getEmbeddedPictures().get(0).getPictureData();
							picStyle = picStyle + "width:" + String.valueOf(
									run.getEmbeddedPictures().get(0).getCTPicture().getSpPr().getXfrm().getExt().getCx()
											/ EMU_TO_PX_COEF)
									+ "px;";
							picStyle = picStyle + "height:" + String.valueOf(
									run.getEmbeddedPictures().get(0).getCTPicture().getSpPr().getXfrm().getExt().getCy()
											/ EMU_TO_PX_COEF)
									+ "px;";
						} else if (!run.getCTR().getPictList().isEmpty()) {
							// TODO: and Why do it? Maybe, for other pictures?
							CTShape shape = (CTShape) run.getCTR().getPictList().get(0).selectPath(
									"declare namespace v='urn:schemas-microsoft-com:vml' " + ".//v:shape")[0];
							picStyle = shape.selectAttribute("", "style").getDomNode().getNodeValue();

							CTImageData imageData = (CTImageData) shape.selectPath(
									"declare namespace v='urn:schemas-microsoft-com:vml' " + ".//v:imagedata")[0];
							String rId = imageData
									.selectAttribute(
											"http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id")
									.getDomNode().getNodeValue();
							pictureData = run.getDocument().getPictureDataByID(rId);

							if (run.getCTR().getPictList().get(0)
									.selectPath("declare namespace w10='urn:schemas-microsoft-com:office:word' "
											+ ".//w10:wrap") == null) {
								isWrap = false;
							} else {
								isWrap = true;
							}

						} else if (run.getCTR().sizeOfObjectArray() > 0) {
							// Object as image
							for (CTObject picture : run.getCTR().getObjectList()) {
								CTShape[] shapes = (CTShape[]) picture.selectPath(
										"declare namespace v='urn:schemas-microsoft-com:vml' " + ".//v:shape");
								if (shapes.length != 0) {
									// style not used because size is small
									// picStyle = shapes[0].selectAttribute("",
									// "style").getDomNode().getNodeValue();

									CTImageData imageData = (CTImageData) shapes[0]
											.selectPath("declare namespace v='urn:schemas-microsoft-com:vml' "
													+ ".//v:imagedata")[0];
									String rId = imageData.selectAttribute(
											"http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id")
											.getDomNode().getNodeValue();
									pictureData = run.getDocument().getPictureDataByID(rId);
									isWrap = true;
								}
							}
						}

						if (pictureData != null) {
							blockItems.add(new ImageOptionItem(pictureData, picStyle, isWrap));
						}
					} else {
						// Text
						blockItems.add(new TextOptionItem(run));
					}
				}
			}
		}

		return new TextBlock(blockItems);
	}

}
