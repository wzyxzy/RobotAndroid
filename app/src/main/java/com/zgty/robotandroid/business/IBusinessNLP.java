package com.zgty.robotandroid.business;

import android.content.Context;

public abstract class IBusinessNLP {
	
	protected Context mContext;
	private boolean mIsActive;//结束business用，现在只有block性的有用
	
	public IBusinessNLP(Context context){
		mContext = context;
		mIsActive = true;
	}
	
	/**
	 * handle the result of recognize.
	 * @param result
	 * @return true if it's handled; false if not, and should be handled by the next business.
	 */
	public abstract boolean handle(String result, String response);
	/**
	 * callback of recognize error
	 */
	public abstract boolean onError();
	/**
	 * end current task,some task taking a long time need that, such as playing music.
	 */
	public abstract void reset();
	
	public void exit(){
		mIsActive = false;
	}
	
	public boolean isActive(){
		return mIsActive;
	}

	/**
	 * 不支持模糊匹配
	 * @param context
	 * @param id
	 * @param result
	 * @return
	 */
	public static boolean checkAnswer(Context context, int id, String result){
		final String cmd = context.getString(id);
		final String[] cmds = cmd.split(",");
		for (String str : cmds) {
			if (result.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 支持模糊匹配，返回匹配到的指令
	 * @param context
	 * @param id
	 * @param result
	 * @return 匹配到的指令; null, 匹配失败
	 */
	public static String checkAnswerAndGetCmd(Context context, int id, String result){
		final String cmd = context.getString(id);
		final String[] cmds = cmd.split(",");
		for (String str : cmds) {
			if (result.contains(str)) {
				return str;
			}
		}
		return null;
	}
	/**
	 * 不支持模糊匹配
	 * @param answer
	 * @param cmd
	 * @return
	 */
	public static boolean checkAnswer(String cmd, String result) {
		String[] strs = cmd.split(",");
		for (String str : strs) {
			if (result.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}
}
