package com.github.shemhazai.mprw.notify;

import java.util.List;

import com.github.shemhazai.mprw.domain.River;

public interface Notifier {

  void warnAboutFlood(List<String> contacts, List<River> rivers);

  void sendVerifyLink(String contact, String link);

  void notifyAdmin(String title, String message);
}
