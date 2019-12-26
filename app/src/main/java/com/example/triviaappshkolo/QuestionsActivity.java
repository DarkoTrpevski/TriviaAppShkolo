package com.example.triviaappshkolo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaappshkolo.adapters.QuestionAdapter;
import com.example.triviaappshkolo.datamodel.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsActivity";

    String stringQuestionAmount;
    String stringCategory;
    String stringDifficulty;
    String stringType;

    String stringCategoryIntent;

    QuestionAdapter questionAdapter;
    Button buttonFinish;

    int counter = 0;

    public static final String BASE_URL = "https://opentdb.com/api.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        buttonFinish = findViewById(R.id.btnFinish);

        //INITIALIZE THE PROCESSES IN THE ON CREATE METHOD
        initActivity();
    }

//--------------------------------------------------------------------------------------------------
    //INITIALIZE THE PROCESSES IN THE ON CREATE METHOD
    private void initActivity() {
        Intent intent = getIntent();
        if (intent != null) {
            stringQuestionAmount = intent.getStringExtra(MainActivity.KEY_QUERY_AMOUNT_QUESTIONS);
            stringCategoryIntent = intent.getStringExtra(MainActivity.KEY_QUERY_CATEGORY);

            if (stringCategoryIntent != null) {
                if (stringCategoryIntent.equalsIgnoreCase("mythology")) {
                    stringCategory = "20";
                } else if (stringCategoryIntent.equalsIgnoreCase("geography")) {
                    stringCategory = "22";
                } else if (stringCategoryIntent.equalsIgnoreCase("any category")) {
                    stringCategory = "";
                }
            }
            stringDifficulty = intent.getStringExtra(MainActivity.KEY_QUERY_DIFFICULTY);
            stringType = intent.getStringExtra(MainActivity.KEY_QUERY_TYPE);

        } else {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "onCreate: Before creating the async task");
        FetchDataAsyncTask fetchDataAsyncTask = new FetchDataAsyncTask(this);
        fetchDataAsyncTask.execute();
    }

    //BUTTON ON CLICK
    public void finishTheGame(View view) {
        AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(QuestionsActivity.this);
        myAlertBuilder.setTitle(R.string.title);

        List<Boolean> numberOfCorrectAnswers = questionAdapter.getTrueOrFalseAnswers();
        for (int i = 0; i < numberOfCorrectAnswers.size(); i++) {
            //noinspection PointlessBooleanExpression
            if (numberOfCorrectAnswers.get(i) == true){
                counter ++;
            }
        }
        int numberOfTotalAnswers = questionAdapter.getItemCount();
        myAlertBuilder.setMessage("Number of correct answers" + counter + " / " + numberOfTotalAnswers);
//--------------------------------------------------------------------------------------------------
        myAlertBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        myAlertBuilder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        myAlertBuilder.show();
    }

//--------------------------------------------------------------------------------------------------
    class FetchDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Question>> {

        private WeakReference<Context> contextWeakReference;

        FetchDataAsyncTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Question> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Starting the async task");
            ArrayList<Question> questions = new ArrayList<>();
            Uri uri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(MainActivity.KEY_QUERY_AMOUNT_QUESTIONS, stringQuestionAmount)
                    .appendQueryParameter(MainActivity.KEY_QUERY_CATEGORY, stringCategory)
                    .appendQueryParameter(MainActivity.KEY_QUERY_DIFFICULTY, stringDifficulty)
                    .appendQueryParameter(MainActivity.KEY_QUERY_TYPE, stringType)
                    .build();

            try {
                URL url = new URL(uri.toString());
                String jsonResponse = "";

                HttpURLConnection httpURLConnection = null;
                InputStream inputStream = null;

                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    jsonResponse = convertIsToString(inputStream);
                    Log.d(TAG, "doInBackground: jsonResponse is returned");

                    try {

                        JSONObject jsonResponseObject = new JSONObject(jsonResponse);

                        JSONArray jsonResultsArray = jsonResponseObject.getJSONArray("results");
                        Log.d(TAG, "doInBackground: Before starting the loop");
                        for (int i = 0; i < jsonResultsArray.length(); i++) {
                            List<String> incorrectAnswers = new ArrayList<>();
                            Question questionObject = new Question();

                            JSONObject jsonQuestionObject = (JSONObject) jsonResultsArray.get(i);

                            questionObject.setCategory(jsonQuestionObject.getString("category"));
                            questionObject.setType(jsonQuestionObject.getString("type"));
                            questionObject.setDifficulty(jsonQuestionObject.getString("difficulty"));
                            questionObject.setQuestion(jsonQuestionObject.getString("question"));
                            questionObject.setCorrectAnswer(jsonQuestionObject.getString("correct_answer"));

                            JSONArray jsonIncorrectAnswersArray = jsonQuestionObject.getJSONArray("incorrect_answers");
                            for (int j = 0; j < jsonIncorrectAnswersArray.length(); j++) {
                                String incorrectAnswer = jsonIncorrectAnswersArray.getString(j);
                                incorrectAnswers.add(incorrectAnswer);
                            }
                            questionObject.setIncorrectAnswers(incorrectAnswers);

                            Log.d(TAG, "doInBackground: Adding the questionObject in the array list");
                            questions.add(questionObject);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return questions;
        }

        @Override
        protected void onPostExecute(ArrayList<Question> questions) {
            super.onPostExecute(questions);

            Context context = contextWeakReference.get();

            Log.d(TAG, "onPostExecute: Inside the post execute");
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            Log.d(TAG, "onPostExecute: Creating a new adapter");
            questionAdapter = new QuestionAdapter(questions, context);

            Log.d(TAG, "onPostExecute: Setting layout manager and adapter");
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(questionAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            buttonFinish.setVisibility(View.VISIBLE);
        }

        //--------------------------------------------------------------------------------------------------
        //METHOD THAT TAKES AN INPUT STREAM AND RETURNS THE JSONRESPONSE STRING
        private String convertIsToString(InputStream inputStream) throws IOException {
            StringBuilder sb = new StringBuilder();
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();
        }
    }
//--------------------------------------------------------------------------------------------------
}
