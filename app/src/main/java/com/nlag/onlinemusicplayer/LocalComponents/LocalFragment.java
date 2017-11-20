package com.nlag.onlinemusicplayer.LocalComponents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nlag.onlinemusicplayer.R;

/**
 * Created by nlag on 11/20/17.
 */

public class LocalFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View localFragmentView = inflater.inflate(R.layout.local_fragment,container,false);

        String s1 = "abc1";
        String s2 = "abc2";
        String s3 = "abc3";
        String[] s33 = new String[]{s1,s2,s3};
        ArrayAdapter playlistAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,s33);

        ListView playlistListView = (ListView) localFragmentView.findViewById(R.id.playlists_list);
        playlistListView.setAdapter(playlistAdapter);
        return localFragmentView;
    }
}
