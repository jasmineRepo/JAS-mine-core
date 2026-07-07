package microsim.gui.shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The panel used by AboutFrame window.
 *
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public class AboutPanel extends JPanel {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    BorderLayout borderLayout1 = new BorderLayout();

    ImageIcon imageJAS = new ImageIcon(
            java.awt.Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/microsim/gui/icons/logo_2.png")));

    /** Create a new about panel. */
    public AboutPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        this.setLayout(borderLayout1);
    }

    /**
     * Draw the panel content.
     * 
     * @param g The graphic device context.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        int leftCorner = 0;
        int upperCorner = 0;
        int areaHeight = imageJAS.getIconHeight() + 80;

        if (getWidth() > imageJAS.getIconWidth())
            leftCorner = (getWidth() - imageJAS.getIconWidth() - 40) / 2;

        if (getHeight() > areaHeight)
            upperCorner = (getHeight() - areaHeight) / 2;

        g.drawImage(imageJAS.getImage(), leftCorner, upperCorner,
                imageJAS.getIconWidth(), imageJAS.getIconHeight(), this);

        g.setColor(Color.black);
        Font font = new Font("Arial", Font.BOLD, 12);
        Font font2 = new Font("Script", Font.BOLD, 12);
        g.setFont(font);
        // int start = 160 + upperCorner;
        int start = 30 + upperCorner;
        leftCorner += 160;
        g.drawString("JAS-mine", leftCorner + 10, start + 10);

        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        g.drawString("Copyright (C) 2014-" + sdf.format(new Date()) + " Ross E. Richardson", leftCorner + 10,
                start + 30);
        g.drawString("& Matteo Richiardi", leftCorner + 135, start + 50);
        g.setFont(font2);
        g.drawString("https://github.com/jasmineRepo", leftCorner + 10, start + 70);
        // g.setFont(font2);
        g.setColor(new Color(63, 0xc3, 0xe7));
        g.drawString("http://www.jas-mine.net", leftCorner + 10, start + 90);
        g.setColor(Color.black);
        g.setFont(font);
        g.drawString("Distributed under GNU Lesser General Public License", leftCorner - 40, start + 110);
    }
}
