package com.example.calculationtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.Random;

// AndroidViewModel can access SavedSharedPreference
public class MyViewModel extends AndroidViewModel {

    private SavedStateHandle handle;
    final private static String KEY_HIGH_SCORE = "key_high_score";
    final private static String KEY_LEFT_NUMBER = "key_left_number";
    final private static String KEY_RIGHT_NUMBER = "key_right_number";
    final private static String KEY_OPERATOR = "key_operator";
    final private static String KEY_ANSWER = "key_answer";
    final private static String SAVE_SHP_DATA_NAME = "save_shp_data_name";
    final private static String KEY_CURRENT_SCORE = "key_current_score";

    final private static String KEY_USER_INPUT = "key_user_input";
    boolean win_flag;

    public MyViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);

        // if not contains, means initial load or system loss in accident
        if(!handle.contains(KEY_HIGH_SCORE)) {
            SharedPreferences shp = getApplication().getSharedPreferences(SAVE_SHP_DATA_NAME, Context.MODE_PRIVATE);
            handle.set(KEY_HIGH_SCORE, shp.getInt(KEY_HIGH_SCORE ,0));
            handle.set(KEY_LEFT_NUMBER, 0);
            handle.set(KEY_RIGHT_NUMBER, 0);
            handle.set(KEY_OPERATOR, 0);
            handle.set(KEY_ANSWER, 0);
            handle.set(KEY_CURRENT_SCORE, 0);
            handle.set(KEY_USER_INPUT, "");
        }
        this.handle = handle;
    }

    public MutableLiveData<Integer> getLeftNumber() {
        return handle.getLiveData(KEY_LEFT_NUMBER);
    }

    public MutableLiveData<Integer> getRightNumber() {
        return handle.getLiveData(KEY_RIGHT_NUMBER);
    }

    public MutableLiveData<String> getOperator() {
        return handle.getLiveData(KEY_OPERATOR);
    }

    public MutableLiveData<Integer> getHighScore() {
        return handle.getLiveData(KEY_HIGH_SCORE);
    }

    public MutableLiveData<Integer> getCurrentScore() {
        return handle.getLiveData(KEY_CURRENT_SCORE);
    }

    public MutableLiveData<Integer> getAnswer() {
        return handle.getLiveData(KEY_ANSWER);
    }

    public MutableLiveData<String> getUserInput() {
        return handle.getLiveData(KEY_USER_INPUT);
    }

    void generator() {
        int LEVEL = 20;
        Random random = new Random();

        // x, y scope: 0-LEVEL
        int x = random.nextInt(LEVEL) + 1;
        int y = random.nextInt(LEVEL) + 1;

        int random_operator = random.nextInt(100);
        if(random_operator % 2 == 0) {
            getOperator().setValue("+");
            if(x > y) {
                // example: x = 10, y = 2 => LeftNumber = 2, RightNumber = 8
                getAnswer().setValue(x);
                getLeftNumber().setValue(y);
                getRightNumber().setValue(x - y);
            }
            else {
                getAnswer().setValue(y);
                getLeftNumber().setValue(x);
                getRightNumber().setValue(y - x);
            }
        }
        else {
            getOperator().setValue("-");
            if(x > y) {
                getAnswer().setValue(x - y);
                getLeftNumber().setValue(x);
                getRightNumber().setValue(y);
            }
            else {
                getAnswer().setValue(y - x);
                getLeftNumber().setValue(y);
                getRightNumber().setValue(x);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    void save() {
        SharedPreferences shp = getApplication().getSharedPreferences(SAVE_SHP_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putInt(KEY_HIGH_SCORE, getHighScore().getValue());
        editor.apply();
    }

    void saveUserInput() {
        SharedPreferences shp = getApplication().getSharedPreferences(SAVE_SHP_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(KEY_USER_INPUT, getUserInput().getValue());
        editor.apply();
    }

    @SuppressWarnings("ConstantConditions")
    void answerCorrect() {
        getCurrentScore().setValue(getCurrentScore().getValue() + 1);

        if(getCurrentScore().getValue() > getHighScore().getValue()) {
            getHighScore().setValue(getCurrentScore().getValue());
            win_flag = true;
        }
        generator();
    }
}
