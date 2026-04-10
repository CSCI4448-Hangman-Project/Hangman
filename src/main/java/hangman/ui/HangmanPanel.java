package hangman.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class HangmanPanel extends JPanel {
    private int wrongGuessCount;

    public HangmanPanel() {
        setPreferredSize(new Dimension(260, 300));
    }

    public void setWrongGuessCount(int wrongGuessCount) {
        this.wrongGuessCount = wrongGuessCount;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(4f));

        int baseY = 260;
        g2.drawLine(30, baseY, 200, baseY);
        g2.drawLine(70, baseY, 70, 35);
        g2.drawLine(70, 35, 165, 35);
        g2.drawLine(165, 35, 165, 65);

        if (wrongGuessCount >= 1) {
            g2.drawOval(140, 65, 50, 50);
        }
        if (wrongGuessCount >= 2) {
            g2.drawLine(165, 115, 165, 180);
        }
        if (wrongGuessCount >= 3) {
            g2.drawLine(165, 130, 130, 160);
        }
        if (wrongGuessCount >= 4) {
            g2.drawLine(165, 130, 200, 160);
        }
        if (wrongGuessCount >= 5) {
            g2.drawLine(165, 180, 135, 225);
        }
        if (wrongGuessCount >= 6) {
            g2.drawLine(165, 180, 195, 225);
        }

        g2.dispose();
    }
}