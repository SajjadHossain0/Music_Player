package music.player.GUIInterface;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Image;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import music.player.playMusic.Music;
import music.player.playMusic.MusicPlayer;

import java.io.File;

import com.mpatric.mp3agic.Mp3File;

public class PlayerGUI extends JFrame {

	private MusicPlayer musicPlayer;
	private JFileChooser jFileChooser;
	private Music music;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel songTitle;
	private JPanel btnPanel;
	private JButton playBtn, pauseBtn, prevBtn, nextBtn, favBtn;
	private JSlider playbackSlider;

	private List<File> mp3FilesList = new ArrayList<>();
	private JList<String> songList;
	private JPanel songListPanel;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					PlayerGUI frame = new PlayerGUI();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public PlayerGUI() {
		setTitle("Music Player");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 808, 519);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		ImageIcon icon = new ImageIcon("Images/icon.png");
		Image imgIcon = icon.getImage();
		setIconImage(imgIcon);

		setContentPane(contentPane);
		contentPane.setLayout(null);

// ================ menuPanel ======================		

		JPanel menuPanel = new JPanel();
		menuPanel.setBounds(0, 0, 202, 292);
		contentPane.add(menuPanel);
		menuPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Music Player");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 182, 40);
		menuPanel.add(lblNewLabel);

		JButton favListBtn = new JButton("Favourite");
		favListBtn.setBounds(10, 85, 182, 23);
		customizeMenuButton(favListBtn);
		menuPanel.add(favListBtn);

		JButton playListBtn = new JButton("Playlist");
		playListBtn.setBounds(10, 119, 182, 23);
		customizeMenuButton(playListBtn);
		menuPanel.add(playListBtn);

		JButton userBtn = new JButton("User");
		userBtn.setBounds(10, 153, 182, 23);
		customizeMenuButton(userBtn);
		menuPanel.add(userBtn);

// ================ end menuPanel ======================		

// ================ songImgPanel ======================		
		ImageIcon song_Image = new ImageIcon("Images/song_image.png");
		// Resize the image to match the size of the button
		Image imgsong = song_Image.getImage();
		Image resizedImgsong = imgsong.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Adjust the width and height
																						// as needed
		song_Image = new ImageIcon(resizedImgsong);
		JLabel songImage = new JLabel(song_Image);
		songImage.setHorizontalAlignment(SwingConstants.CENTER);
		songImage.setBounds(10, 11, 286, 270);
		JPanel songImgPanel = new JPanel();
		songImgPanel.setBounds(212, 0, 306, 292);
		songImgPanel.add(songImage);
		contentPane.add(songImgPanel);
		songImgPanel.setLayout(null);

// ================ end songImgPanel ======================	

// ================ listPanel ======================

		musicPlayer = new MusicPlayer(this);
		jFileChooser = new JFileChooser();
		// jFileChooser.setCurrentDirectory(new File("mp3"));
		jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
		JButton fileChooseBtn = new JButton("select song folder");
		customizeMenuButton(fileChooseBtn);
		fileChooseBtn.setBounds(554, 11, 213, 23);
		contentPane.add(fileChooseBtn);
		fileChooseBtn.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Set file selection mode to directories only
		        int result = jFileChooser.showOpenDialog(PlayerGUI.this);
		        File selectedDirectory = jFileChooser.getSelectedFile();

		        if (result == jFileChooser.APPROVE_OPTION && selectedDirectory != null) {
		            loadMp3FilesFromDirectory(selectedDirectory); // Load MP3 files from selected directory
		        }
		    }
		});


		loadMp3Files();

		JScrollPane scrollPane = new JScrollPane(songList);
		scrollPane.setBounds(0, 0, 264, 247);
		songListPanel = new JPanel();
		songListPanel.setBounds(528, 45, 264, 247);
		songListPanel.setLayout(null);
		songListPanel.add(scrollPane);
		contentPane.add(songListPanel);


// ========= play track buttons		

		btnPanel = new JPanel();
		btnPanel.setBounds(0, 303, 792, 166);
		contentPane.add(btnPanel);
		btnPanel.setLayout(null);

		ImageIcon playBtnImg = new ImageIcon("Images/play.png");
		// Resize the image to match the size of the button
		Image playBtnImg1 = playBtnImg.getImage();
		Image resizedplayBtnImg = playBtnImg1.getScaledInstance(33, 33, Image.SCALE_SMOOTH); // Adjust the width and //
																								// height as needed
		playBtnImg = new ImageIcon(resizedplayBtnImg);

		playBtn = new JButton(playBtnImg);
		playBtn.setBounds(375, 120, 35, 35);
		customPlaybackButtons(playBtn);
		playBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				enablePauseButtonDisablePlayButton();
				musicPlayer.playCurrentSong();
			}
		});
		btnPanel.add(playBtn);
