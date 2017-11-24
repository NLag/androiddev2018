package com.nlag.onlinemusicplayer.OnlineComponents;

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

public class MusicRankingAdapter extends BaseAdapter {
    private ArrayList<Song> rankingsongList;
    private LayoutInflater layoutInflater;
    private Context context;

    public MusicRankingAdapter(Context context, ArrayList<Song> rankingsongList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.rankingsongList = rankingsongList;
    }

    @Override
    public int getCount() {
        return rankingsongList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankingsongList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.online_musicranking_list_item, null);
            holder = new ViewHolder();
            holder.thumbnail = convertView.findViewById(R.id.musicrank_item_thumb);
            holder.name = convertView.findViewById(R.id.musicrank_item_name);
            holder.artist = convertView.findViewById(R.id.musicrank_item_artist);
            holder.ranknum = convertView.findViewById(R.id.musicrank_item_ranknum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song song = this.rankingsongList.get(position);
        holder.name.setText(song.name);
        holder.artist.setText(song.artist);
        holder.thumbnail.setImageBitmap(song.thumb);
        holder.ranknum.setText(song.ranknum);

        return convertView;
    }

    class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView artist;
        TextView performer;
        TextView ranknum;
    }

}
