package org.smartregister.anc.library.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.model.PreviousContactsSummaryModel;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;


@RunWith(RobolectricTestRunner.class)
public class PreviousContactRepositoryTest extends BaseUnitTest {

    public static final String TABLE_NAME = "previous_contact";
    protected static final String DUMMY_BASE_ENTITY_ID = "4faf5afa-fa7f-4d98-b4cd-4ee39c8d1eb1";
    protected static final String CONTACT_NO = "1";
    protected static final String KEY = "attention_flag_facts";
    private static final PreviousContact previousContact = new PreviousContact();
    String orderBy = "order by abs_contact_no, contact_no,_id DESC";
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private DrishtiApplication drishtiApplication;
    @Mock
    private AncLibrary ancLibrary;
    @Mock
    private PreviousContactRepository previousContactRepository;
    private List<PreviousContactsSummaryModel> previousContactFacts = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());

    }

    @Test
    public void testGetPreviousContactFacts() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        PowerMockito.when(previousContactRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sql = "select *,  abs(" + CONTACT_NO + ") as abs_contact_no from " + TABLE_NAME + " where "
                + DUMMY_BASE_ENTITY_ID + " = ? and ( " + KEY + " = ? or " + KEY + " = ? or " + KEY + " = ? or " + KEY +
                " = ? or " + KEY + " = ? ) " + orderBy;
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(sqLiteDatabase.rawQuery(sql, new String[]{DUMMY_BASE_ENTITY_ID, KEY})).thenReturn(cursor);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.getColumnName(1)).thenReturn(ConstantsUtils.CONTACT_NO);
        PowerMockito.when(cursor.getColumnName(2)).thenReturn(ConstantsUtils.KeyUtils.VALUE);
        previousContactFacts =ancLibrary.getPreviousContactRepository().getPreviousContactsFacts(DUMMY_BASE_ENTITY_ID);
        Assert.assertNotNull(previousContactFacts);


    }

//    @Test
//    public void getPreviousContact() {
//        previousContact.setContactNo("2");
//        previousContact.setBaseEntityId("4faf5afa-fa7f-4d98-b4cd-4ee39c8d1eb1");
//        previousContact.setKey("weight_cat");
//        Assert.assertNotNull(previousContact);
//        PreviousContact returnPreviousContact = ancLibrary.getPreviousContactRepository().getPreviousContact(previousContact);
//        Assert.assertNotNull(returnPreviousContact);
//
//    }
}
