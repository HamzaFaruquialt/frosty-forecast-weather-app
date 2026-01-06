
// all necessary imports for the WeatherApp to function
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * WeatherApp class used to display the hourly and accurate weather of a
 * specific location and will then display it on the GUI
 */
public class WeatherAppAPI {
    /**
     * Retrieves weather data from API
     * weather and the GUI will display the data to user
     * 
     * @param locationName The name of a specific location based on coordinates
     * @return A JSONArray containing weatherData(or null as a default)
     */
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,precipitation,weathercode,windspeed_10m&timezone=auto";

        try {
            // call API and get a response
            HttpURLConnection connection = createConnection(urlString);
            // check for a response status
            // 200 means the connection was a success
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API" + connection.getResponseCode());
                return null;
            }

            // store resulting json data
            StringBuilder resultJsonStringBuilder = new StringBuilder();
            Scanner scannerObject = new Scanner(connection.getInputStream());
            while (scannerObject.hasNext()) {
                resultJsonStringBuilder.append(scannerObject.nextLine());
            }
            scannerObject.close();
            // close connection
            connection.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJsonStringBuilder));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            // we want to get the current hour's data so we will get the index of our
            // current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windSpeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windSpeedData.get(index);

            // get precipitation
            JSONArray precipData = (JSONArray) hourly.get("precipitation");
            double precipitation = (double) precipData.get(index);

            // build the weather json data object that we are going to access in our
            // frontend
            Map<String, Object> weatherDataMap = new HashMap<>();
            weatherDataMap.put("temperature", temperature);
            weatherDataMap.put("weather_condition", weatherCondition);
            weatherDataMap.put("humidity", humidity);
            weatherDataMap.put("windspeed", windspeed);
            weatherDataMap.put("precipitation", precipitation);

            JSONObject weatherData = new JSONObject(weatherDataMap);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Code used to get the location data
     * Retrieves location data (latitude, longitude) based on a location name using
     * an external geocoding API.
     *
     * @param locationName used to change the weather/temperature based on the
     *                     location searched
     * @return A JSONArray containing location data otherwise return null
     */
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in location name to add to API's request format
        // otherwise it will not function
        locationName = locationName.replaceAll(" ", "+");

        // build the API url with location parameter
        String apiUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName
                + "&count=10&language=en&format=json";

        try {
            // call your API to get a response
            HttpURLConnection connection = createConnection(apiUrl);
            StringBuilder resultJson = new StringBuilder();
            // check respone status
            // 200 means successful connection
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not successfully connect to the API");
                return null;
            } else {
                // store the API results
                Scanner scannerObject = new Scanner(connection.getInputStream());
                // read and store the resulting json data into our string builder
                while (scannerObject.hasNext()) {
                    resultJson.append(scannerObject.nextLine());
                }
            }
            // close url connection
            connection.disconnect();

            // parse the JSON String into a JSON ob
            JSONParser parser = new JSONParser(); // creating a new JSON parser object
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            // get the list of location data the API generated from the location name
            JSONArray locationData = (JSONArray) resultJsonObject.get("results");

            return locationData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        // could not find location
        return null;
    }

    /**
     *
     * @param apiUrl the URL of the API endpoint that we are attempting to call
     * @return The HttpURLConnection object for the API response, or returns null if
     *         it doesn't work
     */
    private static HttpURLConnection createConnection(String apiUrl) {
        try {
            // attempt to create connection by creating a url object
            URL urlObj = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // set request method to get because we are trying to get the location with the
            // API
            connection.setRequestMethod("GET");

            // connect to our API
            connection.connect();
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // could not make connection
        return null;
    }

    /**
     * Method where we iterate through the time list in order to find which
     * index in the for loop matches the current time(in real life)
     *
     * @param timeList A JSONArray containing a list of times
     * @return the index of current time (or 0 as a default)
     *
     */
    public static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentDateTimeString();

        // iterate through the time list and see which one matches our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // returns the index
                return i;
            }
        }

        return 0;
    }

    /**
     * Code used to get the current time in the proper format for an API
     * response(getter method)
     *
     * @return A string respresenting the formatted data and time
     */
    public static String getCurrentDateTimeString() {
        // get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format data time to be 2024-01-02T00:00 (this is how it is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T:'HH:mm");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    /**
     * Code used to convert weather code to something more readable
     *
     * @param weathercode parameter used to convert the weathercode to weather
     *                    conditions based on the numbers found on the API website
     * @return A string that respresents the weatherCondition
     */
    public static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            // clear
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) {
            // rain
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            // snow
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
