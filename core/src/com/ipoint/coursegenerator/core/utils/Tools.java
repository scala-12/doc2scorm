package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.ipoint.coursegenerator.core.courseModel.content.PictureInfo;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.AbstractBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.TableItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.tabular.cell.CellBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.AbstractTextualBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.list.ListItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphBlock;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.ParagraphItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.AbstractContentItem;
import com.ipoint.coursegenerator.core.courseModel.content.blocks.paragraphs.textual.paragraph.content.items.ImageContentItem;

public class Tools {

	private static Transformer TRANSFORMER = _getTransformer();

	private static Transformer _getTransformer() {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			return transformer;
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] convertStream2ByteArray(InputStream stream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n;
		while ((n = stream.read(buf)) >= 0) {
			baos.write(buf, 0, n);
		}

		return baos.toByteArray();
	}

	public static Document createNewHTMLDocument() {
		Document html = createEmptyDocument();
		html.appendChild(html.createElement("html"));
		html.getFirstChild().appendChild(html.createElement("head"));
		html.getFirstChild().appendChild(html.createElement("body"));

		return html;
	}

	public static Document createEmptyDocument() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<PictureInfo> getImagesOfParagraph(ParagraphBlock paragraph) {
		ArrayList<PictureInfo> images = new ArrayList<PictureInfo>();

		for (ParagraphItem parItem : paragraph.getItems()) {
			for (AbstractContentItem<?> item : parItem.getValue().getItems()) {
				if (item instanceof ImageContentItem) {
					ImageContentItem imageItem = (ImageContentItem) item;
					PictureInfo image = new PictureInfo(imageItem.getImageFullName(), imageItem.getValue());
					images.add(image);
				}
			}
		}

		return images;
	}

	public static Set<PictureInfo> getImagesRecursive(List<AbstractBlock<?>> blocks) {
		HashSet<PictureInfo> images = new HashSet<>();

		for (AbstractBlock<?> block : blocks) {
			images.addAll(getImagesRecursive(block));
		}

		return images;
	}

	public static Set<PictureInfo> getImagesRecursive(AbstractBlock<?> block) {
		HashSet<PictureInfo> images = new HashSet<>();

		if (block instanceof AbstractTextualBlock) {
			if (block instanceof ParagraphBlock) {
				images.addAll(getImagesOfParagraph((ParagraphBlock) block));
			} else if (block instanceof ListBlock) {
				for (ListItem listItem : ((ListBlock) block).getItems()) {
					if (listItem.getValue() instanceof ParagraphBlock) {
						images.addAll(getImagesOfParagraph((ParagraphBlock) listItem.getValue()));
					} else if (listItem.getValue() instanceof ListBlock) {
						images.addAll(getImagesRecursive(listItem.getValue()));
					}
				}
			}
		} else if (block instanceof TableBlock) {
			for (TableItem row : ((TableBlock) block).getItems()) {
				for (CellBlock cell : row.getValue()) {
					if (cell.getFirstItem().getValue() != null) {
						images.addAll(
								getImagesRecursive(new ArrayList<AbstractBlock<?>>(cell.getFirstItem().getValue())));
					}
				}
			}
		}

		return images;
	}

	public static Node getElementById(Node parent, String id) {
		Node result = null;
		for (int i = 0; (result == null) && (i < parent.getChildNodes().getLength()); i++) {
			Node node = parent.getChildNodes().item(i);
			if (node.hasAttributes() && id.equals(node.getAttributes().getNamedItem("id").getNodeValue())) {
				result = node;
			}
		}

		if (result == null) {
			for (int i = 0; (result == null) && (i < parent.getChildNodes().getLength()); i++) {
				Node node = parent.getChildNodes().item(i);
				if (node.hasChildNodes()) {
					result = getElementById(node, id);
				}
			}
		}

		return result;
	}

	public static String getNodeString(Node node) {
		if (node instanceof Text) {

			return node.getTextContent();
		} else {
			try {
				StringWriter writer = new StringWriter();
				TRANSFORMER.transform(new DOMSource(node), new StreamResult(writer));
				String output = writer.toString();

				return (output.indexOf(">") < output.indexOf("?>")) ? output.substring(output.indexOf("?>") + 2)
						: output;
			} catch (TransformerException e) {
				e.printStackTrace();

				return null;
			}
		}
	}

}
