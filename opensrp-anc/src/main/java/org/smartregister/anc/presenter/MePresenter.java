package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.model.MeFragmentModel;

import java.lang.ref.WeakReference;

public class MePresenter implements MeContract.Presenter {
	private WeakReference<MeContract.View> meView;
	private MeContract.Model model;
	
	public MePresenter(MeContract.View view) {
		this.meView = new WeakReference<>(view);
		this.model = new MeFragmentModel();
	}
	
	protected MeContract.View getView() {
		if (meView != null)
			return meView.get();
		else
			return null;
	}
}
