package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.main;
import render.entity.*;
import render.events.KeyEvents;
import render.events.MouseEvents;
import render.imageUtil.SpriteLoader;

public class Renderer extends JPanel{
	private Graphics2D g2;
	
	//Main vars
	private main m;
	private int fps = 0;
	
	//General menu stuff
	private int keyZpress = 0;
	private int plane_preview_x = 100;
	private int plane_preview_y = 100;
	
	//Player stuff
	private int player_width = 250;
	private int player_height = 250;
	private int player_start_x = 550;
	private int player_start_y = 450;
	private int intro_runway_x = 950;
	private double intro_plane_width = 50;
	private double intro_plane_height = 50;
	private double intro_plane_x = 450;
	private double intro_plane_y = 250;
	private double angle = 0;
	public int playerSpriteChosenX = 1;
	public int playerSpriteChosenY = 1;
	public int playerPlaneIndex = 1;
	
	//Enemy Stuff
	private int enemy_width = 250;
	private int enemy_height = 250;
	private int enemy_start_x = 550;
	private int enemy_start_y = 450;
	public int enemySpriteChosenX = 1;
	public int enemySpriteChosenY = 1;
	public int enemyPlaneIndex = 1;
	//Classes
	private KeyEvents keyEvent = new KeyEvents();
	private MouseEvents mouseEvent = new MouseEvents();
	private SpriteLoader spriteLoader = new SpriteLoader();
	
	//Entities
	public Player player;
	public Enemy enemy;
	//Images / files
	private File playerSpritesF;
	private File enemySpritesF;
	private File guiSpritesF;
	private File intro_runwayF;
	private BufferedImage playerSprites;
	private BufferedImage enemySprites;
	private BufferedImage guiSprites;
	private BufferedImage intro_runway;
	//Game settings
	public boolean gameStarted = false;
	public boolean introStart = false;
	public boolean choosingPlayer = false;
	public boolean choosingEnemy = false;
	private boolean showMainScreen = true;
	private boolean showingMenu = true;
	private boolean introDone = false;
	private boolean startedIntroThread1 = false;
	private boolean startedIntroThread2 = false;
	private BufferedImage playerSSprite = null;
	private BufferedImage enemySSprite = null;
	
