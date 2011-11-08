/*
 * This file is part of the Lokalizator grobów project.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation v3; 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package pl.itiner.nutiteq;


import com.nutiteq.wrappers.Font;
import com.nutiteq.wrappers.Graphics;

import com.nutiteq.components.Label;
import com.nutiteq.components.Point;
import com.nutiteq.log.Log;
import com.nutiteq.utils.Utils;

public class BalloonLabel implements Label {
  public final String name;
  private final String extraInfo;
  private final Font nameFont;
  private final Font extraFont;
  private final int boxWidth;
  private final int boxHeight;

  public BalloonLabel(final String name, final String extraInfo) {
    nameFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    extraFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL);
    this.name = name;
    this.extraInfo = extraInfo;

    boxWidth = Math.max(nameFont.stringWidth(name), extraFont.stringWidth(extraInfo)) + 10;
    boxHeight = nameFont.getHeight() + extraFont.getHeight() + 10;
  }

  public String getLabel() {
    return name + " : " + extraInfo;
  }

  public void paint(final Graphics g, final int screenX, final int screenY, final int displayWidth,
      final int displayHeight) {
    final int boxX = screenX - 20 + boxWidth < displayWidth ? screenX - 20 : displayWidth
        - boxWidth;
    final int boxY = screenY - 20 - boxHeight > 0 ? screenY - 20 - boxHeight : 0;

    g.setColor(0xFF8484FF);
    g.fillTriangle(screenX, screenY, screenX - 15, boxY, screenX + 15, boxY);
    g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

    g.setColor(0xFFFFFFFF);
    g.fillRoundRect(boxX + 1, boxY + 1, boxWidth - 2, boxHeight - 2, 10, 10);

    g.setColor(0xFF000000);
    g.setFont(nameFont);
    g.drawString(name, boxX + boxWidth / 2, boxY + 3, Graphics.TOP | Graphics.HCENTER);
    g.setFont(extraFont);
    g.drawString(extraInfo, boxX + boxWidth / 2, boxY + 3 + nameFont.getHeight(), Graphics.TOP
        | Graphics.HCENTER);
  }

  public boolean pointOnLabel(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight, final int clickX, final int clickY) {
    final int boxX = screenX - 20 + boxWidth < displayWidth ? screenX - 20 : displayWidth
        - boxWidth;
    final int boxY = screenY - 20 - boxHeight > 0 ? screenY - 20 - boxHeight : 0;

    return Utils.rectanglesIntersect(boxX, boxY, boxWidth, boxHeight, clickX, clickY, 1, 1);
  }

  public void labelClicked(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight, final int clickX, final int clickY) {
    Log.debug("Baloon clicked "+name);
  }

  public Point getViewUpdate(final int screenX, final int screenY, final int displayWidth,
      final int displayHeight) {
    return new Point(0, 0);
  }
}
