package com.anstrat.guiComponent;

import java.util.HashMap;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MapList extends Table {
	
	private Table random, list, selected;
	private Label randomError;
	private TextField randX, randY;
	private HashMap<Table,String> tableMap;
	private boolean withRandom;
	private TextButton okButton;
	
	public int randWidth, randHeight;
	public boolean invalidRandSize = false;
	
	private ClickListener closeRandom = new ClickListener() {
        @Override
        public void click(Actor actor,float x,float y ){
        	expandRandom(false);
        }
	};
	private ClickListener mapClick = new ClickListener() {
        @Override
        public void click(Actor actor,float x,float y ){
        	setSelected((Table)actor);
        	expandRandom(false);
        }
	};
	
	public MapList(TextButton okButton){
		super(Assets.SKIN);
		
		this.okButton = okButton;
		this.setBackground(Assets.SKIN.getPatch("single-border"));
	}
	
	/**
	 * Sets the maps of the list.
	 * @param withRandom Include random map?
	 * @param filenames Filenames of the maps you want to load.
	 */
	public void setMaps(boolean withRandom, String... filenames){
		this.withRandom = withRandom;
		tableMap = new HashMap<Table,String>();
		selected = null;
		
		list = new Table(Assets.SKIN);
		list.align("top");
		
		if(withRandom){
			randomError = new Label(Assets.SKIN);
			random = new Table(Assets.SKIN);
			random.setBackground(Assets.SKIN.getPatch("single-border"));
			TextFieldListener tl = new TextFieldListener() {
		    	@Override
				public void keyTyped (TextField textField, char key) {
					if (key == '\n')
						textField.getOnscreenKeyboard().show(false);
					else if (key == '\t')
						textField.next(false);
					try{
						String text = textField.getText();
						if(text.length()>0){
							int size = Integer.parseInt(text);
							if(Map.isValidSize(size)!=0)
								throw new NumberFormatException();
						}
						invalidRandSize = false;
						randomError.setText("");
					}
					catch(NumberFormatException e){
						invalidRandSize = true;
						randomError.setText("Invalid map size");
					}
				}
			};
			randX = ComponentFactory.createTextField("Width", false);
			randX.setTextFieldListener(tl);
			randY = ComponentFactory.createTextField("Height", false);
			randY.setTextFieldListener(tl);

			random.setClickListener(new ClickListener() {
		        @Override
		        public void click(Actor actor,float x,float y ){
	        		setSelected(random);
		        	expandRandom(true);
		        }
		    });
			list.add(random).fillX().expandX();
			
			expandRandom(false);
			tableMap.put(random, "RANDOM");
		}

		list.setClickListener(closeRandom);
		
		//MAPS
		for(String mapPath : filenames){
			Table map = formatMap(mapPath);
			
			if(map != null){
				map.setClickListener(mapClick);
				list.row();
				list.add(map).fillX().expandX().height((int)(10*Main.percentHeight));
				tableMap.put(map, mapPath);
			}
		}
		
		FlickScrollPane scroll = new FlickScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		this.add(scroll).fill().expand();
	}
	
	/**
	 * Returns the selected map. Null if nothing selected.
	 * @return
	 */
	public String getSelected(){
		if(selected == null)
			return null;
		else if(selected == random){
			try{
				randWidth  = Integer.decode(randX.getText());
				randHeight = Integer.decode(randY.getText());
			} catch(NumberFormatException e){
				randWidth  = 10;
				randHeight = 10;
			}
		}
		return tableMap.get(selected);
	}
	
	/**
	 * Sets specified table as selected.
	 */
	public void setSelected(Table t){
		
		if(selected != null){
			selected.setBackground(Assets.SKIN.getPatch("single-border"));
		}
		
		t.setBackground(Assets.SKIN.getPatch("double-border"));
		selected = t;
		
		Assets.SKIN.setEnabled(okButton, true);
	}
	
	/**
	 * Expands the random map table.
	 */
	public void expandRandom(boolean expand){
		if(!withRandom)
			return;
		random.clear();
		
		if(expand){
			Table innerTable = new Table().width((int)(random.width/3f)).height((int)(8f*Main.percentHeight));
			innerTable.add(randX);
			innerTable.add(randY);
			
			random.row();
			random.add("GENERATE MAP").height((int)(Main.percentHeight*4f));
			random.row();
			random.add(innerTable);
			random.row();
			random.add(randomError).height((int)(Main.percentHeight*4f));
			
			list.getCell(random).height((int)(Main.percentHeight*28f));
		} else {
			random.row().center();
			random.add("GENERATE MAP").height((int)(Main.percentHeight*4f));
			list.getCell(random).height((int)(Main.percentHeight*10f));
		}
		list.layout();
	}
	
	/**
	 * 
	 * @param filename Map to format.
	 * @return A table with information about the map.
	 */
	public Table formatMap(String filename){
		
		Map map = Assets.loadMap(filename);
		if(map == null) return null;
		
		
		Label nameLabel = new Label(map.name, Assets.SKIN);
		Label sizeLabel = new Label(String.format("%dx%d", map.getXSize(), map.getYSize()), Assets.SKIN);
		
		Table table = new Table(Assets.SKIN);
		table.setBackground(Assets.SKIN.getPatch("single-border"));
		table.defaults().left().height((int)(4*Main.percentHeight));
		table.add(nameLabel).fillX().expandX();
		table.add(sizeLabel);
		
		return table;
	}
}
