package com.ipoint.coursegenerator.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.svg.SvgGdiException;
import net.arnx.wmf2svg.gdi.wmf.WmfParseException;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Document;

public class ImageFormatConverter {

    private static Transformer getTransformer() throws TransformerException {
	Transformer transformer = null;
	transformer = TransformerFactory.newInstance().newTransformer();

	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	return transformer;
    }

    public static byte[] transcodeWMFtoPNG(byte[] data, int width, int height) {
	WmfParser parser = new WmfParser();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    final SvgGdi gdi = new SvgGdi(false);
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    parser.parse(in, gdi);
	    Document doc = gdi.getDocument();
	    StreamResult sr = new StreamResult(out);
	    getTransformer().transform(new DOMSource(doc), sr);
	    TranscoderInput input = new TranscoderInput(
		    new ByteArrayInputStream(out.toByteArray()));
	    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
	    TranscoderOutput output = new TranscoderOutput(out1);
	    PNGTranscoder transcoder = new PNGTranscoder();
	    Map<TranscodingHints.Key, Object> hints = new HashMap<TranscodingHints.Key, Object>();
	    hints.put(PNGTranscoder.KEY_WIDTH, new Float(width));
	    hints.put(PNGTranscoder.KEY_HEIGHT, new Float(height));
	    hints.put(PNGTranscoder.KEY_XML_PARSER_VALIDATING, new Boolean(false));
	    transcoder.setTranscodingHints(hints);
	    transcoder.transcode(input, output);
	    return out1.toByteArray();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (WmfParseException e) {
	    e.printStackTrace();
	} catch (SvgGdiException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (TranscoderException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static byte[] transcodeWMFtoPNG(byte[] data) {
	WmfParser parser = new WmfParser();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    final SvgGdi gdi = new SvgGdi(false);
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    parser.parse(in, gdi);
	    Document doc = gdi.getDocument();
	    StreamResult sr = new StreamResult(out);
	    getTransformer().transform(new DOMSource(doc), sr);
	    TranscoderInput input = new TranscoderInput(
		    new ByteArrayInputStream(out.toByteArray()));
	    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
	    TranscoderOutput output = new TranscoderOutput(out1);
	    PNGTranscoder transcoder = new PNGTranscoder();
	    transcoder.transcode(input, output);
	    return out1.toByteArray();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (WmfParseException e) {
	    e.printStackTrace();
	} catch (SvgGdiException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (TranscoderException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
