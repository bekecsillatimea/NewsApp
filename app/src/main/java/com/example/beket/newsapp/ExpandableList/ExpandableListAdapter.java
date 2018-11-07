package com.example.beket.newsapp.ExpandableList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.beket.newsapp.R;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter{

    private List<String> mListTopics;
    private Map<String, List<String>> mListSubTopics;
    private LayoutInflater mLayoutInflater;

    public ExpandableListAdapter(Context context, List<String> listTopics, Map<String, List<String>> listSubTopics){

        mListTopics = listTopics;
        mListSubTopics = listSubTopics;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mListTopics.size();
    }

    @Override
    public int getChildrenCount(int topicPosition) {
        return mListSubTopics.get(mListTopics.get(topicPosition)).size();
    }

    @Override
    public Object getGroup(int topicPosition) {
        return mListTopics.get(topicPosition);
    }

    @Override
    public Object getChild(int topicPosition, int subTopicPosition) {
        return mListSubTopics.get(mListTopics.get(topicPosition)).get(subTopicPosition);
    }

    @Override
    public long getGroupId(int topicPosition) {
        return topicPosition;
    }

    @Override
    public long getChildId(int topicPosition, int subTopicPosition) {
        return subTopicPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int topicPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String topic = (String) getGroup(topicPosition);
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.nav_list_group, parent,false);
        }
        TextView topicTextView = convertView.findViewById(R.id.main_topic_item);
        topicTextView.setTypeface(null, Typeface.BOLD);
        topicTextView.setText(topic);
        return convertView;
    }

    @Override
    public View getChildView(int topicPosition, int subTopicPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final String subTopicText = (String) getChild(topicPosition, subTopicPosition);
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.nav_list_item, parent, false);
        }
        TextView subTopicListTextView = convertView.findViewById(R.id.sub_topic_item);
        subTopicListTextView.setText(subTopicText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int topicPosition, int subTopicPosition) {
        return true;
    }
}