//==================================
		ImageIcon pauseBtnImg = new ImageIcon("Images/pause.png");
		// Resize the image to match the size of the button
		Image pauseBtnImg1 = pauseBtnImg.getImage();
		Image resizedpauseBtnImg = pauseBtnImg1.getScaledInstance(33, 33, Image.SCALE_SMOOTH); // Adjust the width and
																								// // height as needed
		pauseBtnImg = new ImageIcon(resizedpauseBtnImg);

		pauseBtn = new JButton(pauseBtnImg);
		pauseBtn.setBounds(375, 120, 35, 35);
		customPlaybackButtons(pauseBtn);
		pauseBtn.setVisible(false);
		pauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				enablePlayButtonDisablePauseButton();
				musicPlayer.pauseSong();
			}
		});

		btnPanel.add(pauseBtn);

//=================================
		ImageIcon nextBtnImg = new ImageIcon("Images/next.png");
		// Resize the image to match the size of the button
		Image nextBtnImg1 = nextBtnImg.getImage();
		Image resizednextBtnImg = nextBtnImg1.getScaledInstance(33, 33, Image.SCALE_SMOOTH); // Adjust the width and
		// height as needed
		nextBtnImg = new ImageIcon(resizednextBtnImg);
		nextBtn = new JButton(nextBtnImg);
		nextBtn.setBounds(420, 120, 35, 35);
		customPlaybackButtons(nextBtn);
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = songList.getSelectedIndex();

				// Check if the current song is the last song in the list
				if (currentIndex < mp3FilesList.size() - 1) {
					int newIndex = currentIndex + 1;
					songList.setSelectedIndex(newIndex);
					File selectedFile = mp3FilesList.get(newIndex);
					playMusic(selectedFile);

					// Reset the slider and song position
					playbackSlider.setValue(0);
					musicPlayer.setCurrentTimeInMilli(0);
				}
			}
		});
		btnPanel.add(nextBtn);
//=================================
		ImageIcon prevBtnImg = new ImageIcon("Images/before.png");
		// Resize the image to match the size of the button
		Image prevBtnImg1 = prevBtnImg.getImage();
		Image resizedprevBtnImg = prevBtnImg1.getScaledInstance(33, 33, Image.SCALE_SMOOTH); // Adjust the width and //
																								// // height as needed
		prevBtnImg = new ImageIcon(resizedprevBtnImg);
		prevBtn = new JButton(prevBtnImg);
		prevBtn.setBounds(330, 120, 35, 35);
		customPlaybackButtons(prevBtn);
		prevBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentIndex = songList.getSelectedIndex();

				// Check if the current song is the first song in the list
				if (currentIndex > 0) {
					int newIndex = currentIndex - 1;
					songList.setSelectedIndex(newIndex);
					File selectedFile = mp3FilesList.get(newIndex);
					playMusic(selectedFile);

					// Reset the slider and song position
					playbackSlider.setValue(0);
					musicPlayer.setCurrentTimeInMilli(0);
				}
			}
		});
		btnPanel.add(prevBtn);
//=================================
		ImageIcon favIcon = new ImageIcon("Images/heart.png");
		// Resize the image to match the size of the button
		Image img2 = favIcon.getImage();
		Image resizedImg2 = img2.getScaledInstance(25, 25, Image.SCALE_SMOOTH); // Adjust the width and height as needed
		favIcon = new ImageIcon(resizedImg2);
		favBtn = new JButton(favIcon);
		favBtn.setBounds(747, 120, 35, 35);
		customPlaybackButtons(favBtn);
		btnPanel.add(favBtn);

//=================================	
// ============ Track image
		ImageIcon addPlaylistIcon = new ImageIcon("Images/playlist.png");
		// Resize the image to match the size of the button
		Image addPlaylistIconImg = addPlaylistIcon.getImage();
		Image resizedaddPlaylistIconImg = addPlaylistIconImg.getScaledInstance(25, 25, Image.SCALE_SMOOTH); // Adjust //																									// needed
		addPlaylistIcon = new ImageIcon(resizedaddPlaylistIconImg);
		JButton addPlaylistBtn = new JButton(addPlaylistIcon);
		addPlaylistBtn.setBounds(10, 120, 35, 35);
		customPlaybackButtons(addPlaylistBtn);
		btnPanel.add(addPlaylistBtn);

