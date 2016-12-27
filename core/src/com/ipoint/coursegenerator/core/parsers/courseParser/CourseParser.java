package com.ipoint.coursegenerator.core.parsers.courseParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.ipoint.coursegenerator.core.courseModel.AbstractTreeNode;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser
		.HeaderInfo;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ipoint.coursegenerator.core.courseModel.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.CoursePage;
import com.ipoint.coursegenerator.core.courseModel.CourseTreeNode;
import com.ipoint.coursegenerator.core.courseModel.blocks.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.blocks.textual.list.ListBlock;
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
	public static CourseModel parse(InputStream stream, String courseName,
									int maxHeaderLevel) {

		CourseModel courseModel = null;
		int maxHeader = (maxHeaderLevel < 1) ? 1 : maxHeaderLevel;

		try {
			//We need in 2 independent Input streams for MathML and XWPFDocument
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

			courseModel = CourseModel.createEmptyCourseModel(courseName);

			List<XWPFParagraph> trueHeaders = document.getBodyElements().stream()
					.filter(elem -> elem.getElementType().equals(BodyElementType.PARAGRAPH)
							&& HeaderParser.HeaderInfo.isHeader((XWPFParagraph) elem)
							&& (new HeaderInfo((XWPFParagraph) elem).getLevel() <= maxHeader)
					)
					.map(elem -> (XWPFParagraph) elem)
					.collect(Collectors.toList());

			int lastHeaderNum = trueHeaders.size() - 1;
			int lastDocElemNum = document.getBodyElements().size() - 1;
			AbstractTreeNode currentNode = null;
			int currentDepth = HeaderInfo.TOP_LEVEL;
			int realDepth = currentDepth;

			for (int headerNum = 0; headerNum < trueHeaders.size(); headerNum++) {
				XWPFParagraph header = trueHeaders.get(headerNum);
				HeaderInfo headerInfo = new HeaderInfo(header);

				if ((currentNode == null) || (headerInfo.getLevel() == HeaderInfo.TOP_LEVEL)) {
					currentNode = courseModel.createChild(headerInfo.getTitle());
					currentDepth = HeaderInfo.TOP_LEVEL;
					realDepth = currentDepth;
				} else {
					if (headerInfo.getLevel() < currentDepth) {
						currentNode = currentNode.getParent().createAfter(headerInfo.getTitle());
						currentDepth--;
					} else if (headerInfo.getLevel() > realDepth) {
						currentNode = currentNode.createChild(headerInfo.getTitle());
						currentDepth++;
					} else {
						currentNode = currentNode.createAfter(headerInfo.getTitle());
					}

					realDepth = headerInfo.getLevel();
				}

				int absParNum = document.getBodyElements().indexOf(header);
				ArrayList<XWPFParagraph> contentPars = new ArrayList<>();
				if ((absParNum != lastDocElemNum)) {
					contentPars.addAll(document.getBodyElements()
							.subList(
									absParNum + 1,
									(headerNum == lastHeaderNum) ? document.getBodyElements().size()
											: document.getBodyElements().indexOf(trueHeaders.get(headerNum + 1)))
							.stream()
							.filter(elem -> elem instanceof XWPFParagraph)
							.map(elem -> (XWPFParagraph) elem)
							.collect(Collectors.toList()));
				}

				if (!contentPars.isEmpty()) {
					ArrayList<AbstractParagraphBlock<?>> contentBlocks = new ArrayList<>();
					if (headerInfo.isTheoryNotTestHeader()) {
						for (int contentElemNum = 0; contentElemNum < contentPars.size(); contentElemNum++) {
							XWPFParagraph subPar = contentPars.get(contentElemNum);
							AbstractParagraphBlock<?> contentBlock = AbstractParagraphParser
									.parse(subPar, mathInfo);
							if (HeaderParser.HeaderInfo.isHeader(subPar)) {
								contentBlock = HeaderParser.parse(subPar, maxHeader);
							} else {
								if (contentBlock instanceof ListBlock) {
									// minus 1 because after this iteration "elNum" will be
									// incremented
									int shift = ((ListBlock) contentBlock).getSize() - 1;
									if (shift > 0) {
										contentElemNum += shift;
									}
								}
							}

							contentBlocks.add(contentBlock);
						}
					} else {
						//TODO: code for test-blocks
					}

					if (!contentBlocks.isEmpty()) {
						CoursePage page = CoursePage.createEmptyPage((CourseTreeNode) currentNode);
						page.addBlocks(contentBlocks);
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

}
