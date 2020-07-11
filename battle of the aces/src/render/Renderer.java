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
import render.events.KeyEvents;
import render.imageUtil.SpriteLoader;

public class Renderer extends JPanel{
	private Graphics2D g2;
	
	private boolean chosenPlayer = false;
	private boolean chosenEnemy = false;
	private boolean showingMenu = true;
	
	//Classes
	private KeyEvents keyEvent = new KeyEvents();
	private SpriteLoader spriteLoader = new SpriteLoader();
	//Player images / files
	private File playerSpritesF;
	private BufferedImage playerSprites;
	private File guiSpritesF;
	private BufferedImage guiSprites;
	private double angle = 0;
	
	public void init(main m) {
		m.addKeyListener(keyEvent);
		//Set variables
		try {
			//playerSpritesF = new File(this.getClass().getResource("res/playerSprites.png").toURI());
			//playerSprites = ImageIO.read(playerSpritesF);
			guiSpritesF = new File(this.getClass().getResource("res/guiSprites.png").toURI());
			guiSprites = ImageIO.read(guiSpritesF);
		}catch (Exception e) {
			
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.g2 = g2;
		
		if (showingMenu == false) {
			if (chosenPlayer == false) {
				
			}
		}else if (showingMenu == true){
			renderMainScreen();
		}
		
	}
	
	public void renderMainScreen() {
		g2.setColor(Color.RED);
		g2.setFont(new Font("Arial",Font.BOLD,30));
		g2.drawString("DOGFIGHTER 8.0: Battle of the aces", 190, 50);
		g2.drawString("Press Z", 500, 400);
		
		
		if (guiSprites == null) {
			return;
		}
		
		BufferedImage plane_image = spriteLoader.loadSprite(guiSprites, 1, 1);
		AffineTransform pos = AffineTransform.getTranslateInstance(50, 200);
		pos.rotate(Math.toRadians(angle+=0.1),plane_image.getWidth()/2,plane_image.getHeight()/2);
		g2.drawImage(plane_image, pos, this);
		
	}
	
	public void renderPlayer() {
		
	}
	
	public void renderEnemy() {
		
	}
	
	public void renderMap() {
		
	}
}
