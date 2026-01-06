
// importing exceptions for the audio related code
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
    
/**
 * The main class used to launch the application
 */
public class Launcher {
    /**
     *
     * @param args Command line arguments (not used in this application).
     * @throws UnsupportedAudioFileException  If an unsupported audio file format is encountered.
     * @throws IOException If an I/O error occurs.
     * @throws LineUnavailableException If a line required for audio playback is unavailable.
     */
    public static void main(String[] args)  throws UnsupportedAudioFileException, IOException, LineUnavailableException  { // main method that runs the app and catches exceptions
        new GUI().setVisible(true); // used to display the graphical user interface(GUI)
    }
    
}