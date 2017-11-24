package com.nlag.onlinemusicplayer.LocalComponents;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nlag.onlinemusicplayer.MainActivity;
import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nlag on 11/21/17.
 */

public class AllSongFragment extends Fragment {

    public ArrayList<Song> localSongsList;
    public ListView allsongs_ListView;
    public localSongAdapter allsongs_ListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_allsongs,container,false);

        localSongsList = new ArrayList<>();
        getAllSong();

        allsongs_ListAdapter = new localSongAdapter(getContext(), localSongsList);

        allsongs_ListView = view.findViewById(R.id.allsongs_list);
        allsongs_ListView.setAdapter(allsongs_ListAdapter);

        allsongs_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) allsongs_ListView.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected: " + song.name, Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).songPicked(position);
            }
        });
        return view;
    }

    public void putSonginIntent(Intent intent, Song song) {
        intent.putExtra("title", song.name);
        intent.putExtra("artist", song.artist);
        intent.putExtra("onlinemusic", song.onlinemusic);
        if (song.onlinemusic) {
            intent.putExtra("sourcelink", song.sourcelink);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            song.thumb.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("thumb", byteArray);
        } else {
            intent.putExtra("filepath", song.filepath);
        }
    }

    public void getAllSong() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTittle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtits = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int idColumn = songCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = songCursor.getLong(idColumn);
                String currentTitle = songCursor.getString(songTittle);
                String currentArtist = songCursor.getString(songArtits);
                String currentPath = songCursor.getString(songPath);
                Song newsong = new Song(getContext(), currentTitle, currentArtist);
                newsong.setOfflineMusic(thisId, currentPath);
                localSongsList.add(newsong);
            } while (songCursor.moveToNext());
        }
    }


}
