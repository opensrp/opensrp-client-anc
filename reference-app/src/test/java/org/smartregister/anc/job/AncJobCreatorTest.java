package org.smartregister.anc.job;

import com.evernote.android.job.Job;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.BaseUnitTest;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
public class AncJobCreatorTest extends BaseUnitTest {

    private AncJobCreator jobCreator;

    @Before
    public void setUp() {

        jobCreator = new AncJobCreator();
    }

    @Test
    public void testAncJobCreatorInstantiatesCorrectly() {
        Assert.assertNotNull(jobCreator);
    }

    @Test
    public void testAncJobCreatorCreatesCorrectJobForJobTag() {
        //Sync job
        Job job = jobCreator.create(SyncServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof SyncServiceJob);

        //Extended Sync Service Job
        job = jobCreator.create(ExtendedSyncServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ExtendedSyncServiceJob);

        //Images upload Service Job
        job = jobCreator.create(ImageUploadServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ImageUploadServiceJob);

        //Pull unique IDs Service Job
        job = jobCreator.create(PullUniqueIdsServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof PullUniqueIdsServiceJob);

        //Validate Sync Service Job
        job = jobCreator.create(ValidateSyncDataServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ValidateSyncDataServiceJob);

        //View configs Service Job
        job = jobCreator.create(ViewConfigurationsServiceJob.TAG);
        Assert.assertNotNull(job);
        Assert.assertTrue(job instanceof ViewConfigurationsServiceJob);

    }
}
