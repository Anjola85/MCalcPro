//Student Name: Muhammed Adeyemi, 216766644
// This Lab was done individually
//https://youtu.be/wG4nTIpqvpo
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

public class MCalcPro_Activity extends AppCompatActivity implements  TextToSpeech.OnInitListener, SensorEventListener{
    private TextToSpeech tts;
    EditText principleView, amortizationView, interestView;
    TextView output;
    Button analyzeBtn;
    String principleInput, amortizationInput, interestInput, s, d, c;
    int amortizationPeriod, dollars, cents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts = new TextToSpeech(this, this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);

//        diff code in amirs
        if(sm != null) {
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }



    }

    public void onInit(int initStatus) {
        this.tts.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) { }

//    diff code in amirs
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            double a = Math.sqrt(ax * ax + ay * ay + az * az);
            if (a > 10) {
                ((EditText) findViewById(R.id.pBox)).setText("");
                ((EditText) findViewById(R.id.aBox)).setText("");
                ((EditText) findViewById(R.id.iBox)).setText("");
                ((TextView) findViewById(R.id.output)).setText("");
            }
        }
    }


    public void btnClicked(View v) {
        principleView =  findViewById(R.id.pBox);

        amortizationView =  findViewById(R.id.aBox);

        interestView =  findViewById(R.id.iBox);

        output =  findViewById(R.id.output);

        analyzeBtn = findViewById(R.id.button);


        try {

            MPro model = new MPro();
            model.setPrinciple(principleView.getText().toString());
            model.setAmortization(amortizationView.getText().toString());
            model.setInterest(interestView.getText().toString());

            String monthlyPayment = model.computePayment("%,.2f");
            amortizationPeriod = Integer.parseInt(model.getAmortization());
            Double mPayment = Double.parseDouble(model.computePayment("%.2f"));
            dollars = (int) (Math.floor(mPayment));
            cents = Integer.parseInt(String.format(Locale.CANADA, "%.0f", Double.parseDouble(String.format(Locale.CANADA, "%.2f", ((mPayment % 1)))) * 100.0D));
            d = "Monthly Payment = " + dollars + (dollars == 1 ? "dollar" : " dollars") + (cents == 0 ? "" : " and");
            c = cents + (cents == 1 ? "cent" : "cents");

            //audio output
            if (dollars != 0 && cents != 0) {
                tts.speak(d, TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(c, TextToSpeech.QUEUE_ADD, null);
            } else if (dollars != 0) {
                tts.speak(d, TextToSpeech.QUEUE_FLUSH, null);
            } else if (cents != 0) {
                tts.speak("Monthly payment = ", TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(c, TextToSpeech.QUEUE_ADD, null);
            } else {
                tts.speak("Monthly payment = 0 dollars and 0 cents", TextToSpeech.QUEUE_FLUSH, null);
            }


//          output table
            s = "Monthly Payment = " + monthlyPayment;
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
                    s += String.format(Locale.CANADA, "%8d", j) + model.outstandingAfter(j, "%,16.0f");
                    s += "\n\n";
                }
                j = 5;
                s += String.format(Locale.CANADA, "%8d", i) + model.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }
            output.setText(s);
        } catch (Exception e) {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }
    }



}