package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.UUID;

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

public class Tools {

	abstract public static class Pair<LT, RT> implements Comparable<Pair<LT, RT>> {
		protected final LT left;
		protected final RT right;

		public Pair(LT left, RT right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public int compareTo(final Pair<LT, RT> other) {
			int leftCompare = 0;
			if (this.left != null) {
				leftCompare += this.left.hashCode();
			}
			if (other.left != null) {
				leftCompare -= other.left.hashCode();
			}

			int rigthCompare = 0;
			if (this.right != null) {
				rigthCompare += this.right.hashCode();
			}

			if (other.right != null) {
				rigthCompare -= other.right.hashCode();
			}

			return leftCompare ^ rigthCompare;
		}

		@Override
		public boolean equals(Object obj) {
			if ((null != obj) && (obj instanceof Pair)) {
				Pair<?, ?> other = (Pair<?, ?>) obj;
				return ((this.left == other.left)
						|| ((null != this.left) && (null != other.left) && this.left.equals(other.left)))
						&& ((this.right == other.right)
								|| ((null != this.right) && (null != other.right) && this.right.equals(other.right)));
			}

			return false;
		}

		@Override
		public int hashCode() {
			return (this.left == null ? 0 : this.left.hashCode()) ^ (this.right == null ? 0 : this.right.hashCode());
		}

		@Override
		public String toString() {
			return new StringBuilder().append('(').append(this.left).append(',').append(this.right).append(')')
					.toString();
		}
	}

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

	public static String getValidFileName(String notValid) {
		String validStr = notValid.replaceAll("(\\&#034;)|(\\&#39;)|[^\\w\\d]", "_").replaceAll("(^_+)|(_+$)", " ")
				.trim().replaceAll("__+", "_");

		return (validStr.isEmpty()) ? String.valueOf(notValid.hashCode()) : validStr;
	}

	public static String removeAllSpecialSymbols(String text) {
		return removeSpecialSymbols(removeSpecialSymbolsFromHtml(text));
	}

	public static String removeSpecialSymbolsFromHtml(String html) {
		return (html == null) ? null
				: replaceStrings(html,
						new String[][] { { "&amp;", " " }, { "&lt;", " " }, { "&gt;", " " }, { "&quot;", " " },
								{ "&#39;", " " }, { "<br/>", " " }, { "&#034;", " " }, { "&#047;", " " },
								{ "&#092;", " " } });
	}

	public static String removeSpecialSymbols(String text) {
		return (text == null) ? null
				: replaceStrings(text, new String[][] { { "&", " " }, { "<", " " }, { ">", " " }, { "\"", " " },
						{ "'", " " }, { "\n", " " } });
	}

	private static String replaceStrings(String s, String[][] sourceTarget) {
		for (int i = 0; i < sourceTarget.length; i++) {
			s = s.replaceAll(sourceTarget[i][0], sourceTarget[i][1]);
		}

		return s;
	}

	public static String removeExtraSpaces(String str) {
		return str.replaceAll("\\s\\s+", " ").trim();
	}

	public static String generateSystemName(String name) {
		return Tools.getValidFileName(Tools.removeAllSpecialSymbols(
				TransliterationTool.convertRU2ENString(name) + '_' + UUID.randomUUID().toString()));
	}

}
