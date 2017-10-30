package test.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.ipoint.coursegenerator.core.parsers.MathInfo;
import com.ipoint.coursegenerator.core.parsers.courseParser.CourseParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.HeaderParser;
import com.ipoint.coursegenerator.core.parsers.courseParser.textualParagraphParser.ListParser;

public class TestTools {

	public static final String COURSE_NAME = "WORD-2010.docx";

	private static XWPFDocument DOC_DOCUMENT;

	public static final int COLUMN_COUNT_COLUMN = 0;
	public static final int ROW_COUNT_COLUMN = 1;
	public static final int CONTENT_FROM_COLUMN = 1;
	public static final int CONTENT_FROM_ROW = 1;

	private static MathInfo MATH_ML_FORMULAS;

	private static List<XWPFParagraph> PARAGRAPHS;
	private static List<XWPFParagraph> HEADERS;
	private static List<List<XWPFParagraph>> LISTS;
	private static List<XWPFParagraph> SIMPLE_PARAGRAPHS;
	private static List<XWPFParagraph> HYPERLINK_PARAGRAPHS;
	private static List<XWPFTable> TABLES;

	private static final String TABLE_PART = "тестирование таблиц";
	private static final String LIST_PART = "тестирование списков";
	private static final String SIMPLE_TEXT_PART = "тестирование простых текстовых параграфов";
	private static final String HYPERLINKS_PART = "тестирование гиперссылок";

	public static final String SIMPLE_TEXT_PAR = "simple";
	public static final String STYLED_TEXT_PAR = "with_text_style";
	public static final String BOLD_TEXT = "bold";
	public static final String ITALIC_TEXT = "italic";
	public static final String UNDERLINE_TEXT = "underline";
	public static final String SUPERSCRIPT_TEXT = "superscript";
	public static final String SUBSCRIPT_TEXT = "subscript";
	public static final String COLORED_TEXT = "color";

	// GLOBAL
	/**
	 * @return InputStream for file from resources or null
	 */
	public static InputStream getFileFromTestResources(String fileName) {
		return TestTools.class.getClassLoader().getResourceAsStream(fileName);
	}

	/**
	 * @return Input stream for test document
	 */
	public static InputStream getTestDocFile() {
		return getFileFromTestResources(COURSE_NAME);
	}

