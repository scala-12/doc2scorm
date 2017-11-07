package com.ipoint.coursegenerator.core.parsers.courseParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ipoint.coursegenerator.core.courseModel.content.AbstractPage;
import com.ipoint.coursegenerator.core.courseModel.content.TestingPage;
import com.ipoint.coursegenerator.core.courseModel.content.TheoryPage;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.BlockCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.exceptions.ItemCreationException;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.AbstractQuestionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.choice.ChoiceItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn.FillInBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.fillIn.FillInItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match.MatchBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.match.MatchBlock.Label2Answer;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sequence.SequenceBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.questionsSection.sequence.SequenceItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.AbstractSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.list.ListSectionItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.simpleSections.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.structure.AbstractTreeNode;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseModel;
import com.ipoint.coursegenerator.core.courseModel.structure.CourseTreeNode;
import com.ipoint.coursegenerator.core.parsers.AbstractParser;
import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser.HeaderInfo;
import com.ipoint.coursegenerator.core.utils.FileTools;
import com.ipoint.coursegenerator.core.utils.Tools;
import com.ipoint.coursegenerator.core.utils.Tools.Pair;

/**
 * Class for parsing to {@link CourseModel}
 *
 * @author Kalashnikov Vladislav
 *
 */
public class CourseParser extends AbstractParser {

	private static class BlockWithShifting extends Pair<AbstractSectionBlock<?>, Integer> {

		public BlockWithShifting(AbstractSectionBlock<?> block, int shift) {
			super(block, (shift > 0) ? shift : 0);
		}

		public AbstractSectionBlock<?> getBlock() {
			return this.left;
		}

		public int getShift() {
			return this.right.intValue();
		}
	}

	private static class QuestionWithAnswers extends Pair<String, List<AbstractSectionBlock<?>>> {

		public QuestionWithAnswers(String task, List<AbstractSectionBlock<?>> answersBlocks) {
			super(task, answersBlocks);
		}

		public String getTask() {
			return this.left;
		}

		public List<AbstractSectionBlock<?>> getAnswersBlocks() {
			return this.right;
		}
	}

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
		// checked value for max header level
		int maxHeader = (maxHeaderLevel < 1) ? 1 : maxHeaderLevel;

