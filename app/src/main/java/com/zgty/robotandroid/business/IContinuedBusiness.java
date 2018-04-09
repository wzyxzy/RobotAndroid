package com.zgty.robotandroid.business;

import android.content.Context;

public abstract class IContinuedBusiness extends IBusiness {
	
	private final int exitCount = 3;
	private int validityCount = 0;

	public IContinuedBusiness(Context context) {
		super(context);
	}

	@Override
	public boolean handle(String result) {
		handleResult(result);
		if(validityCount >= exitCount){
			this.exit();
		}
		return true;
	}

	protected abstract void handleResult(String result);
	
	protected void resultValid(){
		validityCount = 0;
	}
	
	protected void resultNotValid(){
		validityCount++;
	}
}
