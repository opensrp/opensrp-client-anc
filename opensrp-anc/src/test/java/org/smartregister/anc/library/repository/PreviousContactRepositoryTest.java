package org.smartregister.anc.library.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.PreviousContactsSummaryModel;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;


@RunWith(RobolectricTestRunner.class)
public class PreviousContactRepositoryTest extends BaseUnitTest {

    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private AncLibrary ancLibrary;

    List<PreviousContactsSummaryModel> previousContactFacts = new ArrayList<>();
}
