package com.t23.con.entity;

import java.util.List;

import com.t23.con.Game;
import com.t23.con.InputHandler;
import com.t23.con.entity.particle.TextParticle;
import com.t23.con.gfx.Color;
import com.t23.con.gfx.Screen;
import com.t23.con.item.Item;
import com.t23.con.level.Level;
import com.t23.con.level.tile.Tile;

public class Player extends Mob {
	private InputHandler input;
	private int attackTime, attackDir;

	public Game game;
	public Inventory inventory = new Inventory();
	public Item attackItem;
	public Item activeItem;
	public int stamina;
	public int staminaRecharge;
	public int staminaRechargeDelay;
	public int score;
	public int maxStamina = 10;
	private int onStairDelay;
	public int invulnerableTime = 0;

	public Player(Game game, InputHandler input) {
		this.game = game;
		this.input = input;
		x = 24;
		y = 24;
		stamina = 0;

		// inventory.add(new FurnitureItem(new Workbench()));
		// inventory.add(new PowerGloveItem());
	}

	public void tick() {
		super.tick();

		if (invulnerableTime > 0)
			invulnerableTime--;
		// Tile onTile = level.getTile(x >> 4, y >> 4);

		// if (stamina <= 0 && staminaRechargeDelay == 0 && staminaRecharge == 0) {
		// staminaRechargeDelay = 40;
		// }

		// if (staminaRechargeDelay > 0) {
		// staminaRechargeDelay--;
		// }

		if (staminaRechargeDelay == 0) {
			staminaRecharge++;
			while (staminaRecharge > 50) {
				staminaRecharge -= 50;
				if (stamina < maxStamina) {
					stamina++;
				} else {
					this.doHurt(1, 0);
				}
			}
		}

		int xa = 0;
		int ya = 0;
		if (input.up.down)
			ya--;
		if (input.down.down)
			ya++;
		if (input.left.down)
			xa--;
		if (input.right.down)
			xa++;

		if (staminaRechargeDelay % 2 == 0) {
			move(xa, ya);
		}

		if (input.attack.clicked) {
			if (stamina != 0) {
				attack();
			}
		}

		// TODO inventory?
		// if (input.menu.clicked) {
		// if (!use()) {
		// // game.setMenu(new InventoryMenu(this));
		// }
		// }

		if (attackTime > 0)
			attackTime--;
	}

	private boolean use() {
		int yo = -2;
		if (dir == 0 && use(x - 8, y + 4 + yo, x + 8, y + 12 + yo))
			return true;
		if (dir == 1 && use(x - 8, y - 12 + yo, x + 8, y - 4 + yo))
			return true;
		if (dir == 3 && use(x + 4, y - 8 + yo, x + 12, y + 8 + yo))
			return true;
		if (dir == 2 && use(x - 12, y - 8 + yo, x - 4, y + 8 + yo))
			return true;

		int xt = x >> 4;
		int yt = (y + yo) >> 4;
		int r = 12;
		if (attackDir == 0)
			yt = (y + r + yo) >> 4;
		if (attackDir == 1)
			yt = (y - r + yo) >> 4;
		if (attackDir == 2)
			xt = (x - r) >> 4;
		if (attackDir == 3)
			xt = (x + r) >> 4;

		if (xt >= 0 && yt >= 0 && xt < level.w && yt < level.h) {
			if (level.getTile(xt, yt).use(level, xt, yt, this, attackDir))
				return true;
		}

		return false;
	}

	private void attack() {
		walkDist += 8;
		attackDir = dir;
		attackItem = activeItem;
		boolean done = false;

		attackTime = 10;
		int yo = -2;
		int range = 12;
		if (dir == 0 && interact(x - 8, y + 4 + yo, x + 8, y + range + yo))
			done = true;
		if (dir == 1 && interact(x - 8, y - range + yo, x + 8, y - 4 + yo))
			done = true;
		if (dir == 3 && interact(x + 4, y - 8 + yo, x + range, y + 8 + yo))
			done = true;
		if (dir == 2 && interact(x - range, y - 8 + yo, x - 4, y + 8 + yo))
			done = true;
		if (done)
			return;

		int xt = x >> 4;
		int yt = (y + yo) >> 4;
		int r = 12;
		if (attackDir == 0)
			yt = (y + r + yo) >> 4;
		if (attackDir == 1)
			yt = (y - r + yo) >> 4;
		if (attackDir == 2)
			xt = (x - r) >> 4;
		if (attackDir == 3)
			xt = (x + r) >> 4;

		if (level.getTile(xt, yt).interact(level, xt, yt, this, activeItem,
				attackDir)) {
			stamina--;
			staminaRecharge = 0;
			done = true;
		}
	}

