package com.ipoint.coursegenerator.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFPanel;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.util.io.Tag;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.svg.SvgGdiException;
import net.arnx.wmf2svg.gdi.wmf.WmfParseException;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;

public class ImageFormatConverter {

	private final static String SOFFICE_CONVERSION_KEYS = "--invisible --convert-to svg --outdir";

	private final static String SOFFICE_PROGRAM = "soffice";

	private final static long EMF_CONVERSION_TIMEOUT = 15L;

	private final static Logger LOG = Logger.getLogger(ImageFormatConverter.class.getName());

	private final static DocumentBuilderFactory DOC_BUILD_FACTORY = DocumentBuilderFactory.newInstance();

	private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

	private static File pathToOffice = null;

	private static byte[] transcodeSvgToPng(Document svg, Integer width, Integer height) {
		Document opensymbol = GuardianCharacters.getCharactersData();

		if (opensymbol == null) {
			LOG.warning("File \"opensymbol.svg\" is not initialized");
		} else {
			// TODO: what is it?
			Node opensymbolDefs = svg.importNode(opensymbol.getElementsByTagName("defs").item(0), true);
			// if (svg.getElementsByTagName("defs").getLength() != 0) {
			// Node svgDefs = svg.createElement("defs");
			// while (svgDefs.hasChildNodes()) {
			// opensymbolDefs.appendChild(svgDefs.getFirstChild());
			// }
			// svg.removeChild(svgDefs);
			// }
			svg.getDocumentElement().appendChild(opensymbolDefs);

			((Element) svg.getElementsByTagName("g").item(0)).setAttribute("style",
					"font-family:OpenSymbol; fill:black;");
		}

		try {
			NodeList textNodes = svg.getElementsByTagName("text");
			try {
				String nodeText;
				for (int i = 0; i < textNodes.getLength(); i++) {
					Node textNode = textNodes.item(i);
					nodeText = textNode.getTextContent();
					if (nodeText != null) {
						nodeText = nodeText.replaceAll(
								new String(new byte[] { (byte) 0xC2, (byte) 0xB3 }, StandardCharsets.UTF_8), "\u2265");
						nodeText = nodeText.replaceAll(
								new String(new byte[] { (byte) 0xC2, (byte) 0xB6 }, StandardCharsets.UTF_8), "\u2202");
						nodeText = nodeText.replaceAll(
								new String(new byte[] { (byte) 0xC3, (byte) 0xB2 }, StandardCharsets.UTF_8), "\u222b");

						textNode.setTextContent(nodeText);
					}
				}
			} catch (DOMException e) {
				e.printStackTrace();
			}

			TranscoderInput trcoderInput = new TranscoderInput(svg);
			ByteArrayOutputStream pngBOS = new ByteArrayOutputStream();
			TranscoderOutput trcoderOutput = new TranscoderOutput(pngBOS);
			PNGTranscoder transcoder = new PNGTranscoder();

			if (((width != null) && (width > 0)) || ((height != null) && (height > 0))) {
				Map<TranscodingHints.Key, Object> hints = new HashMap<TranscodingHints.Key, Object>();
				if ((width != null) && (width > 0)) {
					hints.put(PNGTranscoder.KEY_WIDTH, Float.valueOf(width));
				}
				if ((height != null) && (height > 0)) {
					hints.put(PNGTranscoder.KEY_HEIGHT, Float.valueOf(height));
				}
				hints.put(PNGTranscoder.KEY_XML_PARSER_VALIDATING, new Boolean(false));
				transcoder.setTranscodingHints(hints);
			}
			transcoder.transcode(trcoderInput, trcoderOutput);
			pngBOS.flush();
			pngBOS.close();

			return pngBOS.toByteArray();
		} catch (TranscoderException e) {
			LOG.warning("SVG can't be converted to PNG");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Node getEmfNodeWithAllTextNodes(byte[] emfBytes) {
		ByteArrayInputStream emfBIS = new ByteArrayInputStream(emfBytes);
		EMFInputStream emfIS = new EMFInputStream(emfBIS);
		ByteArrayInputStream emfPartBIS = null;

		try {
			Tag emfTag;
			StringBuilder emfAsStr = new StringBuilder();
			String emfTagStr = null;
			String emfTagVal = null;

			emfAsStr.append("<emfPart>");
			// searching of text nodes with some attributes: bounds, string, pos
			while ((emfTag = emfIS.readTag()) != null) {
				if ("ExtTextOutW".equalsIgnoreCase(emfTag.getName())) {
					emfTagStr = emfTag.toString().toLowerCase();
					emfAsStr.append("<").append("text").append(" ");
					int attStart;
					if ((attStart = emfTagStr.indexOf("bounds")) != -1) {
						attStart += emfTagStr.substring(attStart).indexOf("[") + 1;
						emfTagVal = emfTagStr.substring(attStart,
								emfTagStr.substring(attStart).indexOf("]") + attStart);
						int sepPos;
						while (((sepPos = (emfTagVal.indexOf(","))) != -1) || (!emfTagVal.isEmpty())) {
							int eqPos = emfTagVal.indexOf("=");
							if (sepPos == -1) {
								sepPos = emfTagVal.length();
							}
							emfAsStr.append("bounds-").append(emfTagVal.substring(0, eqPos)).append("=\"")
									.append(emfTagVal.substring(eqPos + 1, sepPos)).append("\" ");
							emfTagVal = emfTagVal.substring((sepPos == emfTagVal.length()) ? sepPos : sepPos + 1);
						}
					}
					if ((attStart = emfTagStr.indexOf("string")) != -1) {
						attStart += "string: ".length();
						emfAsStr.append("string=\"").append(emfTag.toString().substring(attStart,
								emfTagStr.substring(attStart).indexOf("\n") + attStart)).append("\" ");
					}
					if ((attStart = emfTagStr.indexOf("pos")) != -1) {
						attStart += emfTagStr.substring(attStart).indexOf("[") + 1;
						emfTagVal = emfTagStr.substring(attStart,
								emfTagStr.substring(attStart).indexOf("]") + attStart);
						int sepPos;
						while (((sepPos = (emfTagVal.indexOf(","))) != -1) || (!emfTagVal.isEmpty())) {
							int eqPos = emfTagVal.indexOf("=");
							if (sepPos == -1) {
								sepPos = emfTagVal.length();
							}
							emfAsStr.append("pos-").append(emfTagVal.substring(0, eqPos)).append("=\"")
									.append(emfTagVal.substring(eqPos + 1, sepPos)).append("\" ");
							emfTagVal = emfTagVal.substring((sepPos == emfTagVal.length()) ? sepPos : sepPos + 1);
						}
					}
					emfAsStr.append("/>");
				}
			}

			emfAsStr.append("</emfPart>");
			emfPartBIS = new ByteArrayInputStream(emfAsStr.toString().getBytes());
			DocumentBuilder builder = DOC_BUILD_FACTORY.newDocumentBuilder();

			return builder.parse(emfPartBIS).getElementsByTagName("emfPart").item(0);
		} catch (IOException | SAXException e) {
			// EMF can't be converted in document
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				emfBIS.close();
				emfIS.close();
				if (emfPartBIS != null) {
					emfPartBIS.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private static void repairSvgTextPosition(Document svg, Node emfDocPart) {
		if ((svg != null) && (emfDocPart != null)) {
			NodeList svgTextNodes = svg.getElementsByTagName("text");

			for (int i = 0; i < svgTextNodes.getLength(); ++i) {
				Node svgNode = svgTextNodes.item(i);

				if (svgNode.hasAttributes()) {
					Node emfNode = emfDocPart.getFirstChild();
					boolean isEqual = false;
					while (!isEqual && (emfNode != null)) {
						if (svgNode.getTextContent()
								.equals(emfNode.getAttributes().getNamedItem("string").getNodeValue())) {
							isEqual = true;
						} else {
							emfNode = emfNode.getNextSibling();
						}
					}

					if (isEqual) {
						if (((svgNode.getAttributes().getNamedItem("x") != null)
								&& svgNode.getAttributes().getNamedItem("x").getNodeValue().equals("0"))
								|| (svgNode.getAttributes().getNamedItem("x") == null)) {
							float posX = 0;
							if (emfNode.getAttributes().getNamedItem("bounds-x") != null) {
								posX = Float
										.parseFloat((emfNode.getAttributes().getNamedItem("bounds-x").getNodeValue()));
							}
							if (emfNode.getAttributes().getNamedItem("pos-x") != null) {
								posX -= Float.parseFloat(emfNode.getAttributes().getNamedItem("pos-x").getNodeValue());
							}

							svgNode.getAttributes().getNamedItem("x").setNodeValue(String.valueOf(posX));
						}
						if (((svgNode.getAttributes().getNamedItem("y") != null)
								&& svgNode.getAttributes().getNamedItem("y").getNodeValue().equals("0"))
								|| (svgNode.getAttributes().getNamedItem("y") == null)) {
							float posY = 0;
							if (emfNode.getAttributes().getNamedItem("bounds-y") != null) {
								posY = Float
										.parseFloat((emfNode.getAttributes().getNamedItem("bounds-y").getNodeValue()));
							}

							svgNode.getAttributes().getNamedItem("y").setNodeValue(String.valueOf(posY));
						}

						// if repaired then don't use this node in future
						emfDocPart.removeChild(emfNode);
					}
				}
			}
		}
	}

	public static byte[] transcodeWmfToPng(byte[] data) {
		return transcodeWmfToPng(data, null, null);
	}

	public static byte[] transcodeWmfToPng(byte[] data, int width, int height) {
		return transcodeWmfToPng(data, Integer.valueOf(width), Integer.valueOf(height));
	}

	private static byte[] transcodeWmfToPng(byte[] data, Integer width, Integer height) {
		Document svgData = null;
		if (hasOfficeConverter()) {
			svgData = transcodeImgToSvg(data, "wmf");
		} else {
			WmfParser parser = new WmfParser();
			try {
				final SvgGdi gdi = new SvgGdi(false);
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				parser.parse(in, gdi);

				Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
				ByteArrayOutputStream svgBOS = new ByteArrayOutputStream();
				StreamResult convertedSvgSR = new StreamResult(svgBOS);
				transformer.transform(new DOMSource(gdi.getDocument()), convertedSvgSR);
				svgBOS.close();
				ByteArrayInputStream svgBIS = new ByteArrayInputStream(svgBOS.toByteArray());

				String docParser = XMLResourceDescriptor.getXMLParserClassName();
				svgData = new SAXSVGDocumentFactory(docParser).createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI,
						svgBIS);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SvgGdiException | WmfParseException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}

		return transcodeSvgToPng(svgData, width, height);
	}

	public static byte[] transcodeEmfToPng(byte[] data) {
		return transcodeEmfToPng(data, null, null);
	}

	public static byte[] transcodeEmfToPng(byte[] data, int width, int height) {
		return transcodeEmfToPng(data, Integer.valueOf(width), Integer.valueOf(height));
	}

	private static byte[] transcodeEmfToPng(byte[] data, Integer width, Integer height) {
		Document svgData = null;
		if (hasOfficeConverter()) {
			svgData = transcodeImgToSvg(data, "emf");
		} else {
			ByteArrayInputStream emfBIS = new ByteArrayInputStream(data);
			EMFInputStream emfIS = new EMFInputStream(emfBIS);

			try {
				EMFRenderer emfRenderer = null;

				emfRenderer = new EMFRenderer(emfIS);
				EMFPanel emfPanel = new EMFPanel();
				emfPanel.setRenderer(emfRenderer);

				Properties props = new Properties();
				props.put(SVGGraphics2D.EMBED_FONTS, Boolean.toString(false));
				props.put(SVGGraphics2D.CLIP, Boolean.toString(false));
				props.put(SVGGraphics2D.COMPRESS, Boolean.toString(false));
				props.put(SVGGraphics2D.TEXT_AS_SHAPES, Boolean.toString(false));
				props.put(SVGGraphics2D.STYLABLE, Boolean.toString(false));

				ByteArrayOutputStream svgBOS = new ByteArrayOutputStream();

				// prepare Graphics2D
				SVGGraphics2D graphics2D = new SVGGraphics2D(svgBOS, emfPanel);
				graphics2D.setProperties(props);
				graphics2D.setDeviceIndependent(true);
				graphics2D.startExport();
				emfPanel.paint(graphics2D);
				graphics2D.endExport();

				try {
					svgBOS.flush();
					svgBOS.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				ByteArrayInputStream svgBytes = new ByteArrayInputStream(svgBOS.toByteArray());
				String parser = XMLResourceDescriptor.getXMLParserClassName();
				svgData = new SAXSVGDocumentFactory(parser).createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI,
						svgBytes);
				svgBytes.close();
				repairSvgTextPosition(svgData, getEmfNodeWithAllTextNodes(data));
			} catch (IOException e1) {
				LOG.warning("Input data can't be rendered to EMF");
				e1.printStackTrace();
			} finally {
				try {
					emfIS.close();
					emfBIS.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return transcodeSvgToPng(svgData, width, height);
	}

	private static Document transcodeImgToSvg(byte[] data, String extension) {
		if (hasOfficeConverter()) {
			File svgFile = null;
			File imgFile = null;
			try {
				String fileName = UUID.randomUUID().toString();
				imgFile = File.createTempFile("img", fileName + "." + extension);
				svgFile = new File(imgFile.getParentFile(),
						imgFile.getName().substring(0, imgFile.getName().length() - extension.length() - 1) + ".svg");
				svgFile.createNewFile();

				try {
					FileOutputStream imgOS = new FileOutputStream(imgFile);
					BufferedOutputStream imgBufOS = new BufferedOutputStream(imgOS);
					imgBufOS.write(data);
					imgBufOS.close();
					imgOS.close();

					String cmd = '"' + getPathToOffice().getAbsolutePath() + File.separatorChar + SOFFICE_PROGRAM
							+ "\" " + SOFFICE_CONVERSION_KEYS + " \"" + svgFile.getParentFile().getAbsolutePath()
							+ "\" \"" + imgFile.getAbsolutePath() + '"';
					Process proc = Runtime.getRuntime().exec(cmd.toString());

					if (proc.waitFor(EMF_CONVERSION_TIMEOUT, TimeUnit.SECONDS)) {
						FileInputStream svgIS = new FileInputStream(svgFile);
						BufferedInputStream svgBufIS = new BufferedInputStream(svgIS);
						String parser = XMLResourceDescriptor.getXMLParserClassName();
						Document svg = new SAXSVGDocumentFactory(parser)
								.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, svgIS);
						svgBufIS.close();
						svgIS.close();

						return svg;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					imgFile.delete();
					svgFile.delete();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return null;

	}

	public static File getPathToOffice() {
		return pathToOffice;
	}

	public static void setPathToOffice(String path) {
		pathToOffice = new File(path);
	}

	public static boolean hasOfficeConverter() {
		return (getPathToOffice() != null) && getPathToOffice().exists()
				&& (new File(getPathToOffice(), SOFFICE_PROGRAM).exists()
						|| new File(getPathToOffice(), SOFFICE_PROGRAM + ".exe").exists());
	}

}
