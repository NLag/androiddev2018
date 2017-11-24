package com.nlag.onlinemusicplayer.MusicPlayerActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by nlag on 11/23/17.
 */

public class MusicPlayerActivity extends AppCompatActivity {
    public String playstatus = "pause";
    public String repeatstatus = "none";
    public String shufflestatus = "off";
    public Song songtoplay;
    public MediaPlayer mediaPlayer;
    ImageView playbut;
    ImageView repeatbut;
    ImageView shufflebut;
    String statuspath = "/sdcard/NLag/";
    String statusFile = "playerstatus.dat";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar myToolbar = findViewById(R.id.player_toolbar);
        myToolbar.setTitle(R.string.nowplaying);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        songtoplay = new Song(this, "", "", "");

        try {
            initPlayerStatus(savedInstanceState);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getSongFromBundle(savedInstanceState);

        updateSongInfo();

        initPlayerView();


    }

    public void getSongFromBundle(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            songtoplay.name = extras.getString("title");
            songtoplay.artist = extras.getString("artist");
            songtoplay.performer = extras.getString("performer");
            songtoplay.onlinemusic = extras.getBoolean("onlinemusic");
            if (songtoplay.onlinemusic) {
                songtoplay.sourcelink = extras.getString("sourcelink");
                byte[] byteArray = getIntent().getByteArrayExtra("thumb");
                songtoplay.thumb = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            } else {
                songtoplay.filepath = extras.getString("filepath");
            }
        }
    }

    public void initPlayerView() {
        playbut = findViewById(R.id.playbut);
        if (playstatus.equals("pause")) {
            playbut.setImageResource(R.drawable.play_button);
        } else if (playstatus.equals("playing")) {
            playbut.setImageResource(R.drawable.pause_button);
        }
        playbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playstatus.equals("pause")) {
                    playbut.setImageResource(R.drawable.pause_button);
                    playstatus = "playing";
                } else if (playstatus.equals("playing")) {
                    playbut.setImageResource(R.drawable.play_button);
                    playstatus = "pause";
                }
            }
        });

        repeatbut = findViewById(R.id.repeatbut);
        if (repeatstatus.equals("none")) {
            repeatbut.setImageResource(R.drawable.repeat_none);
        } else if (repeatstatus.equals("one")) {
            repeatbut.setImageResource(R.drawable.repeat_one);
        } else if (repeatstatus.equals("all")) {
            repeatbut.setImageResource(R.drawable.repeat_all);
        }
        repeatbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatstatus.equals("none")) {
                    repeatbut.setImageResource(R.drawable.repeat_one);
                    repeatstatus = "one";
                } else if (repeatstatus.equals("one")) {
                    repeatbut.setImageResource(R.drawable.repeat_all);
                    repeatstatus = "all";
                } else if (repeatstatus.equals("all")) {
                    repeatbut.setImageResource(R.drawable.repeat_none);
                    repeatstatus = "none";
                }
            }
        });

        shufflebut = findViewById(R.id.shufflebut);
        if (shufflestatus.equals("off")) {
            shufflebut.setImageResource(R.drawable.shuffle_off);
        } else if (shufflestatus.equals("on")) {
            shufflebut.setImageResource(R.drawable.shuffle_on);
        }
        shufflebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shufflestatus.equals("off")) {
                    shufflebut.setImageResource(R.drawable.shuffle_on);
                    shufflestatus = "on";
                } else if (shufflestatus.equals("on")) {
                    shufflebut.setImageResource(R.drawable.shuffle_off);
                    shufflestatus = "off";
                }
            }
        });

    }

    public void initPlayerStatus(Bundle savedInstanceState) throws IOException {

        File playerstat = new File(statuspath);
        if (playerstat.exists()) {
            playerstat = new File(statuspath + statusFile);
        } else {
            playerstat.mkdirs();
            playerstat = new File(statuspath + statusFile);
        }

        if (playerstat.exists()) {
            getPlayerStatus(playerstat);
        } else {
            playerstat.createNewFile();
            updatePlayerStatus(playerstat);
        }

    }

    public void getPlayerStatus(File playerstat) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(playerstat));
        playstatus = bufferedReader.readLine();
        repeatstatus = bufferedReader.readLine();
        shufflestatus = bufferedReader.readLine();

        songtoplay.name = bufferedReader.readLine();
        songtoplay.artist = bufferedReader.readLine();
        songtoplay.performer = bufferedReader.readLine();
        String onlinemusic = bufferedReader.readLine();

        if (playstatus == null || repeatstatus == null || shufflestatus == null || songtoplay.name == null
                || songtoplay.artist == null || songtoplay.performer == null || onlinemusic == null) {
            bufferedReader.close();
            playerstat.delete();
            String playerthumb = "/sdcard/Nlag/playerthumb.dat";
            File thumbfile = new File(playerthumb);
            if (thumbfile.exists()) thumbfile.delete();
            recreate();
        }

        if (onlinemusic.equals("True")) {
            songtoplay.onlinemusic = true;
            songtoplay.sourcelink = bufferedReader.readLine();

            String playerthumb = "/sdcard/Nlag/playerthumb.dat";
            File thumbfile = new File(playerthumb);
            if (thumbfile.exists() && thumbfile.canRead()) {
                FileInputStream inputStream = new FileInputStream(thumbfile);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                songtoplay.thumb = bitmap;
            }

        } else {
            songtoplay.onlinemusic = false;
            songtoplay.filepath = bufferedReader.readLine();
        }
        bufferedReader.close();
    }

    public void updatePlayerStatus(File playerstat) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(playerstat));
        try {
            //Player status
            bufferedWriter.write(playstatus + "\n");
            bufferedWriter.append(repeatstatus + "\n");
            bufferedWriter.append(shufflestatus + "\n");

            //current song status
            bufferedWriter.append(songtoplay.name + "\n");
            bufferedWriter.append(songtoplay.artist + "\n");
            bufferedWriter.append(songtoplay.performer + "\n");
            if (songtoplay.onlinemusic) {
                bufferedWriter.append("True\n");
                bufferedWriter.append(songtoplay.sourcelink);

                //save thumb
                String playerthumb = "/sdcard/Nlag/playerthumb.dat";
                File thumbfile = new File(playerthumb);
                if (!thumbfile.exists()) {
                    thumbfile.createNewFile();
                } else {
                    thumbfile.delete();
                    thumbfile.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(thumbfile);
                songtoplay.thumb.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } else {
                bufferedWriter.append("False\n");
                bufferedWriter.append(songtoplay.filepath);
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateSongInfo() {
        TextView title = findViewById(R.id.playersongtitle);
        title.setText(songtoplay.name);
        TextView artist = findViewById(R.id.playersongartist);
        artist.setText(songtoplay.artist);
        TextView performer = findViewById(R.id.playersongperformer);
        performer.setText(songtoplay.performer);
        if (songtoplay.thumb != null) {
            ImageView playerSongIcon = findViewById(R.id.playersongicon);
            playerSongIcon.setImageBitmap(songtoplay.thumb);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            File playerstat = new File(statuspath + statusFile);
            updatePlayerStatus(playerstat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_toolbar_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refesh:
                recreate();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
