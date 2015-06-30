package com.ipoint.coursegenerator.core.elementparser;

import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.imsproject.xsd.imscpRootv1P1P2.ItemType;
import org.imsproject.xsd.imscpRootv1P1P2.ResourceType;

public class ItemInfo {
	private ItemType item;
	private XmlComplexContentImpl parentItem;
	private ResourceType resource;
	private int styleId;
	private String directoryPath;
	private String htmlPath;
	private String path;
	private String url;
	private String filename;

	public ItemInfo(ItemType item, int styleId, String directoryPath,
			String url, String filename) {
		this.item = item;
		this.styleId = styleId;
		this.directoryPath = directoryPath;
		this.url = url;
		this.filename = filename;
	}

	public ItemType getItem() {
		return item;
	}

	public int getStyleId() {
		return styleId;
	}

	public void setItem(ItemType item) {
		this.item = item;
	}

	public void setStyleId(int styleId) {
		this.styleId = styleId;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getHtmlPath() {
		return htmlPath;
	}

	public void setHtmlPath(String htmlPath) {
		this.htmlPath = htmlPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ResourceType getResource() {
		return resource;
	}

	public void setResource(ResourceType resource) {
		this.resource = resource;
	}

	public XmlComplexContentImpl getParentItem() {
		return parentItem;
	}

	public void setParentItem(XmlComplexContentImpl parentItem) {
		this.parentItem = parentItem;
	}
}