package com.ipoint.coursegenerator.core.parser.courseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ipoint.coursegenerator.core.courseModel.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.CoursePage;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeNode;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.paragraphs.textual.paragraph.HeaderBlock;
import com.ipoint.coursegenerator.core.parser.AbstractParser;
import com.ipoint.coursegenerator.core.parser.MathInfo;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.AbstractParagraphParser;
import com.ipoint.coursegenerator.core.parser.courseModel.paragraphs.textual.paragraph.HeaderParser;

/**
 * Class for parsing to {@link CourseModel}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseModelParser extends AbstractParser {

	private final static String MATH_START = "<math>";

	private final static String MATH_END = MATH_START.replace("<", "</");

	private ArrayList<Integer> levelMap;

	/**
	 * Returns all formulas in MathML notation from OMML formuls of
	 * docx-document
	 * 
	 * @param stream
	 *            Docx document stream
	 * @return formulas in MathML notation
	 */
	private static ArrayList<Node> getMathMLFromDocStream(InputStream stream) {

		String mathML = fmath.conversion.ConvertFromWordToMathML.getMathMLFromDocStream(stream,
				StandardCharsets.ISO_8859_1.displayName());

		if (mathML != null) {
			ArrayList<Node> mathMLList = new ArrayList<Node>();
			mathML = mathML.replace("\n", "");
			int pos = mathML.indexOf("<", 0);
			while (pos != -1) {
				int oldLenght = mathML.length();
				mathML = mathML.substring(0, pos).trim() + mathML.substring(pos);
				pos = mathML.indexOf("<", pos - (oldLenght - mathML.length()) + 1);
			}

			pos = mathML.lastIndexOf(">");
			while (pos != -1) {
				int oldLenght = mathML.length();
				mathML = mathML.substring(0, pos).trim() + mathML.substring(pos);
				pos = mathML.lastIndexOf(">", pos - (oldLenght - mathML.length()) - 1);
			}

			if (!mathML.equalsIgnoreCase(MATH_START + MATH_END)) {
				mathML = replaceSpecialSymbols(mathML);

				String[] mathParts = mathML.split("<mspace linebreak='newline'/>");
				String prefix = new String();
				String suffix = "</math>";
				int count = mathParts.length - 1;
				for (int i = 0; i < mathParts.length; ++i) {
					if (i == count) {
						suffix = new String();
					}

					try {
						Node mathMLNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
								.parse(new InputSource(new StringReader(prefix + mathParts[i] + suffix)))
								.getDocumentElement();

						normalizeNodes(mathMLNode.getChildNodes());
						mathMLList.add(mathMLNode);
					} catch (SAXException | IOException | ParserConfigurationException e) {
						System.err.println("Error : Cannot converting " + mathParts[i] + " in MathML element!");
						e.printStackTrace();
					}

					if (i == 0) {
						prefix = "<math>";
					}
				}

				return mathMLList;
			}
		}

		return null;
	}

	/**
	 * Search header level
	 * 
	 * @param headId
	 *            ID of header
	 * @return
	 */
	private static Integer getNumberOfHeader(String headId) {
		Integer resultVariable = null;
		if ((headId.equals("Heading1")) || (headId.equals("1"))) {
			resultVariable = 1;
		} else if ((headId.equals("Heading2")) || (headId.equals("2"))) {
			resultVariable = 2;
		} else if ((headId.equals("Heading3")) || (headId.equals("3"))) {
			resultVariable = 3;
		} else if ((headId.equals("Heading4")) || (headId.equals("4"))) {
			resultVariable = 4;
		} else if ((headId.equals("Heading5")) || (headId.equals("5"))) {
			resultVariable = 5;
		} else if ((headId.equals("Heading6")) || (headId.equals("6"))) {
			resultVariable = 6;
		} else if ((headId.equals("Heading7")) || (headId.equals("7"))) {
			resultVariable = 7;
		} else if ((headId.equals("Heading8")) || (headId.equals("8"))) {
			resultVariable = 8;
		} else if ((headId.equals("Heading9")) || (headId.equals("9"))) {
			resultVariable = 9;
		}

		return resultVariable;
	}

	/**
	 * Replace escaped characters
	 * 
	 * @param nodes
	 *            Nodes with replaced escaped characters
	 */
	private static void normalizeNodes(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			String value = node.getNodeValue();
			if (value != null) {
				value = value.replace("&apos;", "'").replace("&quot;", "\"").replace("&amp;", "&");
				node.setNodeValue(value);
			}

			if (node.hasChildNodes()) {
				normalizeNodes(node.getChildNodes());
			}
		}
	}

	/**
	 * Escaping characters: &, ', "
	 * 
	 * @param str
	 *            String for Escaping string
	 * @return Escaped string
	 */
	private static String replaceSpecialSymbols(String str) {
		str = str.replace("&", "&amp;");

		String[] oldStr = { "'", "\"" };
		String[] newStr = { "&apos;", "&quot;" };
		for (int i = 0; (i < oldStr.length) && i < newStr.length; ++i) {
			int pos = str.indexOf(oldStr[i], 0);
			while (pos != -1) {
				if (str.indexOf("<", pos) < str.indexOf(">", pos)) {
					str = str.substring(0, pos) + newStr[i] + str.substring(pos + 1);
					pos = str.indexOf(oldStr[i], pos + oldStr[i].length() - newStr[i].length() + 1);
				} else {
					pos = str.indexOf(oldStr[i], pos + 1);
				}
			}
		}

		return str;
	}

	/**
	 * Returns node of course model with using level map. If model don't
	 * includes node then he will be created.
	 * 
	 * @param courseModel
	 *            Model of course
	 * @param levelTitle
	 *            Title of course part
	 * @param levelMap
	 *            Map of course parts
	 * @param headerLevel
	 *            Current level of header
	 * @return node of course model
	 */
	private CourseTreeNode getOrCreateCourseNode(CourseModel courseModel, String levelTitle, int headerLevel) {
		if (this.levelMap.size() == headerLevel) {
			// current level is now,
			this.levelMap.set(this.levelMap.size() - 1, this.levelMap.get(this.levelMap.size() - 1) + 1);
		} else {
			// have new level
			if (this.levelMap.size() > headerLevel) {
				// up level
				ArrayList<Integer> newMap = new ArrayList<Integer>();
				newMap.addAll(this.levelMap.subList(0, headerLevel - 1));
				newMap.add(this.levelMap.get(headerLevel - 1) + 1);
				this.levelMap = newMap; // remove extra
				// levels
			} else {
				// down level
				while (this.levelMap.size() < headerLevel) {
					this.levelMap.add(0); // create new
											// levels
				}
			}
		}

		// search node of level in document tree
		CourseTreeNode treeNode = null;
		for (Integer lvl : this.levelMap) {
			if (treeNode == null) {
				// start node
				if (courseModel.getNode(lvl) == null) {
					// new node
					courseModel.addNode(new CourseTreeNode(levelTitle));
				}
				treeNode = courseModel.getNode(lvl);
			} else {
				// other nodes
				if (treeNode.getNode(lvl) == null) {
					// new node
					treeNode.addNode(new CourseTreeNode(levelTitle));
				}
				treeNode = treeNode.getNodes().get(lvl);
			}
		}

		return treeNode;
	}

	/**
	 * Parsing to {@link CourseModel} from {@link XWPFDocument}
	 * 
	 * @param document
	 *            Course as document MS Word
	 * @param courseName
	 *            Course name
	 * @param maxHeaderLevel
	 *            Max level of header that is paragraph. Max value is 1 that
	 *            first level of header. If this value is no more than 1 then
	 *            first level
	 * @return {@link CourseModel} of course
	 */
	public CourseModel parse(InputStream stream, String courseName, Integer maxHeaderLevel) {

		CourseModel courseModel = new CourseModel(courseName);
		int maxHead = (maxHeaderLevel == null) ? 1 : ((maxHeaderLevel < 1) ? 1 : maxHeaderLevel);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while ((n = stream.read(buf)) >= 0)
				baos.write(buf, 0, n);
			byte[] content = baos.toByteArray();

			InputStream streamCopy1 = new ByteArrayInputStream(content);
			InputStream streamCopy2 = new ByteArrayInputStream(content);

			MathInfo mathInfo = null;
			List<Node> mathMLNodes = getMathMLFromDocStream(streamCopy1);
			if (mathMLNodes != null) {
				mathInfo = new MathInfo(mathMLNodes);
			}

			XWPFDocument document = new XWPFDocument(streamCopy2);

			this.levelMap = new ArrayList<Integer>();
			CoursePage page = new CoursePage();
			for (int i = 0; i < document.getBodyElements().size(); i++) {
				IBodyElement bodyElement = document.getBodyElements().get(i);
				Integer headLevel = null;
				if (bodyElement.getElementType().equals(BodyElementType.PARAGRAPH)) {
					XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

					if (!paragraph.getRuns().isEmpty()) {
						// search header
						if ((paragraph.getStyleID() != null) && !isEmptyRuns(paragraph.getRuns())) {
							headLevel = getNumberOfHeader(paragraph.getStyleID());
							if (headLevel != null) {// This it is header
								if (headLevel <= maxHead) {
									if (!page.getBlocks().isEmpty()) {
										// new page because prev node have old
										// page
										page = new CoursePage();
									}

									// set page to node
									this.getOrCreateCourseNode(courseModel, paragraph.getText(), headLevel)
											.setPage(page);
								}
							}
						}
					}
				}

				if (headLevel == null) {
					AbstractParagraphBlock<?> paragraphBlock = AbstractParagraphParser
							.parse(document.getBodyElements().subList(i, document.getBodyElements().size()), mathInfo);

					if (paragraphBlock instanceof ListBlock) {
						i += ((ListBlock) paragraphBlock).getSize() - 1;
					}

					if (paragraphBlock != null) {
						page.addBlock(paragraphBlock);
					}
				} else if (headLevel > maxHead) {
					HeaderBlock paragraphBlock = HeaderParser.parse((XWPFParagraph) document.getBodyElements().get(i),
							headLevel - maxHead);

					if (paragraphBlock != null) {
						page.addBlock(paragraphBlock);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error : Cannot convert create XWPFDocument from input stream!");
			e.printStackTrace();
		}

		return courseModel;
	}

	private boolean isEmptyRuns(List<XWPFRun> runs) {
		boolean isEmptyRuns = true;
		for (int i = 0; (i < runs.size()) && isEmptyRuns; ++i) {
			isEmptyRuns = runs.get(i).toString().trim().isEmpty();
		}

		return isEmptyRuns;
	}

}
