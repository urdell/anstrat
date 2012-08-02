package com.anstrat.network_old;

import com.anstrat.geography.Map;
import com.badlogic.gdx.Gdx;

public class GameRequest {
	
	public static final int STATUS_UNKNOWN = 0;
	public static final int STATUS_WAIT_OPPONENT = 1;
	public static final int STATUS_SEARCH_GAME = 2;
	
	
	public long nonce;
	public String gameName;
	public String password;
	public Map map;
	public int status = STATUS_UNKNOWN;
	public long timeLimit;
	
	public GameRequest(long nonce, long timelimit, String gameName, String password) {
		this.nonce = nonce;
		this.gameName = gameName;
		this.password = password;
		this.timeLimit = timelimit;
		status = STATUS_UNKNOWN;
		Gdx.app.log("GameRequest", "GameRequest made with nonce, gamename and password");
	}
	
	public GameRequest(long nonce,long timelimit, String gameName, String password, Map map) {
		this.nonce = nonce;
		this.gameName = gameName;
		this.password = password;
		this.map = map;
		this.timeLimit = timelimit;
		status = STATUS_UNKNOWN;
		Gdx.app.log("GameRequest", "GameRequest made with nonce, timelimit, gamename, password and map");
	}

	public GameRequest(long nonce) {
		this.nonce = nonce;
	}
	public GameRequest(long nonce, String gameName) {
		this.nonce = nonce;
		this.gameName = gameName;
		status = STATUS_SEARCH_GAME;
	}
	
	
}
