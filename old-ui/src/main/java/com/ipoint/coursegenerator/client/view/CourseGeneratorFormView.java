package com.ipoint.coursegenerator.client.view;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteHandler;
import com.github.gwtbootstrap.client.ui.HelpBlock;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.aria.client.PressedValue;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.UmbrellaException;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.ipoint.coursegenerator.client.presenter.CourseGeneratorFormPresenter;
import com.ipoint.coursegenerator.client.presenter.FileSelectUIHandler;

public class CourseGeneratorFormView extends
	ViewWithUiHandlers<FileSelectUIHandler> implements
	CourseGeneratorFormPresenter.MyView, ChangeHandler,
	SubmitCompleteHandler {

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
    Hidden uuidFileName;

    @UiField
    Hidden sourceFileName;

    @UiField
    ProgressBar fileUploadProgressBar;
    
    @UiField
    HelpBlock helpBlock;
    
    @UiField
    ControlGroup fileUploadControlGroup;
    
    @UiField
    ProgressBar generateProgressBar;

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
	generateProgressBar.setVisible(true);	
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
		&& (sourceDocFile.getFilename().toLowerCase().endsWith(".doc") || sourceDocFile
			.getFilename().toLowerCase().endsWith(".docx"))) {
	    sourceDocUploadForm.submit();
	    fileUploadProgressBar.setColor(ProgressBar.Color.DEFAULT);
	    fileUploadProgressBar.setType(ProgressBar.Style.ANIMATED);
	    fileUploadProgressBar.setVisible(true);
	    fileUploadControlGroup.setType(ControlGroupType.NONE);
	    helpBlock.setVisible(false);
	} else {
	    Window.alert("Выберите документ MS Word (файл с расширением doc или docx)!");
	}
    }

    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
	String body = event.getResults();
	fileUploadProgressBar.setType(ProgressBar.Style.DEFAULT);
	helpBlock.setVisible(true);
	try {
	    JSONObject response = (JSONObject) JSONParser.parseStrict(body);
	    uuidFileName.setValue(((JSONString) response.get("uuidFileName"))
		    .stringValue());
	    sourceFileName.setValue(((JSONString) response
		    .get("sourceFileName")).stringValue());
	    fileUploadProgressBar.setColor(ProgressBar.Color.SUCCESS);	    
	    fileUploadControlGroup.setType(ControlGroupType.SUCCESS);
	    helpBlock.setText("Файл успешно загружен!");
	} catch (UmbrellaException e) {
	    helpBlock.setText("Произошла ошибка при загрузке файла!");
	    fileUploadProgressBar.setColor(ProgressBar.Color.DANGER);
	    fileUploadControlGroup.setType(ControlGroupType.ERROR);
	}
	fileUploadProgressBar.setVisible(false);
    }

    @Override
    public String getSourceDocFileUuid() {
	return uuidFileName.getValue();
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
	if (useFilenameAsCourseName.getValue()) {
	    return sourceFileName.getValue().substring(0,
		    sourceFileName.getValue().lastIndexOf('.'));
	}
	return courseName.getValue();
    }

    @Override
    public String getFileType() {
	return sourceFileName.getValue().substring(
		sourceFileName.getValue().lastIndexOf('.'));
    }

    @Override
    public void setGenerateProgressBarCompleted() {
	generateProgressBar.setType(ProgressBar.Style.DEFAULT);
	generateProgressBar.setColor(ProgressBar.Color.SUCCESS);
    }
    
    @Override
    public void setGenerateProgressBarFailed() {
	generateProgressBar.setType(ProgressBar.Style.DEFAULT);
	generateProgressBar.setColor(ProgressBar.Color.DANGER);
    }

}
