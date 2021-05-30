package com.gamerguide.android.trackerforsteamachivements.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentFlowAchievement;
import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentGlobalAchievements;
import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentLockedAchievement;
import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentPlannerAchievement;
import com.gamerguide.android.trackerforsteamachivements.Fragment.FragmentUnlockedAchievement;
import com.gamerguide.android.trackerforsteamachivements.Helper.ZUtils;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.gamerguide.android.trackerforsteamachivements.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GameActivity extends AppCompatActivity implements Serializable {

    private int UNLOCKED_OR_NOT_STATUS_GAMES_SET = 0;

    private TextView global, locked, unlocked,planner,flow;
    private ProgressBar progressBar;
    private TextView status, title, sub;
    private ImageView back, option;
    private ViewGroup frame, completionFrame;
    private EditText search;

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;

    private ZUtils zUtils;
    public Game game;
    private String gameId;
    public String searchKey = "";

    private int GAME_SCHEMA_DOWNLOADED = 0;
    public static int ACHIEVEMENT_PERCENTAGE_DOWNLOADED = 0;
    private static final int NUM_PAGES = 3;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (zUtils.getSharedPreferenceBoolean(GameActivity.this, "came_back_activity", false)) {

            setupAchievementAdapter();
            zUtils.insertSharedPreferenceBoolean(GameActivity.this, "came_back_activity", false);
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/asd.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());*/

        setContentView(R.layout.activity_game);
        mFragmentManager = getSupportFragmentManager();

        frame = findViewById(R.id.frame);
        completionFrame = findViewById(R.id.completion_frame);
        frame.setVisibility(View.GONE);
        completionFrame.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress);
        status = findViewById(R.id.status);
        sub = findViewById(R.id.sub);
        back = findViewById(R.id.back);
        option = findViewById(R.id.option);
        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);
        title = findViewById(R.id.title);
        global = findViewById(R.id.global);
        locked = findViewById(R.id.locked);
        unlocked = findViewById(R.id.unlocked);
        planner = findViewById(R.id.planner);
        flow = findViewById(R.id.flow);

        progressBar.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);

        zUtils = new ZUtils(GameActivity.this);


        getAllAchievementsAndData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertDialog;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
                LayoutInflater inflater = GameActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.option_game_sort, null);
                TextView name1 = dialogView.findViewById(R.id.name1);
                TextView percentage1 = dialogView.findViewById(R.id.percentage1);
                TextView hidden = dialogView.findViewById(R.id.hidden);
                TextView unlock = dialogView.findViewById(R.id.unlock);
                TextView big = dialogView.findViewById(R.id.big);
                TextView small = dialogView.findViewById(R.id.small);
                TextView detailed = dialogView.findViewById(R.id.detailed);
                Button plannerB = dialogView.findViewById(R.id.plannerB);
                dialogBuilder.setView(dialogView);
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                name1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "Sorted by Name", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_sort_order", 0);
                        completeAndSetFragments();
                    }
                });
                percentage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "Sorted by Easiest", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_sort_order", 1);
                        completeAndSetFragments();
                    }
                });
                hidden.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "Sorted by Hidden", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_sort_order", 2);
                        completeAndSetFragments();
                    }
                });


                unlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "Sorted by Unlocktime", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_sort_order", 3);
                        completeAndSetFragments();
                    }
                });


                big.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "View changed to List", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_view_type", 0);
                        completeAndSetFragments();
                    }
                });

                small.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "View changed to Icon", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_view_type", 1);
                        completeAndSetFragments();
                    }
                });

                detailed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(GameActivity.this, "View changed to Detailed", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(GameActivity.this, "achievement_view_type", 2);
                        completeAndSetFragments();
                    }
                });



                plannerB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Intent in = new Intent(GameActivity.this, PlannerActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(game);
                        //String achievementObjectJSON = gson.toJson(achievements.get(position));
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) game.getAchievements());
                        in.putExtra("BUNDLE", args);
                        in.putExtra("game_single", gameObjectJSON);
                        //i.putExtra("achievement_single", achievementObjectJSON);
                        startActivity(in);
                    }
                });

            }
        });

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zUtils.insertSharedPreferenceInt(GameActivity.this, "game_default", 0);
                setupDefaultFragment();
            }
        });

        unlocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zUtils.insertSharedPreferenceInt(GameActivity.this, "game_default", 1);
                setupDefaultFragment();
            }
        });

        locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zUtils.insertSharedPreferenceInt(GameActivity.this, "game_default", 2);
                setupDefaultFragment();
            }
        });

        planner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zUtils.insertSharedPreferenceInt(GameActivity.this, "game_default", 3);
                setupDefaultFragment();
            }
        });

        flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zUtils.insertSharedPreferenceInt(GameActivity.this, "game_default", 4);
                setupDefaultFragment();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchKey = String.valueOf(s);
                setupDefaultFragment();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void getAllAchievementsAndData() {

        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        game = gson.fromJson(getIntent().getStringExtra("game"), Game.class);
        getGamesAndAssociatedData();

    }

    private void getGamesAndAssociatedData() {

        getGameSchema(game);
    }

    private void getGameSchema(final Game game) {

        RequestQueue queue = Volley.newRequestQueue(GameActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getGameSchemaURL(game.getId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        zUtils.logError("JEEVA_URL: " + ZUtils.getGameSchemaURL(game.getId()));
                        parseGameSchemaJSON(response, game);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        queue.add(stringRequest);
    }

    private void showNoAchievementMessage() {

        frame.setVisibility(View.GONE);
        status.setText("Game has no Steam achievements..");
        progressBar.setVisibility(View.GONE);
    }

    private void parseGameSchemaJSON(String response, Game game) {

        status.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        try {

            JSONObject obj = new JSONObject(response);
            JSONObject gameI = obj.getJSONObject("game");
            JSONObject stats = gameI.getJSONObject("availableGameStats");

            JSONArray achievements = stats.getJSONArray("achievements");

            if (achievements.length() != 0) {

                for (int i = 0; i < achievements.length(); i++) {

                    status.setText("Downloading game data..");

                    JSONObject innerObj = new JSONObject(String.valueOf(achievements.get(i)));
                    Achievement achievement = new Achievement();
                    achievement.setId(innerObj.getString("name").trim());
                    achievement.setName(innerObj.getString("displayName").trim());
                    achievement.setIcon(innerObj.getString("icon").trim());
                    achievement.setHidden(innerObj.getString("hidden").trim());
                    achievement.setIconLocked(innerObj.getString("icongray").trim());

                    if (innerObj.getString("hidden").equalsIgnoreCase("0")) {
                        achievement.setDesc(innerObj.getString("description"));
                    } else {
                        achievement.setDesc(zUtils.getSharedPreferenceString(GameActivity.this, game.getId()
                                + achievement.getId(), "Hidden Achievement"));
                    }

                    game.addAchievement(achievement);
                    zUtils.logError("JEEVA_INSERT:" + game.getName() + " { " + achievement.getName() + " " + achievement.getIcon() + " " + achievement.getUnlocked());

                }
                setupGamesInList();
            }


        } catch (JSONException e) {
            showNoAchievementMessage();
        }

    }


    private void setupGamesInList() {

        getAllPlayerAchievement(game);

    }

    private void getAllPlayerAchievement(final Game game) {

        RequestQueue queue = Volley.newRequestQueue(GameActivity.this);

        zUtils.logError("JEEVA_PERCENTAGE:" + ZUtils.getPlayerAchievements(game.getId()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getPlayerAchievements(game.getId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseAllPlayerAchievementJSON(response, game);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                zUtils.logError("JEEVA_ERROR:" + error.getLocalizedMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void parseAllPlayerAchievementJSON(String response, Game game) {

        try {

            JSONObject obj = new JSONObject(response);

            JSONObject stats = obj.getJSONObject("playerstats");

            JSONArray achievements = stats.getJSONArray("achievements");


            if (achievements.length() != 0) {

                for (int i = 0; i < achievements.length(); i++) {

                    JSONObject innerObj = new JSONObject(String.valueOf(achievements.get(i)));

                    String achievementID = innerObj.getString("apiname").trim();
                    String achievementUnlocked = innerObj.getString("achieved").trim();
                    String achievementUnlockedTime = innerObj.getString("unlocktime").trim();

                    status.setText("Checking for unlocked achievements..");
                    for (Achievement achievement : game.getAchievements()) {
                        if (achievement.getId().equalsIgnoreCase(achievementID)) {
                            achievement.setUnlocked(achievementUnlocked);
                            achievement.setUnlockTime(achievementUnlockedTime);
                        }
                    }

                    zUtils.logError("JEEVA_INSERT:" + achievementID + " { " + achievementUnlocked + " " + achievementUnlockedTime + " ");

                }


                UNLOCKED_OR_NOT_STATUS_GAMES_SET += 1;
                status.setText("Populating achievement list..");
                setupDownloadForAchievementPercentage(game);

            }

        } catch (JSONException e) {

        }


    }


    private void setupDownloadForAchievementPercentage(final Game game) {

        RequestQueue queue = Volley.newRequestQueue(GameActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getGameGlobalPercentage(game.getId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseAllPercentageAchievementJSON(response, game);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void parseAllPercentageAchievementJSON(String response, Game game) {

        try {

            JSONObject obj = new JSONObject(response);
            JSONObject gameI = obj.getJSONObject("achievementpercentages");
            JSONArray achievements = gameI.getJSONArray("achievements");
            if (achievements.length() != 0) {

                for (int i = 0; i < achievements.length(); i++) {

                    JSONObject innerObj = new JSONObject(String.valueOf(achievements.get(i)));
                    for (Achievement achievement : game.getAchievements()) {
                        if (innerObj.getString("name").equalsIgnoreCase(achievement.getId())) {
                            achievement.setPercentage(innerObj.getString("percent"));
                            ACHIEVEMENT_PERCENTAGE_DOWNLOADED++;
                        }
                    }
                    setupAchievementAdapter();
                }

            }


        } catch (JSONException e) {

        }
    }

    private void setupAchievementAdapter() {
        completeAndSetFragments();
        //getAchievementDescription();
    }

    int pointer = -1;


    private void parseHTMLHidden(String response, Achievement achievement) {

        Document doc = Jsoup.parse(response);
        try {


            Elements tr = doc.getElementsByTag("tr");


            for (int i = 1; i < tr.size(); i++) {

                String nameAll = tr.get(i).text().substring(0, tr.get(i).text().indexOf("%") - 4);

                if (nameAll.contains(achievement.getName())) {
                    achievement.setDesc(nameAll.replaceFirst(achievement.getName(), ""));
                }
            }
            ACHIEVEMENT_PERCENTAGE_DOWNLOADED++;
            completeAndSetFragments();


        } catch (Exception e) {
            e.printStackTrace();
            zUtils.logError("JEEVA_HTMLERROR: " + e.getMessage());
        }
    }


    private void completeAndSetFragments() {

        setupDefaultFragment();
    }

    private void setupDefaultFragment() {

        int tag = zUtils.getSharedPreferenceInt(GameActivity.this, "game_default", 0);


        switch (tag) {
            case 0:
                setupGlobalFragment();
                break;
            case 1:
                setupUnlockedFragment();
                break;
            case 2:
                setupLockedFragment();
                break;
            case 3:
                setupPlannerFragment();
                break;
            case 4:
                setupFlowFragment();
                break;
            default:
                setupGlobalFragment();
                break;
        }

    }

    private void setupGlobalFragment() {

        setGlobalSelected();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new FragmentGlobalAchievements();
        mFragmentTransaction.replace(R.id.fragment_container_main, mFragment).commit();
        setupCounts();
    }

    private void setupUnlockedFragment() {

        setUnlockedSelected();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new FragmentUnlockedAchievement();
        mFragmentTransaction.replace(R.id.fragment_container_main, mFragment).commit();
        setupCounts();
    }

    private void setupLockedFragment() {

        setLockedSelected();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new FragmentLockedAchievement();
        mFragmentTransaction.replace(R.id.fragment_container_main, mFragment).commit();
        setupCounts();
    }

    private void setupPlannerFragment() {

        setPlannerSelected();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new FragmentPlannerAchievement();
        mFragmentTransaction.replace(R.id.fragment_container_main, mFragment).commit();
        setupCounts();
    }

    private void setupFlowFragment() {

        setFlowSelected();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new FragmentFlowAchievement();
        mFragmentTransaction.replace(R.id.fragment_container_main, mFragment).commit();
        setupCounts();
    }


    ///////////////////////////////////////////////////
    // HELPER METHODS
    ///////////////////////////////////////////////////

    public Game getGame() {

        return game;
    }



    public void updateStatusText(String s) {

        status.setText(s);
    }

    public void updateStatusSubText(String s) {

        sub.setText(s);
    }

    public void hideLoadingUI() {

    }


    public void setGlobalSelected() {
        global.setTextColor(getResources().getColor(R.color.green));
        unlocked.setTextColor(getResources().getColor(R.color.colorTextLight));
        locked.setTextColor(getResources().getColor(R.color.colorTextLight));
        planner.setTextColor(getResources().getColor(R.color.colorTextLight));
        flow.setTextColor(getResources().getColor(R.color.colorTextLight));
    }


    public void setUnlockedSelected() {
        global.setTextColor(getResources().getColor(R.color.colorTextLight));
        unlocked.setTextColor(getResources().getColor(R.color.green));
        locked.setTextColor(getResources().getColor(R.color.colorTextLight));
        planner.setTextColor(getResources().getColor(R.color.colorTextLight));
        flow.setTextColor(getResources().getColor(R.color.colorTextLight));
    }

    public void setLockedSelected() {
        global.setTextColor(getResources().getColor(R.color.colorTextLight));
        unlocked.setTextColor(getResources().getColor(R.color.colorTextLight));
        locked.setTextColor(getResources().getColor(R.color.green));
        planner.setTextColor(getResources().getColor(R.color.colorTextLight));
        flow.setTextColor(getResources().getColor(R.color.colorTextLight));
    }

    public void setPlannerSelected() {
        global.setTextColor(getResources().getColor(R.color.colorTextLight));
        unlocked.setTextColor(getResources().getColor(R.color.colorTextLight));
        locked.setTextColor(getResources().getColor(R.color.colorTextLight));
        planner.setTextColor(getResources().getColor(R.color.green));
        flow.setTextColor(getResources().getColor(R.color.colorTextLight));
    }

    public void setFlowSelected() {
        global.setTextColor(getResources().getColor(R.color.colorTextLight));
        unlocked.setTextColor(getResources().getColor(R.color.colorTextLight));
        locked.setTextColor(getResources().getColor(R.color.colorTextLight));
        planner.setTextColor(getResources().getColor(R.color.colorTextLight));
        flow.setTextColor(getResources().getColor(R.color.green));
    }

    public void setupCounts() {

        frame.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        status.setVisibility(View.GONE);


        int globalC = 0, unlockedC = 0, lockedC = 0,plannerC = 0,flowC = 0;

        for (Achievement achievement : game.getAchievements()) {
            globalC++;

            if (achievement.getUnlocked().equalsIgnoreCase("1")) {
                unlockedC++;
            } else {
                lockedC++;
            }

            if(zUtils.getSharedPreferenceString(GameActivity.this,game.getId() + achievement.getId() + "_tagStage","").equalsIgnoreCase("stagea"))
                plannerC++;
            if(zUtils.getSharedPreferenceString(GameActivity.this,game.getId() + achievement.getId() + "_tagStage","").equalsIgnoreCase("stageb"))
                plannerC++;
            if(zUtils.getSharedPreferenceString(GameActivity.this,game.getId() + achievement.getId() + "_tagStage","").equalsIgnoreCase("stagec"))
                plannerC++;
            if(zUtils.getSharedPreferenceString(GameActivity.this,game.getId() + achievement.getId() + "_tagStage","").equalsIgnoreCase("staged"))
                plannerC++;
            if(zUtils.getSharedPreferenceString(GameActivity.this,game.getId() + achievement.getId() + "_tagStage","").equalsIgnoreCase("stagee"))
                plannerC++;
            if(zUtils.getSharedPreferenceLong(GameActivity.this,game.getId() + achievement.getId() + "_timestamp",0) != 0){
                flowC++;
            }
        }

        global.setText("Global\n" + String.valueOf(globalC));
        unlocked.setText("Unlocked\n" + String.valueOf(unlockedC));
        locked.setText("Locked\n" + String.valueOf(lockedC));
        planner.setText("Planner\n" + String.valueOf(plannerC));
        flow.setText("Flowchart\n" + String.valueOf(flowC));
        title.setText(String.valueOf(game.getName()));

        checkAndShowAchievementSearchHint();

        if (game.getAchievements().size() == unlockedC) {

            if(unlockedC == 0){

                Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "Congrats ! You've obtained all Achievements.", Toast.LENGTH_SHORT).show();
                completionFrame.setVisibility(View.VISIBLE);
                ImageView completionIcon = findViewById(R.id.completion_icon);

                completionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_trophy));
                completionIcon.setColorFilter(ContextCompat.getColor(GameActivity.this, R.color.gold), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void checkAndShowAchievementSearchHint() {

        Random random = new Random();
        int number = random.nextInt(100);
        int checkNumber = zUtils.getSharedPreferenceInt(this, "achivement_search_hint_shown", 0);
        if ((number % 5) == 0) {
            if(zUtils.getSharedPreferenceBoolean(GameActivity.this,"snack_bar_achievement_search_main",false)){

            }else{

                Snackbar snackbar = Snackbar
                        .make(frame, "Click the Achievement Icon to search about it in Google..", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("HIDE", new YesSearchClickListener());
                snackbar.show();
            }
        }

        if((number % 7) == 0){

            if(zUtils.getSharedPreferenceBoolean(GameActivity.this,"snack_bar_steam_search",false)){

            }else{

                Snackbar snackbar = Snackbar
                        .make(frame, "Click the Game title at top to search in Steam guides..", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("HIDE", new YesSteamClickListener());
                snackbar.show();
            }
        }
        checkNumber = checkNumber + 1;
        zUtils.insertSharedPreferenceInt(this, "achivement_search_hint_shown", checkNumber);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://steamcommunity.com/app/" + game.getId() + "/guides/?browsesort=toprated&browsefilter=toprated&requiredtags%5B0%5D=English&p=1";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        title.setTextColor(getResources().getColor(R.color.green));


    }


    private class YesSteamClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            zUtils.insertSharedPreferenceBoolean(GameActivity.this, "snack_bar_steam_search",true);
        }
    }

    private class YesSearchClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            zUtils.insertSharedPreferenceBoolean(GameActivity.this, "snack_bar_achievement_search_main",true);
        }
    }

    public void updateSnackBar(String s) {
        Snackbar snackbar = Snackbar
                .make(frame, s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
