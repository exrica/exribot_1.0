package com.hellajenn.exribot.application;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TwitchSettings {

	private static final String FILE = "twitch.properties"; //file name
	
	private Properties props;
	private boolean loaded;
	
	public TwitchSettings() {
		props = new Properties();
		try {
			loadSettings();
			loaded = true;
		} catch (Exception e) {
			loaded = false;
		}
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public void loadSettings() throws IOException {
		FileReader reader = new FileReader(FILE);
		props.load(reader);
	}

	public String getTwitchId() {
		return props.getProperty("twitchId");
	}

	public void setTwitchId(String twitchId) {
		props.setProperty("twitchId", twitchId);
	}

	public String getTwitchAuth() {
		return props.getProperty("twitchAuth");
	}

	public void setTwitchAuth(String twitchAuth) {
		props.setProperty("twitchAuth", twitchAuth);
	}

	public String getTwitchChannel() {
		return props.getProperty("twitchChannel");
	}

	public void setTwitchChannel(String twitchChannel) {
		props.setProperty("twitchChannel", twitchChannel);
	}
	
	public void save() throws IOException {
		FileOutputStream outStream = new FileOutputStream(FILE);
		props.store(outStream, "Updated " + (new java.util.Date()).toString());
		outStream.close();
	}
	
	

}
