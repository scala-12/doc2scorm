package com.ipoint.coursegenerator.core.parsers.courseParser;

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
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.paragraph.HeaderBlock;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;

/**
 * Class for parsing to {@link CourseModel}
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class CourseParser extends AbstractParser {

	private static final String MATH_START = "<math>";

	private static final String MATH_END = MATH_START.replace("<", "</");

	private ArrayList<Integer> pathToHeader;

	/**
	 * Returns all formulas in MathML notation from OMML formuls of
	 * docx-document
	 * 
	 * @param stream
	 *            Docx document stream
	 * @return formulas in MathML notation
	 */
	private static ArrayList<Node> getMathMLFromDocStream(InputStream stream) {

		String mathML = fmath.conversion.ConvertFromWordToMathML
				.getMathMLFromDocStream(stream,
						StandardCharsets.ISO_8859_1.displayName());

		ArrayList<Node> mathMLList = new ArrayList<>();
		mathML = mathML.replace("\n", "");
		int pos = mathML.indexOf("<", 0);
		while (pos != -1) {
			int oldLenght = mathML.length();
			mathML = mathML.substring(0, pos).trim()
					+ mathML.substring(pos);
			pos = mathML.indexOf("<", pos - (oldLenght - mathML.length())
					+ 1);
		}

		pos = mathML.lastIndexOf(">");
		while (pos != -1) {
			int oldLenght = mathML.length();
			mathML = mathML.substring(0, pos).trim()
					+ mathML.substring(pos);
			pos = mathML.lastIndexOf(">",
					pos - (oldLenght - mathML.length()) - 1);
		}

		if (!mathML.equalsIgnoreCase(MATH_START + MATH_END)) {
			mathML = replaceSpecialSymbols(mathML);

			String[] mathParts = mathML
					.split("<mspace linebreak='newline'/>");
			String prefix = "";
			String suffix = "</math>";
			int count = mathParts.length - 1;
			for (int i = 0; i < mathParts.length; ++i) {
				if (i == count) {
					suffix = "";
				}

				try {
					Node mathMLNode = DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(new InputSource(new StringReader(prefix
									+ mathParts[i] + suffix)))
							.getDocumentElement();

					normalizeNodes(mathMLNode.getChildNodes());
					mathMLList.add(mathMLNode);
				} catch (SAXException | IOException
						| ParserConfigurationException e) {
					System.err.println("Error : Cannot converting "
							+ mathParts[i] + " in MathML element!");
					e.printStackTrace();
				}

				if (i == 0) {
					prefix = "<math>";
				}
			}

			return mathMLList;
		}

		return null;
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
				value = value.replace("&apos;", "'").replace("&quot;", "\"")
						.replace("&amp;", "&");
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
					str = str.substring(0, pos) + newStr[i]
							+ str.substring(pos + 1);
					pos = str.indexOf(oldStr[i], pos + oldStr[i].length()
							- newStr[i].length() + 1);
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
	 * @param headerLevel
	 *            Current level of header
	 * @return node of course model
	 */
	private CourseTreeNode getOrCreateCourseNode(CourseModel courseModel,
			String levelTitle, int headerLevel) {
		if (this.pathToHeader.size() == headerLevel) {
			// current level is now,
			this.pathToHeader.set(this.pathToHeader.size() - 1,
					this.pathToHeader.get(this.pathToHeader.size() - 1) + 1);
		} else {
			// have new level
			if (this.pathToHeader.size() > headerLevel) {
				// up level
				ArrayList<Integer> newMap = new ArrayList<>();
				newMap.addAll(this.pathToHeader.subList(0, headerLevel - 1));
				newMap.add(this.pathToHeader.get(headerLevel - 1) + 1);
				this.pathToHeader = newMap; // remove extra
				// levels
			} else {
				// down level
				while (this.pathToHeader.size() < headerLevel) {
					this.pathToHeader.add(0); // create new
											// levels
				}
			}
		}

		// search node of level in document tree
		CourseTreeNode treeNode = null;
		for (Integer lvl : this.pathToHeader) {
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

	public static MathInfo getAllFormulsAsMathML(InputStream docAsStream) {
		MathInfo mathInfo = null;
		List<Node> mathMLNodes = getMathMLFromDocStream(docAsStream);
		if (mathMLNodes != null) {
			mathInfo = new MathInfo(mathMLNodes);
		}

		return mathInfo;
	}

	/**
	 * Parsing to {@link CourseModel} from {@link XWPFDocument}
	 * 
	 * @param stream
	 *            Document MS Word as stream
	 * @param courseName
	 *            Course name
	 * @param maxHeaderLevel
	 *            Max level of header that is paragraph. Max value is 1 that
	 *            first level of header. If this value is no more than 1 then
	 *            first level
	 * @return {@link CourseModel} of course
	 */
	public CourseModel parse(InputStream stream, String courseName,
			int maxHeaderLevel) {

		CourseModel courseModel = null;
		int maxHead = (maxHeaderLevel < 1) ? 1 : maxHeaderLevel;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n;
			while ((n = stream.read(buf)) >= 0)
				baos.write(buf, 0, n);
			byte[] content = baos.toByteArray();

			MathInfo mathInfo = getAllFormulsAsMathML(new ByteArrayInputStream(
					content));

			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(
					content));

			if ((courseName == null) || courseName.isEmpty()) {
				courseName = "course" + stream.hashCode();
			}
			courseModel = new CourseModel(courseName);

			this.pathToHeader = new ArrayList<>();
			CoursePage page = new CoursePage();
			for (int i = 0; i < document.getBodyElements().size(); i++) {
				IBodyElement bodyElement = document.getBodyElements().get(i);
				Integer headLevel = null;
				if (bodyElement.getElementType().equals(
						BodyElementType.PARAGRAPH)) {
					XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

					if (!paragraph.getRuns().isEmpty()) {
						// search header
						if ((paragraph.getStyleID() != null)
								&& !isEmptyRuns(paragraph.getRuns())) {
							headLevel = HeaderParser.getHeaderLevel(paragraph);
							if (headLevel != null) {// This it is header
								if (headLevel <= maxHead) {
									if (!page.getBlocks().isEmpty()) {
										// new page because prev node have old
										// page
										page = new CoursePage();
									}

									// set page to node
									this.getOrCreateCourseNode(courseModel,
											paragraph.getText(), headLevel)
											.setPage(page);
								}
							}
						}
					}
				}

				if (headLevel == null) {
					AbstractParagraphBlock<?> paragraphBlock = AbstractParagraphParser
							.parse(bodyElement, mathInfo);

					if (paragraphBlock instanceof ListBlock) {
						// minus 1 because after this iteration "i" will be
						// incremented
						int iShift = ((ListBlock) paragraphBlock).getSize() - 1;
						if (iShift > 0) {
							i += iShift;
						}
					}

					if (paragraphBlock != null) {
						page.addBlock(paragraphBlock);
					}
				} else if (headLevel > maxHead) {
					HeaderBlock paragraphBlock = HeaderParser.parse(
							(XWPFParagraph) document.getBodyElements().get(i),
							maxHead);

					if (paragraphBlock != null) {
						page.addBlock(paragraphBlock);
					}
				}
			}
		} catch (IOException e) {
			System.err
					.println("Error : Cannot convert create XWPFDocument from input stream!");
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
