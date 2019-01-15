package ai.sapcai.sdk_android;

import android.os.Environment;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

	private static final String	sapcaiAPI = "https://api.cai.tools.sap/v2/request";
	private static final String converseAPI = "https://api.cai.tools.sap/v2/converse";

	public String token;
	public String language;

	public Request(String token){
		this.token = token;
	}

	public Request(String token, String language){
		this.token = token;
		this.language = language;
	}

	/**
     * Performs a text request to SAP Conversational AI with the token of the Client
     * @param myText The text to be processed
     * @return The Response corresponding to the input
     * @throws SapcaiException if SAP Conversational AI can't process the text
     * @see Response
     */
	public Response doTextRequest(String myText) throws SapcaiException {
		URL obj;
		try {
			obj = new URL(sapcaiAPI);
			String sapcaiJson = this.doApiRequest(myText, this.token, this.language, obj);
			return new Response(sapcaiJson);
		} catch (MalformedURLException e) {
			throw new SapcaiException("Invalid URL", e);
		}
	}

	/**
     * Performs a voice file request to SAP Conversational AI. Note that the audio must not exceed 10 seconds and be in wav format.
     * @param myfile The name of the file
     * @return The Response corresponding to your input
     * @throws SapcaiException if the file is invalid or SAP Conversational AI can't process the file
     */
	public Response doFileRequest (String myfile) throws SapcaiException {
		return new Response(this.sendAudioFile(myfile, this.token, this.language));
	}

	public Conversation doTextConverse (String myText) {
		URL obj;
		try {
			obj = new URL(converseAPI);
			String sapcaiJson = this.doApiRequest(myText, this.token, this.language, obj);
			return new Conversation(sapcaiJson, this.token);
		} catch (MalformedURLException e) {
			throw new SapcaiException("Invalid URL", e);
		}
	}

	private String sendAudioFile(String name, String token, String language) throws SapcaiException {
        String sapcaiJson = "";
		StringBuilder sb;
        try {
            MultipartUtility multipart = new MultipartUtility(sapcaiAPI, "UTF-8", token);
            File f = new File(name);
            if (!f.exists()) {
                throw new SapcaiException("File not found: " + name);
            }
            multipart.addFilePart("voice", f);
			if (language != null) {
				multipart.addFormField("language", language);
			} else if (this.language != null) {
                multipart.addFormField("language", this.language);
            }
            List<String> response = multipart.finish();
			sb = new StringBuilder(response.size() * 2);
            for (String line : response) {
				sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SapcaiException("Error during request", e);
        }
        return sb.toString();
	}

	public String doApiRequest(String text, String token, String language, URL obj) throws SapcaiException {
//        URL					obj;
        HttpsURLConnection	con;
        OutputStream		os;
        int					responseCode;
        String				inputLine;
        StringBuffer		responseBuffer;
        String				sapcaiJson;

        try {
//            obj = new URL(sapcaiAPI);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization",  "Token " + token);
            con.setDoOutput(true);
            text = "text=" + text;
            os = con.getOutputStream();
            os.write(text.getBytes());
			if (language != null) {
				String l = "&language=" + language;
				os.write(l.getBytes());
			}
            os.flush();
            os.close();

            responseCode = con.getResponseCode();
        } catch (MalformedURLException e) {
            throw new SapcaiException("Invalid URL", e);
        } catch (IOException e) {
            throw new SapcaiException("Unable to read response from SAP Conversational AI", e);
        }

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                responseBuffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                reader.close();
            } catch (IOException e) {
                throw new SapcaiException("Unable to read response from SAP Conversational AI", e);
            }
            sapcaiJson = responseBuffer.toString();
        } else {
            System.out.println(responseCode);
            throw new SapcaiException(responseCode);
        }
        return sapcaiJson;
	}

}
