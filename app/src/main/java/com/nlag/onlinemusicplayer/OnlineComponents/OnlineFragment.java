package com.nlag.onlinemusicplayer.OnlineComponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by nlag on 11/20/17.
 */

public class OnlineFragment extends Fragment {
    public ArrayList<Song> ranklist = new ArrayList<>();
    public ListView music_rank_ListView;
    public MusicRankingAdapter music_rank_Adapter;
    public MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View onlineFragmentView = inflater.inflate(R.layout.fragment_online,container,false);

        //get Music Ranking json
        getRankListFromZingMP3();
        music_rank_Adapter = new MusicRankingAdapter(getContext(), ranklist);

        music_rank_ListView = onlineFragmentView.findViewById(R.id.music_rank_ListView);
        music_rank_ListView.setAdapter(music_rank_Adapter);

        music_rank_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) music_rank_ListView.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected: " + song.name , Toast.LENGTH_SHORT).show();
                getSongKey(song);
            }
        });

        return onlineFragmentView;
    }

    public void getRankListFromZingMP3() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=20";
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("data")
                                    .getJSONArray("song");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectIter = jsonArray.getJSONObject(i);
                                parseJsonDataToArrayList(jsonObjectIter, Integer.toString(i + 1), i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music_rank_Adapter.notifyDataSetChanged();
                        for (int i = 0; i < ranklist.size(); i++) {
                            getSongThumbnail(ranklist.get(i).thumburl, i);
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
    }

    public void parseJsonDataToArrayList(JSONObject songJsonObject, String ranknum, int position) {
        try {
            String name = songJsonObject.getString("name");
            String artist = songJsonObject.getString("artists_names");
            String performer = songJsonObject.getString("performer");
            String pageurl = "https://mp3.zing.vn" + songJsonObject.getString("link");
            String thumburl = songJsonObject.getString("thumbnail");
            Song newsong = new Song(getContext(), name, artist, performer);
            newsong.setOnlineMusic(pageurl, thumburl, ranknum);
            ranklist.add(position, newsong);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSongThumbnail(String thumburl, final int position) {
        ImageRequest imageRequest = new ImageRequest(thumburl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        ranklist.get(position).thumb = response;
                        music_rank_Adapter.notifyDataSetChanged();
                    }
                },
                0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(imageRequest);
    }

    public void getSongKey(final Song song) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, song.pageurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document mp3zing = Jsoup.parse(response);
                        Element audioElement = mp3zing.getElementById("zplayerjs-wrapper");
                        String songKey = audioElement.attr("data-xml");
                        int keypivot = songKey.lastIndexOf("key=");
                        songKey = songKey.substring(keypivot + 4);
                        song.songKey = songKey;
                        getSongSource(song);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(stringRequest);
    }

    public void getSongSource(final Song song) {
        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + song.songKey;
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        song.JsonData = response;
                        getSourceLinkFromJsonData(song);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(request);
    }

    public void getSourceLinkFromJsonData(Song song) {
        try {
            JSONObject jsonObject = new JSONObject(song.JsonData);
            String sourceLink = jsonObject
                    .getJSONObject("data")
                    .getJSONObject("source")
                    .getString("128");
            sourceLink = "http:" + sourceLink;
            song.sourcelink = sourceLink;
//            passToMediaPlayer(sourseLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                holder.performer = convertView.findViewById(R.id.musicrank_item_performer);
                holder.ranknum = convertView.findViewById(R.id.musicrank_item_ranknum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Song song = this.rankingsongList.get(position);
            holder.name.setText(song.name);
            holder.artist.setText(song.artist);
            holder.performer.setText(song.performer);
            if (song.thumb != null) {
                holder.thumbnail.setImageBitmap(song.thumb);
            } else {
                holder.thumbnail.setImageDrawable(song.thumbsample);
            }

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

}