		try {
			// need in 2 independent Input streams for MathML and XWPFDocument
			File tmpDoc = File.createTempFile("course_", ".docx");
			try (BufferedInputStream bufIS = new BufferedInputStream(stream)) {
				FileTools.saveRawFile(bufIS, tmpDoc);
			}

			MathInfo mathInfo = null;
			try (FileInputStream fIS = new FileInputStream(tmpDoc)) {
				mathInfo = getAllFormulsAsMathML(new BufferedInputStream(fIS));
			}

			XWPFDocument document = null;
			try (FileInputStream fIS = new FileInputStream(tmpDoc)) {
				document = new XWPFDocument(new BufferedInputStream(fIS));
			}
			tmpDoc.delete();

			courseModel = CourseModel.createEmptyCourseModel(courseName);

			List<XWPFParagraph> trueHeaders = document.getBodyElements().stream()
					.filter(elem -> elem.getElementType().equals(BodyElementType.PARAGRAPH)
							&& HeaderParser.HeaderInfo.isHeader((XWPFParagraph) elem)
							&& (HeaderInfo.getHeaderInfo((XWPFParagraph) elem).getLevel() <= maxHeader))
					.map(elem -> (XWPFParagraph) elem).collect(Collectors.toList());

			int lastHeaderNum = trueHeaders.size() - 1;
			int lastDocElemNum = document.getBodyElements().size() - 1;

			AbstractTreeNode currentNode = null;
			// depth in course model
			int currentDepth = HeaderInfo.TOP_LEVEL;
			int realDepth = currentDepth;

			int headerNumber = -1;
			for (XWPFParagraph header : trueHeaders) {
				headerNumber += 1;
				HeaderInfo headerInfo = HeaderInfo.getHeaderInfo(header);

				if ((currentNode == null) || (headerInfo.getLevel() == HeaderInfo.TOP_LEVEL)) {
					// create node on top of model
					currentNode = courseModel.createChild(headerInfo.getTitle());
					// current depth is depth in course-model
					currentDepth = HeaderInfo.TOP_LEVEL;
					// real depth is depth in MS Word
					realDepth = currentDepth;
				} else {
					// TODO: проверить использование переменных 'currentDepth' и
					// 'realDepth'
					// TODO: проверить в сложной структуре - скачущие уровни
					// заголовков типа "1-4-2-4-3-1-6-1-2"
					if (headerInfo.getLevel() < currentDepth) {
						// create node after current-node-parent
						currentNode = currentNode.getParent().createAfter(headerInfo.getTitle());
						currentDepth--;
					} else if (headerInfo.getLevel() > realDepth) {
						// create node as child of current-node
						currentNode = currentNode.createChild(headerInfo.getTitle());
						currentDepth++;
					} else {
						// create node after current-node
						currentNode = currentNode.createAfter(headerInfo.getTitle());
					}

					realDepth = headerInfo.getLevel();
				}

				int absHeaderNumber = document.getBodyElements().indexOf(header);
				List<IBodyElement> chapterParsAndTables = (absHeaderNumber == lastDocElemNum)
						? Collections.<IBodyElement>emptyList()
						: document.getBodyElements()
								.subList(absHeaderNumber + 1,
										(headerNumber == lastHeaderNum) ? document.getBodyElements().size()
												: document.getBodyElements().indexOf(trueHeaders.get(headerNumber + 1)))
								.stream().filter(elem -> (elem instanceof XWPFParagraph) || (elem instanceof XWPFTable))
								.collect(Collectors.toList());

				if (!chapterParsAndTables.isEmpty()) {
					AbstractPage<?> page = null;

					if (headerInfo.isTheoryNoneTestHeader()) {
						// is not test
						ArrayList<AbstractSectionBlock<?>> chapterBlocks = new ArrayList<>();

						int chapterElemNum = 0;
						while (chapterElemNum < chapterParsAndTables.size()) {
							BlockWithShifting blockAndShift = getBlockWithShifting(
									chapterParsAndTables.get(chapterElemNum), mathInfo, maxHeader);
							chapterBlocks.add(blockAndShift.getBlock());

							chapterElemNum += (1 + blockAndShift.getShift());
						}

						if (!chapterBlocks.isEmpty()) {
							TheoryPage theoryPage = TheoryPage.createEmptyPage();
							theoryPage.setBlocks(chapterBlocks);
							page = theoryPage;
						}
					} else {
						// is test

						ArrayList<AbstractSectionBlock<?>> introBlocks = new ArrayList<>();
						ArrayList<QuestionWithAnswers> questionsWithAnswers = new ArrayList<>();

						ArrayList<AbstractSectionBlock<?>> questionAnswers = null;
						StringBuilder questionTask = null;

						HashMap<String, XWPFParagraph> htmlAnswer2RealPar = new HashMap<>();

						Document html = Tools.createEmptyDocument();

						boolean hasQuestion = false;
						int chapterElemNum = 0;
						while (chapterElemNum < chapterParsAndTables.size()) {
							IBodyElement chapterElem = chapterParsAndTables.get(chapterElemNum);

							XWPFParagraph par = (chapterElem instanceof XWPFParagraph) ? (XWPFParagraph) chapterElem
									: null;

							if ((null != par) && HeaderParser.HeaderInfo.isQuestion(par)) {
								// task header as question text
								hasQuestion = true;

								if ((null == questionAnswers) || !questionAnswers.isEmpty()) {
									// new answer blocks
									questionAnswers = new ArrayList<>();
									questionTask = new StringBuilder();
								}

								questionTask.append(par.getText());
							} else {
								BlockWithShifting blockWithShift = getBlockWithShifting(chapterElem, mathInfo,
										maxHeader);
								if (blockWithShift.getBlock() != null) {
									if (hasQuestion) {
										questionsWithAnswers
												.add(new QuestionWithAnswers(questionTask.toString(), questionAnswers));

										// TODO: check all
										if (blockWithShift.getBlock() instanceof ListSectionBlock) {
											ListSectionBlock listBlock = (ListSectionBlock) blockWithShift.getBlock();
											int shift = blockWithShift.getShift();

											int i = 0;
											int count = 0;
											int lastChapterParElementShift = chapterParsAndTables.size()
													- chapterElemNum;
											while ((count <= shift) && (i < lastChapterParElementShift)) {
												IBodyElement elem = chapterParsAndTables.get(chapterElemNum + i);
												if (elem instanceof XWPFParagraph) {
													htmlAnswer2RealPar.put(
															Tools.getNodeString(
																	listBlock.getItems().get(i).toHtml(html)),
															(XWPFParagraph) elem);
													count += 1;
												}

												i += 1;
											}

											chapterElemNum += shift;
										} else if (null != par) {
											htmlAnswer2RealPar.put(
													Tools.getNodeString(blockWithShift.getBlock().toHtml(html)), par);
										}

										questionAnswers.add(blockWithShift.getBlock());
									} else {
										introBlocks.add(blockWithShift.getBlock());
									}
								}
							}

							chapterElemNum += 1;
						}

						ArrayList<AbstractQuestionBlock<?>> questionsBlocks = new ArrayList<>(
								questionsWithAnswers.size());
						for (QuestionWithAnswers question : questionsWithAnswers) {
							AbstractQuestionBlock<?> questBlock = null;

							if (question.getAnswersBlocks().get(0) instanceof TableBlock) {
								TableBlock block = (TableBlock) question.getAnswersBlocks().get(0);
								List<TableItem> rows = block.getItems();
								if (rows.get(0).getValue().size() == 1) {
									if (rows.size() == 1) {
										questBlock = new FillInBlock(new FillInItem(block.getText()));
									} else {
										ArrayList<SequenceItem> items = new ArrayList<>();
										for (TableItem row : rows) {
											items.add(new SequenceItem(row.getValue().get(0).getItem().getValue()));
										}

										questBlock = new SequenceBlock(items);
									}
								} else if (rows.size() == 1) {
									ArrayList<SequenceItem> items = new ArrayList<>();
									for (CellBlock cell : rows.get(0).getValue()) {
										items.add(new SequenceItem(cell.getItem().getValue()));
									}
									questBlock = new SequenceBlock(items);

								} else if (rows.get(0).getValue().size() == 2) {
									ArrayList<Label2Answer> items = new ArrayList<>();
									for (TableItem row : rows) {
										items.add(new Label2Answer(row.getValue().get(0).getItem().getValue(),
												row.getValue().get(1).getItem().getValue()));
									}
									questBlock = new MatchBlock(items);
								}
							} else if (question.getAnswersBlocks().get(0) instanceof ListSectionBlock) {
								ArrayList<ChoiceItem> items = new ArrayList<>();
								for (ListSectionItem item : ((ListSectionBlock) question.getAnswersBlocks().get(0))
										.getItems()) {
									items.add(new ChoiceItem((ParagraphBlock) item.getValue(),
											HeaderParser.HeaderInfo.isCorrectAnswer(
													htmlAnswer2RealPar.get(Tools.getNodeString(item.toHtml(html))))));
								}
								questBlock = new ChoiceBlock(items);
							}

							if (questBlock != null) {
								questBlock.setTask(question.getTask());
								questionsBlocks.add(questBlock);
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
		} catch (

		IOException e) {
			System.err.println("Error : Cannot convert create XWPFDocument from input stream!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return courseModel;
	}

	private static BlockWithShifting getBlockWithShifting(IBodyElement elem, MathInfo mathInfo, int maxHeader)
			throws BlockCreationException, ItemCreationException {
		AbstractSectionBlock<?> block = AbstractParagraphParser.parse(elem, mathInfo);
		int shift = 0;

		XWPFParagraph par = (elem instanceof XWPFParagraph) ? (XWPFParagraph) elem : null;

		if ((par != null) && HeaderParser.HeaderInfo.isHeader(par)) {
			block = HeaderParser.parse(par, maxHeader);
		} else if (block instanceof ListSectionBlock) {
			// minus 1 because after this iteration
			// "elNum" will be
			// incremented
			shift = ((ListSectionBlock) block).getSize() - 1;
		}

		return new BlockWithShifting(block, shift);
	}

}
