package ai.sapcai.sdk_android;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class RecastRecorder {

   static final int SAMPLE_RATE = 44100;
   static final int CHANNELS = 1;
   static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
   private AudioRecord mRecorder = null;
   private boolean mRecording = false;
   private int mBufferSize;
   private int mFramePeriod;
   private static String TAG = "RecastRecorder";
   private String mFilepath;
   private int mSize;
   private byte[] mBuffer;
   private RandomAccessFile mFileWriter;

   RecastRecorder (String file) throws RecastException {
       mFilepath = file;
       mSize = 0;

       mFramePeriod = SAMPLE_RATE * 120 / 1000;
       mBufferSize = mFramePeriod * 2 * 16  * 1 / 8; //2 * samples * channels / 8

       if (mBufferSize < AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, FORMAT)) { // Check to make sure buffer size is not smaller than the smallest allowed one
           mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, FORMAT);
           // Set frame period and timer interval accordingly
           mFramePeriod = mBufferSize / (2 * 16 * 1 / 8);
           Log.w(TAG, "Increasing buffer size to " + Integer.toString(mBufferSize));
       }

       mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
               SAMPLE_RATE,
               AudioFormat.CHANNEL_IN_MONO,
               FORMAT,
               mBufferSize);

       if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
           throw new RecastException("Record initialization failed");
       }
       mBuffer = new byte[mBufferSize];
       try {
           this.initOutputFile();
       } catch (IOException e) {
           throw new RecastException("Unable to open output file for recording");
       }

       AudioRecord.OnRecordPositionUpdateListener updater = new AudioRecord.OnRecordPositionUpdateListener() {
           @Override
           public void onMarkerReached(AudioRecord recorder) {

           }

           @Override
           public void onPeriodicNotification(AudioRecord recorder) {
               try {
                   int read = recorder.read(mBuffer, 0, mBuffer.length);
                   mSize += read;
                   mFileWriter.write(mBuffer);
               } catch (IOException e) {
                   Log.w(TAG, "An error occured while reading audio input, aborting");
                   done();
               }
           }
       };

       mRecorder.setRecordPositionUpdateListener(updater);
       mRecorder.setPositionNotificationPeriod(mFramePeriod);
   }

   public void initOutputFile() throws IOException {
       File f = new File(mFilepath);
       if (f.exists()) {
           f.delete();
           f.createNewFile();
       } else {
           f.createNewFile();
       }
       //WAV audio header
       mFileWriter = new RandomAccessFile(mFilepath, "rw");
       mFileWriter.setLength(0);
               mFileWriter.writeBytes("RIFF");
               mFileWriter.writeInt(0);
               mFileWriter.writeBytes("WAVE");
               mFileWriter.writeBytes("fmt ");
               mFileWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
               mFileWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
               mFileWriter.writeShort(Short.reverseBytes((short)1));// Number of channels, 1 for mono, 2 for stereo
               mFileWriter.writeInt(Integer.reverseBytes(SAMPLE_RATE)); // Sample rate
               mFileWriter.writeInt(Integer.reverseBytes(SAMPLE_RATE * 16 * 1 / 8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
               mFileWriter.writeShort(Short.reverseBytes((short) (CHANNELS * 16 / 8))); // Block align, NumberOfChannels*BitsPerSample/8
               mFileWriter.writeShort(Short.reverseBytes((short)16)); // Bits per sample
               mFileWriter.writeBytes("data");
               mFileWriter.writeInt(0); // Data chunk size not known yet, write 0
       mBuffer = new byte[mFramePeriod * 16 / 8 * 1];
   }

   public void done() {
       if (mRecorder != null) {
           mRecorder.stop();
           mRecorder.release();
           mRecorder = null;
       }
   }

   public void startRecording() throws RecastException {
       mRecording = true;
       mRecorder.startRecording();
       mRecorder.read(mBuffer, 0, mBuffer.length);

   }

   public void stopRecording() throws IOException {
       mRecording = false;
       done();
       mFileWriter.seek(4);
       mFileWriter.writeInt(Integer.reverseBytes(36 + mSize));
       mFileWriter.seek(40);
       mFileWriter.writeInt(Integer.reverseBytes(mSize));
       mFileWriter.close();
   }

   public boolean isRecording() {
       return mRecording;
   }
}
