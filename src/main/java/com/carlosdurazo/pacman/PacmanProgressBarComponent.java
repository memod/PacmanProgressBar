package com.carlosdurazo.pacman;

import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;

public class PacmanProgressBarComponent {
    public PacmanProgressBarComponent() {
        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
        messageBusConnection.subscribe(LafManagerListener.TOPIC, __ -> updateProgressBarUi());

        updateProgressBarUi();
    }

    private void updateProgressBarUi() {
        UIManager.put("ProgressBarUI", PacmanProgressBarUi.class.getName());
        UIManager.getDefaults().put(PacmanProgressBarUi.class.getName(), PacmanProgressBarUi.class);
    }

}
