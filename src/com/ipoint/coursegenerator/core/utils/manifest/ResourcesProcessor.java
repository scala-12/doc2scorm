package com.ipoint.coursegenerator.core.utils.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TagFindingVisitor;
import org.imsproject.xsd.imscpRootv1P1P2.ManifestType;

public class ResourcesProcessor {

	/**
	 * @param scoFactory
	 * @param courseFactory
	 * @param courseId
	 * @param siteId
	 */
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

	/**
	 * Create Resources for Manifest
	 *
	 * @param manifest
	 * @throws ParserException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void createResources(ManifestType manifest)
			throws UnsupportedEncodingException, FileNotFoundException,
			ParserException {
		manifest.addNewResources();
		/*for (int i = 0; i < scos.size(); i++) {
			manifest.getResources().addNewResource();
		}*/
		//int i = 0;
		//ResourceType[] rts = manifest.getResources().getResourceArray();
		
	}

}
