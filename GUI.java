
// All the necessary imports for the app to function
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame; // Importing JFrame which is a class in the Java Swing Library used to represent a top level window with a title and border which is commonly used for a GUI
import javax.swing.JLabel; // Importing JLabel which  is a class in the Java Swing library used to display a single line of text or a single image
import javax.swing.JTextField; // Importing JTextField is a class in the Java Swing Library used to creaate a single line text input field used for the GUI in which users can input text into
import javax.swing.SwingConstants;

import org.json.simple.JSONObject; // Importing JSONObject which are objects used from the JSON tet formatting

/**
 * The GUI class for the weather app
 */
public class GUI extends JFrame {
    // instance variables
    private JSONObject weatherData;
    private static Clip audioClip;

    /**
     * Default constructor that initializes the GUI with default features
     */
    public GUI() {
        super("Frosty Forecast");

        setDefaultCloseOperation(EXIT_ON_CLOSE); // Set default close operation to be to exit

        setSize(510, 620); // Sets the default size of the GUI

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        initializeComponents();

    }

    /**
     * Adds all the components to the GUI including text fields, search buttons,
     * labels and more
     */
    private void initializeComponents() {
        // new searchbar object
        JTextField searchBar = new JTextField();

        // set location and size of our component
        searchBar.setBounds(50, 10, 351, 45);

        // change font size and style
        Font font = new Font("Arial", Font.PLAIN, 24);
        searchBar.setFont(font);

        // set the color of the search bar and text
        searchBar.setBackground(new Color(191, 209, 230));
        searchBar.setForeground(new Color(64, 64, 64));
        add(searchBar);

        try {
            audioClip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        // weather image
        JLabel weatherConditionLabel = new JLabel(loadImageIcon("src\\assets\\cloudy.png"));
        weatherConditionLabel.setBounds(35, 90, 450, 217);
        add(weatherConditionLabel);
        playMusic("src\\assets\\wind.wav");

        // temperature text (degrees celcius)
        JLabel temperatureText = new JLabel("0 C");
        temperatureText.setBounds(46, 315, 450, 54);
        Font tempFont = new Font("Arial", Font.BOLD, 48);
        temperatureText.setFont(tempFont); // reusing the font object I previously created

        // Sets the alignment of the temperature text to the center
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description text
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(40, 355, 450, 54);
        Font weatherFont = new Font("Arial", Font.PLAIN, 28);
        weatherConditionDesc.setFont(weatherFont);

        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImageIcon("src\\assets\\humidity.png"));
        humidityImage.setBounds(-10, 500, 74, 79);
        add(humidityImage);

        // humidity text
        JLabel humidityText1 = new JLabel("<html><b>Humidity</b> 11%</html>");
        humidityText1.setBounds(60, 500, 85, 55);
        Font humidFont = new Font("Arial", Font.PLAIN, 16);
        humidityText1.setFont(humidFont);
        add(humidityText1);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImageIcon("src\\assets\\windspeed.png"));
        windspeedImage.setBounds(140, 510, 84, 76);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 1km/h</html>");
        windspeedText.setBounds(230, 500, 85, 55);
        Font windspeedFont = new Font("Arial", Font.PLAIN, 15);
        windspeedText.setFont(windspeedFont);
        add(windspeedText);

        // precipitation image
        JLabel precipitationImage = new JLabel(loadImageIcon("src\\assets\\precipitation.png"));
        precipitationImage.setBounds(305, 492, 94, 86);
        add(precipitationImage);

        // precipitation text
        JLabel precipText = new JLabel("<html><b>Precipitation</b> 5mm</html>");
        precipText.setBounds(390, 500, 115, 55);
        Font precipFont = new Font("Arial", Font.PLAIN, 15);
        precipText.setFont(precipFont);
        add(precipText);

        // display current date/time using HTML format
        JLabel dateTimeText = new JLabel("<html><b>Current Date/Time</b> yyyy-MM-dd<br>'HH:00'</html>");
        Font dateTimeFont = new Font("Arial", Font.PLAIN, 10);
        dateTimeText.setBounds(230, 395, 129, 55);
        dateTimeText.setFont(dateTimeFont);
        dateTimeText.setHorizontalAlignment(SwingConstants.CENTER);
        add(dateTimeText);

        // search button used in weather app
        JButton searchButton = new JButton(loadImageIcon("src\\assets\\search.png"));
        // change cursor to hand cursor when hovering above this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(398, 10, 47, 45);
        searchButton.setBackground(new Color(140, 145, 150));
        searchButton.addActionListener(new ActionListener() {

            /**
             * Called when a user-triggered action occurs, such as a button click.
             * Handles user input, retrieves weather data, and updates the GUI accordingly.
             *
             * @param e The ActionEvent triggering the action (not extensively used in this
             *          method).
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchBar.getText();

                // validate input - remove whitespace to ensure non- empty text
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // retrieve weather data
                weatherData = WeatherAppAPI.getWeatherData(userInput);

                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // depending on the condition, we wil update the weather image that corresponds
                // with the condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionLabel.setIcon(loadImageIcon("src\\assets\\clear.png"));
                        playMusic("src\\assets\\birds.wav");
                        break;
                    case "Cloudy":
                        weatherConditionLabel.setIcon(loadImageIcon("src\\assets\\cloudy.png"));
                        playMusic("src\\assets\\wind.wav");
                        break;
                    case "Rain":
                        weatherConditionLabel.setIcon(loadImageIcon("src\\assets\\rain.png"));
                        playMusic("src\\assets\\rain.wav");
                        break;
                    case "Snow":
                        weatherConditionLabel.setIcon(loadImageIcon("src\\assets\\snow.png"));
                        playMusic("src\\assets\\ice.wav");
                        break;
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText1.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

                // update precipitation text
                double precipitation = (double) weatherData.get("precipitation");
                precipText.setText("<html><b>Precipitation</b> " + precipitation + "mm</html>");

                // update date and time
                dateTimeText.setText(
                        "<html><b>Current Date/Time</b> " + WeatherAppAPI.getCurrentDateTimeString().substring(0, 10)
                                + "<br> " + WeatherAppAPI.getCurrentDateTimeString().substring(11) + ":00</html>");

            }
        });
        add(searchButton);
    }

    /**
     * Method used to play the music. Creating a new file and getting the audio
     * depending on which .wav file is used
     *
     * @param filePath The file path of the music file to be played.
     */
    public static void playMusic(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);

            // Stop the previous clip if it's still running
            if (audioClip != null && audioClip.isRunning()) {
                audioClip.stop();
            }

            // Close the previous clip
            if (audioClip.isOpen()) {
                audioClip.close();
            }

            // Open and start the new clip
            audioClip.open(audioInput);
            audioClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to load the image based on the resource path
     *
     * @param resourcePath The resource path of the image.
     * @return An ImageIcon representing the loaded image.
     */
    private ImageIcon loadImageIcon(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // return a new image icon so that our component can render it
            return new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not Find Resource" + resourcePath);
        return null;
    }

}
