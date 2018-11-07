package com.example.beket.newsapp.ExpandableList;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.beket.newsapp.R;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListData {
    private static List<String> newsTopics;
    private static List<String> opinionTopics;
    private static List<String> sportTopics;
    private static List<String> cultureTopics;
    private static List<String> lifestyleTopics;

    private static Map<String, String> sectionId;

    public static Map<String, List<String>> getData(Context context) {
        @SuppressLint({"NewApi", "LocalSuppress"}) Map<String, List<String>> listData = new LinkedHashMap<>();

        List<String> mainTopics = Arrays.asList(context.getResources().getStringArray(R.array.mainTopics));

        newsTopics = Arrays.asList(context.getResources().getStringArray(R.array.newsSubTopics));
        opinionTopics = Arrays.asList(context.getResources().getStringArray(R.array.opinionSubTopics));
        sportTopics = Arrays.asList(context.getResources().getStringArray(R.array.sportSubTopics));
        cultureTopics = Arrays.asList(context.getResources().getStringArray(R.array.cultureSubTopics));
        lifestyleTopics = Arrays.asList(context.getResources().getStringArray(R.array.lifestyleSubTopics));

        listData.put(mainTopics.get(0), newsTopics);
        listData.put(mainTopics.get(1), opinionTopics);
        listData.put(mainTopics.get(2), sportTopics);
        listData.put(mainTopics.get(3), cultureTopics);
        listData.put(mainTopics.get(4), lifestyleTopics);

        return listData;
    }

    public static void setSectionId(Context context) {

        List<String> newsTopicsIds = Arrays.asList(context.getResources().getStringArray(R.array.newsSubTopicsIds));
        List<String> opinionTopicsIds = Arrays.asList(context.getResources().getStringArray(R.array.opinionSubTopicsIds));
        List<String> sportTopicsIds = Arrays.asList(context.getResources().getStringArray(R.array.sportSubTopicsIds));
        List<String> cultureTopicsIds = Arrays.asList(context.getResources().getStringArray(R.array.cultureSubTopicsIds));
        List<String> lifestyleTopicsIds = Arrays.asList(context.getResources().getStringArray(R.array.lifestyleSubTopicsIds));

        sectionId = new LinkedHashMap<>();
        for(int i = 0; i < newsTopics.size(); i++)
            sectionId.put(newsTopics.get(i), newsTopicsIds.get(i));

        for(int i = 0; i < opinionTopics.size(); i++)
            sectionId.put(opinionTopics.get(i), opinionTopicsIds.get(i));

        for(int i = 0; i < sportTopics.size(); i++)
            sectionId.put(sportTopics.get(i), sportTopicsIds.get(i));

        for(int i = 0; i < cultureTopics.size(); i++)
            sectionId.put(cultureTopics.get(i), cultureTopicsIds.get(i));

        for(int i = 0; i < lifestyleTopics.size(); i++)
            sectionId.put(lifestyleTopics.get(i), lifestyleTopicsIds.get(i));
    }

    public static String getSectionId(String sectionName){
        return String.valueOf(sectionId.get(sectionName));
    }
}
