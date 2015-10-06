package com.hellajenn.exribot.application;

import java.io.Serializable;

public class BotCommand implements Serializable {
	
	private static final long serialVersionUID = 2139710639914059990L;
	private String command;
	private String response;

	public BotCommand() {
		command = "";
		response = "";
	}
	
	public BotCommand(String command, String response) {
		this.command = command;
		this.response = response;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	@Override
	public String toString() {
		return command + " " + response;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BotCommand other = (BotCommand) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		return true;
	}
	

	

}
