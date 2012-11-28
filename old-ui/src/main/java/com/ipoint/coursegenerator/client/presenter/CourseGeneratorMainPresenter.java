package com.ipoint.coursegenerator.client.presenter;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.ipoint.coursegenerator.client.NameTokens;

public class CourseGeneratorMainPresenter
	extends
	Presenter<CourseGeneratorMainPresenter.MyView, CourseGeneratorMainPresenter.MyProxy> {

    public interface MyView extends View {
    }

    @ContentSlot
    public static final Type<RevealContentHandler<?>> SLOT_mainContent = new Type<RevealContentHandler<?>>();
    
    @ProxyCodeSplit
    @NameToken(NameTokens.main)
    public interface MyProxy extends ProxyPlace<CourseGeneratorMainPresenter> {
    }

    @Inject
    public CourseGeneratorMainPresenter(final EventBus eventBus, final MyView view,
    	final MyProxy proxy) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);        
    }

    @Override
    protected void onBind() {
        super.onBind();        
    }
}
