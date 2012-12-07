package com.ipoint.coursegenerator.core.utils.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.w3c.dom.Node;

public class ResourcesProcessor {


    public ResourcesProcessor() {
	super();
    }

    private TagFindingVisitor getHtmlVisitor(File scoFile)
	    throws UnsupportedEncodingException, FileNotFoundException,
	    ParserException {
	Parser htmlResourceParser = new Parser(new org.htmlparser.lexer.Lexer(
		new org.htmlparser.lexer.Page(new FileInputStream(scoFile),
			"UTF-8")));
	TagFindingVisitor visitor = new TagFindingVisitor(new String[] {
		"SCRIPT", "SCRIPT SRC" });
	htmlResourceParser.visitAllNodesWith(visitor);
	return visitor;
    }
    
    public void createResources(ManifestType manifest) {
	manifest.addNewResources();
    }

    public static void createResource(ManifestType manifest, String path,
	    String iref) {
	if (manifest.getResources() == null
		|| manifest.getResources().getResourceArray() == null) {
	    manifest.addNewResources();
	}
	ResourceType resource = manifest.getResources().addNewResource();
	resource.setIdentifier(iref);
	resource.setType("webcontent");
	Node attNode = resource.getDomNode().getOwnerDocument()
		.createAttributeNS("http://www.adlnet.org/xsd/adlcp_rootv1p2", "scormType");
	attNode.setNodeValue("sco");
	resource.getDomNode().getAttributes().setNamedItem(attNode);
	resource.setHref(path);
	resource.addNewFile().setHref(path);
    }

}
