package com.github.shemhazai.mprw.notify;

import java.util.List;

public interface Notifier {
	public void notifyOne(String contact, String title, String message);

	public void notifyAdmin(String title, String message);

	public void notifyEveryone(List<String> contacts, String title, String message);
}
