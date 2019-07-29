package org.smartregister.anc.library.presenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.view.contract.MeContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MePresenterTest extends BaseUnitTest {
    @Mock
    private MeContract.View view;

    @Mock
    private MeContract.Model model;

    private MeContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new MePresenter(view);
    }

    @Test
    public void testPresenterInitializedCorrectly() {
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMM yyyy";
        ReflectionHelpers.setStaticField(CoreLibrary.class, "buildTimeStamp", System.currentTimeMillis());
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, presenter.getBuildDate());
    }

    @Test
    public void testUpdateInitials() {
        MePresenter mePresenter = (MePresenter) presenter;
        mePresenter.setModel(model);

        String initials = "TR";
        Mockito.doReturn(initials).when(model).getInitials();

        presenter.updateInitials();

        Mockito.verify(view).updateInitialsText(initials);
    }

    @Test
    public void testUpdateName() {
        MePresenter mePresenter = (MePresenter) presenter;
        mePresenter.setModel(model);

        String name = "ANC Reference";
        Mockito.doReturn(name).when(model).getName();

        presenter.updateName();

        Mockito.verify(view).updateNameText(name);
    }

}
