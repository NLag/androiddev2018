package com.nlag.onlinemusicplayer.MusicPlayerActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nlag.onlinemusicplayer.R;

/**
 * Created by nlag on 11/23/17.
 */

public class MusicPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }
}
