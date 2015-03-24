package com.netbug.speechcalc;

import com.google.common.base.Joiner;
import com.netbug.speechcalc.RecognitionGuess;
import com.netbug.speechcalc.SpeechCalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import com.netbug.speechcalc.*;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private TextView txtSpeechInput;
	TextToSpeech ttobj;
	String userResponse;
	String FILENAME = "transcription";
	TreeMap<Integer, LinkedList<String>> reminders;
	int category;
	public static String language = "en_US";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		userResponse="";
        reminders = new TreeMap<Integer, LinkedList<String>>();
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
    	category = 0;
        
        ttobj=new TextToSpeech(getApplicationContext(), 
        new TextToSpeech.OnInitListener() {    
        @Override
        public void onInit(int status) {
           if(status != TextToSpeech.ERROR){
               if (language == "en_US")
                   ttobj.setLanguage(Locale.US);
               else {
                   Locale locale = new Locale("ru");
                   ttobj.setLanguage(locale);
               }
              }				
           }
        });
	}

    public void requestSpeech(View view){
    	speakText(getString(R.string.speech_prompt), 2400);
    	promptSpeechInput();
    }
       
    @SuppressWarnings("deprecation")
	public void speakText(String toSpeak, int timeDelay){
  	   Log.d("convo", toSpeak);
 	   ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
 	   try {
		   Thread.sleep(timeDelay);
 	   }
 	   catch (InterruptedException e) {
 		   Thread.currentThread().interrupt();
		   return;
 	   }
    }
     
    
    private final int REQ_CODE_SPEECH_INPUT = 100;
    
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void generateNoteOnSD(String sFileName, String sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "TranscriptionNotes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
             e.printStackTrace();
        }
       }  
    
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQ_CODE_SPEECH_INPUT: {
            if (resultCode == RESULT_OK && null != data) {
 				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                List<RecognitionGuess> results = SpeechCalculator.processRecognitionResult(result);

				txtSpeechInput.setText(Joiner.on("\n\n").join(results));
                ttobj.speak(results.get(0).toTTSForm(), TextToSpeech.QUEUE_FLUSH, null);
                //generateNoteOnSD("result",results.get(0));
            }
         }
         break;
       }
     }
    
    @Override
    public void onPause(){
       if(ttobj !=null){
          ttobj.stop();
       }
       super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            language = "ru_RU";
            Locale locale = new Locale("ru");
            ttobj.setLanguage(locale);
        }else {
            language = "en_US";
            ttobj.setLanguage(Locale.US);
        }
    }
}
