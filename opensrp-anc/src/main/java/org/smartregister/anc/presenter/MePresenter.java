package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.MeContract;

import java.lang.ref.WeakReference;

public class MePresenter implements MeContract.Presenter {
	private WeakReference<MeContract.View> meView;
	
	public MePresenter(MeContract.View view) {
		this.meView = new WeakReference<>(view);
	}
}
