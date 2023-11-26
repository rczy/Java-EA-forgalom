package app.rest;

import app.mnb.MNBArfolyamServiceSoap;
import app.mnb.MNBArfolyamServiceSoapImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserRestClient {
    private final String endpoint = "https://gorest.co.in/public/v2/users";
    private final String token = "883aaf31c8874be0730ad015000de6d98d0e4a2c2a509db27d183ec0dfb0d1d1";

    private static ObjectMapper mapper;

    public UserRestClient() {
        mapper = new ObjectMapper();
    }

    private URL getURL(Integer id) throws MalformedURLException {
        String URL = (id == null) ? endpoint : endpoint + '/' + id;
        return new URL(URL);
    }

    private String sendRequest(URL url, String method, String data, int successCode) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Bearer " + token);
        // Adat küldése
        if (!(data == null || data.isEmpty())) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)
            );
            bw.write(data);
            bw.close();
            connection.connect();
        }
        // Ha a válaszkód nem egyezik a várttal
        if (successCode != connection.getResponseCode()) {
            throw new Exception("Nem várt válaszkód: " + connection.getResponseCode());
        }
        // Válasz kiolvasása
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        connection.disconnect();
        return sb.toString();
    }

    public User GET(Integer id) throws Exception {
        String result = sendRequest(getURL(id), "GET", null, HttpsURLConnection.HTTP_OK);
        return mapper.readerFor(User.class).readValue(result);
    }

    public List<User> GET() throws Exception {
        String result = sendRequest(getURL(null), "GET", null, HttpsURLConnection.HTTP_OK);
        return mapper.readerForListOf(User.class).readValue(result);
    }

    public User POST(User user) throws Exception {
        String data = mapper.writeValueAsString(user);
        String result = sendRequest(getURL(null), "POST", data, HttpsURLConnection.HTTP_CREATED);
        return mapper.readerFor(User.class).readValue(result);
    }

    public void PUT(User user) throws Exception {
        Integer id = user.getId();
        String data = mapper.writeValueAsString(user);
        sendRequest(getURL(id), "PUT", data, HttpsURLConnection.HTTP_OK);
    }

    public void DELETE(User user) throws Exception {
        Integer id = user.getId();
        sendRequest(getURL(id), "DELETE", null, HttpsURLConnection.HTTP_NO_CONTENT);
    }
}
