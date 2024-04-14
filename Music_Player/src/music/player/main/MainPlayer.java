package music.player.main;

import java.awt.EventQueue;

import music.player.GUIInterface.PlayerGUI;

public class MainPlayer {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				PlayerGUI playerGUI = new PlayerGUI();
				playerGUI.setVisible(true);

			}
		});
	}

}
