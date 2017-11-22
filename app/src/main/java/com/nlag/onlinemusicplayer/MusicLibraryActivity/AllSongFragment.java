package com.nlag.onlinemusicplayer.MusicLibraryActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nlag.onlinemusicplayer.R;

import java.util.ArrayList;

/**
 * Created by nlag on 11/21/17.
 */

public class AllSongFragment extends Fragment {

    private ArrayList<String> localSongsList;
    private ListView allsongs_listview;
    private ArrayAdapter<String> allsongs_listadapter;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_allsongs,container,false);

        allsongs_listview = view.findViewById(R.id.allsongs_list);
        localSongsList = new ArrayList<>();

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i("songUripath", songUri.getPath());
        final Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTittle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtits = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                String currentTitle = songCursor.getString(songTittle);
                String currentArtist = songCursor.getString(songArtits);
                localSongsList.add(currentTitle + "\n" + currentArtist);
            } while (songCursor.moveToNext());
        }
        allsongs_listadapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, localSongsList);
        allsongs_listview.setAdapter(allsongs_listadapter);

        allsongs_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                songCursor.moveToPosition(position);
//                if (mediaPlayer != null) {
//                    mediaPlayer.reset();
//                } else {
//                    mediaPlayer = new MediaPlayer();
//                }
//                try {
//                    String filepath = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                    Log.i("path",filepath);
//                    mediaPlayer.setDataSource(filepath);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        });
        return view;
    }

}
