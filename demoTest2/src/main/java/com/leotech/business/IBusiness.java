package com.leotech.business;

import android.content.Context;

public abstract class IBusiness extends IBusinessNLP{
	
	public IBusiness(Context context){
		super(context);
	}
	/**
	 * handle the result of recognize.
	 * @param result
	 * @return true if it's handled; false if not, and should be handled by the next business.
	 */
	public abstract boolean handle(String result);
	
	@Override
	public boolean handle(String result, String response){
		return handle(result);
	}
	
	@Override
	public boolean onError() {
		return false;
	}
}
