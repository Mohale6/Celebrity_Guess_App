package com.example.celebrityguessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    int locationOfCorrectAnswer = 0;
    ArrayList<String> celebNames = new ArrayList<String>();

    public void chosenCeleb(View view){

        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct! ", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Incorrect! " , Toast.LENGTH_LONG).show();

    }
    public static class DownloadTask extends AsyncTask<String, Void, String>{


        public static class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

            @Override
            protected Bitmap doInBackground(String... urls) {
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    return BitmapFactory.decodeStream(inputStream);
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int chosenCeleb = 0;
        int locationOfCorrectAnswer = 0;
        String[] answers = new String[3];
        ArrayList<String> celebURLs = new ArrayList<String>();
        ArrayList<String> celebNames = new ArrayList<String>();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("C:\\Users\\Dell\\AndroidStudioProjects\\CelebrityGuessApp2\\app\\src\\main\\res\\drawable").get();

            String[] splitResult  = result.split("<div class=\"listedArticles\"/>");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()){
                celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()){
                celebNames.add(m.group(1));
            }
            Random random = new Random();
            chosenCeleb = random.nextInt(celebURLs.size());

            DownloadTask.ImageDownloader imageTask = new DownloadTask.ImageDownloader();
            Bitmap celebImage;
            celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswer = random.nextInt(3);

            int incorrectAnswerLocation;

            for(int i=0; i<3; i++){

                if(i == locationOfCorrectAnswer){

                    answers[i] = celebNames.get(chosenCeleb);
                }else {

                    incorrectAnswerLocation = random.nextInt(celebURLs.size());

                    while (incorrectAnswerLocation==chosenCeleb){

                        incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    }

                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}