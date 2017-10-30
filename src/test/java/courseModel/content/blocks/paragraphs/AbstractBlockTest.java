package test.java.courseModel.content.blocks.paragraphs;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;

import test.java.courseModel.ConvertableTest;

public abstract class AbstractBlockTest implements ConvertableTest {
	private Document HTML_DOC;

	protected Document getHtmlDocument() {
		if (HTML_DOC == null) {
			try {
				HTML_DOC = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().newDocument();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

		}

		return HTML_DOC;
	}

	@Override
	@Test
	abstract public void toHtml();
}
