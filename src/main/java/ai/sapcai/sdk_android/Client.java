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

/**
 * The Client class handles requests to SAP Conversational AI API.
 * Note that requests methods and stopRecording should not be called in the main thread of your application because they process http requests.
 *
 * @author Francois Triquet
 * @version 2.0.0
 * @since 2016-05-17
 *
 */
public class Client {
    private static final String		sapcaiAPI = "https://api.cai.tool.sap/v2/request";
    private String					token;
	private String					language;
    private SapcaiRecorder          recorder;

    public Request request;

    /**
     * Initialize a SAP Conversational AI Client with a authentication token
     * @param token Your token from SAP Conversational AI
     */
    public Client(String token) {
        this.token = token;
		this.language = null;
        this.recorder = null;

        this.request = new Request(token);
    }

	public Client(String token, String language) {
		this.token = token;
		this.language = language;
		this.recorder = null;

		this.request = new Request(token, language);
	}

    /**
     * Initialize a SAP Conversational AI Client without authentication token
     */
    public Client() {
        this("", null);
    }

    /**
     * Sets the token of the Client
     * @param token The token to authenticate to SAP Conversational AI
     */
    public void setToken(String token) {
        this.token = token;
    }

	/**
	 * Sets the language of the Client
	 * @param language The language for the language processing
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

   private static String getOutputFile() {
       File sapcaiDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SAPConversationalAI");
       if (!sapcaiDir.exists())
           sapcaiDir.mkdir();
       String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
       return filepath + "/SAPConversationalAI/sapcai_audio.wav";
   }

    /**
     * Starts recording audio from the microphone. Note that the audio must be shorter than 10 seconds to be processed by SAP Conversational AI
     * @throws SapcaiException if the client is already recording (the recording will stop)
     */
   public synchronized void startRecording() throws SapcaiException {
       if (recorder != null) {
           try {
               this.stopRecording();
           } catch (Exception ignore) {}
           throw new SapcaiException("Invalid recording state");
       }
       recorder = new SapcaiRecorder(getOutputFile());
       recorder.startRecording();
   }

    /**
     * Stops recording from the microphone and returns the Response corresponding to the audio input after beeing processed
     * @return A SAP Conversational AI Response
     * @throws SapcaiException if the Client is not recording of if an error occurs (invalid audio...)
     * @see Response
     */
   public synchronized Response stopRecording() throws SapcaiException {
       Response r;

       if (this.recorder == null || !this.recorder.isRecording()) {
           throw new SapcaiException("Illegal recording state");
       }
       try {
           recorder.stopRecording();
           File f = new File(getOutputFile());
           r = fileRequest(getOutputFile());
       } catch (IOException e) {
           throw new SapcaiException("Unable to record audio", e);
       } finally {
           recorder = null;
       }
       return r;
   }

    /**
     * Performs a text request to SAP Conversational AI
     * @param text The text to be processed
     * @param options A map of parameters to the request. Parameters can be "token" and "language"
     * @return The Response corresponding to the input
     * @throws SapcaiException if SAP Conversational AI can't process the text
     * @see Response
     */
	public Response textRequest(String text, Map<String, String> options) throws SapcaiException {
		String	sapcaiJson;
		String	token;
		String	language;

		token = options.get("token");
		if (token == null)
			token = this.token;
		language = options.get("language");
		if (language == null)
			language = this.language;
		sapcaiJson = this.doApiRequest(text, token, language);
		return new Response(sapcaiJson);
	}

    /**
     * Performs a text request to SAP Conversational AI with the token of the Client
     * @param text The text to be processed
     * @return The Response corresponding to the input
     * @throws SapcaiException if SAP Conversational AI can't process the text
     * @see Response
     */
    public Response	textRequest(String text) throws SapcaiException {
		Map <String, String> params = new HashMap<>();
		params.put("language", this.language);
		params.put("token", this.token);
        return this.textRequest(text, params);
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

    /**
     * Performs a voice file request to SAP Conversational AI. Note that the audio must not exceed 10 seconds and be in wav format.
     * @param filename The name of the file
     * @return The Response corresponding to your input
     * @throws SapcaiException if the file is invalid or SAP Conversational AI can't process the file
     */
    public Response fileRequest(String filename) throws SapcaiException {
        return new Response(this.sendAudioFile(filename, this.token, null));
    }

    /**
     * Performs a voice file request to SAP Conversational AI. Note that the audio must not exceed 10 seconds and be in wav format.
     * @param filename The name of the file
     * @param options A map of parameters for the request. This map can contains "token" and/or "language".If a parameter is not provided, the request will use the token or language of the Client. I it has not language, the language used will be the default language of the corresponding bot
     * @return The Response corresponding to your input
     * @throws SapcaiException if the file is invalid or SAP Conversational AI can't process the file
     */
    public Response fileRequest(String filename, Map<String,String> options) throws SapcaiException {
		String token;
		String language;

		token = options.get("token");
		if (token == null)
			token = this.token;
		language = options.get("language");
		if (language == null)
			language = this.language;
        String resp = sendAudioFile(filename, token, language);
        return new Response(resp);
    }


    public String			doApiRequest(String text, String token, String language) throws SapcaiException {
        URL					obj;
        HttpsURLConnection	con;
        OutputStream		os;
        int					responseCode;
        String				inputLine;
        StringBuffer		responseBuffer;
        String				sapcaiJson;

        try {
            obj = new URL(sapcaiAPI);
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
