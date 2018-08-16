package org.smartregister.anc.presenter;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.NoMatchDialogContract;
import org.smartregister.anc.contract.ProfileContract;

public class NoMatchPresenterTest extends BaseUnitTest {
	@Mock
	private NoMatchDialogContract.View view;
	
	private NoMatchDialogContract.Presenter presenter;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		presenter = new NoMatchDialogPresenter(view);
	}
	
	
}