//=================================		
//============== song title and playback slider
		
		songTitle = new JLabel();
		songTitle.setFont(new Font("Serif", Font.BOLD, 14));
		songTitle.setHorizontalAlignment(SwingConstants.CENTER);
		songTitle.setBounds(10, 11, 772, 20);
		btnPanel.add(songTitle);

		playbackSlider = new JSlider();
		playbackSlider.setValue(0);
		playbackSlider.setPaintTicks(true);
		playbackSlider.setBounds(10, 42, 772, 67);
		btnPanel.add(playbackSlider);

//=================================	

	}
	private void loadMp3FilesFromDirectory(File directory) {
	    File[] files = directory.listFiles();

	    if (files != null) {
	        mp3FilesList.clear(); // Clear the existing list
	        DefaultListModel<String> listModel = (DefaultListModel<String>) songList.getModel(); // Get the list model

	        listModel.clear(); // Clear the existing list model

	        for (File file : files) {
	            if (file.isFile() && file.getName().endsWith(".mp3")) {
	                mp3FilesList.add(file);
	                listModel.addElement(file.getName()); // Add the file name to the list model
	            }
	        }
	    }
	}
	
	private void loadMp3Files() {
	    // Create JList to display the mp3 file names
	    songList = new JList<>(new DefaultListModel<>());
	    songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    songList.addListSelectionListener(new ListSelectionListener() {
	        @Override
	        public void valueChanged(ListSelectionEvent e) {
	            if (!e.getValueIsAdjusting()) { // Check if the selection change is complete
	                int selectedIndex = songList.getSelectedIndex();
	                if (selectedIndex != -1) { // Check if an item is selected
	                    File selectedFile = mp3FilesList.get(selectedIndex);
	                    playMusic(selectedFile);
	                    String filename = selectedFile.getName();
	                    songTitle.setText(filename);
	                }
	            }
	        }
	    });
	}

	private void playMusic(File file) {
		try {
			Mp3File mp3File = new Mp3File(file.getAbsolutePath());
			Music music = new Music(file.getAbsolutePath());

			musicPlayer.stopCurrentSong();

			musicPlayer.loadSong(music);
			updatePlaybackSlider(music);
			enablePauseButtonDisablePlayButton();
			startPlaybackSliderThread();

			// Reset the slider and song position
			this.setPlaybackSliderValue(0);
			musicPlayer.setCurrentTimeInMilli(0);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setMusicPlayer(MusicPlayer musicPlayer) {
		this.musicPlayer = musicPlayer;
		this.musicPlayer.setPlaybackSlider(playbackSlider);
	}

	private void updatePlaybackSlider(Music music) {
		playbackSlider.setMaximum(music.getMp3File().getFrameCount());
		playbackSlider.setValue(0);
		Hashtable<Integer, JLabel> labelTableHashtable = new Hashtable<>();

		JLabel labelStart = new JLabel("00:00");
		labelStart.setFont(new Font("Dialog", Font.BOLD, 12));
		labelStart.setForeground(null);

		JLabel labelEnd = new JLabel(music.getSongLength());
		labelStart.setFont(new Font("Dialog", Font.BOLD, 12));
		labelStart.setForeground(null);

		labelTableHashtable.put(0, labelStart);
		labelTableHashtable.put(music.getMp3File().getFrameCount(), labelEnd);

		playbackSlider.setLabelTable(labelTableHashtable);
		playbackSlider.setPaintLabels(true);

	}

	public void setPlaybackSliderValue(int value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (playbackSlider != null) {
					playbackSlider.setValue(value);
				}
			}
		});
	}
	

	private void startPlaybackSliderThread() {
		if (musicPlayer != null) {
			musicPlayer.startPlaybackSliderThread(); // Start the playback slider thread in MusicPlayer
		}
	}

	public JSlider getPlaybackSlider() {
		return playbackSlider;
	}

	private void enablePauseButtonDisablePlayButton() {

		playBtn.setEnabled(false);
		playBtn.setVisible(false);

		pauseBtn.setEnabled(true);
		pauseBtn.setVisible(true);
	}

	private void enablePlayButtonDisablePauseButton() {

		playBtn.setEnabled(true);
		playBtn.setVisible(true);

		pauseBtn.setEnabled(false);
		pauseBtn.setVisible(false);

	}

	private static void customizeMenuButton(JButton jButton) {

		// jButton.setContentAreaFilled(false); // Set content area filled to false
		jButton.setUI(new BasicButtonUI());

		jButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				jButton.setBackground(null);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				jButton.setBackground(null);
			}
		});
	}

	private static void customPlaybackButtons(JButton jButton) {
		jButton.setOpaque(false); // Set button opacity to false
		jButton.setContentAreaFilled(false); // Set content area filled to false
		jButton.setBorderPainted(false); // Set border painted to false
		jButton.setUI(new BasicButtonUI());
	}



}
