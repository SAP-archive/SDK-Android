package ai.recast.sdk_android;


import android.os.Environment;

import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.io.File;
import java.util.List;

/**
 * The Client class handles requests to Recast.AI API.
 * Note that requests methods and stopRecording should not be called in the main thread of your application because they process http requests.
 *
 * @author Francois Triquet
 * @version 1.0.0
 * @since 2016-05-17
 *
 */
public class Client {
    private static final String		recastAPI = "https://api.recast.ai/v1/request";
    private String					token;
    private ExtAudioRecorder        myRecorder;

    /**
     * Initialize a Recast.AI Client with a authentication token
     * @param token Your token from Recast.AI
     */
    public Client(String token) {
        this.token = token;
    }

    /**
     * Initialize a Recast.AI Client without authentication token
     */
    public Client() {
        this("");
    }

    /**
     * Sets the token of the Client
     * @param token The token to authenticate to Recast.AI
     */
    public void setToken(String token) {
        this.token = token;
    }

    private static String getOutputFile() {
        File recastDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecastAudio");
        if (!recastDir.exists())
            recastDir.mkdir();
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return filepath + "/RecastAudio/recast_audio.wav";
    }

    /**
     * Starts recording audio from the microphone. Note that the audio must be shorter than 10 seconds to be processed by Recast.AI
     * @throws RecastException if an error occurs while recording
     */
    public synchronized void startRecording() throws RecastException {
        if (this.myRecorder != null) {
            this.myRecorder.stop();
            this.myRecorder.release();
        }
        try {
            myRecorder = ExtAudioRecorder.getInstanse(false);
            myRecorder.setOutputFile(getOutputFile());
            myRecorder.prepare();
            myRecorder.start();
        } catch (Exception e) {
            throw new RecastException ("Unable to record", e);
        }
    }

    /**
     * Stops recording from the microphone and returns the Response corresponding to the audio input after beeing processed
     * @return A Recast.AI Response
     * @throws RecastException if the Client is not recording of if an error occurs (invalid audio...)
     * @see Response
     */
    public synchronized Response stopRecording() throws RecastException {
        Response r;
        if (this.myRecorder == null) {
            throw new RecastException("Illegal recording state");
        }
        myRecorder.stop();
        try {
            File f = new File(getOutputFile());
            r = fileRequest(getOutputFile());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RecastException("Unable to send file to Recast", e);
        }
        finally {
            myRecorder.reset();
            myRecorder.release();
            myRecorder = null;
        }
        return r;
    }

    /**
     * Performs a text request to Recast.AI
     * @param text The text to be processed
     * @param token A token to authenticate to Recast.AI
     * @return The Response corresponding to the input
     * @throws RecastException if Recast.AI can't process the text
     * @see Response
     */
    public Response textRequest(String text, String token) throws RecastException {
        String				recastJson;

        recastJson = this.doApiRequest(text, token);
        return new Response(recastJson);
    }

    /**
     * Performs a text request to Recast.AI with the token of the Client
     * @param text The text to be processed
     * @return The Response corresponding to the input
     * @throws RecastException if Recast.AI can't process the text
     * @see Response
     */
    public Response	textRequest(String text) throws RecastException {
        return this.textRequest(text, this.token);
    }

    private String sendAudioFile(String name, String token) {
        String recastJson = "";
        try {
            MultipartUtility multipart = new MultipartUtility(recastAPI, "UTF-8", token);
            File f = new File(name);
            if (!f.exists()) {
                throw new RecastException("File not found: " + name);
            }
            multipart.addFilePart("voice", f);
            List<String> response = multipart.finish();
            for (String line : response) {
                recastJson += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RecastException("Error during request", e);
        }
        return recastJson;
    }

    /**
     * Performs a voice file request to Recast.AI. Note that the audio must not exceed 10 seconds and be in wav format.
     * @param filename The name of the file
     * @return The Response corresponding to your input
     * @throws RecastException if the file is invalid or Recast.Ai can't process the file
     */
    public Response fileRequest(String filename) throws RecastException {
        return this.fileRequest(filename, this.token);
    }

    /**
     * Performs a voice file request to Recast.AI. Note that the audio must not exceed 10 seconds and be in wav format.
     * @param filename The name of the file
     * @param token A token to authenticate to Recast.AI
     * @return The Response corresponding to your input
     * @throws RecastException if the file is invalid or Recast.Ai can't process the file
     */
    public Response fileRequest(String filename, String token) throws RecastException {
        String resp = sendAudioFile(filename, token);
        return new Response(resp);
    }


    private String			doApiRequest(String text, String token) throws RecastException {
        URL					obj;
        HttpsURLConnection	con;
        OutputStream		os;
        int					responseCode;
        String				inputLine;
        StringBuffer		responseBuffer;
        String				recastJson;

        try {
            obj = new URL(recastAPI);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization",  "Token " + token);
            con.setDoOutput(true);
            text = "text=" + text;
            os = con.getOutputStream();
            os.write(text.getBytes());
            os.flush();
            os.close();

            responseCode = con.getResponseCode();
        } catch (MalformedURLException e) {
            throw new RecastException("Invalid URL", e);
        } catch (IOException e) {
            throw new RecastException("Unable to read response from Recast", e);
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
                throw new RecastException("Unable to read response from Recast", e);
            }
            recastJson = responseBuffer.toString();
        } else {
            throw new RecastException(responseCode);
        }
        return recastJson;
    }
}