	public void init(main m) {
		this.m = m;
		m.addKeyListener(keyEvent);
		mouseEvent.init(this);
		m.addMouseListener(mouseEvent);
		//Set variables
		try {
			playerSpritesF = new File(this.getClass().getResource("res/playerSprites.png").toURI());
			playerSprites = ImageIO.read(playerSpritesF);
			
			enemySpritesF = new File(this.getClass().getResource("res/enemySprites.png").toURI());
			enemySprites = ImageIO.read(enemySpritesF);
			
			guiSpritesF = new File(this.getClass().getResource("res/guiSprites.png").toURI());
			guiSprites = ImageIO.read(guiSpritesF);
			
			intro_runwayF = new File(this.getClass().getResource("res/runway.png").toURI());
			intro_runway = ImageIO.read(intro_runwayF);
		}catch (Exception e) {
			System.out.println("Resource loading error");
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.g2 = g2;
		fps = m.fps;
		
		if (showMainScreen == true) {
			renderMainScreen();
		}
		if (gameStarted) {
			gameInit();
			//create clouds
			
			//Measuring markers, does lag the game,
			//but it lets me see where the plane is moving and how fast.
			for (int i = 0; i < 1200; i++) {
				Random r = new Random();
				g2.setColor(Color.BLACK);
				g2.fillRect(player.getWidth()+i*50, player.getHeight(), 50, 50);
				g2.fillRect(player.getWidth()-i*50, player.getHeight(), 50, 50);
				g2.fillRect(player.getWidth(), player.getHeight()-i*50, 50, 50);
				g2.fillRect(player.getWidth(), player.getHeight()+i*50, 50, 50);
			}	
			
			//render entities (player, enemy)
			renderEnemy();
			renderPlayer();
		}
		
		if (keyEvent.keyZ == true && keyZpress < 4 && introStart == false) {
			keyEvent.keyZ = false;
			keyZpress++;
	
			switch (keyZpress) {
			case 1:
				showingMenu = false;
				choosingPlayer = true;
				break;
			case 2:
				choosingPlayer = false;
				choosingEnemy = true;
				break;
			case 3:
				choosingEnemy = false;
				introStart = true;
				break;
			}
		}
		if (introDone == true) {
			playerSSprite = spriteLoader.loadPlayerSprite(playerSprites, playerSpriteChosenX, playerSpriteChosenY);
			enemySSprite = spriteLoader.loadEnemySprite(enemySprites, enemySpriteChosenX, enemySpriteChosenY);
			gameStarted = true;
		}
	}
	public void renderUI(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.g2 = g2;
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial",Font.BOLD,30));
		g2.drawString("UPDATES: "+fps, 10, 50);
	}
	public void renderMainScreen() {
		if (showingMenu == true) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial",Font.BOLD,30));
			g2.drawString("DOGFIGHTER 8.0: Battle of the aces", 190, 50);
			g2.drawString("Press Z to choose player", 500, 400);
			
			
			if (guiSprites == null) {
				return;
			}
			
			BufferedImage plane_image = spriteLoader.loadGUISprite(guiSprites, 1, 1);
			AffineTransform pos = AffineTransform.getTranslateInstance(plane_preview_x, plane_preview_y);
			pos.rotate(Math.toRadians(angle+=0.1),plane_image.getWidth()/2,plane_image.getHeight()/2);
			g2.drawImage(plane_image, pos, this);
		}else if (choosingPlayer == true) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial",Font.BOLD,30));
			g2.drawString("Choose your player", 190, 50);
			g2.drawString("Press Z to choose enemy", 500, 400);
			
			
			if (playerSprites == null) {
				return;
			}
			if (playerSpriteChosenX > 10) { 
				playerSpriteChosenX = 1;
				playerSpriteChosenY++;
			}
			
			if (mouseEvent.back == true && playerSpriteChosenY > 1 && playerSpriteChosenX == 1) {
				mouseEvent.back = false;
				playerSpriteChosenY--;
				playerSpriteChosenX = 10;
			}
			
			playerPlaneIndex = 10*(playerSpriteChosenY-1)+playerSpriteChosenX;
			
			BufferedImage plane_image = spriteLoader.loadPlayerSprite(playerSprites, playerSpriteChosenX, playerSpriteChosenY);
			AffineTransform pos = AffineTransform.getTranslateInstance(plane_preview_x, plane_preview_y);
			pos.rotate(Math.toRadians(angle+=0.1),plane_image.getWidth()/2,plane_image.getHeight()/2);
			g2.drawImage(plane_image, pos, this);
			
			g2.setColor(Color.WHITE);
			g2.drawOval(0, 500, 100, 100);
			g2.drawOval(300, 500, 100, 100);
			g2.setFont(new Font("Arial",Font.BOLD,22));
			g2.drawString("Previous",5, 560);
			g2.drawString("Next",325, 560);
			g2.drawString("Plane "+playerPlaneIndex, 165, 560);
			
		}else if (choosingEnemy == true) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial",Font.BOLD,30));
			g2.drawString("Choose your enemy", 190, 50);
			g2.drawString("Press Z to start", 500, 400);
			
			
			if (guiSprites == null) {
				return;
			}
			
			if (playerSpriteChosenX > 10) { 
				enemySpriteChosenX = 1;
				enemySpriteChosenY++;
			}
			
			if (mouseEvent.back == true && playerSpriteChosenY > 1 && playerSpriteChosenX == 1) {
				mouseEvent.back = false;
				enemySpriteChosenY--;
				enemySpriteChosenX = 10;
			}
			
			enemyPlaneIndex = 10*(enemySpriteChosenY-1)+enemySpriteChosenX;
			
			BufferedImage plane_image = spriteLoader.loadGUISprite(guiSprites, 1, 1);
			AffineTransform pos = AffineTransform.getTranslateInstance(plane_preview_x, plane_preview_y);
			pos.rotate(Math.toRadians(angle+=0.1),plane_image.getWidth()/2,plane_image.getHeight()/2);
			g2.drawImage(plane_image, pos, this);
			
