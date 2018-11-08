package org.smartregister.anc.interactor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheck;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class QuickCheckInteractorTest extends BaseUnitTest {

    private QuickCheckContract.Interactor interactor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<JSONObject> jsonArgumentCaptor;

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        interactor = new QuickCheckInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @Test
    @Ignore
    public void testSaveProceedQuickCheckEvent() throws JSONException {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        ECSyncHelper syncHelper = Mockito.mock(ECSyncHelper.class);

        QuickCheckContract.InteractorCallback callback = Mockito.mock(QuickCheckContract.InteractorCallback.class);

        ((QuickCheckInteractor) interactor).setAllSharedPreferences(allSharedPreferences);
        ((QuickCheckInteractor) interactor).setSyncHelper(syncHelper);

        QuickCheck quickCheck = new QuickCheck();

        Field reason = new Field("Reason", "dbAlias");
        quickCheck.setSelectedReason(reason);

        Field complaint = new Field("Complaint", "dbAlias");
        Set<Field> complaintSet = new HashSet<>();
        complaintSet.add(complaint);
        quickCheck.setSpecificComplaints(complaintSet);

        Field dangerSign = new Field("Danger Sign", "dbAlias");
        Set<Field> dangerSignSet = new HashSet<>();
        dangerSignSet.add(dangerSign);
        quickCheck.setSelectedDangerSigns(dangerSignSet);

        String specify = "Other specify";
        quickCheck.setOtherSpecify(specify);

        quickCheck.setProceedToContact(context.getString(R.string.proceed_to_normal_contact));
        quickCheck.setReferAndCloseContact(context.getString(R.string.refer_and_close_contact));
        quickCheck.setYes(context.getString(R.string.yes));
        quickCheck.setNo(context.getString(R.string.no));

        quickCheck.setHasDangerSigns(true);
        quickCheck.setProceedRefer(true);
        quickCheck.setTreat(null);

        String baseEntityId = UUID.randomUUID().toString();
        String providerId = "Provider";
        String locationId = "LocationId";
        String team = "Team";
        String teamId = "TeamId";

        Mockito.doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(locationId).when(allSharedPreferences).fetchDefaultLocalityId(providerId);
        Mockito.doReturn(team).when(allSharedPreferences).fetchDefaultTeam(providerId);
        Mockito.doReturn(teamId).when(allSharedPreferences).fetchDefaultTeamId(providerId);


        Mockito.doNothing().when(syncHelper).addEvent(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));

        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, callback);

        Mockito.verify(syncHelper, Mockito.timeout(ASYNC_TIMEOUT)).addEvent(Mockito.eq(baseEntityId), jsonArgumentCaptor.capture());
        Mockito.verify(callback, Mockito.timeout(ASYNC_TIMEOUT)).quickCheckSaved(true, true);

        JSONObject eventJson = jsonArgumentCaptor.getValue();
        Assert.assertNotNull(eventJson);
        Assert.assertEquals(baseEntityId, eventJson.getString("baseEntityId"));
        Assert.assertEquals(providerId, eventJson.getString("providerId"));
        Assert.assertEquals(locationId, eventJson.getString("locationId"));
        Assert.assertEquals(team, eventJson.getString("team"));
        Assert.assertEquals("Quick Check", eventJson.getString("eventType"));
        Assert.assertEquals("ec_woman", eventJson.getString("entityType"));

        JSONArray obsArray = eventJson.getJSONArray("obs");
        JSONObject contactReason = obsArray.getJSONObject(0);
        Assert.assertEquals("contact_reason", contactReason.getString("fieldCode"));
        Assert.assertEquals("select one", contactReason.getString("fieldDataType"));
        Assert.assertEquals(reason.getDisplayName(), contactReason.getJSONArray("values").getString(0));

        JSONObject specificComplaint = obsArray.getJSONObject(1);
        Assert.assertEquals("specific_complaint", specificComplaint.getString("fieldCode"));
        Assert.assertEquals("select multiple", specificComplaint.getString("fieldDataType"));
        Assert.assertEquals(complaint.getDisplayName(), specificComplaint.getJSONArray("values").getString(0));

        JSONObject specifyOther = obsArray.getJSONObject(2);
        Assert.assertEquals("specific_complaint_other", specifyOther.getString("fieldCode"));
        Assert.assertEquals("text", specifyOther.getString("fieldDataType"));
        Assert.assertEquals(specify, specifyOther.getJSONArray("values").getString(0));

        JSONObject dangerSigns = obsArray.getJSONObject(3);
        Assert.assertEquals("danger_signs", dangerSigns.getString("fieldCode"));
        Assert.assertEquals("select multiple", dangerSigns.getString("fieldDataType"));
        Assert.assertEquals(dangerSign.getDisplayName(), dangerSigns.getJSONArray("values").getString(0));

        JSONObject proceed = obsArray.getJSONObject(4);
        Assert.assertEquals("danger_signs_proceed", proceed.getString("fieldCode"));
        Assert.assertEquals("select one", proceed.getString("fieldDataType"));
        Assert.assertEquals(quickCheck.getProceedToContact(), proceed.getJSONArray("values").getString(0));

    }


    @Test
    @Ignore
    public void testSaveReferAndCloseQuickCheckEvent() throws JSONException {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        ECSyncHelper syncHelper = Mockito.mock(ECSyncHelper.class);

        QuickCheckContract.InteractorCallback callback = Mockito.mock(QuickCheckContract.InteractorCallback.class);

        ((QuickCheckInteractor) interactor).setAllSharedPreferences(allSharedPreferences);
        ((QuickCheckInteractor) interactor).setSyncHelper(syncHelper);

        QuickCheck quickCheck = new QuickCheck();

        Field reason = new Field("Reason", "dbAlias");
        quickCheck.setSelectedReason(reason);

        Field complaint = new Field("Complaint", "dbAlias");
        Set<Field> complaintSet = new HashSet<>();
        complaintSet.add(complaint);
        quickCheck.setSpecificComplaints(complaintSet);

        Field dangerSign = new Field("Danger Sign", "dbAlias");
        Set<Field> dangerSignSet = new HashSet<>();
        dangerSignSet.add(dangerSign);
        quickCheck.setSelectedDangerSigns(dangerSignSet);

        String specify = "Other specify";
        quickCheck.setOtherSpecify(specify);

        quickCheck.setProceedToContact(context.getString(R.string.proceed_to_normal_contact));
        quickCheck.setReferAndCloseContact(context.getString(R.string.refer_and_close_contact));
        quickCheck.setYes(context.getString(R.string.yes));
        quickCheck.setNo(context.getString(R.string.no));

        quickCheck.setHasDangerSigns(true);
        quickCheck.setProceedRefer(false);
        quickCheck.setTreat(true);

        String baseEntityId = UUID.randomUUID().toString();
        String providerId = "Provider";
        String locationId = "LocationId";
        String team = "Team";
        String teamId = "TeamId";

        Mockito.doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(locationId).when(allSharedPreferences).fetchDefaultLocalityId(providerId);
        Mockito.doReturn(team).when(allSharedPreferences).fetchDefaultTeam(providerId);
        Mockito.doReturn(teamId).when(allSharedPreferences).fetchDefaultTeamId(providerId);


        Mockito.doNothing().when(syncHelper).addEvent(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));

        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, callback);

        Mockito.verify(syncHelper, Mockito.timeout(ASYNC_TIMEOUT)).addEvent(Mockito.eq(baseEntityId), jsonArgumentCaptor.capture());
        Mockito.verify(callback, Mockito.timeout(ASYNC_TIMEOUT)).quickCheckSaved(false, true);

        JSONObject eventJson = jsonArgumentCaptor.getValue();
        Assert.assertNotNull(eventJson);
        Assert.assertEquals(baseEntityId, eventJson.getString("baseEntityId"));
        Assert.assertEquals(providerId, eventJson.getString("providerId"));
        Assert.assertEquals(locationId, eventJson.getString("locationId"));
        Assert.assertEquals(team, eventJson.getString("team"));
        Assert.assertEquals("Quick Check", eventJson.getString("eventType"));
        Assert.assertEquals("ec_woman", eventJson.getString("entityType"));

        JSONArray obsArray = eventJson.getJSONArray("obs");
        JSONObject contactReason = obsArray.getJSONObject(0);
        Assert.assertEquals("contact_reason", contactReason.getString("fieldCode"));
        Assert.assertEquals("select one", contactReason.getString("fieldDataType"));
        Assert.assertEquals(reason.getDisplayName(), contactReason.getJSONArray("values").getString(0));

        JSONObject specificComplaint = obsArray.getJSONObject(1);
        Assert.assertEquals("specific_complaint", specificComplaint.getString("fieldCode"));
        Assert.assertEquals("select multiple", specificComplaint.getString("fieldDataType"));
        Assert.assertEquals(complaint.getDisplayName(), specificComplaint.getJSONArray("values").getString(0));

        JSONObject specifyOther = obsArray.getJSONObject(2);
        Assert.assertEquals("specific_complaint_other", specifyOther.getString("fieldCode"));
        Assert.assertEquals("text", specifyOther.getString("fieldDataType"));
        Assert.assertEquals(specify, specifyOther.getJSONArray("values").getString(0));

        JSONObject dangerSigns = obsArray.getJSONObject(3);
        Assert.assertEquals("danger_signs", dangerSigns.getString("fieldCode"));
        Assert.assertEquals("select multiple", dangerSigns.getString("fieldDataType"));
        Assert.assertEquals(dangerSign.getDisplayName(), dangerSigns.getJSONArray("values").getString(0));

        JSONObject proceed = obsArray.getJSONObject(4);
        Assert.assertEquals("danger_signs_proceed", proceed.getString("fieldCode"));
        Assert.assertEquals("select one", proceed.getString("fieldDataType"));
        Assert.assertEquals(quickCheck.getReferAndCloseContact(), proceed.getJSONArray("values").getString(0));

        JSONObject treat = obsArray.getJSONObject(5);
        Assert.assertEquals("danger_signs_treat", treat.getString("fieldCode"));
        Assert.assertEquals("select one", treat.getString("fieldDataType"));
        Assert.assertEquals(quickCheck.getYes(), treat.getJSONArray("values").getString(0));

    }

    @Test
    @Ignore
    public void testSaveReferWithoutTreatingQuickCheckEvent() throws JSONException {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        ECSyncHelper syncHelper = Mockito.mock(ECSyncHelper.class);

        QuickCheckContract.InteractorCallback callback = Mockito.mock(QuickCheckContract.InteractorCallback.class);

        ((QuickCheckInteractor) interactor).setAllSharedPreferences(allSharedPreferences);
        ((QuickCheckInteractor) interactor).setSyncHelper(syncHelper);

        QuickCheck quickCheck = new QuickCheck();

        Field reason = new Field("Reason", "dbAlias");
        quickCheck.setSelectedReason(reason);

        Field complaint = new Field("Complaint", "dbAlias");
        Set<Field> complaintSet = new HashSet<>();
        complaintSet.add(complaint);
        quickCheck.setSpecificComplaints(complaintSet);

        Field dangerSign = new Field("Danger Sign", "dbAlias");
        Set<Field> dangerSignSet = new HashSet<>();
        dangerSignSet.add(dangerSign);
        quickCheck.setSelectedDangerSigns(dangerSignSet);

        String specify = "Other specify";
        quickCheck.setOtherSpecify(specify);

        quickCheck.setProceedToContact(context.getString(R.string.proceed_to_normal_contact));
        quickCheck.setReferAndCloseContact(context.getString(R.string.refer_and_close_contact));
        quickCheck.setYes(context.getString(R.string.yes));
        quickCheck.setNo(context.getString(R.string.no));

        quickCheck.setHasDangerSigns(true);
        quickCheck.setProceedRefer(false);
        quickCheck.setTreat(false);

        String baseEntityId = UUID.randomUUID().toString();
        String providerId = "Provider";
        String locationId = "LocationId";
        String team = "Team";
        String teamId = "TeamId";

        Mockito.doReturn(providerId).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(locationId).when(allSharedPreferences).fetchDefaultLocalityId(providerId);
        Mockito.doReturn(team).when(allSharedPreferences).fetchDefaultTeam(providerId);
        Mockito.doReturn(teamId).when(allSharedPreferences).fetchDefaultTeamId(providerId);


        Mockito.doNothing().when(syncHelper).addEvent(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));

        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, callback);

        Mockito.verify(syncHelper, Mockito.timeout(ASYNC_TIMEOUT)).addEvent(Mockito.eq(baseEntityId), jsonArgumentCaptor.capture());
        Mockito.verify(callback, Mockito.timeout(ASYNC_TIMEOUT)).quickCheckSaved(false, true);

        JSONObject eventJson = jsonArgumentCaptor.getValue();
        Assert.assertNotNull(eventJson);
        Assert.assertEquals(baseEntityId, eventJson.getString("baseEntityId"));
        Assert.assertEquals(providerId, eventJson.getString("providerId"));
        Assert.assertEquals(locationId, eventJson.getString("locationId"));
        Assert.assertEquals(team, eventJson.getString("team"));
        Assert.assertEquals("Quick Check", eventJson.getString("eventType"));
        Assert.assertEquals("ec_woman", eventJson.getString("entityType"));

        JSONArray obsArray = eventJson.getJSONArray("obs");
        JSONObject contactReason = obsArray.getJSONObject(0);
        Assert.assertEquals("contact_reason", contactReason.getString("fieldCode"));
        Assert.assertEquals("select one", contactReason.getString("fieldDataType"));
        Assert.assertEquals(reason.getDisplayName(), contactReason.getJSONArray("values").getString(0));

        JSONObject specificComplaint = obsArray.getJSONObject(1);
        Assert.assertEquals("specific_complaint", specificComplaint.getString("fieldCode"));
        Assert.assertEquals("select multiple", specificComplaint.getString("fieldDataType"));
        Assert.assertEquals(complaint.getDisplayName(), specificComplaint.getJSONArray("values").getString(0));

        JSONObject specifyOther = obsArray.getJSONObject(2);
        Assert.assertEquals("specific_complaint_other", specifyOther.getString("fieldCode"));
        Assert.assertEquals("text", specifyOther.getString("fieldDataType"));
        Assert.assertEquals(specify, specifyOther.getJSONArray("values").getString(0));

        JSONObject dangerSigns = obsArray.getJSONObject(3);
        Assert.assertEquals("danger_signs", dangerSigns.getString("fieldCode"));
        Assert.assertEquals("select multiple", dangerSigns.getString("fieldDataType"));
        Assert.assertEquals(dangerSign.getDisplayName(), dangerSigns.getJSONArray("values").getString(0));

        JSONObject proceed = obsArray.getJSONObject(4);
        Assert.assertEquals("danger_signs_proceed", proceed.getString("fieldCode"));
        Assert.assertEquals("select one", proceed.getString("fieldDataType"));
        Assert.assertEquals(quickCheck.getReferAndCloseContact(), proceed.getJSONArray("values").getString(0));

        JSONObject treat = obsArray.getJSONObject(5);
        Assert.assertEquals("danger_signs_treat", treat.getString("fieldCode"));
        Assert.assertEquals("select one", treat.getString("fieldDataType"));
        Assert.assertEquals(quickCheck.getNo(), treat.getJSONArray("values").getString(0));

    }


}
