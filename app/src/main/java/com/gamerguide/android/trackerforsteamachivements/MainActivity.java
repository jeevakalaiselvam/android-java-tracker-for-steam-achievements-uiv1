package com.gamerguide.android.trackerforsteamachivements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.gamerguide.android.trackerforsteamachivements.Activity.GameActivity;
import com.gamerguide.android.trackerforsteamachivements.Activity.HelpActivity;
import com.gamerguide.android.trackerforsteamachivements.Helper.ZUtils;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private ZUtils zUtils;

    private String SEARCH_QUERY = "";

    public static ArrayList<Game> games;
    private int viewType = 0;
    private RecyclerView gamesList;
    private ProgressBar progressBar;
    private ImageView setting, sort;
    private TextView status;
    private EditText search;

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

        setContentView(R.layout.activity_main);


        setContentView(R.layout.activity_main);
        gamesList = findViewById(R.id.games_list);
        progressBar = findViewById(R.id.progress);
        setting = findViewById(R.id.setting);
        status = findViewById(R.id.status);
        sort = findViewById(R.id.sort);
        search = findViewById(R.id.search);

        zUtils = new ZUtils(MainActivity.this);

        progressBar.setVisibility(View.VISIBLE);

        if (zUtils.getSharedPreferenceBoolean(this, "is_first_time", true)) {

            Intent i = new Intent(MainActivity.this, HelpActivity.class);
            startActivityForResult(i, 999);

        } else {
            setupGamesFromSteam();
        }

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSettingsPanel();
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog alertDialog;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.option_main_sort, null);
                TextView name = dialogView.findViewById(R.id.name);
                TextView playtime = dialogView.findViewById(R.id.playtime);
                TextView recent = dialogView.findViewById(R.id.recent);
                TextView view1 = dialogView.findViewById(R.id.view1);
                TextView view2 = dialogView.findViewById(R.id.view2);
                dialogBuilder.setView(dialogView);
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                recent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(MainActivity.this, "Games sorted by Recent Search", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(MainActivity.this, "maingames_sort", 0);
                        setupAdapterForList();
                    }
                });
                playtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(MainActivity.this, "Games sorted by Playtime", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(MainActivity.this, "maingames_sort", 1);
                        setupAdapterForList();
                    }
                });

                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(MainActivity.this, "Games sorted by Name", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(MainActivity.this, "maingames_sort", 2);
                        setupAdapterForList();
                    }
                });
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(MainActivity.this, "Detailed View", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(MainActivity.this, "maingames_view", 0);
                        setupAdapterForList();
                    }
                });
                view2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        Toast.makeText(MainActivity.this, "Normal View", Toast.LENGTH_SHORT).show();
                        zUtils.insertSharedPreferenceInt(MainActivity.this, "maingames_view", 1);
                        setupAdapterForList();
                    }
                });
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SEARCH_QUERY = String.valueOf(s);
                setupAdapterForList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        checkUpdateForApp();
        showRatingPage();
        showSharingPage();

    }

    private void showSharingPage() {

        int sharingCounterDone = zUtils.getSharedPreferenceInt(MainActivity.this,"sharing_counter_done",0);
        int sharingCounter = zUtils.getSharedPreferenceInt(MainActivity.this,"sharing_counter",0);

        if(sharingCounterDone == 0){
            sharingCounter = sharingCounter + 1;
            zUtils.insertSharedPreferenceInt(MainActivity.this,"sharing_counter",sharingCounter);
            if((sharingCounter % 10) == 0){

                showSharingAlert(sharingCounter);
            }
        }
    }

    private void showRatingPage() {

        int ratingCounterDone = zUtils.getSharedPreferenceInt(MainActivity.this,"rating_counter_done",0);
        int ratingCounter = zUtils.getSharedPreferenceInt(MainActivity.this,"rating_counter",0);

        if(ratingCounterDone == 0){
            ratingCounter = ratingCounter + 1;
            zUtils.insertSharedPreferenceInt(MainActivity.this,"rating_counter",ratingCounter);
            if((ratingCounter % 7) == 0){

                showRatingAlert(ratingCounter);
            }
        }
    }

    private void showRatingAlert(final int ratingCounter) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("RATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                zUtils.insertSharedPreferenceInt(MainActivity.this,"rating_counter_done",1);
                checkRating();

            }
        });


        dialogBuilder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private void showSharingAlert(final int sharingCounter) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_sharing, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("SHARE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                checkSharing();
                zUtils.insertSharedPreferenceInt(MainActivity.this,"sharing_counter_done",1);

            }
        });


        dialogBuilder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private void checkSharing() {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Steam Achievement Tracker");
            String shareMessage = "\nLet me recommend you this application to help you track your achievement progress in your Steam games. Its pretty useful, Check it out.\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + "com.gamerguide.android.trackerforsteamachivements" + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }

    }

    private void checkRating() {

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.gamerguide.android.r6tabpro")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.gamerguide.android.r6tabpro")));
            }

    }

    private void checkUpdateForApp() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference listRef = storage.getReference().child("files/");

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getItems()) {

                            checkUpdateAndShow(String.valueOf(item.getName()).trim());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        zUtils.logError("JEEVA_FIREBASE: " + e.getMessage());
                    }
                });
    }

    private void checkUpdateAndShow(String version) {

        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);

            if (Integer.parseInt(version.trim()) > pi.versionCode) {

                showUpdateAlert();
            }else{
                Toast.makeText(MainActivity.this,String.valueOf("No Update found.").trim(),Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

        }
    }

    private void showUpdateAlert() {

android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                checkUpdate();

            }
        });


        dialogBuilder.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


            }
        });


        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void checkUpdate() {

        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gamerguide.android.trackerforsteamachivements")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gamerguide.android.trackerforsteamachivements")));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        search.setText("");
        setupAdapterForList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showSettingsPanel();

    }

    private void showSettingsPanel() {

        final AlertDialog alertDialog;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.option_main_settings, null);
        //final TextView steam_api = dialogView.findViewById(R.id.steam_api);
        final TextView player_key = dialogView.findViewById(R.id.player_key);
        //steam_api.setText(zUtils.getSharedPreferenceString(MainActivity.this, "STEAM_KEY", ""));
        player_key.setText(zUtils.getSharedPreferenceString(MainActivity.this, "PLAYER_USERNAME", ""));
        Button save = dialogView.findViewById(R.id.save);
        Button report = dialogView.findViewById(R.id.report);
        TextView help = dialogView.findViewById(R.id.help);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSteamIdFromWeb(alertDialog, String.valueOf(player_key.getText()).trim().toLowerCase(), MainActivity.this);
            }
        });


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, "kalaiselvamg1995@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void getSteamIdFromWeb(final AlertDialog alertDialog, final String username, final Activity activity) {

        Toast.makeText(activity, "Please wait..", Toast.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ZUtils.getSteamPlayerID(username),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        zUtils.logError("JEEVA_URL: " + ZUtils.getSteamPlayerID(username));

                        parseHTMLHidden(response, username, alertDialog, activity);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                zUtils.logError("JEEVA_HTMLERROR: " + error.getLocalizedMessage());
                Toast.makeText(activity, "Kindly enter valid profile URL or if your profile is private, Make it public.", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);

    }

    private void parseHTMLHidden(String response, String username, AlertDialog dialog, Activity activity) {

        String steamID64 = "";

        Document doc = Jsoup.parse(response);
        try {


            Elements tr = doc.getElementsByTag("input");


            steamID64 = tr.get(5).val();

            zUtils.logError("JEEVA_ID: " + steamID64);


        } catch (Exception e) {
            e.printStackTrace();
            zUtils.logError("JEEVA_HTMLERROR: " + e.getMessage());
            Toast.makeText(this, "Error finding username..", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(MainActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
        zUtils.insertSharedPreferenceString(MainActivity.this, "PLAYER_ID", String.valueOf(steamID64));
        zUtils.insertSharedPreferenceString(MainActivity.this, "PLAYER_USERNAME", String.valueOf(username));
        zUtils = new ZUtils(MainActivity.this);
        dialog.hide();
        zUtils.insertSharedPreferenceBoolean(activity, "is_first_time", false);
        setupGamesFromSteam();
    }


    private void setupGamesFromSteam() {

        status.setText("Downloading data from Steam..");

        games = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        zUtils.logError("GAME: " + zUtils.gamesOwnedSteamURL());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, zUtils.gamesOwnedSteamURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        status.setText("Fetching data for all owned games..");
                        zUtils.logError(zUtils.gamesOwnedSteamURL());
                        parseGamesFromSteamStorage(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                status.setText("Error Fetching from Steam, Make sure you setup app properly. Click Settings icon on top left and click green help icon..");
                zUtils.logError("Error: " + error.getLocalizedMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void parseGamesFromSteamStorage(String response) {

        try {

            JSONObject obj = new JSONObject(response);

            JSONObject totalGames = obj.getJSONObject("response");
            JSONArray allGames = totalGames.getJSONArray("games");

            if (allGames != null) {

                for (int i = 0; i < allGames.length(); i++) {

                    JSONObject innerObj = new JSONObject(String.valueOf(allGames.get(i)));

                    status.setText("Saving owned game data..");
                    Game game = new Game(innerObj.getString("appid"),
                            innerObj.getString("playtime_forever").trim(),
                            innerObj.getString("name"),
                            zUtils.getSharedPreferenceLong(MainActivity.this, innerObj.getString("appid") + "_recent", 0));

                    games.add(game);
                    zUtils.logError("\nJEEVA_GAMES: Game { " + " Name: " + game.getName() +
                            " Id: " + game.getId() +
                            " Playtime: " + game.getPlaytime() +
                            " }");

                }
            }

        } catch (JSONException e) {
            zUtils.logError("JEEVA_ERROR: " + e.getMessage());
        }

        viewType = zUtils.getSharedPreferenceInt(MainActivity.this, "maingames_view", 0);

        setupAdapterForList();
    }


    private void setupAdapterForList() {

        if(games == null){
            setupGamesFromSteam();
        }

        int sort = zUtils.getSharedPreferenceInt(MainActivity.this, "maingames_sort", 0);


        if (sort == 0) {
            Collections.sort(games, new RecentComparator());
        } else if (sort == 1) {
            Collections.sort(games, new PlaytimeComparator());
        } else {
            Collections.sort(games, new NameComparator());
        }


        ArrayList<Game> searchContainedGames = new ArrayList<>();
        for (Game game : games) {
            if (game.getName().toLowerCase().trim().contains(SEARCH_QUERY)) {
                searchContainedGames.add(game);
            }

            if (viewType == 0) {
                MainGamesAdapter mainGamesAdapter = new MainGamesAdapter(searchContainedGames,viewType);
                GridLayoutManager mainGamesManager = new GridLayoutManager(MainActivity.this, 2);
                gamesList.setLayoutManager(mainGamesManager);
                gamesList.setAdapter(mainGamesAdapter);
            } else {
                MainGamesAdapter mainGamesAdapter = new MainGamesAdapter(searchContainedGames,viewType);
                GridLayoutManager mainGamesManager = new GridLayoutManager(MainActivity.this, 1);
                gamesList.setLayoutManager(mainGamesManager);
                gamesList.setAdapter(mainGamesAdapter);
            }


        }




    }

    ///////////////////////////////////////////////////////////
    //ADAPTERS
    ///////////////////////////////////////////////////////////

    class MainGamesAdapter extends RecyclerView.Adapter<MyViewHolder1> {

        ArrayList<Game> games;
        int mainViewType;


        public MainGamesAdapter(ArrayList<Game> games, int mainViewType) {

            this.mainViewType = mainViewType;
            progressBar.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);

            this.games = games;

            for (Game game : games) {

                ArrayList<Achievement> onlyUnlockedAchievements = new ArrayList<>();

                for (Achievement achievement : game.getAchievements()) {

                    if (achievement.getUnlocked().equalsIgnoreCase("1")) {
                        onlyUnlockedAchievements.add(achievement);
                    }
                }

                float completionPercentage = (((float) onlyUnlockedAchievements.size() / (float) game.getAchievements().size()) * 100f);
                game.setCompletion(completionPercentage);

            }

        }

        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            switch (mainViewType) {
                case 1:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_main_game, parent, false);
                    break;
                case 0:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_main_game_image, parent, false);
                    break;
                default:
                    v = (View) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_main_game, parent, false);
                    break;
            }
            MyViewHolder1 vh = new MyViewHolder1(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder1 holder, final int position) {

            if (viewType == 1) {

                ViewGroup frame = holder.mView.findViewById(R.id.frame);
                TextView time = holder.mView.findViewById(R.id.time);
                TextView name = holder.mView.findViewById(R.id.name);
                ImageView image = holder.mView.findViewById(R.id.image);

                Picasso.get().load(ZUtils.getGameImageURL(games.get(position).getId())).resize(460, 215).into(image);
                name.setText(games.get(position).getName());
                time.setText(games.get(position).getPlaytime() + " Hrs");

                //CHECKING AND SETTING RECENT SIX ACHIEVEMENTS

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        zUtils.insertSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", System.currentTimeMillis());
                        games.get(position).setSearchTime(zUtils.getSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", 0));
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(games.get(position));
                        i.putExtra("game", gameObjectJSON);
                        startActivity(i);
                    }
                });

                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        zUtils.insertSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", System.currentTimeMillis());
                        games.get(position).setSearchTime(zUtils.getSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", 0));
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(games.get(position));
                        i.putExtra("game", gameObjectJSON);
                        startActivity(i);
                    }
                });


            } else {
                ViewGroup frame = holder.mView.findViewById(R.id.frame);
                ImageView image = holder.mView.findViewById(R.id.image);

                Picasso.get().load(ZUtils.getGameImageURL(games.get(position).getId())).resize(460, 215).into(image);

                //CHECKING AND SETTING RECENT SIX ACHIEVEMENTS

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        zUtils.insertSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", System.currentTimeMillis());
                        games.get(position).setSearchTime(zUtils.getSharedPreferenceLong(MainActivity.this, games.get(position).getId() + "_recent", 0));
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
                        String gameObjectJSON = gson.toJson(games.get(position));
                        i.putExtra("game", gameObjectJSON);
                        startActivity(i);
                    }
                });


            }
        }


        @Override
        public int getItemCount() {

            return games.size();
        }


    }


    class MyViewHolder1 extends RecyclerView.ViewHolder {

        public View mView;

        public MyViewHolder1(View v) {
            super(v);
            mView = v;
        }

    }

    ///////////////////////////////////////////////////////////
    //COMPARATORS
    ///////////////////////////////////////////////////////////

    public class RecentComparator implements Comparator<Game> {
        @Override
        public int compare(Game o1, Game o2) {
            return smaller(o1.getSearchTime(), o2.getSearchTime());
        }
    }

    public class PlaytimeComparator implements Comparator<Game> {
        @Override
        public int compare(Game o1, Game o2) {
            return smaller(Integer.parseInt(o1.getPlaytime()), Integer.parseInt(o2.getPlaytime()));
        }
    }

    public class UnlockTimeComparator implements Comparator<Achievement> {
        @Override
        public int compare(Achievement a1, Achievement a2) {
            return smaller(Long.parseLong(a1.getUnlockTime()), Long.parseLong(a2.getUnlockTime()));
        }
    }

    public class CompletionComparator implements Comparator<Game> {
        @Override
        public int compare(Game o1, Game o2) {
            return smaller(o1.getCompletion(), o2.getCompletion());
        }
    }


    public class NameComparator implements Comparator<Game> {
        @Override
        public int compare(Game o1, Game o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private int smaller(int arg1, int arg2) {

        if (arg1 < arg2)
            return 1;
        else if (arg1 == arg2)
            return 0;
        else
            return -1;
    }

    private int smaller(float arg1, float arg2) {

        if (arg1 < arg2)
            return 1;
        else if (arg1 == arg2)
            return 0;
        else
            return -1;
    }


    ///////////////////////////////////////////////////////
    //ACTIVITY ACCESS HELPERS
    ///////////////////////////////////////////////////////


    public static Game getGameById(String gameId) {

        for (Game game : games) {
            if (game.getId().equalsIgnoreCase(gameId)) {
                return game;
            } else {
                return null;
            }
        }

        return null;
    }


}
