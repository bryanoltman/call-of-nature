package com.t23.con.screen;

import com.t23.con.gfx.Color;
import com.t23.con.gfx.Font;
import com.t23.con.gfx.Screen;
import com.t23.con.screen.Menu;

public class AboutMenu extends Menu {
	private Menu parent;

	public AboutMenu(Menu parent) {
		this.parent = parent;
	}

	public void tick() {
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);

		Font.draw("About Call of Nature", screen, 0 * 8, 1 * 8, Color.get(0, 555, 555, 555));
		Font.draw("Call of nature is", screen, 0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333));
		Font.draw("a mod of Minicraft,", screen, 0 * 8 + 4, 4 * 8, Color.get(0, 333, 333, 333));
		Font.draw("made by Markus", screen, 0 * 8 + 4, 5 * 8, Color.get(0, 333, 333, 333));
		Font.draw("Persson.", screen, 0 * 8 + 4, 6 * 8, Color.get(0, 333, 333, 333));
	}
}