	private boolean use(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e != this)
				if (e.use(this, attackDir))
					return true;
		}
		return false;
	}

	private boolean interact(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e != null && e != this)
				if (e.interact(this, activeItem, attackDir))
					return true;
		}
		return false;
	}

	private void hurt(int x0, int y0, int x1, int y1) {
		List<Entity> entities = level.getEntities(x0, y0, x1, y1);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e != this)
				e.hurt(this, getAttackDamage(e), attackDir);
		}
	}

	private int getAttackDamage(Entity e) {
		int dmg = random.nextInt(3) + 1;
		if (attackItem != null) {
			dmg += attackItem.getAttackDamageBonus(e);
		}
		
		return dmg;
	}

	public void render(Screen screen) {
		int xt = 0;
		int yt = 14;

		int flip1 = (walkDist >> 3) & 1;
		int flip2 = (walkDist >> 3) & 1;

		if (dir == 1) {
			xt += 2;
		}
		if (dir > 1) {
			flip1 = 0;
			flip2 = ((walkDist >> 4) & 1);
			if (dir == 2) {
				flip1 = 1;
			}
			xt += 4 + ((walkDist >> 3) & 1) * 2;
		}

		int xo = x - 8;
		int yo = y - 11;

//		if (attackTime > 0 && attackDir == 1) {
//			screen.render(xo + 0, yo - 4, 6 + 13 * 32,
//					Color.get(-1, 555, 555, 555), 0);
//			screen.render(xo + 8, yo - 4, 6 + 13 * 32,
//					Color.get(-1, 555, 555, 555), 1);
//			if (attackItem != null) {
//				attackItem.renderIcon(screen, xo + 4, yo - 4);
//			}
//		}
		
		int col = Color.get(-1, 100, 220, 532);
		if (hurtTime > 0) {
			col = Color.get(-1, 555, 555, 555);
		}

		// TODO
		// if (activeItem instanceof FurnitureItem) {
		// yt += 2;
		// }

		screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1);
		screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1);
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col,
				flip2);

		//
		// if (attackTime > 0 && attackDir == 2) {
		// screen.render(xo - 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555),
		// 1);
		// screen.render(xo - 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555,
		// 555), 3);
		// if (attackItem != null) {
		// attackItem.renderIcon(screen, xo - 4, yo + 4);
		// }
		// }
		// if (attackTime > 0 && attackDir == 3) {
		// screen.render(xo + 8 + 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555,
		// 555), 0);
		// screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555,
		// 555, 555), 2);
		// if (attackItem != null) {
		// attackItem.renderIcon(screen, xo + 8 + 4, yo + 4);
		// }
		// }
		// if (attackTime > 0 && attackDir == 0) {
		// screen.render(xo + 0, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555,
		// 555, 555), 2);
		// screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555,
		// 555, 555), 3);
		// if (attackItem != null) {
		// attackItem.renderIcon(screen, xo + 4, yo + 8 + 4);
		// }
		// }

		// TODO
		// if (activeItem instanceof FurnitureItem) {
		// Furniture furniture = ((FurnitureItem) activeItem).furniture;
		// furniture.x = x;
		// furniture.y = yo;
		// furniture.render(screen);
		// }
	}

	public void touchItem(ItemEntity itemEntity) {
		itemEntity.take(this);
		inventory.add(itemEntity.item);
	}

	public boolean canSwim() {
		return true;
	}

	public boolean findStartPos(Level level) {
		while (true) {
			int x = random.nextInt(level.w);
			int y = random.nextInt(level.h);
			if (level.getTile(x, y) == Tile.grass) {
				this.x = x * 16 + 8;
				this.y = y * 16 + 8;
				return true;
			}
		}
	}

	public boolean payStamina(int cost) {
		if (cost > stamina)
			return false;
		stamina -= cost;
		return true;
	}

	public void changeLevel(int dir) {
		// TODO
		// game.scheduleLevelChange(dir);
	}

	public int getLightRadius() {
		int r = 2;
		if (activeItem != null) {
			// TODO
			// if (activeItem instanceof FurnitureItem) {
			// int rr = ((FurnitureItem) activeItem).furniture.getLightRadius();
			// if (rr > r) r = rr;
			// }
		}
		return r;
	}

	protected void die() {
		super.die();
		// TODO
		// Sound.playerDeath.play();
	}

	protected void touchedBy(Entity entity) {
		if (!(entity instanceof Player)) {
			entity.touchedBy(this);
		}
	}

	protected void doHurt(int damage, int attackDir) {
		if (hurtTime > 0 || invulnerableTime > 0)
			return;

		// TODO
		// Sound.playerHurt.play();
		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 504, 504,
				504)));
		health -= damage;
		// if (attackDir == 0) yKnockback = +6;
		// if (attackDir == 1) yKnockback = -6;
		// if (attackDir == 2) xKnockback = -6;
		// if (attackDir == 3) xKnockback = +6;
		hurtTime = 10;
		invulnerableTime = 30;
	}

	public void gameWon() {
		level.player.invulnerableTime = 60 * 5;
		game.won();
	}
}