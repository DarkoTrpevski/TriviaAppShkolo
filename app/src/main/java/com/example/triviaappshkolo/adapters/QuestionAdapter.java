package com.example.triviaappshkolo.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaappshkolo.R;
import com.example.triviaappshkolo.datamodel.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolderSpinner> {
    private static final String TAG = "QuestionAdapter";
    private List<Question> questionList;
    private Context context;

    //Declare a int member variable and initialize to 0 (at the top of your class)
    private int lastSpinnerPosition = 0;
//--------------------------------------------------------------------------------------------------
    // NOVO
    int iCurrentSelection = 0;
//--------------------------------------------------------------------------------------------------

    private List<String> selectedAnswers;
    private List<Boolean> trueOrFalseAnswers;
    public List<Boolean> getTrueOrFalseAnswers() {
        return trueOrFalseAnswers;
    }

    public QuestionAdapter(List<Question> questionList, Context context) {
        this.questionList = questionList;
        this.context = context;
        selectedAnswers = new ArrayList<>();
        trueOrFalseAnswers = new ArrayList<>();
    }

    @NonNull
    @Override
    public QuestionViewHolderSpinner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflateView = layoutInflater.inflate(R.layout.single_card_item_spinner, parent, false);
        return new QuestionViewHolderSpinner(inflateView);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionViewHolderSpinner holder, int position) {

        // ZA DA MOZHE DA RABOTI LISTENER-OT NA SPINNER-OT TREBA FINAL NA CURRENT QUESTION-OT
        final Question currentQuestion = questionList.get(position);

        String question = currentQuestion.getQuestion();
        holder.textViewSpinnerQuestion.setText(question);

        String correctAnswer = currentQuestion.getCorrectAnswer();
        List<String> incorrectAnswers = currentQuestion.getIncorrectAnswers();

        List<String> spinnerTotalAnswers = new ArrayList<>(incorrectAnswers);
        spinnerTotalAnswers.add(correctAnswer);

        Collections.shuffle(spinnerTotalAnswers);
        spinnerTotalAnswers.add(0, "Select an answer");


        final ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerTotalAnswers);
        answerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerAnswer.setAdapter(answerAdapter);

        for (int i = 0; i < spinnerTotalAnswers.size(); i++) {
            for (int j = 0; j < selectedAnswers.size(); j++) {
                if (selectedAnswers.get(j).equals(spinnerTotalAnswers.get(i))) {
                    String compareValue = selectedAnswers.get(j);

                    if (compareValue != null) {
                        int spinnerPosition = answerAdapter.getPosition(compareValue);
                        holder.spinnerAnswer.setSelection(spinnerPosition);
                    }

                }
            }
        }
//--------------------------------------------------------------------------------------------------
        // NOVO
        iCurrentSelection = holder.spinnerAnswer.getSelectedItemPosition();
//--------------------------------------------------------------------------------------------------

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//--------------------------------------------------------------------------------------------------
                // NOVO
                if (iCurrentSelection != i){
//--------------------------------------------------------------------------------------------------
                    String spinnerSelectedAnswer = holder.spinnerAnswer.getSelectedItem().toString();
                    selectedAnswers.add(spinnerSelectedAnswer);

                    if (spinnerSelectedAnswer.equalsIgnoreCase(currentQuestion.getCorrectAnswer())) {
                        Toast.makeText(context, "Correct answer", Toast.LENGTH_SHORT).show();
                        trueOrFalseAnswers.add(holder.getAdapterPosition(),true);
                    } else {
                        Toast.makeText(context, "Wrong answer", Toast.LENGTH_SHORT).show();
                        trueOrFalseAnswers.add(holder.getAdapterPosition(),false);
                    }
                }
                iCurrentSelection = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        };

        holder.spinnerAnswer.setOnItemSelectedListener(listener);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
//--------------------------------------------------------------------------------------------------

    public class QuestionViewHolderSpinner extends RecyclerView.ViewHolder {
        TextView textViewSpinnerQuestion;
        Spinner spinnerAnswer;

        QuestionViewHolderSpinner(@NonNull View itemView) {
            super(itemView);
            textViewSpinnerQuestion = itemView.findViewById(R.id.text_view_spinner_question);
            spinnerAnswer = itemView.findViewById(R.id.spinner_answer);
        }
    }
}
