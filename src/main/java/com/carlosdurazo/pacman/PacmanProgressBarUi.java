package com.carlosdurazo.pacman;

import com.intellij.openapi.util.ScalableIcon;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class PacmanProgressBarUi extends BasicProgressBarUI {
    private volatile int offset = 0;
    private volatile int objPosition = 0;
    private volatile int direction = 1;
    private static final float arcRoundCorner = JBUIScale.scale(15f);

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
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

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        int w = c.getWidth();
        int h = c.getPreferredSize().height;
        if (!isEven(c.getHeight() - h)) h++;

        // Creates the rectangle object used for the background color
        final Area containingRoundRect = new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, arcRoundCorner, arcRoundCorner));

        // Sets colors for background used on regular/dark themes
        graphics2D.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        graphics2D.fill(containingRoundRect);

        offset = (offset + 1) % getPeriodLength();
        objPosition += direction;

        if (objPosition <= 8) { // Object is "touching" the left corner
            objPosition = 8;
            direction = 1; // Set to move right
        } else if (objPosition >= w - JBUI.scale(10)) { // Object is "touching" the right corner
            objPosition = w - JBUI.scale(15);
            direction = -1; // Set to move left
        }

        Area area = new Area(new Rectangle2D.Float(0, 0, w, h));
        area.subtract(new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, arcRoundCorner, arcRoundCorner)));
        graphics2D.setPaint(Gray._128);
        if (c.isOpaque()) {
            graphics2D.fill(area);
        }

        area.subtract(new Area(new RoundRectangle2D.Float(0, 0, w, h, arcRoundCorner, arcRoundCorner)));

        if (c.isOpaque()) {
            graphics2D.fill(area);
        }

        Icon scaledIcon = direction > 0 ? // Is the object moving to the right?
                ((ScalableIcon) PacmanIcons.PAC_GIF_R):
                ((ScalableIcon) PacmanIcons.PAC_GIF_L);

        scaledIcon.paintIcon(progressBar, graphics2D, objPosition - JBUI.scale(10), 0);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(graphics2D, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width);
            } else {
                paintString(graphics2D, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height);
            }
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

        int w = progressBar.getWidth();
        int h = progressBar.getPreferredSize().height;
        if (!isEven(c.getHeight() - h)) h++;

        final float R = JBUIScale.scale(15f);
        final float R2 = JBUIScale.scale(9f);

        if (w <= 0 || h <= 0) {
            return;
        }

        // Creates the rectangle object used for the background color
        final Area containingRoundRect = new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R));

        // Sets colors for background used on regular/dark themes
        graphics2D.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        graphics2D.fill(containingRoundRect);

        int amountFull = getAmountFull(b, w, h);
        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();

        g.setColor(background);

        PacmanIcons.PAC_GIF_R.paintIcon(progressBar, graphics2D, amountFull - (JBUI.scale(9) * 2), 0);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top,
                    w, h,
                    amountFull, b);
        }
    }

    private void paintString(Graphics g, int x, int y, int w, int h, int fillStart, int amountFull) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        String progressString = progressBar.getString();
        g2.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g2, progressString,
                x, y, w, h);
        Rectangle oldClip = g2.getClipBounds();

        g2.setClip(oldClip);
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength;
    }

    private int getPeriodLength() {
        return JBUI.scale(16);
    }
}
