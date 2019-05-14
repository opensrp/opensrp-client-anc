package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.ContactSummarySendContract;
import org.smartregister.anc.util.AppExecutors;

import java.util.concurrent.Executors;

public class ContactSummaryInteractorTest extends BaseUnitTest {
    private ContactSummarySendContract.Interactor interactor;

    @Before
    public void setUp() {
        interactor = new ContactSummaryInteractor(
                new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(),
                        Executors.newSingleThreadExecutor()));
    }

    @Test(expected = NullPointerException.class)
    public void testGetPreviousContactNo() {
        ContactSummaryInteractor registerInteractor = (ContactSummaryInteractor) interactor;
        Assert.assertEquals(registerInteractor.getPreviousContactNo("-1"),0);
        Assert.assertEquals(registerInteractor.getPreviousContactNo("-8"),7);
        Assert.assertEquals(registerInteractor.getPreviousContactNo(null),0);
    }
}
