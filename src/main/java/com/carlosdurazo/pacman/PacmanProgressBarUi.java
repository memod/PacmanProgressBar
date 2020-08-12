package com.carlosdurazo.pacman;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class PacmanProgressBarUi extends BasicProgressBarUI {
    private static final int INITIAL_GHOST_POS = -5;
    private static final float RUNNING_PACE = 0.2f;
    private static final Color DOT_COLOR = Color.WHITE;
    private volatile int pacmanPosition = 0;
    private volatile int ghostPosition = INITIAL_GHOST_POS;
    private volatile int direction = 1;

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new PacmanProgressBarUi();
    }

    private static boolean isEven(int value) {
        return value % 2 == 0;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(super.getPreferredSize(c).width, JBUI.scale(20));
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        progressBar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
            }
        });
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {

        if (!(g instanceof Graphics2D)) {
            return;
        }
        Graphics2D graphics2D = (Graphics2D) g;

        int width = progressBar.getWidth();
        int height = progressBar.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        if (!isEven(c.getHeight() - height)) height++;

        // Creates the rectangle object used for the background color
        final Area containingRect = new Area(new Rectangle2D.Float(1f, 1f, width - 2f, height - 2f));

        // Sets colors for background used on regular/dark themes
        graphics2D.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        graphics2D.fill(containingRect);

        pacmanPosition += direction;

        if (pacmanPosition <= 8) { // Object is "touching" the left corner
            pacmanPosition = 8;
            direction = 1; // Set to move right
        } else if (pacmanPosition >= width - JBUI.scale(10)) { // Object is "touching" the right corner
            pacmanPosition = width - JBUI.scale(15);
            direction = -1; // Set to move left
        }

        // Assets behavior based on Pacman's direction
        if (direction > 0) { // If pacman is walking to the right
            graphics2D.setColor(DOT_COLOR);
            drawDottedLine(graphics2D, width, height, pacmanPosition);

            // Draw cherry
            PacmanIcons.CHERRY_PNG.paintIcon(progressBar, graphics2D, width - 25, 0);

            // Makes sure that ghost appear when Pacman is half way thru the progress bar
            if (pacmanPosition >= width / 2) {
                ghostPosition = ghostPosition < INITIAL_GHOST_POS ? INITIAL_GHOST_POS + direction :
                        ghostPosition + direction;
                PacmanIcons.GHOST_PNG.paintIcon(progressBar, graphics2D, ghostPosition, 0);
            }


            PacmanIcons.PAC_GIF_R.paintIcon(progressBar, graphics2D, pacmanPosition - JBUI.scale(10), 0);
        } else { // If pacman is walking back
            ghostPosition += (direction - RUNNING_PACE); // at this point, ghost will run from pacman
            PacmanIcons.GHOST_VULNERABLE_PNG.paintIcon(progressBar, graphics2D, ghostPosition, 0);
            PacmanIcons.PAC_GIF_L.paintIcon(progressBar, graphics2D,
                    pacmanPosition - (JBUI.scale(10)), 0);
        }
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) g;

        if (progressBar.getOrientation() != SwingConstants.HORIZONTAL ||
                !c.getComponentOrientation().isLeftToRight()) {
            super.paintDeterminate(g, c);
            return;
        }

        Insets b = progressBar.getInsets(); // area for border

        int width = progressBar.getWidth();
        int height = progressBar.getPreferredSize().height;
        if (!isEven(c.getHeight() - height)) height++;

        if (width <= 0 || height <= 0) {
            return;
        }

        // Creates the rectangle object used for the background color
        final Area containingRoundRect = new Area(new Rectangle2D.Float(1f, 1f, width - 2f, height - 2f));

        // Sets colors for background used on regular/dark themes
        graphics2D.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        graphics2D.fill(containingRoundRect);

        int amountFull = getAmountFull(b, width, height);

        // Draw dotted line
        graphics2D.setColor(DOT_COLOR);
        drawDottedLine(graphics2D, width, height, amountFull);

        // Draw Pacman
        PacmanIcons.PAC_GIF_R.paintIcon(progressBar, graphics2D, amountFull - (JBUI.scale(9) * 2), 0);
    }

    private void drawDottedLine(Graphics2D graphics2D, int width, int height, int amountFull) {
        for (int pos = width; pos > amountFull; pos = pos - 20) {
            graphics2D.fillRoundRect(pos, height / 4,
                    8, 8,
                    13, 13);
        }
    }

}
