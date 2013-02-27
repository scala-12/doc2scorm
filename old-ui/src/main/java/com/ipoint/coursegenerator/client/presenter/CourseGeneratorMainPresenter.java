package com.ipoint.coursegenerator.client.presenter;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.ipoint.coursegenerator.client.Messages;
import com.ipoint.coursegenerator.client.NameTokens;
import com.ipoint.coursegenerator.client.presenter.uihandlers.MessagesHolder;

public class CourseGeneratorMainPresenter extends
		Presenter<CourseGeneratorMainPresenter.MyView, CourseGeneratorMainPresenter.MyProxy> implements MessagesHolder {

	public interface MyView extends View, HasUiHandlers<MessagesHolder> {
		void showLockingDialog();

		void setUsername(String username);

		void setDaysRemains(int daysRemains);

	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_mainContent = new Type<RevealContentHandler<?>>();

	@ContentSlot
	public static final Type<RevealContentHandler<?>> SLOT_NAVBAR_CONTENT = new Type<RevealContentHandler<?>>();
	
	private final Messages messages;

	@ProxyCodeSplit
	@NameToken(NameTokens.main)
	public interface MyProxy extends ProxyPlace<CourseGeneratorMainPresenter> {
	}

	private PlanChoiceWidgetPresenter planChoiceWidgetPresenter;
	private CourseGeneratorFormPresenter courseGeneratorFormPresenter;

	@Inject
	public CourseGeneratorMainPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
			final PlanChoiceWidgetPresenter planChoiceWidgetPresenter,
			final CourseGeneratorFormPresenter courseGeneratorFormPresenter,
			Messages messages) {
		super(eventBus, view, proxy);
		this.planChoiceWidgetPresenter = planChoiceWidgetPresenter;
		this.courseGeneratorFormPresenter = courseGeneratorFormPresenter;
		this.messages = messages;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().setUiHandlers(this);
		planChoiceWidgetPresenter.setMainPresenter(this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(SLOT_NAVBAR_CONTENT, planChoiceWidgetPresenter);
	}

	public void showLockingDialog() {
		getView().showLockingDialog();
	}

	public void setUsername(String username) {
		getView().setUsername(username);
	}

	public void setDaysRemains(int daysRemains) {
		getView().setDaysRemains(daysRemains);
		if (daysRemains > 0) {
			courseGeneratorFormPresenter.enableGenerateButton();
		}
	}

	@Override
	public Messages getMessages() {
		return messages;
	}
}
