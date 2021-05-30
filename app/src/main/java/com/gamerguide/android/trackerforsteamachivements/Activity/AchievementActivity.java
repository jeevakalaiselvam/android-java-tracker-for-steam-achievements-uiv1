package com.gamerguide.android.trackerforsteamachivements.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamerguide.android.trackerforsteamachivements.Helper.ZUtils;
import com.gamerguide.android.trackerforsteamachivements.Object.Achievement;
import com.gamerguide.android.trackerforsteamachivements.Object.Game;
import com.gamerguide.android.trackerforsteamachivements.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AchievementActivity extends AppCompatActivity {

    ArrayList<Achievement> achievements;
    Game game;
    ZUtils zUtils;
    private ImageView back, edit, delete;
    private ViewGroup dataFrame;
    private ViewGroup frame;

    TextView nameT;
    EditText editT;
    TextView data;
    ImageView iconT;
    ImageView closeT;
    TextView story;
    TextView unlock;
    TextView title;
    TextView easy;
    TextView hard;
    TextView miss;
    TextView grind;
    TextView multiplayer;
    TextView dlc1;
    TextView dlc2;
    TextView dlc3;
    Button saveT;
    ImageView next,previous;
    TextView descT;
    int positionOld;

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
        setContentView(R.layout.activity_achievement);

        zUtils = new ZUtils(AchievementActivity.this);
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        //achievement = gson.fromJson(getIntent().getStringExtra("achievement_single"), Achievement.class);
        game = gson.fromJson(getIntent().getStringExtra("game_single"), Game.class);
        positionOld = getIntent().getIntExtra("position",0);
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        achievements = (ArrayList<Achievement>) args.getSerializable("ARRAYLIST");
        //achievement = ((GameActivity)getCallingActivity()).

        frame = findViewById(R.id.frame);
        dataFrame = findViewById(R.id.dataFrame);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        editT = findViewById(R.id.editT);
        back = findViewById(R.id.back);
        nameT = findViewById(R.id.name);
        iconT = findViewById(R.id.icon);
        data = findViewById(R.id.data);
        closeT = findViewById(R.id.close);
        story = findViewById(R.id.story);
        unlock = findViewById(R.id.unlock);
        unlock.setVisibility(View.GONE);
        title = findViewById(R.id.title);
        easy = findViewById(R.id.easy);
        hard = findViewById(R.id.hard);
        miss = findViewById(R.id.miss);
        grind = findViewById(R.id.grind);
        multiplayer = findViewById(R.id.multiplayer);
        story.setBackground(getResources().getDrawable(R.drawable.ic_label));
        easy.setBackground(getResources().getDrawable(R.drawable.ic_label));
        hard.setBackground(getResources().getDrawable(R.drawable.ic_label));
        miss.setBackground(getResources().getDrawable(R.drawable.ic_label));
        grind.setBackground(getResources().getDrawable(R.drawable.ic_label));
        multiplayer.setBackground(getResources().getDrawable(R.drawable.ic_label));
        saveT = findViewById(R.id.save);
        descT = findViewById(R.id.desc);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        setupData(positionOld);

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(positionOld + 1  >= achievements.size() ){
                    Snackbar snackbar = Snackbar
                            .make(frame, "No more to the right..", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else{
                    positionOld = positionOld + 1;
                    setupData(positionOld);
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positionOld - 1  < 0){

                    Snackbar snackbar = Snackbar
                            .make(frame, "No more to the left..", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else{
                    positionOld = positionOld - 1;
                    setupData(positionOld);
                }
        }
        });
    }

    private void setupData(final int position) {

        editT.setVisibility(View.GONE);
        dataFrame.setVisibility(View.VISIBLE);
        data.setText(zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), ""));

        nameT.setText(achievements.get(position).getName());
        descT.setText(achievements.get(position).getDesc());

        if (zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), "").equalsIgnoreCase("")) {
            data.setText("No data present.");
        }
        Picasso.get().load(achievements.get(position).getIcon()).resize(64, 64).into(iconT);

        editT.setText(zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), ""));
        saveT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementActivity.this, "Notes saved..", Toast.LENGTH_SHORT).show();
                zUtils.insertSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), String.valueOf(editT.getText()));
                dataFrame.setVisibility(View.VISIBLE);
                editT.setVisibility(View.GONE);

            }
        });

        iconT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.google.com/search?q=" + achievements.get(position).getName() + " " +
                        game.getName() + " achievement guide";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        unlock.setVisibility(View.GONE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                zUtils.insertSharedPreferenceBoolean(AchievementActivity.this, "came_back_activity", true);
                zUtils.insertSharedPreferenceInt(AchievementActivity.this, "came_back_position", positionOld);
            }
        });

        title.setText(game.getName());
        saveT.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        previous.setVisibility(View.VISIBLE);

        zUtils.setupSavedLabels(AchievementActivity.this, story, easy, hard, miss, grind, multiplayer, achievements.get(position), game);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), "").equalsIgnoreCase("")) {
                    editT.setText("");
                } else {
                    editT.setText(zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() +achievements.get(position).getId(), ""));
                }

                editT.setVisibility(View.VISIBLE);
                editT.requestFocus();
                dataFrame.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                saveT.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
                previous.setVisibility(View.INVISIBLE);

                zUtils.checkAndSetupLabels(AchievementActivity.this, story, easy, hard, miss, grind, multiplayer, achievements.get(position), game);
            }
        });

        saveT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementActivity.this, "Notes saved..", Toast.LENGTH_SHORT).show();
                zUtils.insertSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), String.valueOf(editT.getText()));
                dataFrame.setVisibility(View.VISIBLE);
                editT.setVisibility(View.GONE);
                if (zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), "").equalsIgnoreCase("")) {
                    data.setText("No data present.");
                } else {
                    data.setText(zUtils.getSharedPreferenceString(AchievementActivity.this, "custom_note" + game.getId() + achievements.get(position).getId(), ""));
                }
                saveT.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                previous.setVisibility(View.VISIBLE);
                data.setMovementMethod(LinkMovementMethod.getInstance());


                zUtils.checkAndSetupLabels(AchievementActivity.this, story, easy, hard, miss, grind, multiplayer, achievements.get(position), game);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editT.setText("");
            }
        });

        if(zUtils.getSharedPreferenceBoolean(AchievementActivity.this,"snack_bar_achievement_search",false)){

        }else{

            Snackbar snackbar = Snackbar
                    .make(frame, "Click the Achievement Icon to search about it in Google..", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("HIDE", new YesClickListener());
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {

        zUtils.insertSharedPreferenceBoolean(AchievementActivity.this, "came_back_activity", true);
        zUtils.insertSharedPreferenceInt(AchievementActivity.this, "came_back_position", positionOld);
        finish();
    }

    private class YesClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            zUtils.insertSharedPreferenceBoolean(AchievementActivity.this, "snack_bar_achievement_search",true);
        }
    }
}
