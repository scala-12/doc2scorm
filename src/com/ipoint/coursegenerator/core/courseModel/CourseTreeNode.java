package com.ipoint.coursegenerator.core.courseModel;

/**
 * Node of tree. These may includes one page.
 *
 * @author Kalashnikov Vladislav
 * @see AbstractTreeNode
 */
public class CourseTreeNode extends AbstractTreeNode {

    private AbstractCourseTreeNodeContent page;

    private String title;

    /**
     * Create course node
     *
     * @param page  Page of node
     * @param title Title of node. These can't be null.
     */
    public CourseTreeNode(TheoryContent page, String title) {
        super();
        if (!this.setTitle(title)) {
            // TODO:exception
        }
        this.setPage(page);
    }

    /**
     * @see CourseTreeNode#CourseTreeNode(TheoryContent, String)
     */
    public CourseTreeNode(String title) {
        this(null, title);
    }

    public AbstractCourseTreeNodeContent getPage() {
        return this.page;
    }

    public String getTitle() {
        return this.title;
    }

    /**
     * Set page title
     *
     * @param title New title of page. If it is not string with chars then return
     *              false
     * @return If successful then false
     */
    public boolean setTitle(String title) {
        if ((title != null) && !title.isEmpty()) {
            this.title = title;

            return true;
        }

        return false;
    }

    public void setPage(AbstractCourseTreeNodeContent page) {
        if ((page != null) && ((page.getParent() != this) || (page.getParent() == null))) {
            // because class TheoryContent have call of this method
            // method that below need for set link between page and node
            page.setParent(this);
        }

        if ((this.page != page) && (this.page != null) && (page != null)) {
            this.page.setParent(null);
        }

        this.page = page;
    }

}
