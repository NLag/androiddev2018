package com.nlag.onlinemusicplayer.MusicLibraryActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import java.util.ArrayList;

/**
 * Created by nlag on 11/21/17.
 */

public class AllSongFragment extends Fragment {

    private ArrayList<Song> localSongsList;
    private ListView allsongs_ListView;
    private localSongAdapter allsongs_ListAdapter;
    private MediaPlayer mediaPlayer;

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

            }
        });
        return view;
    }

    public void getAllSong() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTittle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtits = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTittle);
                String currentArtist = songCursor.getString(songArtits);
                String currentPerformer = "";
                String currentPath = songCursor.getString(songPath);
                Song newsong = new Song(getContext(), currentTitle, currentArtist, currentPerformer);
                newsong.setOfflineMusic(currentPath);
                localSongsList.add(newsong);
            } while (songCursor.moveToNext());
        }
    }

    public class localSongAdapter extends BaseAdapter {
        private ArrayList<Song> localsongList;
        private LayoutInflater layoutInflater;
        private Context context;

        public localSongAdapter(Context context, ArrayList<Song> localsongList) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.localsongList = localsongList;
        }

        @Override
        public int getCount() {
            return localsongList.size();
        }

        @Override
        public Object getItem(int position) {
            return localsongList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.local_song_list_item, null);
                holder = new ViewHolder();
                holder.thumbnail = convertView.findViewById(R.id.localsong_item_thumb);
                holder.name = convertView.findViewById(R.id.localsong_item_name);
                holder.artist = convertView.findViewById(R.id.localsong_item_artist);
                holder.performer = convertView.findViewById(R.id.localsong_item_performer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Song song = this.localsongList.get(position);
            holder.name.setText(song.name);
            holder.artist.setText(song.artist);
            holder.performer.setText(song.performer);
            if (song.thumb != null) {
                holder.thumbnail.setImageBitmap(song.thumb);
            } else {
                holder.thumbnail.setImageDrawable(song.thumbsample);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView thumbnail;
            TextView name;
            TextView artist;
            TextView performer;
        }

    }

}
