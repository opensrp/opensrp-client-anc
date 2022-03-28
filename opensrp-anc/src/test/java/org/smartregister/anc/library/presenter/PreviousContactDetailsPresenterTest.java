package org.smartregister.anc.library.presenter;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import android.text.TextUtils;

import org.jeasy.rules.api.Facts;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.PreviousContactsDetails;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.model.PreviousContactsSummaryModel;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AncLibrary.class, TextUtils.class})
public class PreviousContactDetailsPresenterTest extends BaseUnitTest {

    @Mock
    private PreviousContactsDetails.Presenter previousContactPresenter;

    @Mock
    private PreviousContactsDetails.View profileView;

    @Mock
    private AncLibrary ancLibrary;

    @Mock
    private Context context;

    @Mock
    private PreviousContactRepository previousContactRepository;

    @Captor
    private ArgumentCaptor<List<ContactSummaryModel>> schedulesArgs;

    @Captor
    private ArgumentCaptor<LinkedHashMap<String, List<Facts>>> filteredContacts;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        previousContactPresenter = new PreviousContactDetailsPresenter(profileView);

    }

    @Test
    public void testGetProfileView() {
        Assert.assertNotNull((previousContactPresenter.getProfileView()));
    }

    @Test
    public void testLoadPreviousContactSchedule() {
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(null)).thenReturn(true);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.getStringResource(R.string.contact_number)).thenReturn("Contact %1$d");
        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        String baseEntityId = BaseUnitTest.DUMMY_BASE_ENTITY_ID;
        String contactNo = "1";
        String edd = "2019-09-01";
        Facts facts = new Facts();
        facts.put(ConstantsUtils.CONTACT_SCHEDULE, "[30, 34, 36, 38, 40, 41]");
        Mockito.doReturn(facts).when(previousContactRepository).getImmediatePreviousSchedule(baseEntityId, contactNo);
        previousContactPresenter.loadPreviousContactSchedule(baseEntityId, contactNo, edd);
        verify(profileView, atLeastOnce()).displayPreviousContactSchedule(schedulesArgs.capture());
    }

    @Test
    public void testLoadPreviousContacts() throws ParseException, JSONException, IOException {
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(!TextUtils.isEmpty(null)).thenReturn(true);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);

        String baseEntityId = BaseUnitTest.DUMMY_BASE_ENTITY_ID;
        String contactNo = "1";
        List<PreviousContactsSummaryModel> allContactsFacts = new ArrayList<>();
        populatePreviousContacts(allContactsFacts);
        Mockito.doReturn(allContactsFacts).when(previousContactRepository).getPreviousContactsFacts(baseEntityId);
        previousContactPresenter.loadPreviousContacts(baseEntityId, contactNo);
        verify(profileView, atLeastOnce()).loadPreviousContactsDetails(filteredContacts.capture());
    }

    private void populatePreviousContacts(List<PreviousContactsSummaryModel> allContactsFacts) {
        //Add  10 dummy records to allContactFacts list
        Facts fact1 = new Facts();
        fact1.put(ConstantsUtils.GEST_AGE_OPENMRS, "21");
        Facts fact2 = new Facts();
        fact1.put(ConstantsUtils.DANGER_NONE, "[none]");
        Facts fact3 = new Facts();
        fact1.put(ConstantsUtils.CONTACT_DATE, "2019-05-30");
        Facts fact4 = new Facts();
        fact1.put(ConstantsUtils.GEST_AGE_OPENMRS, "18");
        Facts fact5 = new Facts();
        fact1.put(ConstantsUtils.DANGER_NONE, "[bleeding_vaginally]");

        PreviousContactsSummaryModel prevContactModel1 = new PreviousContactsSummaryModel("2", "2019-05-30", fact1);
        PreviousContactsSummaryModel prevContactModel2 = new PreviousContactsSummaryModel("2", "2019-05-30", fact2);
        PreviousContactsSummaryModel prevContactModel3 = new PreviousContactsSummaryModel("2", "2019-05-30", fact3);
        PreviousContactsSummaryModel prevContactModel4 = new PreviousContactsSummaryModel("1", "2019-05-08", fact4);
        PreviousContactsSummaryModel prevContactModel5 = new PreviousContactsSummaryModel("1", "2019-05-08", fact5);

        allContactsFacts.add(prevContactModel1);
        allContactsFacts.add(prevContactModel2);
        allContactsFacts.add(prevContactModel3);
        allContactsFacts.add(prevContactModel4);
        allContactsFacts.add(prevContactModel5);
    }


}
