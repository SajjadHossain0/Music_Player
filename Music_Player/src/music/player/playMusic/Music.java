package music.player.playMusic;

import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import com.mpatric.mp3agic.Mp3File;

public class Music {

	private String songTitle;
	private String songLength;
	private String filepath;
	private Mp3File mp3File;
	private double frameRate;

	public Music(String filepath) {
		this.filepath = filepath;

		try {

			// this is for the counting frame rate to play and pause from the same frame
			mp3File = new Mp3File(filepath);
			frameRate = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
			songLength = convertToMusicLength();

			AudioFile audioFile = AudioFileIO.read(new File(filepath));

			org.jaudiotagger.tag.Tag tag = audioFile.getTag();

			if (tag != null && audioFile.getTag() != null) {
				songTitle = tag.getFirst(FieldKey.TITLE);
			} else {
				songTitle = "Unknown";
			}

		} catch (Exception e) {
			e.printStackTrace();
			songTitle = "Error";
		}
	}

	private String convertToMusicLength() {

		long minutes = mp3File.getLengthInSeconds() / 60;
		long seconds = mp3File.getLengthInSeconds() % 60;
		String formatedTimeString = String.format("%02d:%02d", minutes, seconds);

		return formatedTimeString;
	}

	public Mp3File getMp3File() {
		return mp3File;
	}

	public void setMp3File(Mp3File mp3File) {
		this.mp3File = mp3File;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}

	public String getSongLength() {
		return songLength;
	}

	public void setSongLength(String songLength) {
		this.songLength = songLength;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String toString() {
		return "Music [songTitle=" + songTitle + ", songLength=" + songLength + ", filepath=" + filepath + "]";
	}

}
