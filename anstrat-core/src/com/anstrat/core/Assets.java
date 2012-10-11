package com.anstrat.core;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Map;
import com.anstrat.geography.TerrainType;
import com.anstrat.gui.GTile;
import com.anstrat.gui.GUnit.AnimationState;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;

public final class Assets {

	public static boolean USE_GENERATED_FONTS = true;

	// TODO: Ugly way of storing two values, but a rewrite of the animation system is required to do this properly
	private static HashMap<UnitType, Pair<Animation[], boolean[]>> unitAnimations;
	
	public static Animation thrownAxeAnimation;
	
	/**
	 * Hexagon meshes with side length one, with texture coordinates set for each terrain type
	 * Has two sets of indices:
	 * render(GL.TRIANGLES, 0, 12)	to render the hexagon
	 * render(GL.LINES, 12, 12)		to render the outline
	 */
	// terrainMeshes[0] for flat hexagons, terrainMeshes[1] for pointy hexagons
	private static HexagonMesh[][] terrainMeshes;
	
	public static BitmapFont STANDARD_FONT,MENU_FONT,UI_FONT, DESCRIPTION_FONT, UI_FONT_BIG;
	
	public static Skin SKIN;
	
	/** White 16x16 square */
	public static Texture WHITE;
	
	public static Texture[] unitTeamIndicators;
	private static TextureAtlas atlas;
	private static AnimationLoader animationLoader;
	public static AssetManager manager;
	public static Color apTextColor = new Color(0.4f, 1f, 1f, 1f);

	public static void startLoadingTextures(){
		manager = new AssetManager();
		manager.load("textures_packed/pack", TextureAtlas.class);	//pack.atlas
	}
	
	public static void load(){
		Gdx.app.log("Assets", "load()");

		atlas = manager.get("textures_packed/pack", TextureAtlas.class);	//pack.atlas
		
		animationLoader = new AnimationLoader(Gdx.files.internal("data/animations.xml"), atlas);
		terrainMeshes = new HexagonMesh[2][];
		loadFonts();
		loadUnmanagedTextures();
		loadSkin();
		
		
		
		// Axe throw animation
		thrownAxeAnimation = new Animation(1f/6f, 
				getTextureRegion("axe-effect-0001"), 
				getTextureRegion("axe-attack-effect-0001"), 
				getTextureRegion("axe-effect-0003"));
		
		unitAnimations = new HashMap<UnitType, Pair<Animation[],boolean[]>>();
	}

	public static void dispose(){
		STANDARD_FONT.dispose();
		MENU_FONT.dispose();
		UI_FONT.dispose();
		UI_FONT_BIG.dispose();
		
		for(HexagonMesh[] hexagonType : terrainMeshes){
			if(hexagonType == null) continue;
			
			for(HexagonMesh m : hexagonType){
				if(m != null) m.dispose();
			}
		}
		
		SKIN.dispose();
		WHITE.dispose();
		atlas.dispose();
		
		manager.dispose();
		
		unitTeamIndicators = null;
		atlas = null;
		unitAnimations = null;
		animationLoader = null;
		thrownAxeAnimation = null;
		STANDARD_FONT = null;
		MENU_FONT = null;
		UI_FONT = null;
		terrainMeshes = null;
		SKIN = null;
		WHITE = null;
	}
	
	public static void onApplicationResume(){
		// Unmanaged textures needs to be reloaded manually
		loadUnmanagedTextures();
	}
	
	public static void onApplicationPause(){
		if(WHITE != null) WHITE.dispose();
		
		for(Texture t : unitTeamIndicators){
			if(t != null) t.dispose();
		}
	}
	
	// Textures created from Pixmaps are unmanaged textures and needs to be reloaded manually on resume()
	private static void loadUnmanagedTextures(){
		Pixmap map;
		
		// Create white 16x16 texture
		map = new Pixmap(16, 16, Format.RGB565);
		map.setColor(Color.WHITE);
		map.fill();
		
		WHITE = new Texture(map);
		map.dispose();
		
		// Circle team indicators
		unitTeamIndicators = new Texture[Player.primaryColor.length];
		for(int i = 0; i < unitTeamIndicators.length; i++){
			
			map = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
			map.setColor(Player.primaryColor[i]);
			map.fillCircle(7, 7, 7);
			
			Texture t = new Texture(map);
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			unitTeamIndicators[i] = t;
			
			map.dispose();
		}
	}
	
