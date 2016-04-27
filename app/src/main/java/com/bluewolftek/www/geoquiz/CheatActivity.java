package com.bluewolftek.www.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    //Variables for saved states
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bluewolftek.www.geoquiz.answer_is_true";  //Save answer true to Intent
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bluewolftek.www.geoquiz.answer_shown";  //Save cheater across Intent
    private static final String DATA_USER_CHEATED =
            "com.bluewolftek.www.geoquiz.user_cheated";  //Save cheater across onSavedInstanceState

    //Member variables
    private boolean mAnswerIsTrue;
    private boolean mUserCheated;
    private TextView mAnswerTextView;
    private Button mShowAnswer;
    private TextView mAPITextView;
    private int mAPILevel;

    //Create Intent to store boolean "true" where answerIsTrue
    //(this gets sent to QuizActivity)
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }

    //Method to get Extra from Intent for whether answer was shown or not
    //(this will be used from QuizActivity)
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    //Begin onCreate for CheatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //If user cheated and rotated screen, they still cheated!
        if(savedInstanceState != null) {

            //Get saved info
            setAnswerShownResult(savedInstanceState.getBoolean(DATA_USER_CHEATED, false));
            mUserCheated = savedInstanceState.getBoolean(DATA_USER_CHEATED);
            mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
            mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

            //Restore text view info from onSavedInstanceState
            if(mUserCheated && mAnswerIsTrue) {
                mAnswerTextView.setText("True");
            } else if(mUserCheated && !mAnswerIsTrue) {
                mAnswerTextView.setText("False");
            } else {
                mUserCheated = false;
                setAnswerShownResult(false);
            }
        }


        //Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get mAnswerTrue from Intent
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        //Set text view for answer
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        //Show answer button
        mShowAnswer = (Button) findViewById(R.id.show_answer_button);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
                mUserCheated = true;

                //Add fancy circular animation
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswer.getWidth() / 2;
                    int cy = mShowAnswer.getHeight() / 2;
                    float radius = mShowAnswer.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswer, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnswerTextView.setVisibility(View.VISIBLE);
                            mShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mAnswerTextView.setVisibility(View.VISIBLE);
                    mShowAnswer.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Show API Level
        mAPILevel = Build.VERSION.SDK_INT;
        mAPITextView = (TextView) findViewById(R.id.api_text_view);
        mAPITextView.setText("API level " + mAPILevel);

    } //End onCreate

    //Method to send whether user cheated or not to an Intent
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    //Override onSavedInstanceState to store mUserCheated and mAnswerIsTrue
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(DATA_USER_CHEATED, mUserCheated);
        savedInstanceState.putBoolean(EXTRA_ANSWER_IS_TRUE, mAnswerIsTrue);
    }

}
