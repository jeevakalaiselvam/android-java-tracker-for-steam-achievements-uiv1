package com.gamerguide.android.trackerforsteamachivements.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.gamerguide.android.trackerforsteamachivements.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ZUtils {
    //public static String PLAYER_ID = "76561198983167428";
    //public static String STEAM_KEY = "3BA3A3631A49583DED8883A0BE25246A";

    public static float PLATINUM = 10.0f;
    public static float GOLD = 20.0f;
    public static float SILVER = 40.0f;
    public static float BRONZE = 100.0f;

    public static String PLAYER_ID = "";
    public static String STEAM_KEY = "";

    public ZUtils(Activity activity) {

        PLAYER_ID = getSharedPreferenceString(activity, "PLAYER_ID", "");
        //STEAM_KEY = getSharedPreferenceString(activity, "STEAM_KEY", "");
        STEAM_KEY = "777E368255E8B993B39D50433499C608";
    }

    public static String getSteamDBHiddden(String gameID) {
        return "https://completionist.me/steam/app/" + gameID + "/achievements";
    }

    public static String getSteamPlayerID(String username) {
        return "https://steamid.xyz/" + username ;
    }

    public static String getGameGlobalPercentage(String gameID) {
        return "https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/?gameid=" + gameID + "&format=json";
    }

    public static String getPlayerAchievements(String gameID) {
        return "https://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=" + gameID + "&key=" +  ZUtils.STEAM_KEY + "&steamid=" + ZUtils.PLAYER_ID;
    }

    public static String getGameSchemaURL(String gameID) {
        return "https://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=" + ZUtils.STEAM_KEY + "&appid=" + gameID;
    }

    public String gamesOwnedSteamURL() {
        return "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" + ZUtils.STEAM_KEY + "&steamid=" + ZUtils.PLAYER_ID + "&format=json&include_appinfo=true";
    }

    public static String getGameImageURL(String gameID) {
        return "https://steamcdn-a.akamaihd.net/steam/apps/" + gameID + "/header.jpg";
    }


    public void logError(String t) {
        Log.d("MYTRACKER", t);
    }

    public void insertSharedPreferenceString(Activity activity, String tag, String data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(tag, data);
        editor.commit();
    }

    public void insertSharedPreferenceBoolean(Activity activity, String tag, boolean data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(tag, data);
        editor.commit();
    }

    public void insertSharedPreferenceInt(Activity activity, String tag, int data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(tag, data);
        editor.commit();
    }

    public void insertSharedPreferenceLong(Activity activity, String tag, long data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong(tag, data);
        editor.commit();
    }

    public void insertSharedPreferenceFloat(Activity activity, String tag, float data) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editor.putFloat(tag, data);
        editor.commit();
    }

    public String getSharedPreferenceString(Activity activity, String tag, String defaultV) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);

        return sharedPref.getString(tag, defaultV);
    }

    public int getSharedPreferenceInt(Activity activity, String tag, int defaultV) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);

        return sharedPref.getInt(tag, defaultV);
    }

    public long getSharedPreferenceLong(Activity activity, String tag, long defaultV) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);

        return sharedPref.getLong(tag, defaultV);
    }

    public float getSharedPreferenceFloat(Activity activity, String tag, float defaultV) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);

        return sharedPref.getFloat(tag, defaultV);
    }


    public boolean getSharedPreferenceBoolean(Activity activity, String tag, boolean defaultV) {

        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getResources().getString(R.string.key_preferences), Context.MODE_PRIVATE);

        return sharedPref.getBoolean(tag, defaultV);
    }

    ///////////////////////////////////////////////////
    //HELPERS
    ///////////////////////////////////////////////////

    public void setupUnlockedIcons(ArrayList<Achievement> achievements,
                                   ArrayList<Achievement> onlyUnlockedAchievements,
                                   ImageView a1, ImageView a2, ImageView a3, ImageView a4, ImageView a5, ImageView a6, ImageView a7,
                                   Activity activity) {

        if (onlyUnlockedAchievements.size() == 0) {

            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a1);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a2);
            Picasso.get().load(achievements.get(2).getIconLocked()).resize(64, 64).into(a3);
            Picasso.get().load(achievements.get(3).getIconLocked()).resize(64, 64).into(a4);
            Picasso.get().load(achievements.get(4).getIconLocked()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(5).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(6).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 1) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a2);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a3);
            Picasso.get().load(achievements.get(2).getIconLocked()).resize(64, 64).into(a4);
            Picasso.get().load(achievements.get(3).getIconLocked()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(4).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(5).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 2) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a3);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a4);
            Picasso.get().load(achievements.get(2).getIconLocked()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(3).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(4).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 3) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(onlyUnlockedAchievements.get(2).getIcon()).resize(64, 64).into(a3);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a4);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(2).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(3).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 4) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(onlyUnlockedAchievements.get(2).getIcon()).resize(64, 64).into(a3);
            Picasso.get().load(onlyUnlockedAchievements.get(3).getIcon()).resize(64, 64).into(a4);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(2).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 5) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(onlyUnlockedAchievements.get(2).getIcon()).resize(64, 64).into(a3);
            Picasso.get().load(onlyUnlockedAchievements.get(3).getIcon()).resize(64, 64).into(a4);
            Picasso.get().load(onlyUnlockedAchievements.get(4).getIcon()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a6);
            Picasso.get().load(achievements.get(1).getIconLocked()).resize(64, 64).into(a7);

        } else if (onlyUnlockedAchievements.size() == 6) {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(onlyUnlockedAchievements.get(2).getIcon()).resize(64, 64).into(a3);
            Picasso.get().load(onlyUnlockedAchievements.get(3).getIcon()).resize(64, 64).into(a4);
            Picasso.get().load(onlyUnlockedAchievements.get(4).getIcon()).resize(64, 64).into(a5);
            Picasso.get().load(onlyUnlockedAchievements.get(5).getIcon()).resize(64, 64).into(a5);
            Picasso.get().load(achievements.get(0).getIconLocked()).resize(64, 64).into(a7);

        } else {

            Picasso.get().load(onlyUnlockedAchievements.get(0).getIcon()).resize(64, 64).into(a1);
            Picasso.get().load(onlyUnlockedAchievements.get(1).getIcon()).resize(64, 64).into(a2);
            Picasso.get().load(onlyUnlockedAchievements.get(2).getIcon()).resize(64, 64).into(a3);
            Picasso.get().load(onlyUnlockedAchievements.get(3).getIcon()).resize(64, 64).into(a4);
            Picasso.get().load(onlyUnlockedAchievements.get(4).getIcon()).resize(64, 64).into(a5);
            Picasso.get().load(onlyUnlockedAchievements.get(5).getIcon()).resize(64, 64).into(a6);
            Picasso.get().load(onlyUnlockedAchievements.get(6).getIcon()).resize(64, 64).into(a7);

        }

    }

    public void getAchievementDescription(final String id, final Achievement achievement, Activity activity, final TextView desc) {

        final String[] description = {""};

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getSteamDBHiddden(id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        logError("JEEVA_HIDDENURL: " + ZUtils.getSteamDBHiddden(id));
                        parseHTMLHidden(response, achievement, desc);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                logError("JEEVA_HIDDENERROR: " + error.getLocalizedMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void parseHTMLHidden(String response, Achievement achievement, final TextView desc) {

        Document doc = Jsoup.parse(response);
        try {


            Elements tr = doc.getElementsByTag("tr");


            for (int i = 1; i < tr.size(); i++) {

                String nameAll = tr.get(i).text().substring(0, tr.get(i).text().indexOf("%") - 4);

                if (nameAll.contains(achievement.getName())) {
                    achievement.setDesc(nameAll.replaceFirst(achievement.getName(), ""));
                }


            }

            desc.setText(String.valueOf(achievement.getDesc()));


        } catch (Exception e) {
            e.printStackTrace();
            logError("JEEVA_HTMLERROR: " + e.getMessage());
        }
    }

    public void checkAndSetupLabels(final Activity activity,final TextView story, final TextView easy,
                                    final TextView hard, final TextView miss, final TextView grind, final TextView online,
                                    final ArrayList<Achievement> achievements, final Game game, final int position) {

        story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));

        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "story");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "easy");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "hard");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "miss");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        grind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "grind");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "online");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                online.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });

    }

    public void checkAndSetupLabels(final Activity activity, final TextView story, final TextView easy,
                                    final TextView hard, final TextView miss, final TextView grind, final TextView multiplayer,
                                    final Achievement achievement, final Game game) {

        setupSavedLabels(activity,story,easy,hard,miss,grind,multiplayer,achievement,game);

        story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "story");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "easy");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "hard");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "miss");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });
        grind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "grind");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
            }
        });

        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "online");
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
            }
        });
    }
    public void setupSavedLabels(Activity activity,TextView none, TextView story, TextView easy, TextView hard,
                                 TextView miss, TextView grind,TextView multiplayer, Achievement achievement, Game game) {

        none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));

        switch (getSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "")) {

            case "":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                none.setTextColor(activity.getResources().getColor(R.color.colorText));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;

            case "story":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "easy":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "hard":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "miss":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "grind":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "online":
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            default:
                break;
        }
    }
    public void setupSavedLabels(Activity activity, TextView story, TextView easy, TextView hard,
                                 TextView miss, TextView grind,TextView multiplayer, Achievement achievement, Game game) {

        story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
        multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));

        switch (getSharedPreferenceString(activity, game.getId() + achievement.getId() + "_tag", "")) {



            case "story":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "easy":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "hard":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "miss":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "grind":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "online":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            default:
                break;
        }
    }

    public void setupSavedLabels(Activity activity, TextView story, TextView easy, TextView hard,
                                 TextView miss, TextView grind,TextView multiplayer, ArrayList<Achievement> achievements, Game game, int position) {

        switch (getSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "")) {

            case "story":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "easy":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "hard":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "miss":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "grind":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                break;
            case "online":
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label_selected));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            default:
                break;
        }
    }



    public void showLabelInAchievement(Activity activity,  TextView none,TextView story, TextView easy,
                                       TextView hard, TextView miss, TextView grind,TextView multiplayer, ArrayList<Achievement> achievements, Game game, int position) {

        none.setVisibility(View.GONE);
        story.setVisibility(View.GONE);
        easy.setVisibility(View.GONE);
        hard.setVisibility(View.GONE);
        miss.setVisibility(View.GONE);
        grind.setVisibility(View.GONE);
        multiplayer.setVisibility(View.GONE);

        switch (getSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "")) {
            case "":
                none.setVisibility(View.VISIBLE);
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                none.setTextColor(activity.getResources().getColor(R.color.colorText));
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "story":
                story.setVisibility(View.VISIBLE);
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "easy":
                story.setVisibility(View.GONE);
                easy.setVisibility(View.VISIBLE);
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "hard":
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.VISIBLE);
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "miss":
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.VISIBLE);
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "grind":
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.VISIBLE);
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setVisibility(View.GONE);
                break;
            case "online":
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.VISIBLE);
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            default:
                break;
        }
    }

    public void showLabelInAchievement(FragmentActivity activity,TextView none, TextView story, TextView easy,
                                       TextView hard, TextView miss, TextView grind,TextView multiplayer, ArrayList<Achievement> achievements, Game game, int position) {

        none.setVisibility(View.GONE);
        story.setVisibility(View.GONE);
        easy.setVisibility(View.GONE);
        hard.setVisibility(View.GONE);
        miss.setVisibility(View.GONE);
        grind.setVisibility(View.GONE);
        multiplayer.setVisibility(View.GONE);

        switch (getSharedPreferenceString(activity, game.getId() + achievements.get(position).getId() + "_tag", "")) {

            case "":
                none.setVisibility(View.VISIBLE);
                none.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                none.setTextColor(activity.getResources().getColor(R.color.colorText));
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;

            case "story":
                none.setVisibility(View.GONE);
                story.setVisibility(View.VISIBLE);
                story.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                story.setTextColor(activity.getResources().getColor(R.color.colorText));
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "easy":
                none.setVisibility(View.GONE);
                story.setVisibility(View.GONE);
                easy.setVisibility(View.VISIBLE);
                easy.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                easy.setTextColor(activity.getResources().getColor(R.color.colorText));
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "hard":
                none.setVisibility(View.GONE);
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.VISIBLE);
                hard.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                hard.setTextColor(activity.getResources().getColor(R.color.colorText));
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "miss":
                none.setVisibility(View.GONE);
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.VISIBLE);
                miss.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                miss.setTextColor(activity.getResources().getColor(R.color.colorText));
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.GONE);
                break;
            case "grind":
                none.setVisibility(View.GONE);
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.VISIBLE);
                grind.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                grind.setTextColor(activity.getResources().getColor(R.color.colorText));
                multiplayer.setVisibility(View.GONE);
                break;
            case "online":
                none.setVisibility(View.GONE);
                story.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                miss.setVisibility(View.GONE);
                grind.setVisibility(View.GONE);
                multiplayer.setVisibility(View.VISIBLE);
                multiplayer.setBackground(activity.getResources().getDrawable(R.drawable.ic_label));
                multiplayer.setTextColor(activity.getResources().getColor(R.color.colorText));
                break;
            default:
                break;
        }
    }

    public String getDateFromMillis(long milliSeconds) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds * 1000);
        return formatter.format(calendar.getTime());
    }
}