	// Creates a separate mesh for each terrain type to avoid having to change
	// the texture coordinates between terrain types
	public static HexagonMesh getHexagonMesh(TerrainType type, boolean flat){
		int flatOrPointy = flat ? 0 : 1;
		
		if(terrainMeshes[flatOrPointy] == null) terrainMeshes[flatOrPointy] = new HexagonMesh[TerrainType.values().length];
		if(terrainMeshes[flatOrPointy][type.ordinal()] == null) terrainMeshes[flatOrPointy][type.ordinal()] = new HexagonMesh(GTile.getTextures(type)[0], flat);
		
		return terrainMeshes[flatOrPointy][type.ordinal()];
	}

	private static void loadSkin(){
		SKIN = new Skin();
		SKIN.add("default-font", Assets.STANDARD_FONT); 
		SKIN.add("menu-font", Assets.MENU_FONT);
		SKIN.add("ui-font", Assets.UI_FONT);

		//SKIN.add("single-border", atlas.createPatch("border-thin"));		//TEXTPACK2
		SKIN.add("single-border", new NinePatch(Assets.getTextureRegion("border-thin"),15,15,15,15));

		NinePatch singleBorderWhite = new NinePatch(Assets.getTextureRegion("border-thin-white"),15,15,15,15);
		SKIN.add("single-border-white", singleBorderWhite);
		TextureRegion line = Assets.getTextureRegion("white-line-hard");
		SKIN.add("line-border", new NinePatch(
				null,line,null,
				null,null,null,
				null,line,null));
		
		TextureRegion singleBorderWhiteBottomCenter = NinePatchUtils.getBottomCenter(Assets.getTextureRegion("border-thin-white"), 15, 15, 15, 15);
		
		SKIN.add("line-border-thin", new NinePatch(
				null,null,null,
				null,null,null,
				null,singleBorderWhiteBottomCenter,null));
		
		//SKIN.add("default-window", atlas.createPatch("border-window"));		//TEXTPACK2
		//SKIN.add("double-border", atlas.createPatch("border-thick"));			//TEXTPACK2
		SKIN.add("default-window", new NinePatch(Assets.getTextureRegion("border-window"),20,20,51,20));
		SKIN.add("double-border", new NinePatch(Assets.getTextureRegion("border-thick"),20,20,20,20));
		
		TextureRegion borderThickBottomCenter = NinePatchUtils.getBottomCenter(Assets.getTextureRegion("border-thick"), 20, 20, 20, 20);
		TextureRegion borderThickTopCenter = NinePatchUtils.getTopCenter(Assets.getTextureRegion("border-thick"), 20, 20, 20, 20);
		
		SKIN.add("border-thick-updown", new NinePatch(
				null,borderThickTopCenter,null,
				null,null,null,
				null,borderThickBottomCenter,null));
		SKIN.add("border-thick-down", new NinePatch(
				null,null,null,
				null,null,null,
				null,borderThickBottomCenter,null));
		SKIN.add("button-up",       new NinePatch(Assets.getTextureRegion("button-available")));
		SKIN.add("button-down",     new NinePatch(Assets.getTextureRegion("button-pressed")));
		SKIN.add("button-disabled", new NinePatch(Assets.getTextureRegion("button-unavailable")));
		SKIN.add("check-on",    new TextureRegion(Assets.getTextureRegion("check-on")));
		SKIN.add("check-off",   new TextureRegion(Assets.getTextureRegion("check-off")));
		
		
		Pixmap map = new Pixmap(1, 1, Format.RGB565);
		map.setColor(Color.WHITE);
		map.fill();
		Texture one = new Texture(map);
		
		Pixmap map2 = new Pixmap(1, 1, Format.RGB565);
		map2.setColor(new Color(0f, 0f, 0f, 0f));
		map2.fill();
		TextureRegion wh = new TextureRegion(new Texture(map));

		NinePatch np = new NinePatch(Assets.WHITE,8,8,8,8);
		np.setColor(new Color(0f, 0f, 0f, 0f));
		SKIN.add("empty-down", np);
		
		NinePatch np3 = new NinePatch(one,0,0,0,0);
		np3.setColor(new Color(0f, 0f, 0f, 0f));
		SKIN.add("empty", np3);
		
		NinePatch np2 = new NinePatch(one,0,0,0,0);
		np2.setColor(Color.BLACK);
		SKIN.add("black", np2);
		
		SKIN.add("cursor", new NinePatch(		//TODO UIFIX
				null,null,null,
				null,wh,null,
				null,null,null));
		
		SKIN.add("selection", new TextureRegion(wh));
		SKIN.add("white", Color.WHITE);
		SKIN.add("black", Color.BLACK);
		SKIN.add("light-gray",Color.LIGHT_GRAY);
		
		SKIN.load(Gdx.files.internal("data/skin.json"));
		System.out.println("skin!");
	}
	
