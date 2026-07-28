package arpm.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Shared color palette and fonts used across the application.
 */
public final class Theme {

    private Theme() {
    }

    public static final Color NAVY_DARK = new Color(0x0F, 0x18, 0x3D);
    public static final Color NAVY = new Color(0x18, 0x22, 0x52);
    public static final Color NAVY_LIGHT = new Color(0x22, 0x2F, 0x66);

    public static final Color GOLD = new Color(0xE8, 0xC1, 0x3A);
    public static final Color GOLD_DARK = new Color(0xC9, 0x9A, 0x1A);

    public static final Color GREEN = new Color(0x2E, 0x7D, 0x32);

    public static final Color TEXT_LIGHT = new Color(0xE8, 0xEC, 0xF7);
    public static final Color TEXT_MUTED = new Color(0xB7, 0xBE, 0xD8);

    public static final Color RED_DARK = new Color(0x7A, 0x1F, 0x1F);
    public static final Color RED_TEXT = new Color(0xFF, 0xB3, 0xA8);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
}
