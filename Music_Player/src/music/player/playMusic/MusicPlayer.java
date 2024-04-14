package music.player.playMusic;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import com.mpatric.mp3agic.Mp3File;

import music.player.GUIInterface.PlayerGUI;

public class MusicPlayer {

	private static final Object playObject = new Object();
	private PlayerGUI playerGUI;
	private Music currentMusic;
	private AdvancedPlayer advancedPlayer;
	private FileInputStream fileInputStream;
	private BufferedInputStream bufferedInputStream;
	public Boolean isPaused = false;
	private Thread playbackSliderThread;
	private int currentFramePosition = 0;
	private int currentFrame;
	private JSlider playbackSlider;
	private volatile boolean isPlaying = false;
	private int currentTimeInMilli;

	public MusicPlayer(PlayerGUI playerGUI) {
		this.playerGUI = playerGUI;
		this.playerGUI.setMusicPlayer(this);
	}

	public void loadSong(Music music) {
		currentMusic = music;

		if (currentMusic != null) {
			createAdvancedPlayer();
			playCurrentSong();
		}
	}

	private void createAdvancedPlayer() {
		try {
			fileInputStream = new FileInputStream(currentMusic.getFilepath());
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			advancedPlayer = new AdvancedPlayer(bufferedInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int calculateMaxFrame(Mp3File mp3file, double frameRate) {
		int durationSeconds = (int) mp3file.getLengthInSeconds();
		return (int) (durationSeconds * frameRate);
	}

	public void playCurrentSong() {
		if (currentMusic == null) {
			return;
		}
		try {
			FileInputStream fileInputStream = new FileInputStream(currentMusic.getFilepath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

			advancedPlayer = new AdvancedPlayer(bufferedInputStream);

			advancedPlayer.setPlayBackListener(new PlaybackListener() {
				@Override
				public void playbackStarted(PlaybackEvent evnt) {
					System.out.println("playbackStarted");
					currentFramePosition = 0;
				}

				@Override
				public void playbackFinished(PlaybackEvent evnt) {
					System.out.println("playbackFinished");
					
						if (isPaused) {
							currentFrame += (int) ((double) evnt.getFrame() * currentMusic.getFrameRate());
						}
				}
			});

			startPlaybackSliderThread();

			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
				@Override
				protected Void doInBackground() throws Exception {
					//advancedPlayer.play();
					try {
						if (isPaused) {
							synchronized (playObject) {
								isPaused = false;
								playObject.notify();

							}
							advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
						} else {
							advancedPlayer.play();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void done() {
					// Playback finished
				}
			};
			worker.execute();

			// Get MP3 file duration and calculate maximum frame value
			Mp3File mp3file = new Mp3File(currentMusic.getFilepath());
			int maxFrame = calculateMaxFrame(mp3file, currentMusic.getFrameRate());

			updatePlaybackSliderValue(maxFrame);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startMusicThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (isPaused) {
						synchronized (playObject) {
							isPaused = false;
							playObject.notify();

						}
						advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
					} else {
						advancedPlayer.play();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void pauseSong() {
		synchronized (playObject) {
			if (advancedPlayer != null) {
				isPaused = true;
				stopSong();
			}
		}
	}

	public void stopSong() {
		synchronized (playObject) {
			if (advancedPlayer != null) {
				advancedPlayer.stop();
				advancedPlayer.close();
				advancedPlayer = null;
			}
		}
	}

	public void stopCurrentSong() {

		if (advancedPlayer != null) {
			isPaused = false;
			stopSong();
			currentFramePosition = 0;
			if (playbackSliderThread != null && playbackSliderThread.isAlive()) {
				playbackSliderThread.interrupt(); // Interrupt the playback slider thread
			}
		}
	}

	public void startPlaybackSliderThread() {
		if (playbackSliderThread != null && playbackSliderThread.isAlive()) {
			playbackSliderThread.interrupt(); // Interrupt the previous thread
		}

		playbackSliderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						if (advancedPlayer != null) {
							currentTimeInMilli++;							
							int calculateFrame = (int) ((double) currentTimeInMilli * currentMusic.getFrameRate());
							
							// Check if calculateFrame is within the slider range
							if (calculateFrame <= playerGUI.getPlaybackSlider().getMaximum()
									&& calculateFrame >= playerGUI.getPlaybackSlider().getMinimum()) {
								SwingUtilities.invokeLater(() -> {
									playerGUI.setPlaybackSliderValue(calculateFrame);
								});
							}
						}
						Thread.sleep(1); // Sleep for 100ms
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		playbackSliderThread.start();
	}

	private void updatePlaybackSliderValue(int value) {
		SwingUtilities.invokeLater(() -> {
			playerGUI.setPlaybackSliderValue(value);
		});
	}

	public void setPlaybackSlider(JSlider playbackSlider) {
		this.playbackSlider = playbackSlider;
	}

    public void setCurrentTimeInMilli(int timeInMilli){
        currentTimeInMilli = timeInMilli;
    }
}
