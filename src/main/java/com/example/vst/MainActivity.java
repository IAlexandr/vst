package com.example.vst;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;
        import android.widget.Button;
        import android.view.View;

public class MainActivity extends AppCompatActivity {
    boolean state = false;
    TextView txtNextTime;
    Button btnSwitch;
    TextView txtLvl;
    TextView txtCurrentFrequency;
    TextView txtFrStart;
    TextView txtFrEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.init();
    }

    void init() {
        btnSwitch = (Button) findViewById(R.id.btnSwitch);
        txtNextTime = (TextView) findViewById(R.id.txtNextTime);
        txtLvl = (TextView) findViewById(R.id.txtLvl);
        txtCurrentFrequency = (TextView) findViewById(R.id.txtCurrentFrequency);
        txtFrStart = (TextView) findViewById(R.id.txtFrStart);
        txtFrEnd = (TextView) findViewById(R.id.txtFrEnd);
    }

    public void TestStateSwitch (View view) {
        this.state = !this.state;
        if (this.state) {
            btnSwitch.setText("Остановить");
        } else {
            btnSwitch.setText("Начать");
        }
    }
}
