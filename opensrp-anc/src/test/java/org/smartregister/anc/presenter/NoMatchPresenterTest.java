package org.smartregister.anc.presenter;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.NoMatchDialogContract;

public class NoMatchPresenterTest extends BaseUnitTest {
	@Mock
	private NoMatchDialogContract.View view;
	
	private NoMatchDialogContract.Presenter presenter;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		presenter = new NoMatchDialogPresenter(view);
	}
	
	@Test
	public void testGoToAdvancedSearch() {
		NoMatchDialogPresenter noMatchDialogPresenter = (NoMatchDialogPresenter) presenter;
		noMatchDialogPresenter.goToAdvancedSearch(BaseUnitTest.WHO_ANC_ID);
		
	}
}
