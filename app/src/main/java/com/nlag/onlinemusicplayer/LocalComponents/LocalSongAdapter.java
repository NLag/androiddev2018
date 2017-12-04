package com.nlag.onlinemusicplayer.LocalComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import java.util.ArrayList;

/**
 * Created by nlag on 11/24/17.
 */

public class LocalSongAdapter extends BaseAdapter {
    public ArrayList<Song> localsongList;
    public LayoutInflater layoutInflater;
    public Context context;

    public LocalSongAdapter(Context context, ArrayList<Song> localsongList) {
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song song = this.localsongList.get(position);
        holder.name.setText(song.name);
        holder.artist.setText(song.artist);
        holder.thumbnail.setImageBitmap(song.thumb);

        return convertView;
    }

    class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView artist;
        TextView performer;
    }

}

