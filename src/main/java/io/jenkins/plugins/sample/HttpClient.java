package io.jenkins.plugins.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient
{
    public static String executeGet(String targetURL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("contentType", "utf-8");
            connection.setRequestMethod("GET");

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = rd.readLine()) != null)
            {
                System.out.println(line);
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
