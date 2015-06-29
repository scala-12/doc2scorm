package com.ipoint.coursegenerator.core.internalCourse.Course;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//TODO: rename class, because it is document, and write constructor
/**
 * Model of document
 * 
 * @author Kalashnikov Vladislav
 *
 */
public class Course extends CourseTree {

	/**
	 * Name of course
	 */
	private String title;

	// TODO: methods for creating the manifest and delete manifest as field
	private String manifest;

	/**
	 * Returns name of document
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Change course name
	 * 
	 * @param title
	 *            Name of course
	 * @return If name is not null, then true
	 */
	public boolean setTitle(String title) {
		this.title = (title == null) ? null : (title.isEmpty() ? null : title);
		return (title == null) ? false : (!title.isEmpty());
	}

	public String getManifest() {
		return manifest;
	}

	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

	private void tree(List<CourseTreeItem> tree, String path, int partNum) {
 		for (CourseTreeItem item : tree) {
 			Document html = null;
 			
 			if (item.getPage() != null) {
 				html = createNewHTMLDocument();
 				Element body = item.getPage().toHtml(html);
 				html.getFirstChild().appendChild(body);
 				Element header = html.createElement("h1");
 				body.appendChild(header);
 				header.setTextContent(item.getTitle());
 			}
 			
 			String fileName = "part_".concat(String.valueOf(partNum));

			if (item.getCourseTree() != null) {
				if (!item.getCourseTree().isEmpty()) {
					tree(item.getCourseTree(), path.concat(fileName).concat(File.separator), partNum + 1);	//to next level of header
				}
			}
			
			//TODO: add html in zip
		}
	}

	public Document toHtml() {
		List<CourseTreeItem> tree = this.getCourseTree();
		this.tree(tree, File.separator, 1);	// 1 is a number of part

		return null;
	}

	private static Document createNewHTMLDocument() {
		try {
			Document html = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			html.appendChild(html.createElement("html"));
			html.getFirstChild().appendChild(html.createElement("head"));
			return html;
		} catch (ParserConfigurationException e) {
		}
		return null;
	}

}
