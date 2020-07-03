package com.example.mcalcpro;
import ca.roumani.i2c.MPro;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MCalcPro_Activity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener, SensorEventListener{
    private TextToSpeech tts;
    EditText principleView, amortizationView, interestView;
    Button analyzeBtn;
    String principleInput, amortizationInput, interestInput, s;
    int amortizationPeriod, dollars, cents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts = new TextToSpeech(this, this);
//        new code
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sm != null){
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
        principleView = (EditText) findViewById(R.id.pBox);

        amortizationView = (EditText) findViewById(R.id.aBox);

        interestView = (EditText) findViewById(R.id.iBox);

        analyzeBtn = findViewById(R.id.button);
        analyzeBtn.setOnClickListener(this);

    }

    @Override
    public void onInit(int initStatus) {
        this.tts.setLanguage(Locale.US);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            double a = Math.sqrt(ax * ax + ay * ay + az * az);
            if (a > 20) {
                ((EditText) findViewById(R.id.pBox)).setText("");
                ((EditText) findViewById(R.id.aBox)).setText("");
                ((EditText) findViewById(R.id.iBox)).setText("");
                ((TextView) findViewById(R.id.output)).setText("");
            }
        }
    }


    public void onClick(View v) {
        principleInput = principleView.getText().toString();
        amortizationInput = amortizationView.getText().toString();
        interestInput = interestView.getText().toString();

        try {
            MPro mp = new MPro();
            mp.setPrinciple(principleInput);
            mp.setAmortization(amortizationInput);
            mp.setInterest(interestInput);
            String monthly = mp.computePayment("%,.2f");
            amortizationPeriod = Integer.parseInt(amortizationInput);

//            bug start
            double mPayment =  Double.parseDouble(monthly);
//            if you comment line 88 out and other lines that reference it(that is line 92 to 108) it works).
//            To hear the computer read the details, uncomment line 117
//            bug end

            dollars = (int) (Math.floor(mPayment));
            cents = Integer.parseInt(String.format(Locale.CANADA, "%.0f", Double.parseDouble(String.format(Locale.CANADA, "%.2f", ((mPayment % 1)))) * 100.0D));
            String d = "Monthly Payment = " + dollars + (dollars == 1 ? "dollar" : " dollars") + (cents == 0 ? "" : " and");
            String c = cents + (cents == 1 ? "cent" : "cents");

//            audio
            if (dollars != 0 && cents != 0) {
                tts.speak(d, TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(c, TextToSpeech.QUEUE_ADD, null);
            } else if (cents == 0) {
                tts.speak(d, TextToSpeech.QUEUE_FLUSH, null);
            } else if (dollars == 0) {
                tts.speak("Monthly payment = ", TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(c, TextToSpeech.QUEUE_ADD, null);
            } else {
                tts.speak("Monthly payment = 0 dollars", TextToSpeech.QUEUE_FLUSH, null);
            }

            s = "Monthly Payment = " + monthly;
            s += "\n\n\n";
            s += "By making this payments monthly for " + amortizationPeriod + " years, the mortgage will be paid in full. But if" +
                    " you terminate the mortgage on its nth " +
                    "anniversary, the balance still owing depends " +
                    "on n as shown below: ";
//            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            s += "\n\n";
            s += String.format("%8s", "n") + String.format("%16s", "Balance");
            s += "\n\n";
            int j = 0;
            for (int i=5; i<= amortizationPeriod; i = i + 5) {
                for(; j < 5; j++) {
                    s += String.format("%8d", j) + mp.outstandingAfter(j, "%,16.0f");
                    s += "\n\n";
                }
                j = 5;
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }
            ((TextView) findViewById(R.id.output)).setText(s);
        }
        catch (Exception e) {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }


    }



}