package org.smartregister.anc.library.adapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.adapter.ServiceLocationsAdapter;
import org.smartregister.anc.library.activity.BaseUnitTest;

import java.util.ArrayList;
import java.util.List;

public class ServiceLocationsAdapterTest extends BaseUnitTest {
    private ServiceLocationsAdapter serviceLocationsAdapter;

    @Mock
    private ArrayList<String> locationNames;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serviceLocationsAdapter = new ServiceLocationsAdapter(RuntimeEnvironment.application, locationNames);
    }

    @Test
    public void testGetCount() {
        locationNames.addAll(ArgumentMatchers.anyList());
        Assert.assertNotNull(locationNames);

        int size = serviceLocationsAdapter.getCount();
        Assert.assertEquals(0, size);
    }

    @Test
    public void testGetLocationNames() {
        locationNames.addAll(ArgumentMatchers.anyList());
        Assert.assertNotNull(locationNames);

        List<String> arrayList = serviceLocationsAdapter.getLocationNames();
        Assert.assertNotNull(arrayList);
    }

    @Test
    public void testGetLocationAt() {
        Mockito.when(locationNames.get(0)).thenReturn("Test Location");

        String location = serviceLocationsAdapter.getLocationAt(0);
        Assert.assertEquals("Test Location", location);
    }

    @Test
    public void testGetItemId() {
        int position = 90;
        long foundPosition = serviceLocationsAdapter.getItemId(position);
        Assert.assertEquals(2411, foundPosition);
    }
}
