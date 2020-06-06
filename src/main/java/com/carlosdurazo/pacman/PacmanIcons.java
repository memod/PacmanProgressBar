package com.carlosdurazo.pacman;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface PacmanIcons {
    Icon PAC_GIF_R = IconLoader.getIcon("/pacman-face-right.gif");
    Icon PAC_GIF_L = IconLoader.getIcon("/pacman-face-left.gif");
    Icon CHERRY_PNG = IconLoader.getIcon("/pacman-cherry.png");
    Icon GHOST_PNG = IconLoader.getIcon("/pacman-ghost.png");
    Icon GHOST_VULNERABLE_PNG = IconLoader.getIcon("/pacman-ghost-vulnerable.png");
}
