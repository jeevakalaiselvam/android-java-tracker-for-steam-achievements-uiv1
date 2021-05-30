package com.gamerguide.android.trackerforsteamachivements.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentGlobalAchievements;
import com.gamerguide.android.trackerforsteamachivements.Helper.ZUtils;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.gamerguide.android.trackerforsteamachivements.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PlannerActivity extends AppCompatActivity {


    ArrayList<Achievement> achievements;
    ArrayList<Achievement> onlyLockedAchievements;
    ArrayList<Achievement> plannerAchievements;
    ArrayList<Achievement> searchKeyAchievements;
    Game game;
    ZUtils zUtils;
    TextView status;
    Button addPlannerMain;
    ImageView delete;
    ImageView back;
    String searchKey = "";
    AlertDialog alertDialogMain;

    private ViewGroup frame;
    private RecyclerView plannerList;
    int mainPosition;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/asd.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());



        setContentView(R.layout.activity_planner);
        back = findViewById(R.id.back);
        plannerList = findViewById(R.id.planner_list);
        delete = findViewById(R.id.delete);
        status = findViewById(R.id.status);
        frame = findViewById(R.id.frame);
        addPlannerMain = findViewById(R.id.addPlannerMain);
        achievements = new ArrayList<>();
        onlyLockedAchievements = new ArrayList<>();

        zUtils = new ZUtils(PlannerActivity.this);
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        //achievement = gson.fromJson(getIntent().getStringExtra("achievement_single"), Achievement.class);
        game = gson.fromJson(getIntent().getStringExtra("game_single"), Game.class);
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        achievements = (ArrayList<Achievement>) args.getSerializable("ARRAYLIST");
        //achievement = ((GameActivity)getCallingActivity()).

        for(Achievement achievement: achievements){
            if(achievement.getUnlocked().equalsIgnoreCase("0")){
                onlyLockedAchievements.add(achievement);
            }
        }
        achievements = onlyLockedAchievements;


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupPlannerList();

        addPlannerMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToAddToPlanner(0L);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar snackbar = Snackbar
                        .make(frame, "Remove all Achievements from Planner?", Snackbar.LENGTH_LONG);
                snackbar.setAction("YES", new RemoveAllPlannerListener());
                snackbar.show();

            }
        });


    }

    public class RemoveAllPlannerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            for (Achievement achievement: achievements){
                zUtils.insertSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp",0);

            }
            setupPlannerList();
            Snackbar snackbar = Snackbar.make(frame,"All Achievements in Planner cleared..", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void showDialogToAddToPlanner(final Long oldAchievementTime) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlannerActivity.this);
        LayoutInflater inflater = PlannerActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_addtoplanner, null);
        final RecyclerView plannerDialogList = dialogView.findViewById(R.id.plannerDialogList);
        final EditText search = dialogView.findViewById(R.id.search);
        final TextView status = dialogView.findViewById(R.id.status);

        final ArrayList<Achievement> dialogplannerAchievements;
        dialogplannerAchievements = new ArrayList<>();


        dialogBuilder.setView(dialogView);
        alertDialogMain = dialogBuilder.create();
        alertDialogMain.show();

        for (Achievement achievement : achievements) {
            if(zUtils.getSharedPreferenceLong(PlannerActivity.this,game.getId() + achievement.getId() + "_timestamp",0) == 0){

                dialogplannerAchievements.add(achievement);
            }else{
            }
        }


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchKey = String.valueOf(s);
                searchUpdateAchievements(dialogplannerAchievements,plannerDialogList,oldAchievementTime,status,search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Collections.sort(dialogplannerAchievements, new NameComparatorAZ());

        searchUpdateAchievements(dialogplannerAchievements,plannerDialogList,oldAchievementTime,status,search);


        search.setText("");

    }

    private void searchUpdateAchievements(ArrayList<Achievement> dialogplannerAchievements, RecyclerView plannerDialogList, Long oldAchievementTime,TextView status,EditText search) {

        searchKeyAchievements = new ArrayList<>();

        for (Achievement achievement : dialogplannerAchievements) {
            if ((achievement.getName().toLowerCase().trim().contains(searchKey.toLowerCase().trim())
                    || achievement.getDesc().toLowerCase().trim().contains(searchKey.toLowerCase().trim())) && achievement.getUnlocked().equalsIgnoreCase("0")) {
                searchKeyAchievements.add(achievement);
            }
        }

        search.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        plannerDialogList.setVisibility(View.VISIBLE);
        search.requestFocus();

        if(searchKeyAchievements.size() == 0){
            status.setVisibility(View.VISIBLE);
            plannerDialogList.setVisibility(View.GONE);

            if(String.valueOf(search.getText()).equalsIgnoreCase("")){
                search.setVisibility(View.GONE);
                status.setText("All Achievements completed..");
            }else{
                status.setText("No Achievements met search criteria..");
            }
        }else{
            status.setVisibility(View.GONE);
            plannerDialogList.setVisibility(View.VISIBLE);
            DialogAchievementAdapterForList adapter = new DialogAchievementAdapterForList(searchKeyAchievements, oldAchievementTime);
            GridLayoutManager mLayoutManager = new GridLayoutManager(PlannerActivity.this, 1);
            plannerDialogList.setLayoutManager(mLayoutManager);
            plannerDialogList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            plannerDialogList.scrollToPosition(mainPosition);
        }
    }

    private void setupPlannerList() {

        plannerAchievements = new ArrayList<>();

        for (Achievement achievement : achievements) {


            zUtils.logError("JEEVA_ACHIVEMENT: " + achievement.getName());

            if (zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp", 0) == 0) {

            } else {
                plannerAchievements.add(achievement);
            }
        }


        if (plannerAchievements.size() == 0) {

            status.setVisibility(View.VISIBLE);
            addPlannerMain.setVisibility(View.VISIBLE);
            plannerList.setVisibility(View.GONE);
        } else {

            status.setVisibility(View.GONE);
            addPlannerMain.setVisibility(View.GONE);
            plannerList.setVisibility(View.VISIBLE);


            Collections.sort(plannerAchievements, new AddTimeComparator());

            AchievementAdapterForList adapter = new AchievementAdapterForList(plannerAchievements);
            GridLayoutManager mLayoutManager = new GridLayoutManager(PlannerActivity.this, 1);
            plannerList.setLayoutManager(mLayoutManager);
            plannerList.setAdapter(adapter);
            plannerList.scrollToPosition(mainPosition);
        }
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

            v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_achievement_global_planner_add, parent, false);

            MyViewHolder1 vh = new MyViewHolder1(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder1 holder, final int position) {

            final ViewGroup main = holder.mView.findViewById(R.id.main);
            ImageView image = holder.mView.findViewById(R.id.icon);


            Picasso.get().load(achievements.get(position).getIcon()).resize(300, 300).into(image);

            DecimalFormat form = new DecimalFormat("0.0");
            final float trophyPercentage = Float.parseFloat(achievements.get(position).getPercentage());

            ImageView delete = holder.mView.findViewById(R.id.delete);
            ImageView addMainList = holder.mView.findViewById(R.id.addMainList);
            TextView name = holder.mView.findViewById(R.id.name);
            TextView unlock = holder.mView.findViewById(R.id.unlock);
            TextView desc = holder.mView.findViewById(R.id.desc);
            TextView none = holder.mView.findViewById(R.id.none);
            TextView story = holder.mView.findViewById(R.id.story);
            TextView easy = holder.mView.findViewById(R.id.easy);
            TextView hard = holder.mView.findViewById(R.id.hard);
            TextView miss = holder.mView.findViewById(R.id.miss);
            TextView grind = holder.mView.findViewById(R.id.grind);
            TextView multiplayer = holder.mView.findViewById(R.id.multiplayer);

            unlock.setVisibility(View.GONE);
            zUtils.showLabelInAchievement(PlannerActivity.this, none,story, easy, hard, miss, grind, multiplayer, achievements, game, position);


            final String presentData = zUtils.getSharedPreferenceString(PlannerActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), "");


            //data.setText(String.valueOf(zUtils.getSharedPreferenceLong(PlannerActivity.this,game.getId() + achievements.get(position).getId() + "_timestamp",0)));

            if (name != null)
                name.setText(achievements.get(position).getName().trim());

            if (name != null)
                desc.setText(achievements.get(position).getDesc().trim());

            Picasso.get().load(achievements.get(position).getIcon()).resize(64, 64).into(image);



            if (desc != null) {
                desc.setText(achievements.get(position).getDesc());
            } else {
                desc.setText("NOT PRESENT");
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://www.google.com/search?q=" + achievements.get(position).getName() + " " +
                            game.getName() + " achievement guide";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(PlannerActivity.this, AchievementActivity.class);
                    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                    String gameObjectJSON = gson.toJson(game);
                    //String achievementObjectJSON = gson.toJson(achievements.get(position));
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) achievements);
                    i.putExtra("BUNDLE", args);
                    i.putExtra("position", position);
                    i.putExtra("game_single", gameObjectJSON);
                    //i.putExtra("achievement_single", achievementObjectJSON);
                    startActivity(i);


                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Snackbar snackbar = Snackbar
                            .make(frame, "Remove the Achievement from Planner?", Snackbar.LENGTH_LONG);
                    snackbar.setAction("YES", new RemoveIndividualListener(position));
                    snackbar.show();

                }
            });

            addMainList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainPosition = position;
                    showDialogToAddToPlanner(zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + achievements.get(position).getId() + "_timestamp", 0));
                }
            });


        }

        public class RemoveIndividualListener implements View.OnClickListener {
            int position;
            public RemoveIndividualListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {

                zUtils.insertSharedPreferenceLong(PlannerActivity.this, game.getId() + achievements.get(position).getId() + "_timestamp", 0);
                setupPlannerList();
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

    public class AddTimeComparator implements Comparator<Achievement> {
        @Override
        public int compare(Achievement a1, Achievement a2) {
            return smaller(zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + a1.getId() + "_timestamp", 0)
                    , zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + a2.getId() + "_timestamp", 0));
        }
    }

    private int smaller(long arg1, long arg2) {

        if (arg1 < arg2)
            return -1;
        else if (arg1 == arg2)
            return 0;
        else
            return 1;
    }

    public class NameComparatorAZ implements Comparator<Achievement> {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return o1.getName().replace("\"", "").compareTo(o2.getName().replace("\"", ""));
        }
    }


    //DIALOG ADAPTER

    class DialogAchievementAdapterForList extends RecyclerView.Adapter<MyViewHolder1> {

        ArrayList<Achievement> achievements;
        long oldAchievementTime;


        public DialogAchievementAdapterForList(ArrayList<Achievement> achievements, long oldAchievementTime) {

            this.achievements = achievements;
            this.oldAchievementTime = oldAchievementTime;
        }


        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewTypeD) {

            View v = null;

            v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_achievement_global_single_minimal, parent, false);

            MyViewHolder1 vh = new MyViewHolder1(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder1 holder, final int position) {

            final ViewGroup main = holder.mView.findViewById(R.id.main);
            ImageView image = holder.mView.findViewById(R.id.icon);


            Picasso.get().load(achievements.get(position).getIcon()).resize(300, 300).into(image);

            DecimalFormat form = new DecimalFormat("0.0");
            final float trophyPercentage = Float.parseFloat(achievements.get(position).getPercentage());

            ImageView trophy = holder.mView.findViewById(R.id.trophy);
            TextView name = holder.mView.findViewById(R.id.name);
            TextView unlock = holder.mView.findViewById(R.id.unlock);
            TextView desc = holder.mView.findViewById(R.id.desc);
            TextView none = holder.mView.findViewById(R.id.none);
            TextView story = holder.mView.findViewById(R.id.story);
            TextView easy = holder.mView.findViewById(R.id.easy);
            TextView hard = holder.mView.findViewById(R.id.hard);
            TextView miss = holder.mView.findViewById(R.id.miss);
            TextView grind = holder.mView.findViewById(R.id.grind);
            TextView multiplayer = holder.mView.findViewById(R.id.multiplayer);

            if (name != null)
                name.setText(achievements.get(position).getName().trim());

            if (name != null)
                desc.setText(achievements.get(position).getDesc().trim());

            unlock.setVisibility(View.GONE);
            unlock.setVisibility(View.GONE);

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (oldAchievementTime == 0) {
                        zUtils.insertSharedPreferenceLong(PlannerActivity.this, game.getId() + achievements.get(position).getId() + "_timestamp", System.currentTimeMillis());
                        alertDialogMain.hide();
                        setupPlannerList();
                    } else {
                        changeOthersBy1000(oldAchievementTime);
                        zUtils.insertSharedPreferenceLong(PlannerActivity.this, game.getId() + achievements.get(position).getId() + "_timestamp", oldAchievementTime + 1);
                        alertDialogMain.hide();
                        setupPlannerList();
                    }
                }

            });

            zUtils.showLabelInAchievement(PlannerActivity.this,none,story,easy,hard,miss,grind,multiplayer,achievements,game,position);
        }


        @Override
        public int getItemCount() {

            return achievements.size();
        }
    }

    private void changeOthersBy1000(long oldAchievementTime) {

        for (Achievement achievement: achievements){

            if(zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp",0) > oldAchievementTime){

                if(zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp",0) != 0){

                    zUtils.insertSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp",
                            zUtils.getSharedPreferenceLong(PlannerActivity.this, game.getId() + achievement.getId() + "_timestamp",0) + 1000);
                }
            }
        }
    }


}
