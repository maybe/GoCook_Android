package com.m6.gocook.biz.account;

public interface OnAccountChangedListener {
	public void onLogin(String email);
	public void onLogout();
}
