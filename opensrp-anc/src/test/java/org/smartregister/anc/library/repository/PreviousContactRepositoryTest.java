package org.smartregister.anc.library.repository;


import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.PreviousContact;

import java.util.List;

public class PreviousContactRepositoryTest extends BaseUnitTest {

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private PreviousContactRepository previousContactRepository;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        previousContactRepository = Mockito.spy(new PreviousContactRepository());
    }

    @Test
    public void testGetPreviousContactsShouldReturnPreviousLists() {
        String baseEntityId = "3243-4wew-323";
        String[] keysArr = new String[]{"contact_date"};

        Mockito.doReturn(0).when(cursor)
                .getColumnIndex(PreviousContactRepository.ID);
        Mockito.doReturn(1).when(cursor)
                .getColumnIndex(PreviousContactRepository.KEY);
        Mockito.doReturn(2).when(cursor)
                .getColumnIndex(PreviousContactRepository.VALUE);
        Mockito.doReturn(3).when(cursor)
                .getColumnIndex(PreviousContactRepository.BASE_ENTITY_ID);


        Mockito.doReturn(1L).when(cursor)
                .getLong(0);
        Mockito.doReturn("contact_date").when(cursor)
                .getString(1);
        Mockito.doReturn("2020-08-08").when(cursor)
                .getString(2);
        Mockito.doReturn(baseEntityId).when(cursor)
                .getString(3);

        Mockito.doAnswer(new Answer() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (count > 0) {
                    return false;
                }
                count++;
                return true;
            }
        }).when(cursor).moveToNext();

        Mockito.doReturn(sqLiteDatabase)
                .when(previousContactRepository).getReadableDatabase();

        Mockito.doReturn(cursor).when(sqLiteDatabase)
                .query(Mockito.eq(PreviousContactRepository.TABLE_NAME),
                        Mockito.any(String[].class),
                        Mockito.anyString(),
                        Mockito.any(String[].class),
                        Mockito.eq(null),
                        Mockito.eq(null),
                        Mockito.anyString(),
                        Mockito.eq(null));


        List<PreviousContact> previousContactList = previousContactRepository.getPreviousContacts(baseEntityId, keysArr);
        Assert.assertEquals(1, previousContactList.size());
    }

}