	private static void loadFonts(){
		int menuSize   = (int)(Main.percentHeight*4);	//Menu button text size
		int uiSize     = (int)(Main.percentHeight*3);	//UI text size
		int uiBigSize  = (int)(Main.percentHeight*6);  //Big UI text size
		int descriptionSize = (int)(Main.percentHeight*2.7);	//Description text size
		int ingameSize = 128/2;  // Half a tile in height. Text drawn on map needs same resolution no matter what resolution the device has.
		
		// TODO: Temporary ugly fix until we can find the "ACCESS_VIOLATION" problem
		// when generating fonts (related to freetype library) that may or may not have something to do with
		// Java 32-bit vs 64-bit.
		
		if(USE_GENERATED_FONTS){
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/goudy_bookletter_1911.otf"));
			MENU_FONT = generator.generateFont(menuSize, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|?-+=()*&.;:,{}´`’/", false);
			generator.dispose();
			
			generator = new FreeTypeFontGenerator(Gdx.files.internal("data/goudy_bookletter_1911.otf"));
			DESCRIPTION_FONT = generator.generateFont(descriptionSize, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|?-+=()*&.;:,{}´`’/", false);
			generator.dispose();
			
			generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Crimson-Bold.otf"));//*/
			UI_FONT   = generator.generateFont(uiSize, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|?-+=()*&.;:,{}´`’/", false);
			generator.dispose();

			// Reduce margin to next line to make text look better.
			UI_FONT.getData().descent *= 1f/5; // Works, but don't know how.

			
			generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Crimson-Bold.otf"));//*/
			UI_FONT_BIG   = generator.generateFont(uiBigSize, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|?-+=()*&.;:,{}´`’/", false);
			generator.dispose();
			
			generator = new FreeTypeFontGenerator(Gdx.files.internal("data/Crimson-Roman.otf"));//*/
			STANDARD_FONT = generator.generateFont(ingameSize, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|?-+=()*&.;:,{}´`’/", true);
			generator.dispose();
			// STANDARD_FONT's font is for some reason generated at a bit wrong size, this fixes that
			STANDARD_FONT.setScale(ingameSize/STANDARD_FONT.getCapHeight());
		}
		else{
			//Need to make sizes a bit smaller to make them similar in size to the generated ones.
			menuSize   *= 0.75;
			uiSize     *= 0.75;
			uiBigSize  *= 0.75;
			ingameSize *= 1;
			
			Texture tex = new Texture(Gdx.files.internal("data/book.png"));
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			MENU_FONT = new BitmapFont(Gdx.files.internal("data/book.fnt"), new TextureRegion(tex), false);
			MENU_FONT.setScale(menuSize/MENU_FONT.getCapHeight());

			tex = new Texture(Gdx.files.internal("data/cbold.png"));
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);//*/
			UI_FONT = new BitmapFont(Gdx.files.internal("data/cbold.fnt"), new TextureRegion(tex), false);
			UI_FONT.setScale(uiSize/UI_FONT.getCapHeight());
			
			tex = new Texture(Gdx.files.internal("data/cbold.png"));
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);//*/
			UI_FONT_BIG = new BitmapFont(Gdx.files.internal("data/cbold.fnt"), new TextureRegion(tex), false);
			UI_FONT_BIG.setScale(uiBigSize/UI_FONT_BIG.getCapHeight());
			
			tex = new Texture(Gdx.files.internal("data/croman.png"));
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);//*/
			STANDARD_FONT = new BitmapFont(Gdx.files.internal("data/croman.fnt"), new TextureRegion(tex), true);
			STANDARD_FONT.setScale(ingameSize/STANDARD_FONT.getCapHeight());
			
			tex = new Texture(Gdx.files.internal("data/croman.png"));
			tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);//*/
			DESCRIPTION_FONT = new BitmapFont(Gdx.files.internal("data/croman.fnt"), new TextureRegion(tex), true);
			STANDARD_FONT.setScale(ingameSize/STANDARD_FONT.getCapHeight());
		}
	}
	
	public static TextureRegion getTextureRegion(String name){
		if(atlas.findRegion(name) == null) throw new IllegalArgumentException(name + " does not exist.");

		return atlas.findRegion(name);
	}
	
	public static Sprite createSprite(String name){
		return atlas.createSprite(name);
	}
	
	public static NinePatch createPatch(String name){
		return atlas.createPatch(name);
	}
	
	public static synchronized Pair<Animation[], boolean[]> getAnimations(UnitType type){
		// If not already loaded, load
		if(!unitAnimations.containsKey(type)){
			int numStates = AnimationState.values().length;
			Animation[] animationsOut = new Animation[numStates];
			
			// Load animations
			for(AnimationState state : AnimationState.values()){
				String animationName = String.format("%s-%s", type.graphicsFolder, state.toString().toLowerCase());
				if(!animationLoader.animationExists(animationName)) continue;
				
				animationsOut[state.ordinal()] = getAnimation(animationName);
			}
			
			boolean[] isLoopingOut = new boolean[numStates];
			
			// Set which animation types should loop
			isLoopingOut[AnimationState.IDLE.ordinal()] = true;
			isLoopingOut[AnimationState.WALK.ordinal()] = true;
			isLoopingOut[AnimationState.ATTACK.ordinal()] = false;
			isLoopingOut[AnimationState.DEATH.ordinal()] = false;
			isLoopingOut[AnimationState.HURT.ordinal()] = false;
			isLoopingOut[AnimationState.ABILITY.ordinal()] = false;
			
			unitAnimations.put(type, new Pair<Animation[], boolean[]>(animationsOut, isLoopingOut));
		}
		
		return unitAnimations.get(type);
	}
	
	/**
	 * @return a single named animation
	 */
	public static synchronized Animation getAnimation(String animationName){	
		return animationLoader.getAnimation(animationName);
	}
	
	private static String assetsDirectoryRootPath;
	public static String getAssetsDirectoryPath(String path){
		
		if(Gdx.app.getType() != ApplicationType.Desktop) return path;
		
		if(assetsDirectoryRootPath == null){
			String[] possiblePaths = new String[]{".", "bin"};
			
			for(String r : possiblePaths){
				FileHandle h = Gdx.files.internal(r);
				
				if(h.exists() && h.isDirectory()){
					assetsDirectoryRootPath = r;
				}
			}
			
			if(assetsDirectoryRootPath == null) throw new GdxRuntimeException("Could not locate assets directory.");
		}
		
		return assetsDirectoryRootPath + "/" + path;
		
	}
	
	/**
	 * Returns a list of map names available either internally, externally or both.
	 * @param external include external (player-created) maps
	 * @param internal include internal (default) maps
	 * @return a list of map names
	 */
	public static String[] getMapList(boolean external, boolean internal) {
		if (external == false && internal == false) return new String[]{};	// List no maps
		
		ArrayList<FileHandle> files = new ArrayList<FileHandle>();
		if(external)  {
			files.addAll(Arrays.asList(Gdx.files.external("soimaps").list()));
		}
		if(internal) {
			files.addAll(Arrays.asList(Gdx.files.internal(Assets.getAssetsDirectoryPath("maps")).list()));
		}
	
		ArrayList<String> maps = new ArrayList<String>();
		for(FileHandle fh : files){
			maps.add(fh.name());
		}
		
		return maps.toArray(new String[maps.size()]);
	}
	
	public static Map loadMap(String name){
		Map internal = loadMap(name, true);
		return internal != null ? internal : loadMap(name, false);
	}
	
	public static Map loadMap(String name, boolean fromInternal){
		FileHandle fh = fromInternal ? Gdx.files.internal(Assets.getAssetsDirectoryPath("maps/"+name)) : Gdx.files.external("soimaps/"+ name);
		ObjectInputStream ois = null;
		
		try {
			ois = new ObjectInputStream(new GZIPInputStream(fh.read()));
			return (Map) ois.readObject();
		} 
		catch(Exception e){
			//Failed loading compressed map, try loading it uncompressed.
			try{
				ois = new ObjectInputStream(fh.read());
				return (Map) ois.readObject();
			}
			catch(Exception e2){
				Gdx.app.log("Assets", String.format("Failed to load map '%s' (internal = %s) due to '%s'.", name, fromInternal ? "yes" : "no", e.getMessage()));
			}
		}
		finally{
			// Close stream
			try{ ois.close(); } catch (Exception e){/* Failed on close, we don't care */ }
		}
		
		return null;
	}
	
	// Replacement for AbstractMap.SimpleEntry that does not exist in the Android SDK till api level 9
	public static final class Pair<A,B> {
		public final A a;
		public final B b;
		
		public Pair(A a, B b){
			this.a = a;
			this.b = b;
		}
	}
	
	// A hexagon mesh with an associated TextureRegion
	public static class HexagonMesh extends Mesh {
		public final Texture texture;
		
		public HexagonMesh(TextureRegion region, boolean flat){
			super(true, 6, 24, new VertexAttribute(Usage.Position, 2, "a_position"), new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
			
			this.texture = region.getTexture();
			
			// Represents a hexagon with side length 1
			float r = (float)Math.cos(Math.toRadians(30));
		    float h = (float)Math.sin(Math.toRadians(30));
		    short[] indices;
			
			// TODO: Simplify, a lot is repeated below
			float[] vertices, textureCoords;
			
			// Flat
			if(flat){
				indices = new short[]{
					// Represents the triangles of the hexagon
					0, 1, 2,
					1, 2, 3,
					2, 3, 4,
					3, 4, 5,
					
					// Represents the edges of the hexagon
			   		0, 1,	// SW
					1, 3,	// S
					3, 5,	// SE
					5, 4,	// NE
					4, 2,	// N
					2, 0,	// NW
				};
				
				vertices = new float[]{
					-1/2f - h, 0f,	// 0. Left
					-1/2f, r,		// 1. Bottom-left
					-1/2f, -r,		// 2. Top-left
					1/2f, r,		// 3. Bottom-right
					1/2f, -r,		// 4. Top-right
					1/2f + h, 0f,	// 5. Right
				};
				
				textureCoords = new float[]{
					0.0f, 0.5f,
					0.25f, 1f,
					0.25f, 0f,
					0.75f, 1f,
					0.75f, 0f,
					1.0f, 0.5f,
				};
			}
			else{
				indices = new short[]{
					// Represents the triangles of the hexagon
					0, 1, 2,
					1, 2, 3,
					2, 3, 4,
					3, 4, 5,
					
					// Represents the edges of the hexagon
					0, 1,	// SW
					2, 4,	// E
					0, 2,	// SE
					4, 5,	// NE
					1, 3,	// W
			   		3, 5,	// NW
				};
				
				vertices = new float[]{
					0f, 1/2f + h,	// 0. Top
					-r, 1/2f,		// 1. Top-left
					r, 1/2f,		// 2. Top-right
					-r, -1/2f,		// 3. Bottom-left
					r, -1/2f,		// 4. Bottom-right
					0f, -1/2f - h,	// 5. Bottom
				};
				
				// 1-x to flip the texture
				textureCoords = new float[]{
					0.5f, 1f - 0.0f,	// Top
					0.0f, 1f - 0.25f,	// Top left
					1.0f, 1f - 0.25f,	// Top right
					0.0f, 1f - 0.75f,	// Bottom-left
					1.0f, 1f - 0.75f,	// Bottom right
					0.5f, 1f - 1f,		// Bottom
				};
			}
			
			// Create the combined array
			float u = region.getU(), v = region.getV();
			float uDiff = region.getU2() - region.getU();
			float vDiff = region.getV2() - region.getV();
			
			float[] combined = new float[textureCoords.length + vertices.length];
			for(int i = 0; i < vertices.length / 2; i++){
				int cOffset = 4 * i;	// offset for combined array
				int offset = i * 2;		// offset for vertices / texture coords array
				
				// Vector coordinates
				combined[cOffset] = vertices[offset];
				combined[cOffset + 1] = vertices[offset + 1];
				
				// Texture coordinates
				combined[cOffset + 2] = u + textureCoords[offset] * uDiff;
				combined[cOffset + 3] = v + textureCoords[offset + 1] * vDiff;
			}
			
			setVertices(combined);
			setIndices(indices);
		}
	}
	
	public static class NinePatchUtils {
		public static TextureRegion getBottomCenter(TextureRegion region, int left, int right, int top, int bottom){
			int middleWidth = region.getRegionWidth() - left - right;
			int middleHeight = region.getRegionHeight() - top - bottom;
			return new TextureRegion(region, left, top + middleHeight, middleWidth, bottom);
		}
		
		public static TextureRegion getTopCenter(TextureRegion region, int left, int right, int top, int bottom){
			int middleWidth = region.getRegionWidth() - left - right;
			return new TextureRegion(region, left, 0, middleWidth, top);
		}
		
		public static TextureRegion getTopLeft(TextureRegion region, int left, int right, int top, int bottom){
			return new TextureRegion(region, 0, 0, left, top);
		}
	}
}
