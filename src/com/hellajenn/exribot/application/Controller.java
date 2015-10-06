package com.hellajenn.exribot.application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.hellajenn.twitchbot.TwitchBot;

public class Controller implements Initializable {

	private static final String FILENAME = "BotCommands.dat";
	private TwitchBot bot;
	private TwitchSettings settings;
	private ObservableList<BotCommand> botCommandList;
	@FXML private TextField txtTwitchID;
	@FXML private TextField txtTwitchAuth;
	@FXML private TextField txtTwitchChannel;
	@FXML private TextField txtBotCommand;
	@FXML private TextField txtBotResponse;
	@FXML private Button btnEditTwitch;
	@FXML private Button btnSaveTwitch;
	@FXML private Button btnConnect;
	@FXML private Button btnDisconnect;
	@FXML private Button btnAddResponder;
	@FXML private Label lblStatus;
	@FXML private TableView<BotCommand> tblResponders;
	@FXML private TableColumn<BotCommand, String> colCommand;
	@FXML private TableColumn<BotCommand, String> colResponse;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//set the controller object in Main for window closing
		Main.setController(this);
		
		//init tableview
		botCommandList = FXCollections.observableArrayList();
		try {
			botCommandList = loadCommandFile();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		colCommand.setCellValueFactory(new PropertyValueFactory<>("command"));
		colResponse.setCellValueFactory(new PropertyValueFactory<>("response"));
		tblResponders.setItems(botCommandList);
		
		settings = new TwitchSettings();
		if (settings.isLoaded()) {
			txtTwitchID.setText(settings.getTwitchId());
			txtTwitchAuth.setText(settings.getTwitchAuth());
			txtTwitchChannel.setText(settings.getTwitchChannel()); 
		}
		
		if (twitchReady()) {
			txtTwitchID.setDisable(true);
			txtTwitchAuth.setDisable(true);
			txtTwitchChannel.setDisable(true);
			btnConnect.setDisable(false);
			btnEditTwitch.setDisable(false);
			btnSaveTwitch.setDisable(true);
			lblStatus.setText("Ready");
			
			//set the focus to the connect button after init
			Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	btnConnect.requestFocus();
		        }
		    });
			
			
		} else {
			btnConnect.setDisable(true);
			btnEditTwitch.setDisable(true);
			lblStatus.setText("Update Twitch Settings");
		}
	}
	
	@FXML
	public void editTwitchButtonAction(ActionEvent event) {
		txtTwitchID.setDisable(false);
		txtTwitchAuth.setDisable(false);
		txtTwitchChannel.setDisable(false);
		btnEditTwitch.setDisable(true);
		btnSaveTwitch.setDisable(false);
		btnConnect.setDisable(true);
		txtTwitchID.requestFocus();
	}
	
	@FXML
	public void saveTwitchButtonAction(ActionEvent event) {
		txtTwitchID.setDisable(true);
		txtTwitchAuth.setDisable(true);
		txtTwitchChannel.setDisable(true);
		btnSaveTwitch.setDisable(true);

		btnEditTwitch.setDisable(false);
		
		settings.setTwitchId(txtTwitchID.getText());
		settings.setTwitchAuth(txtTwitchAuth.getText());
		settings.setTwitchChannel(txtTwitchChannel.getText());
		try {
			settings.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (twitchReady()) {
			btnConnect.setDisable(false);
			lblStatus.setText("Ready");
		} else {
			lblStatus.setText("Update Twitch Settings");
		}
	}
	
	
	@FXML
	public void connectButtonAction(ActionEvent event) {
		if (twitchReady()) {

			String userName = txtTwitchID.getText();
			String auth = txtTwitchAuth.getText();
			String channel = txtTwitchChannel.getText();
			channel = channel.charAt(0) == '#' ? channel : "#" + channel;		

			//create bot
			bot = new TwitchBot(userName);
			bot.setVerbose(true);
			
			//load commands to bot
			for (BotCommand bc : botCommandList) {
				bot.addResponder(bc.getCommand(), bc.getResponse());
			}
			
			try {
				bot.connect(TwitchBot.SERVER, TwitchBot.PORT, auth);
				bot.joinChannel(channel);

				btnConnect.setDisable(true);
				btnDisconnect.setDisable(false);
				txtTwitchID.setDisable(true);
				txtTwitchAuth.setDisable(true);
				txtTwitchChannel.setDisable(true);
				lblStatus.setText("Connected");

			} catch (NickAlreadyInUseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IrcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			Alert alert = new Alert(AlertType.ERROR,
					"Please enter Twitch ID, auth code, and channel name before connecting.", 
					ButtonType.OK);
			alert.setHeaderText("Twitch Setup Incomplete!");
			alert.showAndWait();
		}

	}

	@FXML
	public void disconnectButtonAction(ActionEvent event) {
		bot.disconnect();
		bot.dispose();
		btnConnect.setDisable(false);
		btnDisconnect.setDisable(true);
		lblStatus.setText("Ready");
	}

	@FXML
	public void addResponderButtonAction(ActionEvent event) {
		String command = txtBotCommand.getText();
		String response = txtBotResponse.getText();
		BotCommand cmd = new BotCommand(command, response);
		if (botCommandList.contains(cmd)) {
			Alert alert = new Alert(AlertType.ERROR,
					"The command you are trying to add already exists. Please delete the existing command or choose a different command to add.", 
					ButtonType.OK);
			alert.setHeaderText("Command already exists!");
			alert.showAndWait();
		} else {
			botCommandList.add(cmd);
			if (bot != null)
				bot.addResponder(command, response);
			txtBotCommand.clear();
			txtBotResponse.clear();
		}
		txtBotCommand.requestFocus();
	}

	@FXML
	public void deleteResponderAction(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			ObservableList<BotCommand> selectedItems = tblResponders.getSelectionModel().getSelectedItems();
			if (bot != null) {
				for (BotCommand bc : selectedItems) {
					bot.removeResponder(bc.getCommand());
				}
			}
			selectedItems.forEach(botCommandList::remove);
		}
	}
	
	
	@FXML
	public void handleCloseSelection(ActionEvent e) {
		closeApplication();
	}
	
	@FXML
	public void handleAboutSelection(ActionEvent e) {
		Alert alert = new Alert(AlertType.INFORMATION,
				"Exribot for Twitch was written by exrica\n"
				+ "http://www.twitch.tv/exrica", 
				ButtonType.OK);
		alert.setHeaderText("Exribot v1.0");
		alert.showAndWait();
	}

	public void closeApplication() {
		if (bot != null) {
			bot.disconnect();
			bot.dispose();
		}

		//write commands to file
		ArrayList<BotCommand> bcs = new ArrayList<BotCommand>(botCommandList);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME));
			oos.writeObject(bcs);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Platform.exit();
		System.exit(0);
	}

	private boolean textFieldHasInput(TextField tf) {
		return tf.getText().length() > 0;
	}
	
	private boolean twitchReady() {
		return textFieldHasInput(txtTwitchID) && textFieldHasInput(txtTwitchAuth) &&
				textFieldHasInput(txtTwitchChannel);
	}

	@SuppressWarnings("unchecked")
	private ObservableList<BotCommand> loadCommandFile() throws IOException, ClassNotFoundException {
		ArrayList<BotCommand> bcs = new ArrayList<BotCommand>();
		Path file = Paths.get(FILENAME);
		ObjectInputStream oIn = null;
		if (Files.exists(file)) {
			oIn = new ObjectInputStream(Files.newInputStream(file));
			bcs = (ArrayList<BotCommand>)oIn.readObject();
			oIn.close();
		}
		return FXCollections.observableArrayList(bcs);
	}



}
