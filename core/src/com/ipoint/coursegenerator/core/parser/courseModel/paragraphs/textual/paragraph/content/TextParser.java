package com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.content;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import schemasMicrosoftComVml.CTImageData;

import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.TextBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.FormulaOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.ImageOptionItem;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.content.contentOptions.TextOptionItem;
import com.ipoint.coursegenerator.core.parser.AbstractParser;

/**
 * Parsing paragraph which includes only text and images
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class TextParser extends AbstractParser {

	public static TextBlock parse(CTOMath formula, boolean paragraph) {
		return new TextBlock(new FormulaOptionItem(formula, paragraph));
	}

	public static TextBlock parse(List<XWPFRun> runs) {
		ArrayList<AbstractContentItem> blockItems = null;

		if (runs != null) {
			if (!runs.isEmpty()) {
				blockItems = new ArrayList<AbstractContentItem>();

				for (XWPFRun run : runs) {
					if (run.toString().isEmpty()) {
						// this run is not simple text
						XWPFPictureData pictureData = null;
						if (!run.getEmbeddedPictures().isEmpty()) {
							pictureData = run.getEmbeddedPictures().get(0)
									.getPictureData();
						} else if (!run.getCTR().getPictList().isEmpty()) {
							NodeList pic = run.getCTR().getPictList().get(0)
									.getDomNode().getChildNodes();
							for (int j = 0; j < pic.getLength(); j++) {
								if (pic.item(j).getLocalName()
										.equalsIgnoreCase("shape")) {
									Node node = pic.item(j).getFirstChild();
									for (; !node.getLocalName().equals(
											"imagedata")
											&& (node != null); node = node
											.getNextSibling()) {
										// waiting
									}
									if (node != null) {
										if (node.getLocalName().equals(
												"imagedata")) {
											if (node.getAttributes()
													.getNamedItem("r:id") != null) {
												pictureData = run
														.getDocument()
														.getPictureDataByID(
																node.getAttributes()
																		.getNamedItem(
																				"r:id")
																		.getNodeValue());
											}
										}
									}
								}
							}
						} else if (run.getCTR().sizeOfObjectArray() > 0) {
							final List<XWPFPictureData> pictures = run
									.getDocument().getAllPackagePictures();

							for (CTObject picture : run.getCTR()
									.getObjectList()) {
								CTImageData[] objs = (CTImageData[]) picture
										.selectPath("declare namespace v='urn:schemas-microsoft-com:vml' "
												+ ".//v:imagedata");

								if (objs.length > 0) {
									String rId = objs[0]
											.selectAttribute(
													"http://schemas.openxmlformats.org/officeDocument/2006/relationships",
													"id").getDomNode()
											.getNodeValue();
									for (XWPFPictureData data : pictures) {
										if (data.getPackageRelationship()
												.getId().equals(rId)) {
											pictureData = data;
											break;
										}
									}
								}
							}
						}

						blockItems.add(new ImageOptionItem(pictureData));
					} else {
						blockItems.add(new TextOptionItem(run));
					}
				}
			}
		}

		return new TextBlock(blockItems);
	}

}
