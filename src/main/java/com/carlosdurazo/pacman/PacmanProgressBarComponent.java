package com.carlosdurazo.pacman;

import com.intellij.ide.ui.LafManager;

import javax.swing.*;

public class PacmanProgressBarComponent {
    public PacmanProgressBarComponent() {
        LafManager.getInstance().addLafManagerListener(__ -> updateProgressBarUi());
        updateProgressBarUi();
    }

    private void updateProgressBarUi() {
        UIManager.put("ProgressBarUI", PacmanProgressBarUi.class.getName());
        UIManager.getDefaults().put(PacmanProgressBarUi.class.getName(), PacmanProgressBarUi.class);
    }
}
