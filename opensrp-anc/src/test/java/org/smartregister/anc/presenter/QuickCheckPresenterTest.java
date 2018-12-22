package org.smartregister.anc.presenter;

import android.content.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheck;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.configurableviews.model.Field;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class QuickCheckPresenterTest extends BaseUnitTest {

    @Mock
    private QuickCheckContract.View view;

    @Mock
    private QuickCheckContract.Interactor interactor;

    @Captor
    private ArgumentCaptor<QuickCheck> quickCheckArgumentCaptor;

    private QuickCheckContract.Presenter presenter;

    private final Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new QuickCheckPresenter(view);
    }

    @Test
    public void testSetReason() {

        String specificComplaint = context.getString(R.string.specific_complaint);
        Field reason = new Field("Display", "DB Alias");

        Mockito.doReturn(specificComplaint).when(view).getString(R.string.specific_complaint);

        presenter.setReason(reason);

        Mockito.verify(view).notifyComplaintAdapter();
        Mockito.verify(view).hideComplaintLayout();

    }

    @Test
    public void testSetSpecificComplaintReason() {

        String specificComplaint = context.getString(R.string.specific_complaint);
        Field reason = new Field(specificComplaint, "DB Alias");

        Mockito.doReturn(specificComplaint).when(view).getString(R.string.specific_complaint);

        presenter.setReason(reason);

        Mockito.verify(view).displayComplaintLayout();

    }

    @Test
    public void testSetReasonAsNull() {

        String specificComplaint = context.getString(R.string.specific_complaint);
        Mockito.doReturn(specificComplaint).when(view).getString(R.string.specific_complaint);

        presenter.setReason(null);

        Mockito.verify(view).displayToast(R.string.validation_no_reason_selected);

    }

    @Test
    public void testGetConfig() {

        QuickCheckConfiguration config = ConfigHelper.defaultQuickCheckConfiguration(context);
        ((QuickCheckPresenter) presenter).setConfig(config);

        Assert.assertEquals(config, presenter.getConfig());

    }

    @Test
    public void testContainsComplaint() {

        Field complaint1 = new Field("complaint 1", "dbAlias");
        Field complaint2 = new Field("complaint 2", "dbAlias");
        Field complaint3 = new Field("complaint 3", "dbAlias");

        Set<Field> specificComplaints = new HashSet<>();
        specificComplaints.add(complaint1);
        specificComplaints.add(complaint2);
        specificComplaints.add(complaint3);

        ((QuickCheckPresenter) presenter).setSpecificComplaints(specificComplaints);

        Assert.assertTrue(presenter.containsComplaintOrDangerSign(complaint1, false));
        Assert.assertTrue(presenter.containsComplaintOrDangerSign(complaint2, false));
        Assert.assertTrue(presenter.containsComplaintOrDangerSign(complaint3, false));
        Assert.assertFalse(presenter.containsComplaintOrDangerSign(new Field("danger sign 1", "dbAlias"), false));

    }

    @Test
    public void testContainsDangerSign() {

        Field dangerSign1 = new Field("danger sign 1", "dbAlias");
        Field dangerSign2 = new Field("danger sign 2", "dbAlias");
        Field dangerSign3 = new Field("danger sign 3", "dbAlias");

        Set<Field> dangerSigns = new HashSet<>();
        dangerSigns.add(dangerSign1);
        dangerSigns.add(dangerSign2);
        dangerSigns.add(dangerSign3);

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(dangerSigns);

        Assert.assertTrue(presenter.containsComplaintOrDangerSign(dangerSign1, true));
        Assert.assertTrue(presenter.containsComplaintOrDangerSign(dangerSign2, true));
        Assert.assertTrue(presenter.containsComplaintOrDangerSign(dangerSign3, true));
        Assert.assertFalse(presenter.containsComplaintOrDangerSign(new Field("complaint 1", "dbAlias"), true));

    }

    @Test
    public void testAddToComplaintList() {

        ((QuickCheckPresenter) presenter).setSpecificComplaints(new HashSet<Field>());

        Field complaint = new Field("complaint 1", "dbAlias");

        Mockito.doReturn(context.getString(R.string.complaint_other_specify)).when(view).getString(R.string.complaint_other_specify);

        presenter.modifyComplaintsOrDangerList(complaint, true, false);

        Set<Field> complaints = ((QuickCheckPresenter) presenter).getSpecificComplaints();

        Assert.assertNotNull(complaints);
        Assert.assertEquals(1, complaints.size());
        Assert.assertEquals(complaint, complaints.iterator().next());

        Mockito.verify(view, Mockito.times(0)).enableSpecifyEditText();
        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();
    }

    @Test
    public void testRemoveFromComplaintList() {

        Field complaint = new Field("complaint 1", "dbAlias");
        Set<Field> set = new HashSet<>();
        set.add(complaint);
        ((QuickCheckPresenter) presenter).setSpecificComplaints(set);

        Mockito.doReturn(context.getString(R.string.complaint_other_specify)).when(view).getString(R.string.complaint_other_specify);

        presenter.modifyComplaintsOrDangerList(complaint, false, false);

        Mockito.verify(view, Mockito.times(0)).enableSpecifyEditText();
        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();

        Set<Field> complaints = ((QuickCheckPresenter) presenter).getSpecificComplaints();

        Assert.assertNotNull(complaints);
        Assert.assertEquals(0, complaints.size());

    }

    @Test
    public void testRemoveFromEmptyComplaintList() {

        ((QuickCheckPresenter) presenter).setSpecificComplaints(new HashSet<Field>());

        Field complaint = new Field("complaint 1", "dbAlias");

        Mockito.doReturn(context.getString(R.string.complaint_other_specify)).when(view).getString(R.string.complaint_other_specify);

        presenter.modifyComplaintsOrDangerList(complaint, false, false);

        Mockito.verify(view, Mockito.times(0)).enableSpecifyEditText();
        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();

        Set<Field> complaints = ((QuickCheckPresenter) presenter).getSpecificComplaints();

        Assert.assertNotNull(complaints);
        Assert.assertEquals(0, complaints.size());

    }

    @Test
    public void testAddOtherSpecifyToComplaintList() {

        ((QuickCheckPresenter) presenter).setSpecificComplaints(new HashSet<Field>());

        String specifyOther = context.getString(R.string.complaint_other_specify);
        Field otherSpecify = new Field(specifyOther, "dbAlias");

        Mockito.doReturn(specifyOther).when(view).getString(R.string.complaint_other_specify);

        presenter.modifyComplaintsOrDangerList(otherSpecify, true, false);

        Mockito.verify(view).enableSpecifyEditText();
        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();

        Set<Field> complaints = ((QuickCheckPresenter) presenter).getSpecificComplaints();

        Assert.assertNotNull(complaints);
        Assert.assertEquals(1, complaints.size());
        Assert.assertEquals(otherSpecify, complaints.iterator().next());

    }

    @Test
    public void testRemoveOtherSpecifyFromComplaintList() {

        String specifyOther = context.getString(R.string.complaint_other_specify);
        Field otherSpecify = new Field(specifyOther, "dbAlias");
        Set<Field> set = new HashSet<>();
        set.add(otherSpecify);
        ((QuickCheckPresenter) presenter).setSpecificComplaints(set);

        Mockito.doReturn(specifyOther).when(view).getString(R.string.complaint_other_specify);

        presenter.modifyComplaintsOrDangerList(otherSpecify, false, false);

        Mockito.verify(view, Mockito.times(0)).enableSpecifyEditText();
        Mockito.verify(view).disableSpecifyEditText();

        Set<Field> complaints = ((QuickCheckPresenter) presenter).getSpecificComplaints();

        Assert.assertNotNull(complaints);
        Assert.assertEquals(0, complaints.size());

    }


    @Test
    public void testAddNoneToEmptyDangerSignList() {

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(new HashSet<Field>());

        String dangerNoneString = context.getString(R.string.danger_none);
        Field dangerNone = new Field(dangerNoneString, "dbAlias");

        Mockito.doReturn(dangerNoneString).when(view).getString(R.string.danger_none);

        presenter.modifyComplaintsOrDangerList(dangerNone, true, true);

        Mockito.verify(view).notifyDangerSignAdapter();
        Mockito.verify(view).displayNavigationLayout();
        Mockito.verify(view).hideReferButton();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(1, dangerSigns.size());
        Assert.assertEquals(dangerNone, dangerSigns.iterator().next());

    }

    @Test
    public void testAddNoneToNonEmptyDangerSignList() {

        Field dangerSign1 = new Field("Danger Sign 1", "dbAlias");
        Field dangerSign2 = new Field("Danger Sign 2", "dbAlias");
        Field dangerSign3 = new Field("Danger Sign 3", "dbAlias");

        Set<Field> set = new HashSet<>();
        set.add(dangerSign1);
        set.add(dangerSign2);
        set.add(dangerSign3);

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(set);

        String dangerNoneString = context.getString(R.string.danger_none);
        Field dangerNone = new Field(dangerNoneString, "dbAlias");

        Mockito.doReturn(dangerNoneString).when(view).getString(R.string.danger_none);

        presenter.modifyComplaintsOrDangerList(dangerNone, true, true);

        Mockito.verify(view).notifyDangerSignAdapter();
        Mockito.verify(view).displayNavigationLayout();
        Mockito.verify(view).hideReferButton();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(1, dangerSigns.size());
        Assert.assertEquals(dangerNone, dangerSigns.iterator().next());

    }

    @Test
    public void testAddToNonEmptyDangerSignListWithNoneChecked() {

        String dangerNoneString = context.getString(R.string.danger_none);
        Field dangerNone = new Field(dangerNoneString, "dbAlias");
        Field dangerSign1 = new Field("Danger Sign 1", "dbAlias");
        Field dangerSign2 = new Field("Danger Sign 2", "dbAlias");

        Set<Field> set = new HashSet<>();
        set.add(dangerNone);
        set.add(dangerSign1);
        set.add(dangerSign2);

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(set);

        Field dangerSignAdd = new Field("Danger Sign Add", "dbAlias");

        Mockito.doReturn(dangerNoneString).when(view).getString(R.string.danger_none);

        presenter.modifyComplaintsOrDangerList(dangerSignAdd, true, true);

        Mockito.verify(view).notifyDangerSignAdapter();
        Mockito.verify(view).displayNavigationLayout();
        Mockito.verify(view).displayReferButton();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(3, dangerSigns.size());
        Assert.assertTrue(dangerSigns.contains(dangerSignAdd));
        Assert.assertFalse(dangerSigns.contains(dangerNone));

    }


    @Test
    public void testAddToNonEmptyDangerSignListWithoutNoneChecked() {


        Field dangerSign1 = new Field("Danger Sign 1", "dbAlias");
        Field dangerSign2 = new Field("Danger Sign 2", "dbAlias");

        Set<Field> set = new HashSet<>();
        set.add(dangerSign1);
        set.add(dangerSign2);

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(set);

        Field dangerSignAdd = new Field("Danger Sign Add", "dbAlias");

        String dangerNoneString = context.getString(R.string.danger_none);
        Mockito.doReturn(dangerNoneString).when(view).getString(R.string.danger_none);

        presenter.modifyComplaintsOrDangerList(dangerSignAdd, true, true);

        Mockito.verify(view, Mockito.times(0)).notifyDangerSignAdapter();
        Mockito.verify(view).displayNavigationLayout();
        Mockito.verify(view).displayReferButton();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(3, dangerSigns.size());
        Assert.assertTrue(dangerSigns.contains(dangerSignAdd));

    }

    @Test
    public void testRemoveFromDangerSignList() {
        Field dangerSignToRemove = new Field("Danger Sign To Remove", "dbAlias");
        Set<Field> set = new HashSet<>();
        set.add(dangerSignToRemove);
        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(set);

        presenter.modifyComplaintsOrDangerList(dangerSignToRemove, false, true);

        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(0, dangerSigns.size());

    }

    @Test
    public void testRemoveFromEmptyDangerSignList() {

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(new HashSet<Field>());

        Field dangerSignToRemove = new Field("Danger Sign To Remove", "dbAlias");

        presenter.modifyComplaintsOrDangerList(dangerSignToRemove, false, true);

        Mockito.verify(view, Mockito.times(0)).disableSpecifyEditText();

        Set<Field> dangerSigns = ((QuickCheckPresenter) presenter).getSelectedDangerSigns();

        Assert.assertNotNull(dangerSigns);
        Assert.assertEquals(0, dangerSigns.size());

    }

    @Test
    public void testProceedToNormalContact() {

        QuickCheckPresenter quickCheckPresenter = (QuickCheckPresenter) presenter;
        quickCheckPresenter.setInteractor(interactor);

        String specify = "Specify";

        Field reason = new Field("Reason 1", "dbAlias");
        quickCheckPresenter.setSelectedReason(reason);

        Field dangerSign = new Field(context.getString(R.string.danger_none), "dbAlias");
        Set<Field> dangerSet = new HashSet<>();
        dangerSet.add(dangerSign);

        quickCheckPresenter.setSelectedDangerSigns(dangerSet);

        String baseEntityId = UUID.randomUUID().toString();
        quickCheckPresenter.setBaseEntityId(baseEntityId);
        quickCheckPresenter.setContactNumber(1);

        Mockito.doReturn(context.getString(R.string.specific_complaint)).when(view).getString(R.string.specific_complaint);
        Mockito.doReturn(context.getString(R.string.danger_none)).when(view).getString(R.string.danger_none);
        Mockito.doReturn(context.getString(R.string.proceed_to_normal_contact)).when(view).getString(R.string.proceed_to_normal_contact);
        Mockito.doReturn(context.getString(R.string.refer_and_close_contact)).when(view).getString(R.string.refer_and_close_contact);
        Mockito.doReturn(context.getString(R.string.yes)).when(view).getString(R.string.yes);
        Mockito.doReturn(context.getString(R.string.no)).when(view).getString(R.string.no);

        Mockito.doNothing().when(interactor).saveQuickCheckEvent(quickCheckArgumentCaptor.capture(), Mockito.eq(baseEntityId), Mockito.any(QuickCheckContract.InteractorCallback.class));

        quickCheckPresenter.proceedToNormalContact(specify);

        QuickCheck quickCheck = quickCheckArgumentCaptor.getValue();

        Assert.assertEquals(reason, quickCheck.getSelectedReason());
        Assert.assertTrue(quickCheck.getSpecificComplaints().isEmpty());
        Assert.assertEquals(dangerSet, quickCheck.getSelectedDangerSigns());
        Assert.assertEquals(specify, quickCheck.getOtherSpecify());

        Assert.assertEquals(context.getString(R.string.proceed_to_normal_contact), quickCheck.getProceedToContact());
        Assert.assertEquals(context.getString(R.string.refer_and_close_contact), quickCheck.getReferAndCloseContact());
        Assert.assertEquals(context.getString(R.string.yes), quickCheck.getYes());
        Assert.assertEquals(context.getString(R.string.no), quickCheck.getNo());

        Assert.assertFalse(quickCheck.getHasDangerSigns());
        Assert.assertTrue(quickCheck.getProceedRefer());
        Assert.assertNull(quickCheck.getTreat());

    }

    @Test
    public void testReferContactAndClose() {

        QuickCheckPresenter quickCheckPresenter = (QuickCheckPresenter) presenter;
        quickCheckPresenter.setInteractor(interactor);

        String specify = "Specify";

        Field reason = new Field("Reason 1", "dbAlias");
        quickCheckPresenter.setSelectedReason(reason);

        Field dangerSign = new Field("Danger Sign 1", "dbAlias");
        Set<Field> dangerSet = new HashSet<>();
        dangerSet.add(dangerSign);

        quickCheckPresenter.setSelectedDangerSigns(dangerSet);

        String baseEntityId = UUID.randomUUID().toString();
        quickCheckPresenter.setBaseEntityId(baseEntityId);
        quickCheckPresenter.setContactNumber(1);

        Mockito.doReturn(context.getString(R.string.specific_complaint)).when(view).getString(R.string.specific_complaint);
        Mockito.doReturn(context.getString(R.string.danger_none)).when(view).getString(R.string.danger_none);
        Mockito.doReturn(context.getString(R.string.proceed_to_normal_contact)).when(view).getString(R.string.proceed_to_normal_contact);
        Mockito.doReturn(context.getString(R.string.refer_and_close_contact)).when(view).getString(R.string.refer_and_close_contact);
        Mockito.doReturn(context.getString(R.string.yes)).when(view).getString(R.string.yes);
        Mockito.doReturn(context.getString(R.string.no)).when(view).getString(R.string.no);

        Mockito.doNothing().when(interactor).saveQuickCheckEvent(quickCheckArgumentCaptor.capture(), Mockito.eq(baseEntityId), Mockito.any(QuickCheckContract.InteractorCallback.class));

        quickCheckPresenter.referAndCloseContact(specify, true);

        QuickCheck quickCheck = quickCheckArgumentCaptor.getValue();

        Assert.assertEquals(reason, quickCheck.getSelectedReason());
        Assert.assertTrue(quickCheck.getSpecificComplaints().isEmpty());
        Assert.assertEquals(dangerSet, quickCheck.getSelectedDangerSigns());
        Assert.assertEquals(specify, quickCheck.getOtherSpecify());

        Assert.assertEquals(context.getString(R.string.proceed_to_normal_contact), quickCheck.getProceedToContact());
        Assert.assertEquals(context.getString(R.string.refer_and_close_contact), quickCheck.getReferAndCloseContact());
        Assert.assertEquals(context.getString(R.string.yes), quickCheck.getYes());
        Assert.assertEquals(context.getString(R.string.no), quickCheck.getNo());

        Assert.assertTrue(quickCheck.getHasDangerSigns());
        Assert.assertFalse(quickCheck.getProceedRefer());
        Assert.assertTrue(quickCheck.getTreat());

    }

    @Test
    public void testCloseContactWithoutReferring() {

        QuickCheckPresenter quickCheckPresenter = (QuickCheckPresenter) presenter;
        quickCheckPresenter.setInteractor(interactor);

        String specify = "Specify";

        Field reason = new Field("Reason 1", "dbAlias");
        quickCheckPresenter.setSelectedReason(reason);

        Field dangerSign = new Field("Danger Sign 1", "dbAlias");
        Set<Field> dangerSet = new HashSet<>();
        dangerSet.add(dangerSign);

        quickCheckPresenter.setSelectedDangerSigns(dangerSet);

        String baseEntityId = UUID.randomUUID().toString();
        quickCheckPresenter.setBaseEntityId(baseEntityId);
        quickCheckPresenter.setContactNumber(1);

        Mockito.doReturn(context.getString(R.string.specific_complaint)).when(view).getString(R.string.specific_complaint);
        Mockito.doReturn(context.getString(R.string.danger_none)).when(view).getString(R.string.danger_none);
        Mockito.doReturn(context.getString(R.string.proceed_to_normal_contact)).when(view).getString(R.string.proceed_to_normal_contact);
        Mockito.doReturn(context.getString(R.string.refer_and_close_contact)).when(view).getString(R.string.refer_and_close_contact);
        Mockito.doReturn(context.getString(R.string.yes)).when(view).getString(R.string.yes);
        Mockito.doReturn(context.getString(R.string.no)).when(view).getString(R.string.no);

        Mockito.doNothing().when(interactor).saveQuickCheckEvent(quickCheckArgumentCaptor.capture(), Mockito.eq(baseEntityId), Mockito.any(QuickCheckContract.InteractorCallback.class));

        quickCheckPresenter.referAndCloseContact(specify, false);

        QuickCheck quickCheck = quickCheckArgumentCaptor.getValue();

        Assert.assertEquals(reason, quickCheck.getSelectedReason());
        Assert.assertTrue(quickCheck.getSpecificComplaints().isEmpty());
        Assert.assertEquals(dangerSet, quickCheck.getSelectedDangerSigns());
        Assert.assertEquals(specify, quickCheck.getOtherSpecify());

        Assert.assertEquals(context.getString(R.string.proceed_to_normal_contact), quickCheck.getProceedToContact());
        Assert.assertEquals(context.getString(R.string.refer_and_close_contact), quickCheck.getReferAndCloseContact());
        Assert.assertEquals(context.getString(R.string.yes), quickCheck.getYes());
        Assert.assertEquals(context.getString(R.string.no), quickCheck.getNo());

        Assert.assertTrue(quickCheck.getHasDangerSigns());
        Assert.assertFalse(quickCheck.getProceedRefer());
        Assert.assertFalse(quickCheck.getTreat());

    }

    @Test
    public void testValidateQuickCheckWithoutReason() {

        presenter.proceedToNormalContact(null);

        Mockito.verify(view).displayToast(R.string.validation_no_reason_selected);

        presenter.referAndCloseContact(null, true);

        Mockito.verify(view, Mockito.times(2)).displayToast(R.string.validation_no_reason_selected);

    }

    @Test
    public void testValidateQuickCheckWithoutSpecificComplaint() {

        Field reason = new Field(context.getString(R.string.specific_complaint), "dbAlias");
        ((QuickCheckPresenter) presenter).setSelectedReason(reason);

        Mockito.doReturn(context.getString(R.string.specific_complaint)).when(view).getString(R.string.specific_complaint);

        presenter.proceedToNormalContact(null);

        Mockito.verify(view).displayToast(R.string.validation_no_specific_complaint);

        presenter.referAndCloseContact(null, true);

        Mockito.verify(view, Mockito.times(2)).displayToast(R.string.validation_no_specific_complaint);

    }

    @Test
    public void testValidateQuickCheckWithoutDangerSign() {

        Field reason = new Field("Reason 1", "dbAlias");
        ((QuickCheckPresenter) presenter).setSelectedReason(reason);

        Mockito.doReturn(context.getString(R.string.specific_complaint)).when(view).getString(R.string.specific_complaint);

        presenter.proceedToNormalContact(null);

        Mockito.verify(view).displayToast(R.string.validation_no_danger_sign);

        presenter.referAndCloseContact(null, true);

        Mockito.verify(view, Mockito.times(2)).displayToast(R.string.validation_no_danger_sign);

    }

    @Test
    public void testValidateQuickCheckReferWithNoneDangerSign() {

        Field reason = new Field("Reason 1", "dbAlias");
        ((QuickCheckPresenter) presenter).setSelectedReason(reason);

        Field dangerSign = new Field(context.getString(R.string.danger_none), "dbAlias");
        Set<Field> set = new HashSet<>();
        set.add(dangerSign);

        ((QuickCheckPresenter) presenter).setSelectedDangerSigns(set);

        Mockito.doReturn(context.getString(R.string.scheduled_contact)).when(view).getString(R.string.scheduled_contact);
        Mockito.doReturn(context.getString(R.string.danger_none)).when(view).getString(R.string.danger_none);

        presenter.referAndCloseContact(null, true);

        Mockito.verify(view).displayToast(R.string.validation_no_valid_danger_sign);

    }

    @Test
    public void testProceedQuickCheckSaved() {

        String baseEntityId = UUID.randomUUID().toString();
        Integer contactNo = 1;

        presenter.setBaseEntityId(baseEntityId);
        presenter.setContactNumber(contactNo);
        ((QuickCheckPresenter) presenter).quickCheckSaved(true, true);

        Mockito.verify(view).dismiss();
        Mockito.verify(view, Mockito.times(0)).displayToast(R.string.validation_unable_to_save_quick_check);
        Mockito.verify(view).proceedToContact(baseEntityId, contactNo);
    }

    @Test
    public void testReferQuickCheckSaved() {

        String baseEntityId = UUID.randomUUID().toString();

        Integer contactNo = 1;
        presenter.setBaseEntityId(baseEntityId);
        presenter.setContactNumber(contactNo);

        ((QuickCheckPresenter) presenter).quickCheckSaved(false, true);

        Mockito.verify(view).dismiss();
        Mockito.verify(view, Mockito.times(0)).displayToast(R.string.validation_unable_to_save_quick_check);
        Mockito.verify(view, Mockito.times(0)).proceedToContact(baseEntityId, contactNo);
    }


    @Test
    public void testUnableToSaveQuickCheck() {

        String baseEntityId = UUID.randomUUID().toString();

        Integer contactNo = 1;
        presenter.setBaseEntityId(baseEntityId);
        presenter.setContactNumber(contactNo);

        ((QuickCheckPresenter) presenter).quickCheckSaved(false, false);

        Mockito.verify(view).dismiss();
        Mockito.verify(view).displayToast(R.string.validation_unable_to_save_quick_check);
        Mockito.verify(view, Mockito.times(0)).proceedToContact(baseEntityId, contactNo);
    }

}

