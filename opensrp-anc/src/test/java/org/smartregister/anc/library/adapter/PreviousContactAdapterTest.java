package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.YamlConfigWrapper;

import java.util.List;

public class PreviousContactAdapterTest extends BaseUnitTest {
    @Mock
    PreviousContactsAdapter previousContactsAdapter;
    private List<Facts> factsList;
    private LayoutInflater inflater;
    private Context context;
    private List<YamlConfigWrapper> lastContactDetails;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
        previousContactsAdapter = new PreviousContactsAdapter(factsList, context);
    }
    @Test
    public void testGetItemCount(){
        Mockito.verify(factsList.size());

    }
}
