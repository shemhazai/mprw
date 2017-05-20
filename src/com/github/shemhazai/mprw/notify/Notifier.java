package com.github.shemhazai.mprw.notify;

import com.github.shemhazai.mprw.domain.River;

import java.util.List;

public interface Notifier {

    void warnAboutFlood(List<String> contacts, List<River> rivers);

    void sendVerifyLink(String contact, String link);

    void notifyAdmin(String title, String message);
}
