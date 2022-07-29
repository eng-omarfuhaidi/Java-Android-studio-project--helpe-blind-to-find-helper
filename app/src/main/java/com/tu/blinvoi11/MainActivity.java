package com.tu.blinvoi11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

    private TextToSpeech tts;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";
    private static final String NAME = "name";
    private static final String AGE = "age";
    private static final String AS_NAME = "as_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Tap the screen and say no if you are a volunteer or say yes if you are blind");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

     /*   findViewById(R.id.microphoneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });*/

      /*  preferences = getSharedPreferences(PREFS,0);
        editor = preferences.edit();*/

        mVoiceInputTv = (TextView) findViewById(R.id.textView);
        mSpeakBtn = (ImageButton) findViewById(R.id.microphoneButton);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });


    }

    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Yes or No?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }





    private void speak(String text){
        tts.setSpeechRate(0.1f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


   /* private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                }
                if(mVoiceInputTv.getText().toString().equals("لا"))
                {
                    Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }
                if(mVoiceInputTv.getText().toString().equals("نعم"))
                {
                    Intent intent =new Intent(getApplicationContext(),BlindActivity.class);
                    startActivity(intent);
                }

                break;
            }

        }
    }

   /* private void recognition(String text){

        Log.e("Speech",""+text);

        //creating an array which contains the words of the answer
        String[] speech = text.split(" ");

        //the last word is our name

        if(text.contains("omar")) {
            //String name = speech[speech.length - 1];
           // Log.e("Your name", "" + name);
            //we got the name, we can put it in local storage and save changes
           // editor.putString(NAME, name).apply();
            Intent my = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivityForResult(my, 0);

            //make the app tell our name
            speak("Your name is " + preferences.getString(NAME, null));
        }
        if(text.contains("years") && text.contains("old")){
            String age = speech[speech.length-3];
            Log.e("THIS", "" + age);
            editor.putString(AGE, age).apply();
        }

//Then ask it for your age
        if(text.contains("how old am I")){
            speak("You are "+preferences.getString(AGE,null)+" years old.");
        }


        if(text.contains("what time is it")){
            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");//dd/MM/yyyy
            Date now = new Date();
            String[] strDate = sdfDate.format(now).split(":");
            if(strDate[1].contains("00"))strDate[1] = "o'clock";
            speak("The time is " + sdfDate.format(now));
        }
    }*/



}
