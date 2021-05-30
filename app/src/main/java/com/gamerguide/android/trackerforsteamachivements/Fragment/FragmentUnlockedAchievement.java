package com.gamerguide.android.trackerforsteamachivements.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerguide.android.trackerforsteamachivements.Activity.AchievementActivity;
import com.gamerguide.android.trackerforsteamachivements.Activity.GameActivity;
import com.gamerguide.android.trackerforsteamachivements.Helper.ZUtils;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.gamerguide.android.trackerforsteamachivements.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentUnlockedAchievement extends Fragment {

    private ZUtils zUtils;
    private int HIDDEN_DESCRIPTORS_SET = 0;
    private int HIDDEN_PRESENT = 0;
    RecyclerView recyclerView;
    EditText search;
    Game game;
    ArrayList<Achievement> searchKeyAchievements;
    ArrayList<Achievement> filteredAchivements;
    private int viewType = 0;
    ViewGroup extraSorter;


    public FragmentUnlockedAchievement() {

    }

    //Release Memory

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        zUtils = new ZUtils(((GameActivity) getActivity()));
        View view = inflater.inflate(R.layout.fragment_main_global_achivements, container, false);
        extraSorter = view.findViewById(R.id.extraSorter);
        extraSorter.setVisibility(View.GONE);
        viewType = zUtils.getSharedPreferenceInt(getActivity(), "achievement_view_type", 0);
        zUtils.logError("JEEVA_VIEWTYPE: " + " View Type is " +  String.valueOf( + viewType));

        recyclerView = view.findViewById(R.id.achievements_list);
        search = view.findViewById(R.id.search);
        game = ((GameActivity) getActivity()).getGame();

        for (Achievement achievement : game.getAchievements()) {

            if (achievement.getHidden().equalsIgnoreCase("1") && achievement.getDesc().equalsIgnoreCase("Hidden")) {
                HIDDEN_PRESENT++;
            }
        }

        setupListAndAdapters(getActivity());

        return view;
    }


    private void setupListAndAdapters(Activity activity) {

        zUtils.logError("JEEVA_SEARCH: " + ((GameActivity) activity).searchKey);

        searchKeyAchievements = new ArrayList<>();
        filteredAchivements = new ArrayList<>();


        for (Achievement achievement : game.getAchievements()) {
            if ((achievement.getName().toLowerCase().trim().contains(((GameActivity) activity).searchKey.toLowerCase().trim())
                    || achievement.getDesc().toLowerCase().trim().contains(((GameActivity) activity).searchKey.toLowerCase().trim())
                    || zUtils.getSharedPreferenceString(getActivity(), "custom_note" + game.getId() + achievement.getId(), "").toLowerCase().trim().contains(((GameActivity) activity).searchKey.toLowerCase().trim()))
                    && achievement.getUnlocked().equalsIgnoreCase("1") ) {

                searchKeyAchievements.add(achievement );
            }
        }

        int sortOrder = zUtils.getSharedPreferenceInt(activity, "achievement_sort_order", 0);

        switch (sortOrder) {
            case 0:
                Collections.sort(searchKeyAchievements, new NameComparatorAZ());
                ((GameActivity)getActivity()).updateStatusSubText("Filtered by Name");
                break;
            case 1:
                Collections.sort(searchKeyAchievements, new PercentComparatorEasy());
                ((GameActivity)getActivity()).updateStatusSubText("Filtered by Percentage");
                break;
            case 2:
                Collections.sort(searchKeyAchievements, new HiddenComparator());
                ((GameActivity)getActivity()).updateStatusSubText("Filtered by Hidden");
                break;
            case 3:
                Collections.sort(searchKeyAchievements, new UnlockTimeComparator());
                ((GameActivity)getActivity()).updateStatusSubText("Filtered by Unlocktime");
                break;

        }

        for (Achievement achievement : searchKeyAchievements) {

            switch (zUtils.getSharedPreferenceString(getActivity(), game.getId() + achievement.getId() + "_tag", "story")) {
                case "story":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"story_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "easy":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"easy_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "hard":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"hard_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "miss":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"miss_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "grind":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"grind_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "multiplayer":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"multiplayer_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "dlc1":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"dlc1_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "dlc2":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"dlc2_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                case "dlc3":
                    if(zUtils.getSharedPreferenceBoolean(getActivity(),"dcl3_should_show",true)){
                        filteredAchivements.add(achievement);
                    }
                    break;
                default:
                    break;
            }
        }

        refreshListAndAdapter(activity);
    }

    private void hiddenDescriptionsDownload() {

        getAchievementDescription(game.getId(), getActivity());
    }

    private void refreshListAndAdapter(Activity activity) {

        if(viewType == 0){

            ((GameActivity) activity).updateStatusText("Populating Achievement List..");
            AchievementAdapterForList adapter = new AchievementAdapterForList(filteredAchivements);
            GridLayoutManager mLayoutManager = new GridLayoutManager(activity, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(zUtils.getSharedPreferenceInt(getActivity(),"came_back_position",0));
            ((GameActivity) activity).hideLoadingUI();

        }else if(viewType == 1){


            ((GameActivity) activity).updateStatusText("Populating Achievement List..");
            AchievementAdapterForList adapter = new AchievementAdapterForList(filteredAchivements);
            GridLayoutManager mLayoutManager = new GridLayoutManager(activity, 6);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(zUtils.getSharedPreferenceInt(getActivity(),"came_back_position",0));
            ((GameActivity) activity).hideLoadingUI();
        }else{
            ((GameActivity) activity).updateStatusText("Populating Achievement List..");
            AchievementAdapterForList adapter = new AchievementAdapterForList(filteredAchivements);
            GridLayoutManager mLayoutManager = new GridLayoutManager(activity, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(zUtils.getSharedPreferenceInt(getActivity(),"came_back_position",0));
            ((GameActivity) activity).hideLoadingUI();
        }


    }


    /////////////////////////////////////////////////////
    // GETTING HIDDEN ONLY
    //////////////////////////////////////////////////////

    private void getAchievementDescription(final String id, final Activity activity) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getSteamDBHiddden(id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        zUtils.logError("JEEVA_HIDDENURL: " + ZUtils.getSteamDBHiddden(id));
                        for (Achievement achievement : game.getAchievements()) {

                            if (achievement.getHidden().equalsIgnoreCase("1") && achievement.getDesc().equalsIgnoreCase("Hidden")) {
                                parseHTMLHidden(response, achievement, activity);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                zUtils.logError("JEEVA_HIDDENERROR: " + error.getLocalizedMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void parseHTMLHidden(String response, Achievement achievement, Activity activity) {

        Document doc = Jsoup.parse(response);
        try {


            Elements tr = doc.getElementsByTag("tr");


            for (int i = 1; i < tr.size(); i++) {

                String nameAll = tr.get(i).text().substring(0, tr.get(i).text().indexOf("%") - 4);

                if (nameAll.contains(achievement.getName())) {
                    HIDDEN_DESCRIPTORS_SET++;
                    achievement.setDesc(nameAll.replaceFirst(achievement.getName(), ""));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            zUtils.logError("JEEVA_HTMLERROR: " + e.getMessage());
        }
        setupListAndAdapters(activity);
    }

    //ADAPTERS

    class AchievementAdapterForList extends RecyclerView.Adapter<MyViewHolder1> {

        ArrayList<Achievement> achievements;


        public AchievementAdapterForList(ArrayList<Achievement> achievements) {

            this.achievements = achievements;
        }

        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewTypeD) {

            View v = null;

            switch (viewType) {
                case 0:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_achievement_global, parent, false);
                    break;
                case 1:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_achievement_global_single, parent, false);
                    break;
                case 2:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_achievement_global_single_minimal, parent, false);
                    break;
                default:
                    break;
            }


            MyViewHolder1 vh = new MyViewHolder1(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder1 holder, final int position) {

            if(viewType == 0){
                final ViewGroup main = holder.mView.findViewById(R.id.main);
                ImageView image = holder.mView.findViewById(R.id.icon);


                Picasso.get().load(achievements.get(position).getIcon()).resize(300, 300).into(image);

                DecimalFormat form = new DecimalFormat("0.0");
                final float trophyPercentage = Float.parseFloat(achievements.get(position).getPercentage());

                ImageView trophy = holder.mView.findViewById(R.id.trophy);
                TextView name = holder.mView.findViewById(R.id.name);
                TextView percentage = holder.mView.findViewById(R.id.percentage);
                TextView unlock = holder.mView.findViewById(R.id.unlock);
                TextView desc = holder.mView.findViewById(R.id.desc);

                unlock.setText(zUtils.getDateFromMillis(Long.parseLong(achievements.get(position).getUnlockTime() )));


                TextView data = holder.mView.findViewById(R.id.data);
                final String presentData = zUtils.getSharedPreferenceString(getActivity(), "custom_note" + game.getId() + achievements.get(position).getId(), "");

                data.setVisibility(View.VISIBLE);
                if (presentData.trim().length() == 0) {
                    data.setVisibility(View.GONE);
                } else {
                    data.setText(presentData
                            .substring(0, presentData.length() < 100 ? presentData.length() : 100)
                            .trim() + "...");
                }


                if (name != null)
                    name.setText(achievements.get(position).getName().trim());

                if (name != null)
                    desc.setText(achievements.get(position).getDesc().trim());

                Picasso.get().load(achievements.get(position).getIcon()).resize(64, 64).into(image);

                setupColorForTrophy(trophyPercentage, percentage, trophy);

                percentage.setText(String.valueOf(form.format(trophyPercentage)) + " %");

                if (achievements.get(position).getDesc().equalsIgnoreCase("Hidden Achievement") && achievements.get(position).getHidden().equalsIgnoreCase("1")) {

                    getAchievementDescription(game.getId(), achievements.get(position), desc);

                } else {
                    desc.setText(achievements.get(position).getDesc());
                }


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.google.com/search?q=" + achievements.get(position).getName() + " achievement trophy "  + game.getName()  ;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        zUtils.insertSharedPreferenceInt(getActivity(),"old_position",position);

                        Intent i = new Intent(getActivity(), AchievementActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(game);
                        //String achievementObjectJSON = gson.toJson(achievements.get(position));
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST",(Serializable) achievements);
                        i.putExtra("BUNDLE",args);
                        i.putExtra("position",position);
                        i.putExtra("game_single", gameObjectJSON);
                        //i.putExtra("achievement_single", achievementObjectJSON);
                        startActivity(i);

                    }
                });


            }else if(viewType == 1){

                final ViewGroup main = holder.mView.findViewById(R.id.main);
                ImageView image = holder.mView.findViewById(R.id.icon);

                DecimalFormat form = new DecimalFormat("0.0");
                final float trophyPercentage = Float.parseFloat(achievements.get(position).getPercentage());

                Picasso.get().load(achievements.get(position).getIcon()).resize(64, 64).into(image);

                if (achievements.get(position).getDesc().equalsIgnoreCase("Hidden Achievement") && achievements.get(position).getHidden().equalsIgnoreCase("1")) {

                    getAchievementDescription(game.getId(), achievements.get(position),null);

                } else {
                }


                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        zUtils.insertSharedPreferenceInt(getActivity(),"old_position",position);

                        Intent i = new Intent(getActivity(), AchievementActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(game);
                        //String achievementObjectJSON = gson.toJson(achievements.get(position));
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST",(Serializable) achievements);
                        i.putExtra("BUNDLE",args);
                        i.putExtra("position",position);
                        i.putExtra("game_single", gameObjectJSON);
                        //i.putExtra("achievement_single", achievementObjectJSON);
                        startActivity(i);

                    }
                });

            }else{
                final ViewGroup main = holder.mView.findViewById(R.id.main);
                ImageView image = holder.mView.findViewById(R.id.icon);


                Picasso.get().load(achievements.get(position).getIcon()).resize(300, 300).into(image);

                DecimalFormat form = new DecimalFormat("0.0");
                final float trophyPercentage = Float.parseFloat(achievements.get(position).getPercentage());

                ImageView trophy = holder.mView.findViewById(R.id.trophy);
                TextView name = holder.mView.findViewById(R.id.name);
                TextView percentage = holder.mView.findViewById(R.id.percentage);
                TextView unlock = holder.mView.findViewById(R.id.unlock);
                TextView desc = holder.mView.findViewById(R.id.desc);
                TextView story = holder.mView.findViewById(R.id.story);
                TextView easy = holder.mView.findViewById(R.id.easy);
                TextView hard = holder.mView.findViewById(R.id.hard);
                TextView grind = holder.mView.findViewById(R.id.grind);
                TextView miss = holder.mView.findViewById(R.id.miss);
                TextView online = holder.mView.findViewById(R.id.multiplayer);
                TextView none = holder.mView.findViewById(R.id.none);
                zUtils.showLabelInAchievement(getActivity(),none,story,easy,hard,miss,grind,online,achievements,game,position);

                unlock.setText(zUtils.getDateFromMillis(Long.parseLong(achievements.get(position).getUnlockTime() )));

                String presentData = zUtils.getSharedPreferenceString(getActivity(), "custom_note" + game.getId() + achievements.get(position).getId(), "");

                if (name != null)
                    name.setText(achievements.get(position).getName().trim());

                if (name != null)
                    desc.setText(achievements.get(position).getDesc().trim());

                Picasso.get().load(achievements.get(position).getIcon()).resize(64, 64).into(image);

                setupColorForTrophy(trophyPercentage, percentage, trophy);

                percentage.setText(String.valueOf(form.format(trophyPercentage)) + " %");

                if (achievements.get(position).getDesc().equalsIgnoreCase("Hidden Achievement") && achievements.get(position).getHidden().equalsIgnoreCase("1")) {

                    getAchievementDescription(game.getId(), achievements.get(position), desc);

                } else {
                    desc.setText(achievements.get(position).getDesc());
                }


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://www.google.com/search?q=" + achievements.get(position).getName() + " achievement trophy "  + game.getName()  ;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        zUtils.insertSharedPreferenceInt(getActivity(),"old_position",position);

                        Intent i = new Intent(getActivity(), AchievementActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(game);
                        //String achievementObjectJSON = gson.toJson(achievements.get(position));
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST",(Serializable) achievements);
                        i.putExtra("BUNDLE",args);
                        i.putExtra("position",position);
                        i.putExtra("game_single", gameObjectJSON);
                        //i.putExtra("achievement_single", achievementObjectJSON);
                        startActivity(i);

                    }
                });
            }


        }

        private void getAchievementDescription(final String id, final Achievement achievement, final TextView desc) {

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getSteamDBHiddden(id),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            zUtils.logError("JEEVA_HIDDENURL: " + ZUtils.getSteamDBHiddden(id));
                            parseHTMLHidden(response, achievement, desc);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    zUtils.logError("JEEVA_HIDDENERROR: " + error.getLocalizedMessage());
                }
            });

            queue.add(stringRequest);
        }

        private void parseHTMLHidden(String response, Achievement achievement, TextView desc) {

            Document doc = Jsoup.parse(response);
            try {


                Elements tr = doc.getElementsByTag("tr");


                for (int i = 1; i < tr.size(); i++) {

                    String nameAll = tr.get(i).text().substring(0, tr.get(i).text().indexOf("%") - 4);

                    if (nameAll.contains(achievement.getName())) {
                        achievement.setDesc(nameAll.replaceFirst(achievement.getName(), "").trim());
                        zUtils.insertSharedPreferenceString(getActivity(), game.getId() + achievement.getId(), nameAll.replaceFirst(achievement.getName(), "").trim());
                    }
                }

                if(desc != null){

                    desc.setText(String.valueOf(achievement.getDesc().trim()));
                }


            } catch (Exception e) {
                e.printStackTrace();
                zUtils.logError("JEEVA_HTMLERROR: " + e.getMessage());
            }
        }

        private void setupColorForTrophy(float trophyPercentage, TextView trophy, ImageView trophyT) {

            if (trophyPercentage <= ZUtils.PLATINUM) {

                trophy.setTextColor(getActivity().getResources().getColor(R.color.gold));
                trophyT.setImageDrawable(getResources().getDrawable(R.drawable.ic_trophy));
                trophyT.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gold), android.graphics.PorterDuff.Mode.SRC_IN);

            } else if (trophyPercentage <= ZUtils.GOLD && trophyPercentage > ZUtils.PLATINUM) {

                trophy.setTextColor(getActivity().getResources().getColor(R.color.silver));
                trophyT.setImageDrawable(getResources().getDrawable(R.drawable.ic_trophy));
                trophyT.setColorFilter(ContextCompat.getColor(getActivity(), R.color.silver), android.graphics.PorterDuff.Mode.SRC_IN);

            } else if (trophyPercentage <= ZUtils.SILVER && trophyPercentage > ZUtils.GOLD) {

                trophy.setTextColor(getActivity().getResources().getColor(R.color.silver));
                trophyT.setImageDrawable(getResources().getDrawable(R.drawable.ic_trophy));
                trophyT.setColorFilter(ContextCompat.getColor(getActivity(), R.color.silver), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {

                trophy.setTextColor(getActivity().getResources().getColor(R.color.silver));
                trophyT.setImageDrawable(getResources().getDrawable(R.drawable.ic_trophy));
                trophyT.setColorFilter(ContextCompat.getColor(getActivity(), R.color.silver), android.graphics.PorterDuff.Mode.SRC_IN);
            }

        }

        private void setupColorForTrophy(float trophyPercentage, ViewGroup color) {

            if (trophyPercentage <= ZUtils.PLATINUM) {

                color.setBackgroundColor(getResources().getColor(R.color.platinum));

            } else if (trophyPercentage <= ZUtils.GOLD && trophyPercentage > ZUtils.PLATINUM) {

                color.setBackgroundColor(getResources().getColor(R.color.gold));

            } else if (trophyPercentage <= ZUtils.SILVER && trophyPercentage > ZUtils.GOLD) {

                color.setBackgroundColor(getResources().getColor(R.color.silver));

            } else {

                color.setBackgroundColor(getResources().getColor(R.color.bronze));
            }

        }


        @Override
        public int getItemCount() {

            return achievements.size();
        }
    }


    class MyViewHolder1 extends RecyclerView.ViewHolder {

        public View mView;

        public MyViewHolder1(View v) {
            super(v);
            mView = v;
        }

    }


    //COMPARATORS
    public class PercentComparatorEasy implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return smaller(Float.parseFloat(o1.getPercentage()),
                    Float.parseFloat(o2.getPercentage()));
        }
    }

    public class PercentComparatorHard implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return smallerR(Float.parseFloat(o1.getPercentage()),
                    Float.parseFloat(o2.getPercentage()));
        }
    }

    public class NameComparatorAZ implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return o1.getName().replace("\"", "").compareTo(o2.getName().replace("\"", ""));
        }
    }

    public class NameComparatorZA implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return o2.getName().replace("\"", "").compareTo(o1.getName().replace("\"", ""));
        }
    }

    public class UnlockComparator implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {

            int one = Integer.parseInt(o1.getUnlocked());
            int two = Integer.parseInt(o2.getUnlocked());

            return smaller(two, one);
        }
    }

    public class HiddenComparator implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {

            int one = Integer.parseInt(o1.getHidden());
            int two = Integer.parseInt(o2.getHidden());

            return smaller(one, two);
        }
    }

    public class UnlockTimeComparator implements Comparator<Achievement> {
        @Override
        public int compare(Achievement a1, Achievement a2) {
            return smaller(Long.parseLong(a1.getUnlockTime()), Long.parseLong(a2.getUnlockTime()));
        }
    }

    public int getInt(boolean tmp) {
        if (tmp)
            return 1;
        else
            return 0;
    }

    private int smaller(int arg1, int arg2) {

        if (arg1 < arg2)
            return 1;
        else if (arg1 == arg2)
            return 0;
        else
            return -1;
    }

    private int larger(int arg1, int arg2) {

        if (arg1 < arg2)
            return 1;
        else if (arg1 == arg2)
            return 0;
        else
            return -1;
    }

    private int smallerR(int arg1, int arg2) {

        if (arg1 < arg2)
            return -1;
        else if (arg1 == arg2)
            return 0;
        else
            return 1;
    }

    private int smallerR(float arg1, float arg2) {

        if (arg1 < arg2)
            return -1;
        else if (arg1 == arg2)
            return 0;
        else
            return 1;
    }

    private int smaller(float arg1, float arg2) {

        if (arg1 < arg2)
            return 1;
        else if (arg1 == arg2)
            return 0;
        else
            return -1;
    }

}

