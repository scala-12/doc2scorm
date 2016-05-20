package com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.contentParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;

import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.FormulaContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.ImageContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.content.items.TextContentItem;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;

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
		return new TextBlock(new FormulaContentItem(mathInfo.read(), paragraphFlag));
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
						float scale = 1;

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
							scale = (float) 1.3; // because object is small
							for (CTObject picture : run.getCTR().getObjectList()) {
								CTShape[] shapes = (CTShape[]) picture.selectPath(
										"declare namespace v='urn:schemas-microsoft-com:vml' " + ".//v:shape");
								if (shapes.length != 0) {
									picStyle = shapes[0].selectAttribute("", "style").getDomNode().getNodeValue();

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
							blockItems.add(new ImageContentItem(pictureData, picStyle, isWrap, scale));
						}
					} else {
						// Text
						blockItems.add(new TextContentItem(run));
					}
				}
			}
		}

		return (blockItems.isEmpty()) ? null : new TextBlock(blockItems);
	}

}
