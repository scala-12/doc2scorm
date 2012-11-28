package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteHandler;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorFormPresenter;
import com.ipoint.coursegenerator.client.presenter.FileSelectUIHandler;
import com.ipoint.coursegenerator.shared.GenerateCourse;

public class CourseGeneratorFormView extends
	ViewWithUiHandlers<FileSelectUIHandler> implements
	CourseGeneratorFormPresenter.MyView, ChangeHandler, SubmitCompleteHandler {

    private final Widget widget;

    @UiField
    ListBox headerLevel;

    @UiField
    FileUpload sourceDocFile;

    @UiField
    ListBox templateForCoursePages;

    @UiField
    TextBox courseName;

    @UiField
    CheckBox useFilenameAsCourseName;

    @UiField
    Form sourceDocUploadForm;
    
    @UiField
    Hidden fileUuid;

    public interface Binder extends UiBinder<Widget, CourseGeneratorFormView> {
    }

    @Inject
    public CourseGeneratorFormView(final Binder binder) {
	widget = binder.createAndBindUi(this);
	sourceDocUploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
    }

    @Override
    public Widget asWidget() {
	return widget;
    }

    @UiHandler("useFilenameAsCourseName")
    public void onValueChanged(ValueChangeEvent<Boolean> event) {
	if (useFilenameAsCourseName.getValue()) {
	    courseName.setEnabled(false);
	} else {
	    courseName.setEnabled(true);
	}
    }
    
    @UiHandler("generateButton")
    public void onClicked(ClickEvent event) {	
	getUiHandlers().generateButtonClicked();
    }

    @Override
    public void setUiHandlers(FileSelectUIHandler uiHandlers) {
	super.setUiHandlers(uiHandlers);
	sourceDocFile.addChangeHandler(this);
	sourceDocUploadForm.addSubmitCompleteHandler(this);	
    }

    @Override
    public void onChange(ChangeEvent event) {
	if (sourceDocFile.getFilename() != null 
		&& (sourceDocFile.getFilename().toLowerCase().endsWith(".doc")
		|| sourceDocFile.getFilename().toLowerCase().endsWith(".docx"))) {
	    sourceDocUploadForm.submit();
	} else {
	    Window.alert("Выберите документ MS Word (файл с расширением doc или docx)!");
	}
    }

    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
	String body = event.getResults();
	fileUuid.setValue(body);
    }

    @Override
    public String getSourceDocFileUuid() {
	return fileUuid.getValue();
    }

    @Override
    public String getHeaderLevel() {
	return headerLevel.getValue();
    }

    @Override
    public String getTemplateForCoursePages() {
	return templateForCoursePages.getValue();
    }

    @Override
    public String getCourseName() {
	return courseName.getValue();
    }   
    
}
