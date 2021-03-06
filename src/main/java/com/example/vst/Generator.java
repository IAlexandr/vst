package com.example.vst;
import com.example.vst.ArrayListStages;
import java.util.*;
import com.example.vst.MainActivity;

import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
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
    double lvlTime = 3000;
    long duration = 100;
    long localTime = 0;
    boolean reverse = false;
    int startStageIndex = 0;
    EditText edtTxtTimeInterval;

    Generator(EditText edtTxtTimeInterval) {
        this.curFr = curFr;
        this.edtTxtTimeInterval = edtTxtTimeInterval;
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
        ALS.add(new ArrayListStages(600, 800, 10));
        ALS.add(new ArrayListStages(800, 1000, 11));
        ALS.add(new ArrayListStages(1000, 1500, 12));
        ALS.add(new ArrayListStages(1500, 2000, 13));

        for (int i = 0; i < ALS.size(); i++) {
            Log.d("Debugger","Index: " + i + " - Item level: " + ALS.get(i).level);
        }
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
        String str = this.edtTxtTimeInterval.getText().toString();
        this.lvlTime = 1000 * Double.valueOf(str);
        this.curStage = this.ALS.get(startStageIndex);
        this.frequency = this.curStage.frStart;
        reverse = false;
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
        this.stageManager();
    }

    public void AudioStop() {
        this.audioTrack.stop();
        this.mPaused = true;
    }

    void build () {
        double oldphase = this.phase;
        this.phase = 0.0d;
        for (int a = 0; a < this.size_in_shorts; a++) {
            this.location[a] = (short) ((int) (Math.sin(this.phase + oldphase) * 32767.0d));
            this.phase = ((((((double) a) + 1.0d) * 2.0d) * 3.141592653589793d) * this.frequency) / ((double) this.sample_rate);
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

    void timer (final double frPart) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (Generator.this.reverse) {
                            Generator.this.frequency -= frPart;
                        } else {
                            Generator.this.frequency += frPart;
                        }
                        Generator.this.localTime += duration;
                        if (Generator.this.mPaused) {

                        } else {
                            if (!Generator.this.reverse) {
                                if (Generator.this.frequency >= Generator.this.curStage.frEnd) {
                                    if (Generator.this.curStage.level != 13) {
                                        Generator.this.curStage = Generator.this.ALS.get(Generator.this.curStage.level);
                                        Generator.this.stageManager();
                                    } else {
                                        Generator.this.reverse = true;
                                        Generator.this.stageManager();
                                    }
                                } else {
                                    Generator.this.timer(frPart);
                                }
                            } else {
                                if (Generator.this.frequency <= Generator.this.curStage.frStart) {
                                    if (Generator.this.curStage.level != 1) {
                                        Generator.this.curStage = Generator.this.ALS.get(Generator.this.curStage.level - 2);
                                        Generator.this.stageManager();
                                    } else {
                                        Generator.this.AudioStop();
                                    }
                                } else {
                                    Generator.this.timer(frPart);
                                }
                            }

                        }
                    }
                },
                duration);
    }

    void stageManager () {
        Generator.this.localTime = 0;
        double sumFr = Generator.this.curStage.frEnd - Generator.this.curStage.frStart;
        double intervalPart = lvlTime / duration;
        double frPart = sumFr / intervalPart;
        this.timer(frPart);
    }
}
