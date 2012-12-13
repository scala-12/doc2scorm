package com.ipoint.coursegenerator.core.elementparser;

public class HeaderInfo {
    private String headerText = "";
    private int headerStyleID = 0;
    private boolean isFirstHeader = true;
    private int headerLevelNumber;
    
    private int previousParStyleID = 0;
    private int nextParStyleID = 0;

    private String templateDir;

    public HeaderInfo(int headerLevelNumber) {
	this.setHeaderLevelNumber(headerLevelNumber);
    }
    
    public String getHeaderText() {
	return headerText;
    }

    public void setHeaderText(String headerText) {
	this.headerText = headerText;
    }

    public int getHeaderStyleID() {
	return headerStyleID;
    }

    public void setHeaderStyleID(int headerStyleID) {
	this.headerStyleID = headerStyleID;
    }

    public boolean isFirstHeader() {
	return isFirstHeader;
    }

    public void setFirstHeader(boolean isFirstHeader) {
	this.isFirstHeader = isFirstHeader;
    }

    public int getHeaderLevelNumber() {
	return headerLevelNumber;
    }

    public void setHeaderLevelNumber(int headerLevelNumber) {
	this.headerLevelNumber = headerLevelNumber;
    }

    public int getPreviousParStyleID() {
        return previousParStyleID;
    }

    public void setPreviousParStyleID(int previousParStyleID) {
        this.previousParStyleID = previousParStyleID;
    }

    public int getNextParStyleID() {
        return nextParStyleID;
    }

    public void setNextParStyleID(int nextParStyleID) {
        this.nextParStyleID = nextParStyleID;
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }
}
