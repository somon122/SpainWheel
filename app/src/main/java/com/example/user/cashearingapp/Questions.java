package com.example.user.cashearingapp;

public class Questions {


    public String mQuestions [] = {

            "What is your name?",
            "What is your father name?",
            "What is your mother name?",
            "What is your country name?",
            "What is 5+5 ?",
            "What is 10+5 ?"

    };

private String mChoices [][]={

        {"Somon","Kalam","Salam","Nirob"},
        {"Somon","Sokur","Salam","Nirob"},
        {"Somon","Rahima","Salam","Nirob"},
        {"India","Bangladesh","Pakistan","Nepal"},
        {"Answer is 12","Answer is 15","Answer is 20","Answer is 10"},
        {"Answer is 12","Answer is 15","Answer is 20","Answer is 10"}
};

private String mCarrectAnswer []={

       "Somon","Sokur","Rahima","Bangladesh","Answer is 10","Answer is 15"};

public String getQuestion (int a)
{
    String question = mQuestions[a] ;
    return question;
}

public String getChoices1 (int a){
    String choice = mChoices[a][0];
    return choice;
}
public String getChoices2 (int a){
    String choice = mChoices[a][1];
    return choice;
}
public String getChoices3 (int a){
    String choice = mChoices[a][2];
    return choice;
}
public String getChoices4 (int a){
    String choice = mChoices[a][3];
    return choice;
}

public String getCarrectAnswer (int a){

    String answer = mCarrectAnswer[a];
    return answer;
}



}
