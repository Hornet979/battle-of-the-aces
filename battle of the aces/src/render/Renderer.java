package render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.main;
import render.entity.*;
import render.events.*;
import render.imageUtil.*;

public class Renderer extends JPanel{
	private Graphics2D g2;
	
	private boolean showMainScreen = true;
	private boolean choosingPlayer = false;
	private boolean choosingEnemy = false;
	private boolean showingMenu = true;
	private int keyZpress = 0;
	private int plane_preview_x = 100;
	private int plane_preview_y = 100;
	private int player_width = 250;
	private int player_height = 250;
	private int player_start_x = 550;
	private int player_start_y = 450;
	public int playerSpriteChosenX = 1;
	public int playerSpriteChosenY = 1;
	public int planeIndex = 1;
	
	
	//Classes
	private KeyEvents keyEvent = new KeyEvents();
	private MouseEvents mouseEvent = new MouseEvents();
	private SpriteLoader spriteLoader = new SpriteLoader();
	
	//Entities
	public Player player;
	
	//Images / files
	private File playerSpritesF;
	private BufferedImage playerSprites;
	private File guiSpritesF;
	private BufferedImage guiSprites;
	private double angle = 0;
	
	//Game settings
	public boolean gameStarted = false;
	private BufferedImage playerSSprite = null;
	
	public void init(main m) {
		m.addKeyListener(keyEvent);
		mouseEvent.init(this);
		m.addMouseListener(mouseEvent);
		//Set variables
		try {
			playerSpritesF = new File(this.getClass().getResource("res/playerSprites.png").toURI());
			playerSprites = ImageIO.read(playerSpritesF);
			
			guiSpritesF = new File(this.getClass().getResource("res/guiSprites.png").toURI());
			guiSprites = ImageIO.read(guiSpritesF);
		}catch (Exception e) {
			
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.g2 = g2;
		
		if (showMainScreen == true) {
			renderMainScreen();
		}
		if (gameStarted) {
			createPlayer();
			
			//create clouds
			g2.setColor(Color.red);
			g2.fillRect(0, 0, 50, 50);	
			
			//System.out.println(player.getDirection());
			
			//create entities (player, enemy)
			renderPlayer();
		}
		
		if (keyEvent.keyZ == true && keyZpress < 4) {
			keyZpress++;
			try {
				Thread.sleep(70);
			}catch(Exception e) {
				
			}
			if (keyZpress == 1) {
				showingMenu = false;
				choosingPlayer = true;
			}
			if (keyZpress == 2) {
				choosingPlayer = false;
				choosingEnemy = true;
			}
			if (keyZpress == 3) {
				choosingEnemy = false;
				playerSSprite = spriteLoader.loadPlayerSprite(playerSprites, playerSpriteChosenX, playerSpriteChosenY);
				gameStarted = true;
				keyZpress++;
			}
		}
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
		}else if (showingMenu == false && choosingPlayer == true) {
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
			if (mouseEvent.back == true && playerSpriteChosenY > 1) {
				playerSpriteChosenY--;
				playerSpriteChosenX = 10*playerSpriteChosenY;
			}
			
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
			g2.drawString("Plane "+planeIndex, 165, 560);
			
		}else if (showingMenu == false && choosingPlayer == false && choosingEnemy == true) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("Arial",Font.BOLD,30));
			g2.drawString("Choose your enemy", 190, 50);
			g2.drawString("Press Z to start", 500, 400);
			
			
			if (guiSprites == null) {
				return;
			}
			
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
			g2.drawString("Plane "+planeIndex, 165, 560);
		}
		
	}
	
	public void createPlayer() {
		if (player == null) {
			player = new Player(g2,playerSSprite);
			player.setWidth(player_width);
			player.setHeight(player_height);
			player.setX(player_start_x-player.getWidth());
			player.setY(player_start_y-player.getHeight());
		}else {
			float oldPX, oldPY, oldDir, oldVelX, oldVelY;
			oldPX = player.getX();
			oldPY = player.getY();
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
		}
		player.setSpeed(0.5f);
	}
	
	public void renderPlayer() {
		this.add(player);
		
		if (keyEvent.leftArrow == true) {
			float newDir = player.getDirection()-0.1f;
			player.setDirection(newDir);
		}
		
		if (keyEvent.rightArrow == true) {
			float newDir = player.getDirection()+0.1f;
			player.setDirection(newDir);
		}
		//System.out.println(player.getX()+" "+player.getY());
		// Positive direction
//		if (player.getDirection() >= 0.0f) {
//			player.setVelx(1);
//		}
//		if (player.getDirection() > 0.0f && player.getDirection() < 180.0f) {
//			player.setVely(1);
//		}
//		if (player.getDirection() >= 90.0f && player.getDirection() >= 270.0f) {
//			player.setVelx(-1);
//		}
//		if (player.getDirection() > 180.0f && player.getDirection() < 0.0f) {
//			player.setVely(-1);
//		}
		//Math.abs(player.getDirection());
		if (Math.abs(player.getDirection()) >= 360.0f) {
			player.setDirection(0);
		}
		
		
		player.tick();
		player.draw();
	}
	
	public void renderEnemy() {
		
	}
	
	public void renderMap() {
		
	}
}
