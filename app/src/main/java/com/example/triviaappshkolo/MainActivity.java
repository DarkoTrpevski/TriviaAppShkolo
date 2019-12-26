package com.example.triviaappshkolo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //--------------------------------------------------------------------------------------------------
    String queryQuestionAmount = "";
    String queryCategory = "";
    String queryDifficulty = "";
    String queryType = "";

    String queryCategoryText = "";
    String queryTypeText = "";
    //--------------------------------------------------------------------------------------------------
    Button buttonStart;
    EditText editTextNumberOfQuestions;
    Spinner spinnerCategory;
    Spinner spinnerDifficulty;
    Spinner spinnerType;
    //--------------------------------------------------------------------------------------------------
    public static final String KEY_QUERY_AMOUNT_QUESTIONS = "amount";
    public static final String KEY_QUERY_CATEGORY = "category";
    public static final String KEY_QUERY_DIFFICULTY = "difficulty";
    public static final String KEY_QUERY_TYPE = "type";
    //--------------------------------------------------------------------------------------------------
    public static int CURRENT_QUESTION_AMOUNT = 0;
    public static final int MIN_QUESTION_VALUE = 10;
    public static final int MAX_QUESTION_VALUE = 20;

    //--------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        createSpinners();

        //CHECK FOR EDIT TEXT CONDITIONS, THEN START ACTIVITY
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_QUESTION_AMOUNT = Integer.valueOf(editTextNumberOfQuestions.getText().toString());
                queryQuestionAmount = editTextNumberOfQuestions.getText().toString();

                if (CURRENT_QUESTION_AMOUNT >= MIN_QUESTION_VALUE && CURRENT_QUESTION_AMOUNT <= MAX_QUESTION_VALUE) {
                    //TODO CHECK AND INTENT HERE
                    Intent intent = new Intent(MainActivity.this, QuestionsActivity.class);
                    intent.putExtra(KEY_QUERY_AMOUNT_QUESTIONS, queryQuestionAmount);
                    intent.putExtra(KEY_QUERY_CATEGORY, queryCategoryText);
                    intent.putExtra(KEY_QUERY_DIFFICULTY, queryDifficulty);
                    intent.putExtra(KEY_QUERY_TYPE, queryTypeText);
                    startActivity(intent);
                }

            }
        });
    }

    //--------------------------------------------------------------------------------------------------
    private void initLayout() {
        editTextNumberOfQuestions = findViewById(R.id.edit_text_number_of_questions);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerType = findViewById(R.id.spinner_type);
        buttonStart = findViewById(R.id.btnStart);
    }

    //--------------------------------------------------------------------------------------------------
    private void createSpinners() {

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_difficulty, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
        spinnerDifficulty.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setOnItemSelectedListener(this);
    }

    //--------------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getId()) {

            case R.id.spinner_category:
                queryCategoryText = parent.getItemAtPosition(position).toString().toLowerCase();
                if (queryCategoryText.equalsIgnoreCase("mythology")) {
                    queryCategory = "20";
                } else if (queryCategoryText.equalsIgnoreCase("geography")) {
                    queryCategory = "22";
                } else if (queryCategoryText.equalsIgnoreCase("any category")) {
                    queryCategory = "";
                }

            case R.id.spinner_difficulty:
                queryDifficulty = parent.getItemAtPosition(position).toString().toLowerCase();

            case R.id.spinner_type:
                queryType = parent.getItemAtPosition(position).toString().toLowerCase();
                queryType = queryType.replace(" ", "");
                if (queryType.equalsIgnoreCase("multiplechoice")) {
                    queryTypeText = "multiple";
                } else if (queryType.equalsIgnoreCase("trueorfalse")) {
                    queryTypeText = "boolean";
                } else {
                    queryTypeText = "";
                }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_LONG).show();
    }
//--------------------------------------------------------------------------------------------------
}
