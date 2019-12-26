package com.example.triviaappshkolo.datamodel;


import com.example.triviaappshkolo.datamodel.Question;

import java.util.ArrayList;

public class QuestionsResponse {

    private int response_code;

    private ArrayList<Question> questionList;

    public QuestionsResponse(int response_code, ArrayList<Question> questionList) {
        this.response_code = response_code;
        this.questionList = questionList;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }
}