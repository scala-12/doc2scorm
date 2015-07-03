package com.ipoint.coursegenerator.core.utils.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.imsproject.xsd.imscpRootv1P1P2.FileType;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;
import org.w3c.dom.Node;

public class ResourcesProcessor {

	public ResourcesProcessor() {
		super();
	}

	public void createResources(ManifestType manifest) {
		manifest.addNewResources();
	}

	/**
	 * Create resourse in manifest
	 * @param manifest Manifest
	 * @param path Path in course
	 * @param resourseId Id of resource
	 * @return Added in manifest resource
	 */
	public static ResourceType createResource(ManifestType manifest,
			String path, String resourseId) {
		if (manifest.getResources() == null
				|| manifest.getResources().getResourceArray() == null) {
			manifest.addNewResources();
		}
		ResourceType resource = manifest.getResources().addNewResource();
		resource.setIdentifier(resourseId);
		resource.setType("webcontent");
		Node attNode = resource
				.getDomNode()
				.getOwnerDocument()
				.createAttributeNS("http://www.adlnet.org/xsd/adlcp_rootv1p2",
						"scormType");
		attNode.setNodeValue("sco");
		resource.getDomNode().getAttributes().setNamedItem(attNode);
		resource.setHref(path.replace(File.separatorChar, '/'));
		resource.addNewFile().setHref(path.replace(File.separatorChar, '/'));
		
		return resource;
	}

	public static void addFilesToResource(String htmlPath,
			ResourceType resource, List<String> pathToResources) {
		for (String path : pathToResources) {
			FileType fileType = resource.addNewFile();
			fileType.setHref(htmlPath.concat(path).replace(File.separatorChar, '/'));
		}
	}

	public static void removeResource(ManifestType manifest,
			ResourceType resource) {
		int i = manifest.getResources().getResourceArray().length;
		for (i = 0; i < manifest.getResources().getResourceArray().length; i++) {
			if (manifest.getResources().getResourceArray(i).getIdentifier()
					.equals(resource.getIdentifier())) {
				break;
			}
		}
		if (i < manifest.getResources().getResourceArray().length) {
			manifest.getResources().removeResource(i);
		}
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
	
	public static String getResourceHrefForItem(ManifestType manifest,
			ItemType item) {
		String href = null;
		for (int i = 0; i < manifest.getResources().sizeOfResourceArray(); i++) {
			if (manifest.getResources().getResourceArray()[i].getIdentifier()
					.equals(item.getIdentifierref())) {
				href = manifest.getResources().getResourceArray()[i].getHref();
				break;
			}
		}
		return href;
	}
	
}
