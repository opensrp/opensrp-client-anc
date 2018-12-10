package org.smartregister.anc.widget;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.util.DBConstants;

import java.util.List;

/**
 * Created by ndegwamartin on 04/07/2018.
 */
public class AncEditTextFactoryTest extends BaseUnitTest {

    private AncEditTextFactory factory;

    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JSONObject jsonObject;

    @Mock
    private MaterialEditText editText;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout relativeLayout;

    @Mock
    private Button addButton;

    @Mock
    private Button minusButton;

    @Mock
    private ImageView imageView;

    private static final String SAMPLE_CLOSE_REG_FORM = " {\n" +
            "        \"key\": \"anc_close_reason\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"161641AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_data_type\": \"select one\",\n" +
            "        \"type\": \"spinner\",\n" +
            "        \"hint\": \"Reason? *\",\n" +
            "        \"values\": [\n" +
            "          \"Live birth\",\n" +
            "          \"Stillbirth\",\n" +
            "          \"Miscarriage\",\n" +
            "          \"Abortion\",\n" +
            "          \"Woman died\",\n" +
            "          \"Moved away\",\n" +
            "          \"False pregnancy\",\n" +
            "          \"Lost to follow-up\",\n" +
            "          \"Wrong Entry\",\n" +
            "          \"Other\"\n" +
            "        ],\n" +
            "        \"openmrs_choice_ids\": {\n" +
            "          \"Live birth\": \"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Stillbirth\": \"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Miscarriage\": \"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Abortion\": \"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Woman Died\": \"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Lost to follow-up\": \"5240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Moved away\": \"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"False Pregnancy\": \"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Wrong entry\": \"163133AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "          \"Other\": \"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "        },\n" +
            "        \"v_required\": {\n" +
            "          \"value\": \"true\",\n" +
            "          \"err\": \"Please select one option *\"\n" +
            "        }\n" +
            "      }";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new AncEditTextFactory();
    }

    @Test
    public void testAncEditTextFactorInstantiatesCorrectly() throws Exception {

        Assert.assertNotNull(factory);
        factory.attachJson("RandomStepName", context, formFragment, jsonObject, editText,imageView);

    }

    @Test
    public void testGetViewsFromJsonCreatesAndReturnsCorrectViews() throws Exception {

        Assert.assertNotNull(factory);
        factory.attachJson(JsonFormConstants.FIRST_STEP_NAME, context, formFragment, jsonObject, editText,imageView);
        JSONObject jsonObject = new JSONObject(SAMPLE_CLOSE_REG_FORM);

        jsonObject.put(DBConstants.KEY.NUMBER_PICKER, true);
        AncEditTextFactory factorySpy = Mockito.spy(factory);
        Mockito.doReturn(relativeLayout).when(factorySpy).getRootLayout(context);
        Mockito.doReturn(editText).when(relativeLayout).findViewById(R.id.edit_text);
        Mockito.doReturn(addButton).when(relativeLayout).findViewById(R.id.addbutton);
        Mockito.doReturn(minusButton).when(relativeLayout).findViewById(R.id.minusbutton);


        List<View> views = factorySpy.getViewsFromJson(JsonFormConstants.FIRST_STEP_NAME, context, formFragment, jsonObject, listener,false);

        Assert.assertNotNull(views);
        Assert.assertTrue(views.size() > 0);


    }

}
