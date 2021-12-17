package org.smartregister.anc.library.shadows;

import com.vijay.jsonwizard.views.CustomTextView;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;
import org.smartregister.view.customcontrols.FontVariant;

@Implements(CustomTextView.class)
public class CustomTextShadow extends ShadowTextView {
    public void setFontVariant(final FontVariant variant) {

    }
}
