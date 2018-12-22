package org.smartregister.anc.widget;

import android.view.LayoutInflater;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.anc.activity.BaseUnitTest;

public class ExpansionPanelWidgetFactoryTest extends BaseUnitTest {
    private ExpansionWidgetFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JSONObject jsonObject;

    @Mock
    private CommonListener listener;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ExpansionWidgetFactory();
    }

    @Ignore
    @Test
    @PrepareForTest({LayoutInflater.class})
    public void testExpansionPanelWidgetFactorInstantiatesCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        factory.attachJson("RandomStepName", context, formFragment, jsonObject, listener, false);

    }
}
