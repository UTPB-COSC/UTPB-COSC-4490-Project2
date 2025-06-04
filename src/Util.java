package src;

import java.awt.Toolkit;

public class Util {
	public static final double SCREEN_WIDTH;
	public static final double SCREEN_HEIGHT;
	public static final Toolkit TK = Toolkit.getDefaultToolkit();

	static {
		SCREEN_WIDTH = TK.getScreenSize().width;
		SCREEN_HEIGHT = TK.getScreenSize().height;
	}

}
