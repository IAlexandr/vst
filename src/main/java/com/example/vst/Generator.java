package com.example.vst;
import com.example.vst.ArrayListStages;
import java.util.*;
import com.example.vst.MainActivity;

import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class Generator  extends AppCompatActivity {
    AudioTrack audioTrack;
    int buff_size;
    double frequency = 0;
    short[] location;
    Object mPauseLock;
    boolean mPaused = false;
    double phase = 0.0d;
    int sample_rate = 44100;
    int size_in_bytes;
    int size_in_shorts;
    TextView curFr;
    double maxFrequency = 200;
    ArrayList<ArrayListStages> ALS;
    ArrayListStages curStage = new ArrayListStages(50, 60, 1);
    Thread t;


    Generator(TextView curFr) {
        this.curFr = curFr;
        ALS = new ArrayList<ArrayListStages>();
        // уровни
        ALS.add(new ArrayListStages(50, 60, 1));
        ALS.add(new ArrayListStages(60, 80, 2));
        ALS.add(new ArrayListStages(80, 100, 3));
        ALS.add(new ArrayListStages(100, 120, 4));
        ALS.add(new ArrayListStages(120, 150, 5));
        ALS.add(new ArrayListStages(150, 200, 6));
        ALS.add(new ArrayListStages(200, 300, 7));
        ALS.add(new ArrayListStages(300, 400, 8));
        ALS.add(new ArrayListStages(400, 600, 9));
        ALS.add(new ArrayListStages(60, 800, 10));
        ALS.add(new ArrayListStages(800, 1000, 11));
        ALS.add(new ArrayListStages(1000, 1500, 12));
        ALS.add(new ArrayListStages(1500, 2000, 13));

        for (int i = 0; i < ALS.size(); i++) {
            Log.d("Debugger","Index: " + i + " - Item level: " + ALS.get(i).level);
        }
        //this.init();
    }

    void init () {
        this.mPauseLock = new Object();
        this.buff_size = AudioTrack.getMinBufferSize(this.sample_rate, 2, 2);
        this.audioTrack = new AudioTrack(3, this.sample_rate, 2, 2, this.buff_size, 1);
        this.sample_rate = AudioTrack.getNativeOutputSampleRate(3);
        this.size_in_shorts = this.buff_size;
        this.size_in_bytes = this.buff_size << 1;
        this.location = new short[this.size_in_shorts];
    }

    public void AudioPlay() {
        this.init();
        this.mPaused = false;
        t = new Thread(new Runnable() {
            public void run() {
            while (Generator.this.frequency < Generator.this.maxFrequency ||
                    Generator.this.frequency >= Generator.this.ALS.get(13).frEnd) {
                while (!Generator.this.mPaused) {
                    Generator.this.build();
                }
            }
            }
        });
        t.setPriority(10);
        t.start();
        this.audioTrack.play();
        //this.mPaused = false;
        this.curStage = this.ALS.get(2);
        this.frequency = this.curStage.frStart;
    }

    public void AudioStop() {
        this.audioTrack.stop();
        this.mPaused = true;
        //this.mPaused = true;
    }

    double getFrequency () {
        // TODO

        //this.curFr.setText("5");
        this.frequency += 1;
        //this.mact.uiUpdate();
        return this.frequency;
    }

    void build () {
        double fr = this.getFrequency();
        double oldphase = this.phase;
        this.phase = 0.0d;
        for (int a = 0; a < this.size_in_shorts; a++) {
            this.location[a] = (short) ((int) (Math.sin(this.phase + oldphase) * 32767.0d));
            this.phase = ((((((double) a) + 1.0d) * 2.0d) * 3.141592653589793d) * fr) / ((double) this.sample_rate);
            if (this.phase > 6.283185307179586d) {
                this.phase -= 6.283185307179586d;
            }
        }
        this.phase += oldphase;
        this.audioTrack.write(this.location, 0, this.size_in_shorts);
        synchronized (this.mPauseLock) {
            while (this.mPaused) {
                try {
                    this.mPauseLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
