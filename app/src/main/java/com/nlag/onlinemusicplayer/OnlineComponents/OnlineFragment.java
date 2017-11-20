package com.nlag.onlinemusicplayer.OnlineComponents;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlag on 11/20/17.
 */

public class OnlineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View onlineFragmentView = inflater.inflate(R.layout.online_fragment,container,false);

        //get Music Ranking json
        ArrayList<JSONObject> rank10songs = getRankFromZingMP3();

        return onlineFragmentView;
    }

    public ArrayList getRankFromZingMP3() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=10";
        final ArrayList<JSONObject> jsonSongsList = new ArrayList<>();
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i( "Json response ", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("data")
                                    .getJSONArray("song");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonSongsList.add(jsonArray.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(request);
        return jsonSongsList;
    }

    public class MusicRankingAdapter extends BaseAdapter {

        private List<OnlineMusicRankingSong> rankingsongList;
        private LayoutInflater layoutInflater;
        private Context context;

        public MusicRankingAdapter(Context context, List<OnlineMusicRankingSong> rankingsongList) {
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
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.musicrank_item_thumb);
                holder.name = (TextView) convertView.findViewById(R.id.musicrank_item_name);
                holder.artist = (TextView) convertView.findViewById(R.id.musicrank_item_artist);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OnlineMusicRankingSong song = this.rankingsongList.get(position);
            holder.name.setText(song.getName());
            holder.artist.setText(song.getArtist());
            holder.thumbnail.setImageBitmap(song.getThumb());

            return convertView;
        }
        class ViewHolder {
            ImageView thumbnail;
            TextView name;
            TextView artist;
        }
    }


}