	/**
	 * @return Test document
	 */
	public static XWPFDocument getTestDoc() {
		if (DOC_DOCUMENT == null) {
			try {
				DOC_DOCUMENT = new XWPFDocument(
						getFileFromTestResources(COURSE_NAME));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return DOC_DOCUMENT;
	}

	public static MathInfo getMathMLFormulas() {
		if (MATH_ML_FORMULAS == null) {
			MATH_ML_FORMULAS = CourseParser
					.getAllFormulsAsMathML(getTestDocFile());
		} else {
			MATH_ML_FORMULAS.reset();
		}

		return MATH_ML_FORMULAS;
	}

	// PARAGRAPHs
	/**
	 * @return List of all text paragraphs
	 */
	public static List<XWPFParagraph> getOnlyTextParagraphs() {
		if (PARAGRAPHS == null) {
			PARAGRAPHS = getTestDoc().getBodyElements().stream()
					.filter(el -> (el instanceof XWPFParagraph))
					.map(par -> (XWPFParagraph) par)
					.filter(par -> !par.getText().isEmpty())
					.collect(Collectors.toList());
		}

		return PARAGRAPHS;
	}

	/**
	 * @return List of headers
	 */
	public static List<XWPFParagraph> getHeaderParagraphs() {
		if (HEADERS == null) {
			HEADERS = getOnlyTextParagraphs().stream()
					.filter(par -> HeaderParser.HeaderInfo.isHeader(par))
					.collect(Collectors.toList());
		}

		return HEADERS;
	}

	/**
	 * @return List of lists that contains paragraphs of one list
	 */
	public static List<List<XWPFParagraph>> getTestListsParagraphs() {
		if (LISTS == null) {
			List<XWPFParagraph> listTestPars = getDocumentPart(LIST_PART)
					.stream().filter(elem -> elem instanceof XWPFParagraph)
					.map(par -> (XWPFParagraph) par)
					.collect(Collectors.toList());

			LISTS = new ArrayList<List<XWPFParagraph>>();
			for (int i = 0; i < listTestPars.size(); i++) {
				XWPFParagraph par = listTestPars.get(i);
				List<XWPFParagraph> pars = null;
				if (ListParser.isListElement(par)) {
					pars = ListParser.getAtomListParagraphs(par);
				}
				if (pars != null) {
					int size = pars.size();
					if (size > 0) {
						LISTS.add(pars);
						i += size - 1;
					}
				}
			}
		}

		return LISTS;
	}

	/**
	 * @return Set of styles for run
	 */
	public static Set<String> getRunStyles(XWPFRun run) {
		HashSet<String> result = new HashSet<>();
		if (run.isBold())
			result.add(BOLD_TEXT);
		if (run.isItalic())
			result.add(ITALIC_TEXT);
		if (run.getSubscript() == VerticalAlign.SUPERSCRIPT)
			result.add(SUPERSCRIPT_TEXT);
		if (run.getSubscript() == VerticalAlign.SUBSCRIPT)
			result.add(SUBSCRIPT_TEXT);
		if (!(run instanceof XWPFHyperlinkRun)) {
			if (((run.getColor() != null) && !run.getColor().isEmpty())) {
				result.add(COLORED_TEXT);
			}
			if (run.getUnderline() != UnderlinePatterns.NONE) {
				result.add(UNDERLINE_TEXT);
			}
		}

		return (result.isEmpty()) ? null : result;
	}

	/**
	 * @return List of text paragraph from special document part
	 */
	public static List<XWPFParagraph> getTestTextParagraphs() {
		if (SIMPLE_PARAGRAPHS == null) {
			SIMPLE_PARAGRAPHS = getDocumentPart(SIMPLE_TEXT_PART).stream()
					.filter(elem -> elem instanceof XWPFParagraph)
					.map(par -> (XWPFParagraph) par)
					.collect(Collectors.toList());
		}

		return SIMPLE_PARAGRAPHS;
	}

	/**
	 * @return List of hyperlink paragraph from special document part
	 */
	public static List<XWPFParagraph> getTestHyperlinks() {
		if (HYPERLINK_PARAGRAPHS == null) {
			HYPERLINK_PARAGRAPHS = getDocumentPart(HYPERLINKS_PART)
					.stream()
					.filter(elem -> elem instanceof XWPFParagraph)
					.map(par -> (XWPFParagraph) par)
					.filter(par -> par.getRuns().stream()
							.map(run -> run instanceof XWPFHyperlinkRun)
							.collect(Collectors.toSet()).contains(true))
					.collect(Collectors.toList());
		}

		return HYPERLINK_PARAGRAPHS;
	}

	/**
	 * @return Part of document without subparts - only parts
	 */
	private static List<IBodyElement> getDocumentPart(String containedString) {
		XWPFParagraph tableTestHeader = getHeaderParagraphs()
				.stream()
				.filter(header -> (header.getText() != null)
						&& header.getText().toLowerCase()
								.contains(containedString)).findFirst().get();
		int fromHeader = getTestDoc().getPosOfParagraph(tableTestHeader) + 1;
		int toHeader = getTestDoc().getBodyElements().indexOf(
				getHeaderParagraphs().get(
						getHeaderParagraphs().indexOf(tableTestHeader) + 1));
		if (toHeader == -1) {
			toHeader = getTestDoc().getBodyElements().size();
		}

		return getTestDoc().getBodyElements().subList(fromHeader, toHeader);
	}

	// TABLEs
	/**
	 * @return List of tables from special document part
	 */
	public static List<XWPFTable> getTestTables() {
		if (TABLES == null) {
			TABLES = getDocumentPart(TABLE_PART)
					.stream()
					.filter(elem -> (elem instanceof XWPFTable)
							&& isSpecialTable((XWPFTable) elem))
					.map(table -> (XWPFTable) table)
					.collect(Collectors.toList());
		}

		return TABLES;
	}

	/**
	 * @return Count of cells in row for special table from cell
	 */
	public static Integer getSpecialCellCountInRow(XWPFTableRow row) {
		try {
			int count = Integer.parseInt(row.getCell(COLUMN_COUNT_COLUMN)
					.getText());
			if (count > 0) {
				return count;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @return Cell info from special table:<br/>
	 *         <strong>0</strong> - row number<br/>
	 *         <strong>1</strong> - cell number<br/>
	 *         <strong>2</strong> - column span<br/>
	 *         <strong>3</strong> - row span<br/>
	 */
	private static int[] getSpecialCellInfo(XWPFTableCell cell) {
		try {
			return new int[] {
					Integer.parseInt(cell.getText().split(";")[0].split("-")[0]),
					Integer.parseInt(cell.getText().split(";")[0].split("-")[1]),
					Integer.parseInt(cell.getText().split(";")[1].split("-")[0]),
					Integer.parseInt(cell.getText().split(";")[1].split("-")[1]) };
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
		}

		return null;
	}

	/**
	 * @return Count of columns for special table from cell
	 */
	public static Integer getSpecialTableColumnCount(XWPFTable table) {
		return getSpecialCellCountInRow(table.getRow(0));
	}

	/**
	 * @return Count of rows for special table from cell
	 */
	public static Integer getSpecialTableRowCount(XWPFTable table) {
		try {
			int count = Integer.parseInt(table.getRow(0)
					.getCell(ROW_COUNT_COLUMN).getText());
			if (count > 0) {
				return count;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns true, if table from special part of document and has valid
	 * content
	 */
	private static boolean isSpecialTable(XWPFTable table) {
		if (getSpecialTableRowCount(table) != null) {
			for (XWPFTableRow row : table.getRows()) {
				for (int cellNum = 0; cellNum < row.getTableCells().size(); cellNum++) {
					XWPFTableCell cell = row.getTableCells().get(cellNum);
					if (getSpecialCellInfo(cell) == null) {
						if (getSpecialCellCountInRow(row) == null) {
							return false;
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns info about all cell of table, that not merged in column
	 * 
	 * @return rows List, row is cell List, cell is
	 *         {@link TestTools#getSpecialCellInfo(org.apache.poi.xwpf.usermodel.XWPFTableCell)}
	 */
	public static List<List<int[]>> getSpecialTableContentCellsInfo(
			XWPFTable table) {
		ArrayList<List<int[]>> tableInfo = new ArrayList<>();
		for (int rowNum = TestTools.CONTENT_FROM_ROW; rowNum < table.getRows()
				.size(); rowNum++) {
			XWPFTableRow tRow = table.getRow(rowNum);

			ArrayList<int[]> rowInfo = new ArrayList<>();
			for (int cellNum = TestTools.CONTENT_FROM_COLUMN; cellNum < tRow
					.getTableCells().size(); cellNum++) {
				int[] cellInfo = TestTools.getSpecialCellInfo(tRow
						.getCell(cellNum));
				if (cellInfo != null) {
					rowInfo.add(cellInfo);
				}
			}
			tableInfo.add(rowInfo);
		}

		return tableInfo;
	}
}
