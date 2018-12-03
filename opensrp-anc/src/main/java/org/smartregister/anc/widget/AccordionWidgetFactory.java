package org.smartregister.anc.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONObject;
import org.smartregister.anc.R;

import java.util.ArrayList;
import java.util.List;

public class AccordionWidgetFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener,Boolean popup) throws Exception {
        List<View> views = new ArrayList<>(1);
        LinearLayout rootLayout = getRootLayout(context);
        views.add(rootLayout);

        return views;
    }

    protected LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.native_form_accordion_layout, null);
    }
}
