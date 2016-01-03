package com.example.vst;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Button;
        import android.view.View;
        import com.example.vst.Generator;
        import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    boolean state = false;
    TextView txtNextTime;
    Button btnSwitch;
    TextView txtLvl;
    TextView txtCurrentFrequency;
    TextView txtFrStart;
    TextView txtFrEnd;
    Generator generator;
    DecimalFormat formatter = new DecimalFormat("#0.00");
    DecimalFormat formatter2 = new DecimalFormat("#0");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.init();
    }

    void init() {
        this.btnSwitch = (Button) findViewById(R.id.btnSwitch);
        this.txtNextTime = (TextView) findViewById(R.id.txtNextTime);
        this.txtLvl = (TextView) findViewById(R.id.txtLvl);
        this.txtCurrentFrequency = (TextView) findViewById(R.id.txtCurrentFrequency);
        this.txtFrStart = (TextView) findViewById(R.id.txtFrStart);
        this.txtFrEnd = (TextView) findViewById(R.id.txtFrEnd);
        generator = new Generator((EditText) findViewById(R.id.edtTxtTimeInterval));
        this.uiUpdate();
    }

    public void uiUpdate (){
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        ArrayListStages curStage = MainActivity.this.generator.curStage;
                        MainActivity.this.txtCurrentFrequency.setText(formatter.format(MainActivity.this.generator.frequency));
                        MainActivity.this.txtFrStart.setText(formatter2.format(curStage.frStart));
                        MainActivity.this.txtFrEnd.setText(formatter2.format(curStage.frEnd));
                        MainActivity.this.txtLvl.setText(Integer.toString(curStage.level));
                        if (MainActivity.this.generator.reverse) {
                            MainActivity.this.txtLvl.setTextColor(0xFFD11E1E);
                        } else {
                            MainActivity.this.txtLvl.setTextColor(0xFF29C02B);
                        }
                        MainActivity.this.txtNextTime.setText(formatter2.format(((MainActivity.this.generator.lvlTime - MainActivity.this.generator.localTime)  / 1000)));
                        MainActivity.this.uiUpdate();
                    }
                },
                100);
    }

    public void TestStateSwitch (View view) {
        this.state = !this.state;
        if (this.state) {
            this.btnSwitch.setText("Остановить");
            this.generator.AudioPlay();
        } else {
            this.btnSwitch.setText("Начать");
            this.generator.AudioStop();
        }
    }
}
