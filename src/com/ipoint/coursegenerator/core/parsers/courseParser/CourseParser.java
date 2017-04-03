package com.ipoint.coursegenerator.core.parsers.courseParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.AbstractParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice.ChoiceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.choice.ChoiceItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.fillIn.FillInBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match.MatchBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.match.MatchItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence.SequenceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questions.sequence.SequenceItem;
import com.ipoint.coursegenerator.core.courseModel.structure.AbstractTreeNode;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser.HeaderInfo;
import com.ipoint.coursegenerator.core.utils.Tools;

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

		String mathML = fmath.conversion.ConvertFromWordToMathML.getMathMLFromDocStream(stream,
				StandardCharsets.ISO_8859_1.displayName());

		ArrayList<Node> mathMLList = new ArrayList<>();
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
			String prefix = "";
			String suffix = "</math>";
			int count = mathParts.length - 1;
			for (int i = 0; i < mathParts.length; ++i) {
				if (i == count) {
					suffix = "";
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
	public static CourseModel parse(InputStream stream, String courseName, int maxHeaderLevel) {

		CourseModel courseModel = null;
		int maxHeader = (maxHeaderLevel < 1) ? 1 : maxHeaderLevel;

		try {
			// We need in 2 independent Input streams for MathML and
			// XWPFDocument
			byte[] content = Tools.convertStream2ByteArray(stream);
			MathInfo mathInfo = getAllFormulsAsMathML(new ByteArrayInputStream(content));
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content));
			content = null;

			courseModel = CourseModel.createEmptyCourseModel(courseName);

			List<XWPFParagraph> trueHeaders = document.getBodyElements().stream()
					.filter(elem -> elem.getElementType().equals(BodyElementType.PARAGRAPH)
							&& HeaderParser.HeaderInfo.isHeader((XWPFParagraph) elem)
							&& (HeaderInfo.getHeaderInfo((XWPFParagraph) elem).getLevel() <= maxHeader))
					.map(elem -> (XWPFParagraph) elem).collect(Collectors.toList());

			int lastHeaderNum = trueHeaders.size() - 1;
			int lastDocElemNum = document.getBodyElements().size() - 1;
			AbstractTreeNode currentNode = null;
			int currentDepth = HeaderInfo.TOP_LEVEL;
			int realDepth = currentDepth;

			for (int headerNum = 0; headerNum < trueHeaders.size(); headerNum++) {
				XWPFParagraph header = trueHeaders.get(headerNum);
				HeaderInfo headerInfo = HeaderInfo.getHeaderInfo(header);

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

				int absNum = document.getBodyElements().indexOf(header);
				ArrayList<XWPFParagraph> chapterPars = new ArrayList<>();
				if ((absNum != lastDocElemNum)) {
					chapterPars.addAll(document.getBodyElements()
							.subList(absNum + 1,
									(headerNum == lastHeaderNum) ? document.getBodyElements().size()
											: document.getBodyElements().indexOf(trueHeaders.get(headerNum + 1)))
							.stream().filter(elem -> elem instanceof XWPFParagraph).map(elem -> (XWPFParagraph) elem)
							.collect(Collectors.toList()));
				}

				if (!chapterPars.isEmpty()) {
					AbstractPage<?> page = null;

					if (headerInfo.isTheoryNoneTestHeader()) {
						ArrayList<AbstractParagraphBlock<?>> chapterBlocks = new ArrayList<>();

						for (int chapterElemNum = 0; chapterElemNum < chapterPars.size(); chapterElemNum++) {
							Object[] blockAndShift = getBlockAndShift(chapterPars.get(chapterElemNum), mathInfo,
									maxHeader);
							chapterElemNum += (int) blockAndShift[1];
							chapterBlocks.add((AbstractParagraphBlock<?>) blockAndShift[0]);
						}

						if (!chapterBlocks.isEmpty()) {
							TheoryPage theoryPage = TheoryPage.createEmptyPage();
							theoryPage.setBlocks(chapterBlocks);
							page = theoryPage;
						}
					} else {
						ArrayList<AbstractQuestionBlock<?>> questionsBlocks = new ArrayList<>();

						ArrayList<AbstractParagraphBlock<?>> introBlocks = new ArrayList<>();
						String task = null;
						ArrayList<AbstractParagraphBlock<?>> answerBlocks = null;
						for (int chapterElemNum = 0; chapterElemNum < chapterPars.size(); chapterElemNum++) {
							XWPFParagraph chapterPar = chapterPars.get(chapterElemNum);

							if (HeaderParser.HeaderInfo.isQuestion(chapterPar)) {
								if ((answerBlocks != null)) {
									AbstractQuestionBlock<?> questBlock = null;
									if (answerBlocks.isEmpty()) {
										questBlock = new FillInBlock(task);
									} else {
										AbstractParagraphBlock<?> answerBlock = answerBlocks.get(0);
										if (answerBlock instanceof TableBlock) {
											TableBlock block = (TableBlock) answerBlock;
											if (block.getFirstItem().getValue().size() == 1) {
												ArrayList<SequenceItem> items = new ArrayList<>();
												for (TableItem row : block.getItems()) {
													items.add(new SequenceItem(
															row.getValue().get(0).getFirstItem().getValue()));
												}
												questBlock = new SequenceBlock(items, task);
											} else if (block.getFirstItem().getValue().size() == 2) {
												ArrayList<MatchItem> items = new ArrayList<>();
												for (TableItem row : block.getItems()) {
													ArrayList<List<AbstractParagraphBlock<?>>> pair = new ArrayList<>();
													pair.set(0, row.getValue().get(0).getFirstItem().getValue());
													pair.set(0, row.getValue().get(1).getFirstItem().getValue());
													items.add(new MatchItem(pair));
												}
												questBlock = new MatchBlock(items, task);
											}
										} else if (answerBlock instanceof ListBlock) {
											ArrayList<ChoiceItem> items = new ArrayList<>();
											for (ListItem item : ((ListBlock) answerBlock).getItems()) {
												ParagraphBlock block = (ParagraphBlock) item.getValue();

												List<List<AbstractContentItem<?>>> parItems = block.getItems().stream()
														.filter(parItem -> parItem.getValue().getFirstItem() != null)
														.map(parItem -> parItem.getValue().getItems())
														.collect(Collectors.toList());

												boolean isFalse = true;
												for (Iterator<List<AbstractContentItem<?>>> listIter = parItems
														.iterator(); listIter.hasNext();) {
													for (Iterator<AbstractContentItem<?>> iter = listIter.next()
															.iterator(); isFalse && iter.hasNext();) {
														isFalse = iter.next().isUnderline();
													}
												}

												items.add(new ChoiceItem(block, !isFalse));
											}
											questBlock = new ChoiceBlock(items, task);
										}
									}

									if (questBlock != null) {
										questionsBlocks.add(questBlock);
									}
								}
								answerBlocks = new ArrayList<>();
								task = chapterPar.getText();
							} else {
								Object[] blockAndShift = getBlockAndShift(chapterPars.get(chapterElemNum), mathInfo,
										maxHeader);
								chapterElemNum += (int) blockAndShift[1];
								if (answerBlocks == null) {
									introBlocks.add((AbstractParagraphBlock<?>) blockAndShift[0]);
								} else {
									answerBlocks.add((AbstractParagraphBlock<?>) blockAndShift[0]);
								}
							}
						}

						if (!questionsBlocks.isEmpty()) {
							TestingPage testPage = TestingPage.createEmptyPage();
							if (!introBlocks.isEmpty()) {
								testPage.setIntroBlocks(introBlocks);
							}
							testPage.setBlocks(questionsBlocks);
							page = testPage;
						}
					}

					if (page != null) {
						page.setParent((CourseTreeNode) currentNode);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error : Cannot convert create XWPFDocument from input stream!");
			e.printStackTrace();
		}

		return courseModel;
	}

	private static Object[] getBlockAndShift(XWPFParagraph par, MathInfo mathInfo, int maxHeader) {
		AbstractParagraphBlock<?> block = AbstractParagraphParser.parse(par, mathInfo);
		int shift = 0;

		if (HeaderParser.HeaderInfo.isHeader(par)) {
			block = HeaderParser.parse(par, maxHeader);
		} else if (block instanceof ListBlock) {
			// minus 1 because after this iteration
			// "elNum" will be
			// incremented
			shift = ((ListBlock) block).getSize() - 1;
		}

		return new Object[] { block, (shift > 0) ? shift : 0 };
	}

}