			g2.setColor(Color.WHITE);
			g2.drawOval(0, 500, 100, 100);
			g2.drawOval(300, 500, 100, 100);
			g2.setFont(new Font("Arial",Font.BOLD,22));
			g2.drawString("Previous",5, 560);
			g2.drawString("Next",325, 560);
			g2.drawString("Plane "+enemyPlaneIndex, 165, 560);
		}else if (introStart == true) {
			renderIntro();
		}
		
	}
	
	public void gameInit() {
		createPlayer();
		createEnemy();
	}
	
	public void createPlayer() {
		if (player == null) {
			player = new Player(g2,playerSSprite);
			player.setWidth(player_width);
			player.setHeight(player_height);
			player.setX(player_start_x-player.getWidth());
			player.setY(player_start_y-player.getHeight());
		}else {
			double oldPX, oldPY, oldDir, oldVelX, oldVelY;
			oldPX = player.getPX();
			oldPY = player.getPY();
			oldVelX = player.getVelx();
			oldVelY = player.getVely();
			oldDir = player.getDirection();
			player = null;
			player = new Player(g2,playerSSprite);
			player.setWidth(player_width);
			player.setHeight(player_height);
			player.setX(oldPX);
			player.setY(oldPY);
			player.setVelx(oldVelX);
			player.setVely(oldVelY);
			player.setDirection(oldDir);
			player.setSpeed(2);
		}
	}
	
	public void createEnemy() {
		if (enemy == null) {
			enemy = new Enemy(g2,playerSSprite);
			enemy.setWidth(enemy_width);
			enemy.setHeight(enemy_height);
			enemy.setX(enemy_start_x-enemy.getWidth());
			enemy.setY(enemy_start_y-enemy.getHeight());
		}else {
			double oldPX, oldPY, oldDir, oldVelX, oldVelY;
			oldPX = enemy.getPX();
			oldPY = enemy.getPY();
			oldVelX = enemy.getVelx();
			oldVelY = enemy.getVely();
			oldDir = enemy.getDirection();
			enemy = null;
			enemy = new Enemy(g2,playerSSprite);
			enemy.setWidth(enemy_width);
			enemy.setHeight(enemy_height);
			enemy.setX(oldPX);
			enemy.setY(oldPY);
			enemy.setVelx(oldVelX);
			enemy.setVely(oldVelY);
			enemy.setDirection(oldDir);
			enemy.setSpeed(2);
		}
	}
	
	public void renderIntro() {
		BufferedImage plane_image = spriteLoader.loadPlayerSprite(playerSprites, playerSpriteChosenX, playerSpriteChosenY);
		AffineTransform runway_transform = AffineTransform.getTranslateInstance(intro_runway_x, 225);
		runway_transform.rotate(Math.toRadians(90));
		g2.drawImage(intro_runway, runway_transform, this);
		g2.drawImage(plane_image, (int)Math.round(intro_plane_x), (int)Math.round(intro_plane_y), (int)Math.round(intro_plane_width), (int)Math.round(intro_plane_height), this);
		final Thread gainAltitude = new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (intro_plane_width < 214) {
						intro_plane_width++;
						intro_plane_x-=1;
						
						try {
							Thread.sleep(1);
						}catch(Exception e) {
							
						}
					}
					if (intro_plane_height < 282) {
						intro_plane_height+=1.4;
						intro_plane_y-=0.4;
						
						try {
							Thread.sleep(1);
						}catch(Exception e) {
							
						}
					}
					if (intro_plane_width >= 214 && intro_plane_height >= 282) {
						try {
							Thread.sleep(1000);
						}catch(Exception e) {
							
						}
						introDone = true;
						introStart = false;
						break;
					}
				}
			}
		});
		
		Thread increaseX = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1);
						intro_runway_x--;
						if (intro_runway_x <= -200 && startedIntroThread2 == false) {
							startedIntroThread2 = true;
							gainAltitude.start();
							break;
						}
					}catch(Exception e) {
						
					}
				}
			}
		});
		
		if (startedIntroThread1 == false) {
			startedIntroThread1 = true;
			increaseX.start();
		}
	}
	
	public void renderPlayer() {
		this.add(player);
		
		double turnSpeed = 0.7;
		
		if (keyEvent.leftArrow == true) {
			double newDir = player.getDirection()-turnSpeed;
			player.setDirection(newDir);
		}
		
		if (keyEvent.rightArrow == true) {
			double newDir = player.getDirection()+turnSpeed;
			player.setDirection(newDir);
		}
		
		if (Math.abs(player.getDirection()) >= 360.0f) {
			player.setDirection(0);
		}
				
		player.tick();
		player.draw();
		this.remove(player);
	}
	
	public void renderEnemy() {
		this.add(enemy);
		
		double turnSpeed = 0.7;
		
		double newDir = enemy.getDirection()-turnSpeed;
		enemy.setDirection(newDir);
		
		if (Math.abs(enemy.getDirection()) >= 360.0f) {
			enemy.setDirection(0);
		}
		enemy.tick();	
		enemy.draw();
		this.remove(enemy);
	}
	
}
