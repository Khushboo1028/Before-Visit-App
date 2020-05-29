package com.beforevisit.beforevisit.Model;

public class Faq {

    String question;
    String answer;
    String date_created;

    public Faq(String question, String answer, String date_created) {
        this.question = question;
        this.answer = answer;
        this.date_created = date_created;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
