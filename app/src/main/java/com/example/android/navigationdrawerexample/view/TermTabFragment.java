package com.example.android.navigationdrawerexample.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.android.navigationdrawerexample.MainActivity;
import com.example.android.navigationdrawerexample.R;
import com.example.android.navigationdrawerexample.model.Phrase;

import java.util.Map;

/**
 * Created by adven on 10.05.14.
 */
public class TermTabFragment extends Fragment {

    // more efficient than HashMap for mapping integers to objects
    SparseArray<Group> groups = new SparseArray<Group>();
    private ExpandableListView listView;
    private MainActivity mActivity;

    public TermTabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) inflater.getContext();
        View rootView = inflater.inflate(R.layout.fragment_term_tab, container, false);
        createData();
        listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        MyExpandableListAdapter adapter =
                new MyExpandableListAdapter(mActivity, groups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mActivity, "setOnItemClickListener", Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Phrase p = ((Group) expandableListView.getExpandableListAdapter()
                        .getGroup(i)).getPhrase();
                mActivity.searchPhrase(p);
                return false;
            }
        });
        return rootView;
    }

    public void createData() {
        Map<Phrase, String> phrases = mActivity.getLearnPhrases();
        int i = 0;
        groups.clear();
        for (Map.Entry<Phrase, String> e : phrases.entrySet()) {
            Group group = new Group(e);
            group.children.add("Sub Item" + 0);
            groups.append(i++, group);
            Log.i("TermList", e.getKey().getPhrase());
        }
    }

    public void reInitListView() {
        createData();
        ((MyExpandableListAdapter) listView.getExpandableListAdapter()).notifyDataSetChanged();
    }
}