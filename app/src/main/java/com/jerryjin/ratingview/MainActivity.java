package com.jerryjin.ratingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jerryjin.ratingview.library.widget.newer.FlexibleRatingView;
import com.jerryjin.ratingview.library.widget.older.RatingView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ratingView)
    RatingView mRv;

    @BindView(R.id.seekBar)
    SeekBar mSeekBar;

    @BindView(R.id.ratingView2)
    FlexibleRatingView flexibleRatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    private void initWidget() {
        ButterKnife.bind(this);
//        mRv.setOnRatingChangeListener(new RatingView.OnRatingChangeListener() {
//            @Override
//            public void onRatingChange(float rating) {
//                Toast.makeText(MainActivity.this, "ratingProgress: " + String.valueOf(rating * 10),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
        flexibleRatingView.setEnableTouch(true);
        flexibleRatingView.setOnScoreChangeListener(new FlexibleRatingView.OnScoreChangeListener() {
            @Override
            public void onScoreChangeListener(float outScores) {
                Toast.makeText(MainActivity.this, "scores: " + outScores, Toast.LENGTH_SHORT).show();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeImpl() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                float percentage = progress * 1.0f / 100;
                Log.e("PERCENTAGE", String.valueOf(percentage));
//                mRv.setOnRatingChangeListener(new RatingView.OnRatingChangeListener() {
//                    @Override
//                    public void onRatingChange(float rating) {
//                        Log.e("OUTRATING:" , String.valueOf(mRv.getRating()));
//                        Toast.makeText(MainActivity.this,
//                                "ratingProgress: " + String.valueOf(rating * 10), Toast.LENGTH_SHORT).show();
//                    }
//                }).setRatingProgress(percentage);
                flexibleRatingView.setScores(percentage * 5);
            }
        });



    }


    public static abstract class OnSeekBarChangeImpl implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
