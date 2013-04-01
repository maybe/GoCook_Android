package com.m6.gocook.biz.account;

public interface OnAccountChangedListener {
	
	public void onLogin(String email, String avatarUrl, String userName);
	
	public void onLogout();
	
	public void onRegister(String email, String avatarUrl, String userName);
}
