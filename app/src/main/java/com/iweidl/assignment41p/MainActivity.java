package com.iweidl.assignment41p;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // Views
    TextView textViewCurrentPhase;
    TextView textViewTimeRemaining;
    ProgressBar progressBarPhaseProgress;
    Button buttonStartTimer;
    Button buttonStopTimer;
    EditText editTextWorkoutDuration;
    EditText editTextRestDuration;

    // Timer Variables
    CountDownTimer countDownTimer;
    long workoutTime;
    long restTime;
    long currentCountdown;

    // Enum to represent the state of the timer
    enum CurrentState {
        STARTED,
        STOPPED
    }
    CurrentState currentState = CurrentState.STOPPED;

    // Vibrator object for vibration after every exercise phase
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        textViewCurrentPhase = findViewById(R.id.textViewCurrentPhase);
        textViewTimeRemaining = findViewById(R.id.textViewTimeRemaining);
        progressBarPhaseProgress = findViewById(R.id.progressBarPhaseProgress);
        buttonStartTimer = findViewById(R.id.buttonStartTimer);
        buttonStopTimer = findViewById(R.id.buttonStopTimer);
        editTextWorkoutDuration = findViewById(R.id.editTextWorkoutDuration);
        editTextRestDuration = findViewById(R.id.editTextRestDuration);

        // Initialize vibrator for later use
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Update UI elements based on the current state
        ReEvaluateUIState();

        // Set the click listener for the start timer button
        buttonStartTimer.setOnClickListener(view -> {
            // Check if the workout and rest duration fields are empty and show a toast if they are
            if (editTextWorkoutDuration.getText().toString().equals("") || editTextRestDuration.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Please set a Workout and Rest duration!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the workout and rest times from the input fields
            workoutTime = Long.parseLong(editTextWorkoutDuration.getText().toString());
            restTime = Long.parseLong(editTextRestDuration.getText().toString());

            // Update the UI
            currentState = CurrentState.STARTED;
            ReEvaluateUIState();

            // Start the workout phase
            startWorkoutPhase(workoutTime);
        });

        // Set the click listener for the stop timer button
        buttonStopTimer.setOnClickListener(view -> {
            // Set the current state to STOPPED and update the UI
            currentState = CurrentState.STOPPED;
            ReEvaluateUIState();
            // Cancel the current countdown timer
            countDownTimer.cancel();
        });
    }

    // Function to start the workout phase
    private void startWorkoutPhase(long workoutTime) {
        // Set the UI elements for the workout phase
        currentCountdown = workoutTime;
        textViewCurrentPhase.setTextColor(Color.RED);
        textViewCurrentPhase.setTypeface(null, Typeface.BOLD);

        progressBarPhaseProgress.setProgress(0);
        progressBarPhaseProgress.setMax((int) workoutTime);
        progressBarPhaseProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));

        textViewTimeRemaining.setTextColor(Color.RED);

        // Create and start a new countdown timer for the workout phase
        // (restTime + 1) is used to handle the transition to startRestPhase()
        countDownTimer = new CountDownTimer((workoutTime + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarPhaseProgress.setProgress((int) currentCountdown);
                textViewTimeRemaining.setText(String.valueOf(currentCountdown));
                currentCountdown--;
            }

            @Override
            public void onFinish() {
                textViewTimeRemaining.setText("0");
                vibrator.vibrate(500);
                // Start the rest phase when the workout phase finishes
                startRestPhase(restTime);
            }
        };
        countDownTimer.start();
        textViewCurrentPhase.setText("Workout Phase");
    }

    // Function to start the rest phase
    private void startRestPhase(long restTime) {
        // Set the UI elements for the rest phase
        String greenColorCode = "#10704e"; // Manually picked green color code, as Color.GREEN is too bright for the white background.
        currentCountdown = restTime;

        textViewCurrentPhase.setTextColor(Color.parseColor(greenColorCode));
        textViewCurrentPhase.setTypeface(null, Typeface.BOLD_ITALIC);

        progressBarPhaseProgress.setProgress(0);
        progressBarPhaseProgress.setMax((int) restTime);
        progressBarPhaseProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor(greenColorCode)));

        textViewTimeRemaining.setTextColor(Color.parseColor(greenColorCode));

        // Create and start a new countdown timer for the rest phase
        // (restTime + 1) is used to handle the transition to startWorkoutPhase()
        countDownTimer = new CountDownTimer((restTime + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBarPhaseProgress.setProgress((int) currentCountdown);
                textViewTimeRemaining.setText(String.valueOf(currentCountdown));
                currentCountdown--;
            }

            @Override
            public void onFinish() {
                textViewTimeRemaining.setText("0");
                vibrator.vibrate(500);
                // Start the workout phase again when the rest phase finishes
                startWorkoutPhase(workoutTime);
            }
        };
        countDownTimer.start();
        textViewCurrentPhase.setText("Rest Phase");
    }

    // Function to update the UI elements based on the current state
    private void ReEvaluateUIState() {
        if (currentState == CurrentState.STARTED) {
            // If the timer is running, enable the stop button and disable the start button
            buttonStopTimer.setEnabled(true);
            buttonStartTimer.setEnabled(false);
        } else {
            // If the timer is not running, enable the start button and disable the stop button
            buttonStopTimer.setEnabled(false);
            buttonStartTimer.setEnabled(true);
        }
    }
}