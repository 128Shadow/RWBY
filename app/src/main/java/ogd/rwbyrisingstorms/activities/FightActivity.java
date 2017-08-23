package ogd.rwbyrisingstorms.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import ogd.rwbyrisingstorms.R;
import ogd.rwbyrisingstorms.fragments.HealthFragment;
import ogd.rwbyrisingstorms.util.OnSwipeListener;

public class FightActivity extends AppCompatActivity implements HealthFragment.OnFragmentInteractionListener {

    private class CharacterFrames {
        int[] idle;
        int idle_align_frame_x;
        int idle_align_frame_y;

        int[] attack;
        int[] attack_frame_change;
        int[] attack_align_frame_x;
        int[] attack_align_frame_y;
        int[] attack_width;
        int[] attack_height;

        int[] swipe_forward;
        int[] swipe_forward_frame_change;
        int[] swipe_forward_align_frame_x;
        int[] swipe_forward_align_frame_y;
        int[] swipe_forward_width;
        int[] swipe_forward_height;

        int[] swipe_back;
        int[] swipe_back_frame_change;
        int[] swipe_back_align_frame_x;
        int[] swipe_back_align_frame_y;
        int[] swipe_back_width;
        int[] swipe_back_height;

        int[] swipe_up;
        int[] swipe_up_frame_change;
        int[] swipe_up_align_frame_x;
        int[] swipe_up_align_frame_y;
        int[] swipe_up_width;
        int[] swipe_up_height;

        int[] swipe_down;
        int[] swipe_down_frame_change;
        int[] swipe_down_align_frame_x;
        int[] swipe_down_align_frame_y;
        int[] swipe_down_width;
        int[] swipe_down_height;

        int[] crouch;
        int[] crouch_align_frame_x;
        int[] crouch_align_frame_y;
        int[] crouch_width;
        int[] crouch_height;

        int[] block;
        int[] block_align_frame_x;
        int[] block_align_frame_y;
        int[] block_width;
        int[] block_height;

        int[] grab;
        int[] grab_align_frame_x;
        int[] grab_align_frame_y;
        int[] grab_width;
        int[] grab_height;

        int[] hit;
        int[] hit_align_frame_x;
        int[] hit_align_frame_y;
        int[] hit_width;
        int[] hit_height;

        ArrayList<MyFrame> sIdleFrames;
        ArrayList<MyFrame> cIdleFrames;
        ArrayList<MyFrame> blockFrames;
        ArrayList<MyFrame> blockToStandFrames;

        ArrayList<MyFrame> cDownIdleFrames;
        ArrayList<MyFrame> cUpIdleFrames;

        ArrayList<MyFrame> attackFrames_0;
        ArrayList<MyFrame> attackFrames_1;
        ArrayList<MyFrame> attackFrames_2;
        ArrayList<MyFrame> attackFrames_3;
        ArrayList<MyFrame> attackFrames_4;
        ArrayList<MyFrame> attackFrames_5;
        ArrayList<MyFrame> attackFrames_6;
        ArrayList<MyFrame> attackFrames_7;
        ArrayList<MyFrame> attackFrames_8;
        ArrayList<MyFrame> attackFrames_9;
        ArrayList<MyFrame> forwardAttackFrames_0;
        ArrayList<MyFrame> forwardAttackFrames_1;
        ArrayList<MyFrame> forwardAttackFrames_2;
        ArrayList<MyFrame> downAttackFrames_0;
        ArrayList<MyFrame> downAttackFrames_1;
        ArrayList<MyFrame> upAttackFrames_0;
        ArrayList<MyFrame> upAttackFrames_1;
        ArrayList<MyFrame> upAttackFrames_2;
        ArrayList<MyFrame> upAttackFrames_3;
        ArrayList<MyFrame> backFrames_0;
        ArrayList<MyFrame> chargeFrames;
        ArrayList<MyFrame> throwFrames;
        ArrayList<MyFrame> throwMissFrames;
        ArrayList<MyFrame> throwGrabFrames;

        ArrayList<MyFrame> hitStandMedium;
        ArrayList<MyFrame> hitStandHigh;
        ArrayList<MyFrame> hitStandLow;

        String status;
        int currentFrame;
        int totalFrames;

        double size;

        int location_up;
        int location_left;

        boolean blocking;

        boolean immediate;
        String nextStatus;
        int nextFrame;

        String lastStatus;
        int lastFrame;
    }

    public static class MyFrame {
        byte[] bytes;
        int duration;
        int move_x;
        int move_y;
        int hit_x;
        int hit_y;
        int hit_width;
        int hit_height;
        Drawable drawable;
        boolean isReady = false;
        boolean attack = false;
    }

    private CharacterFrames[] playerCharacters;
    private CharacterFrames[] enemyCharacters;

    private ImageView background;
    private Context context;

    private ImageView player1;
    private ImageView player2;
    private ImageView player3;
    private ImageView player4;
    private ImageView enemy1;
    private ImageView enemy2;
    private ImageView enemy3;
    private ImageView enemy4;

    private Runnable runPlayer1;
    private Runnable runPlayer2;
    private Runnable runPlayer3;
    private Runnable runPlayer4;
    private Runnable runEnemy1;
    private Runnable runEnemy2;
    private Runnable runEnemy3;
    private Runnable runEnemy4;

    private String character;
    private int active;
    private int enemy;

    private float initialX;
    private float initialY;

    private float twoInitialX;
    private float twoInitialY;

    private float threshold = 200;

    private boolean moving;
    private boolean longPress;
    private boolean two_finger;
    private boolean two_swipe;

    private int screen_height;
    private int screen_width;

    private int screen_multiplier = 2;
    private int screen_margin = 100;

    private GestureDetector gestureDetector;
    private Handler handler;

    private HealthFragment leftFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        context = this;

        Intent intent = getIntent();
        character = intent.getExtras().getString("character");
        Log.d("Fight", "Character: " + character);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //leftFragment = (HealthFragment) getSupportFragmentManager().findFragmentById(R.id.healthbar_left);

        initializeView();
        initializeTouch();
    }

    private void initializeView() {
        player1 = (ImageView) findViewById(R.id.player_1);
        player2 = (ImageView) findViewById(R.id.player_2);
        player3 = (ImageView) findViewById(R.id.player_3);
        player4 = (ImageView) findViewById(R.id.player_4);
        enemy1 = (ImageView) findViewById(R.id.enemy_1);
        enemy2 = (ImageView) findViewById(R.id.enemy_2);
        enemy3 = (ImageView) findViewById(R.id.enemy_3);
        enemy4 = (ImageView) findViewById(R.id.enemy_4);
        background = (ImageView) findViewById(R.id.background_view);

        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screen_height = outMetrics.heightPixels;
        screen_width = outMetrics.widthPixels;

        Log.d("Fight", "Height: " + screen_height);
        Log.d("Fight", "Width: " + screen_width);

        background.getLayoutParams().height = screen_height * screen_multiplier;
        background.getLayoutParams().width = screen_width * screen_multiplier;
        background.setX(0 - (((screen_width * screen_multiplier) - screen_width) / 2));
        background.setY(0 - (screen_height * screen_multiplier) + screen_height);

        ScrollView myScroll = (ScrollView) findViewById(R.id.scroll_view);
        myScroll.setOnTouchListener(touchListener);

        handler = new Handler();

        loadCharacterFrames();
    }

    public Bitmap flipImage(Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        return Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }

    private void loadCharacterFrames() {
        Log.d("Fight", "Test: " + character);

        playerCharacters = new CharacterFrames[1];
        enemyCharacters = new CharacterFrames[1];

        switch(character) {
            case "filia":
                CharacterFrames AllFrames = new CharacterFrames();
                CharacterFrames EnemyFrames = new CharacterFrames();

                AllFrames.idle = new int[] {R.xml.filia_s_idle};

                AllFrames.attack = new int[] {R.xml.filia_attack_0, R.xml.filia_attack_1, R.xml.filia_attack_2, R.xml.filia_attack_3, R.xml.filia_attack_4};
                AllFrames.attack_frame_change = new int[10];
                AllFrames.attack_align_frame_x = new int[10];
                AllFrames.attack_align_frame_y = new int[10];
                AllFrames.attack_width = new int[10];
                AllFrames.attack_height = new int[10];

                AllFrames.swipe_back = new int[] {R.xml.filia_dash_back};
                AllFrames.swipe_back_frame_change = new int[1];
                AllFrames.swipe_back_align_frame_x = new int[1];
                AllFrames.swipe_back_align_frame_y = new int[1];
                AllFrames.swipe_back_width = new int[1];
                AllFrames.swipe_back_height = new int[1];

                AllFrames.swipe_forward = new int[] {R.xml.filia_dash_front, R.xml.filia_swipe_right_0, R.xml.filia_swipe_right_1};
                AllFrames.swipe_forward_frame_change = new int[3];
                AllFrames.swipe_forward_align_frame_x = new int[3];
                AllFrames.swipe_forward_align_frame_y = new int[3];
                AllFrames.swipe_forward_width = new int[3];
                AllFrames.swipe_forward_height = new int[3];

                AllFrames.swipe_down = new int[] {R.xml.filia_swipe_down};
                AllFrames.swipe_down_frame_change = new int[2];
                AllFrames.swipe_down_align_frame_x = new int[2];
                AllFrames.swipe_down_align_frame_y = new int[2];
                AllFrames.swipe_down_width = new int[2];
                AllFrames.swipe_down_height = new int[2];

                AllFrames.swipe_up = new int[] {R.xml.filia_swipe_up_0, R.xml.filia_swipe_up_1, R.xml.filia_swipe_up_2, R.xml.filia_swipe_up_3};
                AllFrames.swipe_up_frame_change = new int[4];
                AllFrames.swipe_up_align_frame_x = new int[4];
                AllFrames.swipe_up_align_frame_y = new int[4];
                AllFrames.swipe_up_width = new int[4];
                AllFrames.swipe_up_height = new int[4];

                AllFrames.crouch = new int[] {R.xml.filia_c_down, R.xml.filia_c_idle, R.xml.filia_charge, R.xml.filia_c_up};
                AllFrames.crouch_align_frame_x = new int[4];
                AllFrames.crouch_align_frame_y = new int[4];
                AllFrames.crouch_width = new int[4];
                AllFrames.crouch_height = new int[4];

                AllFrames.block = new int[] {R.xml.filia_block, R.xml.filia_block_to_stand};
                AllFrames.block_align_frame_x = new int[2];
                AllFrames.block_align_frame_y = new int[2];
                AllFrames.block_width = new int[2];
                AllFrames.block_height = new int[2];

                AllFrames.grab = new int[] {R.xml.filia_throw, R.xml.filia_throw_miss, R.xml.filia_throw_grab};
                AllFrames.grab_align_frame_x = new int[3];
                AllFrames.grab_align_frame_y = new int[3];
                AllFrames.grab_width = new int[3];
                AllFrames.grab_height = new int[3];

                AllFrames.hit = new int[] {R.xml.filia_hit_stand_medium, R.xml.filia_hit_stand_high, R.xml.filia_hit_stand_low};
                AllFrames.hit_align_frame_x = new int[3];
                AllFrames.hit_align_frame_y = new int[3];
                AllFrames.hit_width = new int[3];
                AllFrames.hit_height = new int[3];

                AllFrames.location_up = 0;
                AllFrames.location_left = 0;

                AllFrames.size = 1;


                EnemyFrames.idle = new int[] {R.xml.filia_s_idle};

                EnemyFrames.attack = new int[] {R.xml.filia_attack_0, R.xml.filia_attack_1, R.xml.filia_attack_2, R.xml.filia_attack_3, R.xml.filia_attack_4};
                EnemyFrames.attack_frame_change = new int[10];
                EnemyFrames.attack_align_frame_x = new int[10];
                EnemyFrames.attack_align_frame_y = new int[10];
                EnemyFrames.attack_width = new int[10];
                EnemyFrames.attack_height = new int[10];

                EnemyFrames.swipe_back = new int[] {R.xml.filia_dash_back};
                EnemyFrames.swipe_back_frame_change = new int[1];
                EnemyFrames.swipe_back_align_frame_x = new int[1];
                EnemyFrames.swipe_back_align_frame_y = new int[1];
                EnemyFrames.swipe_back_width = new int[1];
                EnemyFrames.swipe_back_height = new int[1];

                EnemyFrames.swipe_forward = new int[] {R.xml.filia_dash_front, R.xml.filia_swipe_right_0, R.xml.filia_swipe_right_1};
                EnemyFrames.swipe_forward_frame_change = new int[3];
                EnemyFrames.swipe_forward_align_frame_x = new int[3];
                EnemyFrames.swipe_forward_align_frame_y = new int[3];
                EnemyFrames.swipe_forward_width = new int[3];
                EnemyFrames.swipe_forward_height = new int[3];

                EnemyFrames.swipe_down = new int[] {R.xml.filia_swipe_down};
                EnemyFrames.swipe_down_frame_change = new int[2];
                EnemyFrames.swipe_down_align_frame_x = new int[2];
                EnemyFrames.swipe_down_align_frame_y = new int[2];
                EnemyFrames.swipe_down_width = new int[2];
                EnemyFrames.swipe_down_height = new int[2];

                EnemyFrames.swipe_up = new int[] {R.xml.filia_swipe_up_0, R.xml.filia_swipe_up_1, R.xml.filia_swipe_up_2, R.xml.filia_swipe_up_3};
                EnemyFrames.swipe_up_frame_change = new int[4];
                EnemyFrames.swipe_up_align_frame_x = new int[4];
                EnemyFrames.swipe_up_align_frame_y = new int[4];
                EnemyFrames.swipe_up_width = new int[4];
                EnemyFrames.swipe_up_height = new int[4];

                EnemyFrames.crouch = new int[] {R.xml.filia_c_down, R.xml.filia_c_idle, R.xml.filia_charge, R.xml.filia_c_up};
                EnemyFrames.crouch_align_frame_x = new int[4];
                EnemyFrames.crouch_align_frame_y = new int[4];
                EnemyFrames.crouch_width = new int[4];
                EnemyFrames.crouch_height = new int[4];

                EnemyFrames.block = new int[] {R.xml.filia_block, R.xml.filia_block_to_stand};
                EnemyFrames.block_align_frame_x = new int[2];
                EnemyFrames.block_align_frame_y = new int[2];
                EnemyFrames.block_width = new int[2];
                EnemyFrames.block_height = new int[2];

                EnemyFrames.grab = new int[] {R.xml.filia_throw, R.xml.filia_throw_miss, R.xml.filia_throw_grab};
                EnemyFrames.grab_align_frame_x = new int[3];
                EnemyFrames.grab_align_frame_y = new int[3];
                EnemyFrames.grab_width = new int[3];
                EnemyFrames.grab_height = new int[3];

                EnemyFrames.hit = new int[] {R.xml.filia_hit_stand_medium, R.xml.filia_hit_stand_high, R.xml.filia_hit_stand_low};
                EnemyFrames.hit_align_frame_x = new int[3];
                EnemyFrames.hit_align_frame_y = new int[3];
                EnemyFrames.hit_width = new int[3];
                EnemyFrames.hit_height = new int[3];

                EnemyFrames.location_up = 0;
                EnemyFrames.location_left = 0;

                EnemyFrames.size = 1;


                playerCharacters[0] = AllFrames;
                enemyCharacters[0] = EnemyFrames;

                break;
        }

        loadAnimation();
    }

    private void loadAnimation() {

        for(int x = 0; x < playerCharacters.length; x++) {
            loadFrames("player", x, "sIdle", playerCharacters[x].idle[0]);

            for (int i = 0; i < playerCharacters[x].attack.length; i++) {
                loadFrames("player", x, "attack" + i, playerCharacters[x].attack[i]);
            }

            for(int i = 0; i < playerCharacters[x].swipe_back.length; i++) {
                loadFrames("player", x, "swipeBack" + i, playerCharacters[x].swipe_back[i]);
            }

            for(int i = 0; i < playerCharacters[x].swipe_forward.length; i++) {
                loadFrames("player", x, "swipeForward" + i, playerCharacters[x].swipe_forward[i]);
            }

            for(int i = 0; i < playerCharacters[x].swipe_down.length; i++) {
                loadFrames("player", x, "swipeDown" + i, playerCharacters[x].swipe_down[i]);
            }

            for(int i = 0; i < playerCharacters[x].swipe_up.length; i++) {
                loadFrames("player", x, "swipeUp" + i, playerCharacters[x].swipe_up[i]);
            }

            for(int i = 0; i < playerCharacters[x].crouch.length; i++) {
                loadFrames("player", x, "crouch" + i, playerCharacters[x].crouch[i]);
            }

            for(int i = 0; i < playerCharacters[x].block.length; i++) {
                loadFrames("player", x, "block" + i, playerCharacters[x].block[i]);
            }

            for(int i = 0; i < playerCharacters[x].grab.length; i++) {
                loadFrames("player", x, "throw" + i, playerCharacters[x].grab[i]);
            }

            for(int i = 0; i < playerCharacters[x].hit.length; i++) {
                loadFrames("player", x, "hit" + i, playerCharacters[x].hit[i]);
            }

        }

        for(int x = 0; x < enemyCharacters.length; x++) {
            loadFrames("enemy", x, "sIdle", enemyCharacters[x].idle[0]);

            for(int i = 0; i < enemyCharacters[x].attack.length; i++) {
                loadFrames("enemy", x, "attack" + i, enemyCharacters[x].attack[i]);
            }

            for(int i = 0; i < enemyCharacters[x].swipe_back.length; i++) {
                loadFrames("enemy", x, "swipeBack" + i, enemyCharacters[x].swipe_back[i]);
            }

            for(int i = 0; i < enemyCharacters[x].swipe_forward.length; i++) {
                loadFrames("enemy", x, "swipeForward" + i, enemyCharacters[x].swipe_forward[i]);
            }

            for(int i = 0; i < enemyCharacters[x].swipe_down.length; i++) {
                loadFrames("enemy", x, "swipeDown" + i, enemyCharacters[x].swipe_down[i]);
            }

            for(int i = 0; i < enemyCharacters[x].swipe_up.length; i++) {
                loadFrames("enemy", x, "swipeUp" + i, enemyCharacters[x].swipe_up[i]);
            }

            for(int i = 0; i < enemyCharacters[x].crouch.length; i++) {
                loadFrames("enemy", x, "crouch" + i, enemyCharacters[x].crouch[i]);
            }

            for(int i = 0; i < enemyCharacters[x].block.length; i++) {
                loadFrames("enemy", x, "block" + i, enemyCharacters[x].block[i]);
            }

            for(int i = 0; i < enemyCharacters[x].grab.length; i++) {
                loadFrames("enemy", x, "throw" + i, enemyCharacters[x].grab[i]);
            }

            for(int i = 0; i < enemyCharacters[x].hit.length; i++) {
                loadFrames("enemy", x, "hit" + i, enemyCharacters[x].hit[i]);
            }
        }

        playerCharacters[0].currentFrame = 0;
        playerCharacters[0].totalFrames = playerCharacters[0].sIdleFrames.size();
        playerCharacters[0].status = "sIdle";
        playerCharacters[0].lastStatus = "";
        playerCharacters[0].location_left = screen_margin;
        playerCharacters[0].location_up = 0;
        active = 0;

        player1.setImageDrawable(playerCharacters[0].sIdleFrames.get(0).drawable);
        player1.setY((screen_height - screen_margin) - playerCharacters[0].idle_align_frame_y);
        player1.setX(screen_margin);
        player1.getLayoutParams().width = playerCharacters[0].idle_align_frame_x;
        player1.getLayoutParams().height = playerCharacters[0].idle_align_frame_y;

        enemyCharacters[0].currentFrame = 0;
        enemyCharacters[0].totalFrames = enemyCharacters[0].sIdleFrames.size();
        enemyCharacters[0].status = "sIdle";
        enemyCharacters[0].lastStatus = "";
        enemyCharacters[0].location_left = (screen_width) - (enemyCharacters[0].idle_align_frame_x + screen_margin);
        enemyCharacters[0].location_up = 0;
        enemy = 0;

        enemy1.setImageDrawable(enemyCharacters[0].sIdleFrames.get(0).drawable);
        enemy1.setY((screen_height - screen_margin) - enemyCharacters[0].idle_align_frame_y);
        enemy1.setX((screen_width - screen_margin) - enemyCharacters[0].idle_align_frame_y);
        enemy1.getLayoutParams().width = enemyCharacters[0].idle_align_frame_x;
        enemy1.getLayoutParams().height = enemyCharacters[0].idle_align_frame_y;

        runAnimation();
    }

    private void loadFrames(String player, int select, String action, int id) {
        initializeFrames(player, select, action);
        XmlResourceParser parser = this.getResources().getXml(id);

        try {
            int eventType = parser.getEventType();
            int align_x = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("animation-list")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("frame_change")) {
                                int frame_change = Integer.parseInt(parser.getAttributeValue(i));
                                initializeFrameChange(player, select, action, frame_change);
                            } else if (parser.getAttributeName(i).equals("align_x")) {
                                int frame_change = Integer.parseInt(parser.getAttributeValue(i));
                                align_x = frame_change;
                                //initializeFrameAlign(player, select, action, frame_change, true);
                            } else if (parser.getAttributeName(i).equals("align_y")) {
                                int frame_change = Integer.parseInt(parser.getAttributeValue(i));
                                initializeFrameAlign(player, select, action, frame_change, false);
                            }
                        }
                    } else if (parser.getName().equals("item")) {
                        MyFrame myFrame = new MyFrame();
                        byte[] bytes = null;
                        byte[] flip_bytes = null;

                        for (int i=0; i<parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("drawable")) {
                                int resId = Integer.parseInt(parser.getAttributeValue(i).substring(1));
                                bytes = IOUtils.toByteArray(this.getResources().openRawResource(resId));
                                switch(player) {
                                    case "enemy":
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        bmp = flipImage(bmp);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        flip_bytes = stream.toByteArray();
                                        break;
                                    default:
                                        break;
                                }
                            } else if (parser.getAttributeName(i).equals("duration")) {
                                myFrame.duration = parser.getAttributeIntValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("move_x")) {
                                myFrame.move_x = parser.getAttributeIntValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("move_y")) {
                                myFrame.move_y = parser.getAttributeIntValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("attack_align_x")) {
                                myFrame.hit_x = parser.getAttributeIntValue(i, 0);
                                myFrame.attack = true;
                            } else if (parser.getAttributeName(i).equals("attack_align_y")) {
                                myFrame.hit_y = parser.getAttributeIntValue(i, 0);
                                myFrame.attack = true;
                            } else if (parser.getAttributeName(i).equals("attack_width")) {
                                myFrame.hit_width = parser.getAttributeIntValue(i, 0);
                                myFrame.attack = true;
                            } else if (parser.getAttributeName(i).equals("attack_height")) {
                                myFrame.hit_height = parser.getAttributeIntValue(i, 0);
                                myFrame.attack = true;
                            }
                        }

                        switch(player) {
                            case "player":
                                myFrame.bytes = bytes;
                                break;
                            case "enemy":
                                myFrame.bytes = flip_bytes;
                                break;
                            default:
                                break;
                        }

                        initializeFrameSize(player, select, action, myFrame, align_x);
                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                }
                eventType = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        loadDrawable(player, select, action, 0);
    }

    private void initializeFrames(String player, int select, String action) {
        switch(player) {
            case "player":
                switch(action) {
                    case "sIdle":
                        playerCharacters[select].sIdleFrames = new ArrayList<>();
                        break;
                    case "attack0":
                        playerCharacters[select].attackFrames_0 = new ArrayList<>();
                        break;
                    case "attack1":
                        playerCharacters[select].attackFrames_1 = new ArrayList<>();
                        break;
                    case "attack2":
                        playerCharacters[select].attackFrames_2 = new ArrayList<>();
                        break;
                    case "attack3":
                        playerCharacters[select].attackFrames_3 = new ArrayList<>();
                        break;
                    case "attack4":
                        playerCharacters[select].attackFrames_4 = new ArrayList<>();
                        break;
                    case "attack5":
                        playerCharacters[select].attackFrames_5 = new ArrayList<>();
                        break;
                    case "attack6":
                        playerCharacters[select].attackFrames_6 = new ArrayList<>();
                        break;
                    case "attack7":
                        playerCharacters[select].attackFrames_7 = new ArrayList<>();
                        break;
                    case "attack8":
                        playerCharacters[select].attackFrames_8 = new ArrayList<>();
                        break;
                    case "attack9":
                        playerCharacters[select].attackFrames_9 = new ArrayList<>();
                        break;
                    case "swipeBack0":
                        playerCharacters[select].backFrames_0 = new ArrayList<>();
                        break;
                    case "swipeForward0":
                        playerCharacters[select].forwardAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeForward1":
                        playerCharacters[select].forwardAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeForward2":
                        playerCharacters[select].forwardAttackFrames_2 = new ArrayList<>();
                        break;
                    case "swipeDown0":
                        playerCharacters[select].downAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeDown1":
                        playerCharacters[select].downAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeUp0":
                        playerCharacters[select].upAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeUp1":
                        playerCharacters[select].upAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeUp2":
                        playerCharacters[select].upAttackFrames_2 = new ArrayList<>();
                        break;
                    case "swipeUp3":
                        playerCharacters[select].upAttackFrames_3 = new ArrayList<>();
                        break;
                    case "crouch0":
                        playerCharacters[select].cDownIdleFrames = new ArrayList<>();
                        break;
                    case "crouch1":
                        playerCharacters[select].cIdleFrames = new ArrayList<>();
                        break;
                    case "crouch2":
                        playerCharacters[select].chargeFrames = new ArrayList<>();
                        break;
                    case "crouch3":
                        playerCharacters[select].cUpIdleFrames = new ArrayList<>();
                        break;
                    case "block0":
                        playerCharacters[select].blockFrames = new ArrayList<>();
                        break;
                    case "block1":
                        playerCharacters[select].blockToStandFrames = new ArrayList<>();
                        break;
                    case "throw0":
                        playerCharacters[select].throwFrames = new ArrayList<>();
                        break;
                    case "throw1":
                        playerCharacters[select].throwMissFrames = new ArrayList<>();
                        break;
                    case "throw2":
                        playerCharacters[select].throwGrabFrames = new ArrayList<>();
                        break;
                    case "hit0":
                        playerCharacters[select].hitStandMedium = new ArrayList<>();
                        break;
                    case "hit1":
                        playerCharacters[select].hitStandHigh = new ArrayList<>();
                        break;
                    case "hit2":
                        playerCharacters[select].hitStandLow = new ArrayList<>();
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch(action) {
                    case "sIdle":
                        enemyCharacters[select].sIdleFrames = new ArrayList<>();
                        break;
                    case "attack0":
                        enemyCharacters[select].attackFrames_0 = new ArrayList<>();
                        break;
                    case "attack1":
                        enemyCharacters[select].attackFrames_1 = new ArrayList<>();
                        break;
                    case "attack2":
                        enemyCharacters[select].attackFrames_2 = new ArrayList<>();
                        break;
                    case "attack3":
                        enemyCharacters[select].attackFrames_3 = new ArrayList<>();
                        break;
                    case "attack4":
                        enemyCharacters[select].attackFrames_4 = new ArrayList<>();
                        break;
                    case "attack5":
                        enemyCharacters[select].attackFrames_5 = new ArrayList<>();
                        break;
                    case "attack6":
                        enemyCharacters[select].attackFrames_6 = new ArrayList<>();
                        break;
                    case "attack7":
                        enemyCharacters[select].attackFrames_7 = new ArrayList<>();
                        break;
                    case "attack8":
                        enemyCharacters[select].attackFrames_8 = new ArrayList<>();
                        break;
                    case "attack9":
                        enemyCharacters[select].attackFrames_9 = new ArrayList<>();
                        break;
                    case "swipeBack0":
                        enemyCharacters[select].backFrames_0 = new ArrayList<>();
                        break;
                    case "swipeForward0":
                        enemyCharacters[select].forwardAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeForward1":
                        enemyCharacters[select].forwardAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeForward2":
                        enemyCharacters[select].forwardAttackFrames_2 = new ArrayList<>();
                        break;
                    case "swipeDown0":
                        enemyCharacters[select].downAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeDown1":
                        enemyCharacters[select].downAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeUp0":
                        enemyCharacters[select].upAttackFrames_0 = new ArrayList<>();
                        break;
                    case "swipeUp1":
                        enemyCharacters[select].upAttackFrames_1 = new ArrayList<>();
                        break;
                    case "swipeUp2":
                        enemyCharacters[select].upAttackFrames_2 = new ArrayList<>();
                        break;
                    case "swipeUp3":
                        enemyCharacters[select].upAttackFrames_3 = new ArrayList<>();
                        break;
                    case "crouch0":
                        enemyCharacters[select].cDownIdleFrames = new ArrayList<>();
                        break;
                    case "crouch1":
                        enemyCharacters[select].cIdleFrames = new ArrayList<>();
                        break;
                    case "crouch2":
                        enemyCharacters[select].chargeFrames = new ArrayList<>();
                        break;
                    case "crouch3":
                        enemyCharacters[select].cUpIdleFrames = new ArrayList<>();
                        break;
                    case "block0":
                        enemyCharacters[select].blockFrames = new ArrayList<>();
                        break;
                    case "block1":
                        enemyCharacters[select].blockToStandFrames = new ArrayList<>();
                        break;
                    case "throw0":
                        enemyCharacters[select].throwFrames = new ArrayList<>();
                        break;
                    case "throw1":
                        enemyCharacters[select].throwMissFrames = new ArrayList<>();
                        break;
                    case "throw2":
                        enemyCharacters[select].throwGrabFrames = new ArrayList<>();
                        break;
                    case "hit0":
                        enemyCharacters[select].hitStandMedium = new ArrayList<>();
                        break;
                    case "hit1":
                        enemyCharacters[select].hitStandHigh = new ArrayList<>();
                        break;
                    case "hit2":
                        enemyCharacters[select].hitStandLow = new ArrayList<>();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void initializeFrameChange(String player, int select, String action, int frame_change) {
        switch(player) {
            case "player":
                switch(action) {
                    case "attack0":
                        playerCharacters[select].attack_frame_change[0] = frame_change;
                        break;
                    case "attack1":
                        playerCharacters[select].attack_frame_change[1] = frame_change;
                        break;
                    case "attack2":
                        playerCharacters[select].attack_frame_change[2] = frame_change;
                        break;
                    case "attack3":
                        playerCharacters[select].attack_frame_change[3] = frame_change;
                        break;
                    case "attack4":
                        playerCharacters[select].attack_frame_change[4] = frame_change;
                        break;
                    case "attack5":
                        playerCharacters[select].attack_frame_change[5] = frame_change;
                        break;
                    case "attack6":
                        playerCharacters[select].attack_frame_change[6] = frame_change;
                        break;
                    case "attack7":
                        playerCharacters[select].attack_frame_change[7] = frame_change;
                        break;
                    case "attack8":
                        playerCharacters[select].attack_frame_change[8] = frame_change;
                        break;
                    case "attack9":
                        playerCharacters[select].attack_frame_change[9] = frame_change;
                        break;
                    case "swipeBack0":
                        playerCharacters[select].swipe_back_frame_change[0] = frame_change;
                        break;
                    case "swipeForward0":
                        playerCharacters[select].swipe_forward_frame_change[0] = frame_change;
                        break;
                    case "swipeForward1":
                        playerCharacters[select].swipe_forward_frame_change[1] = frame_change;
                        break;
                    case "swipeForward2":
                        playerCharacters[select].swipe_forward_frame_change[2] = frame_change;
                        break;
                    case "swipeDown0":
                        playerCharacters[select].swipe_down_frame_change[0] = frame_change;
                        break;
                    case "swipeDown1":
                        playerCharacters[select].swipe_down_frame_change[1] = frame_change;
                        break;
                    case "swipeUp0":
                        playerCharacters[select].swipe_up_frame_change[0] = frame_change;
                        break;
                    case "swipeUp1":
                        playerCharacters[select].swipe_up_frame_change[1] = frame_change;
                        break;
                    case "swipeUp2":
                        playerCharacters[select].swipe_up_frame_change[2] = frame_change;
                        break;
                    case "swipeUp3":
                        playerCharacters[select].swipe_up_frame_change[3] = frame_change;
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch(action) {
                    case "attack0":
                        enemyCharacters[select].attack_frame_change[0] = frame_change;
                        break;
                    case "attack1":
                        enemyCharacters[select].attack_frame_change[1] = frame_change;
                        break;
                    case "attack2":
                        enemyCharacters[select].attack_frame_change[2] = frame_change;
                        break;
                    case "attack3":
                        enemyCharacters[select].attack_frame_change[3] = frame_change;
                        break;
                    case "attack4":
                        enemyCharacters[select].attack_frame_change[4] = frame_change;
                        break;
                    case "attack5":
                        enemyCharacters[select].attack_frame_change[5] = frame_change;
                        break;
                    case "attack6":
                        enemyCharacters[select].attack_frame_change[6] = frame_change;
                        break;
                    case "attack7":
                        enemyCharacters[select].attack_frame_change[7] = frame_change;
                        break;
                    case "attack8":
                        enemyCharacters[select].attack_frame_change[8] = frame_change;
                        break;
                    case "attack9":
                        enemyCharacters[select].attack_frame_change[9] = frame_change;
                        break;
                    case "swipeBack0":
                        enemyCharacters[select].swipe_back_frame_change[0] = frame_change;
                        break;
                    case "swipeForward0":
                        enemyCharacters[select].swipe_forward_frame_change[0] = frame_change;
                        break;
                    case "swipeForward1":
                        enemyCharacters[select].swipe_forward_frame_change[1] = frame_change;
                        break;
                    case "swipeForward2":
                        enemyCharacters[select].swipe_forward_frame_change[2] = frame_change;
                        break;
                    case "swipeDown0":
                        enemyCharacters[select].swipe_down_frame_change[0] = frame_change;
                        break;
                    case "swipeDown1":
                        enemyCharacters[select].swipe_down_frame_change[1] = frame_change;
                        break;
                    case "swipeUp0":
                        enemyCharacters[select].swipe_up_frame_change[0] = frame_change;
                        break;
                    case "swipeUp1":
                        enemyCharacters[select].swipe_up_frame_change[1] = frame_change;
                        break;
                    case "swipeUp2":
                        enemyCharacters[select].swipe_up_frame_change[2] = frame_change;
                        break;
                    case "swipeUp3":
                        enemyCharacters[select].swipe_up_frame_change[3] = frame_change;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void initializeFrameAlign(String player, int select, String action, int frame_change, boolean horizontal) {
        if(horizontal) {
            switch(player) {
                case "player":
                    switch (action) {
                        case "sIdle":
                            playerCharacters[select].idle_align_frame_x = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack0":
                            playerCharacters[select].attack_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack1":
                            playerCharacters[select].attack_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack2":
                            playerCharacters[select].attack_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack3":
                            playerCharacters[select].attack_align_frame_x[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack4":
                            playerCharacters[select].attack_align_frame_x[4] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack5":
                            playerCharacters[select].attack_align_frame_x[5] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack6":
                            playerCharacters[select].attack_align_frame_x[6] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack7":
                            playerCharacters[select].attack_align_frame_x[7] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack8":
                            playerCharacters[select].attack_align_frame_x[8] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack9":
                            playerCharacters[select].attack_align_frame_x[9] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeBack0":
                            playerCharacters[select].swipe_back_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward0":
                            playerCharacters[select].swipe_forward_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward1":
                            playerCharacters[select].swipe_forward_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward2":
                            playerCharacters[select].swipe_forward_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeDown0":
                            playerCharacters[select].swipe_down_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeDown1":
                            playerCharacters[select].swipe_down_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp0":
                            playerCharacters[select].swipe_up_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp1":
                            playerCharacters[select].swipe_up_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp2":
                            playerCharacters[select].swipe_up_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp3":
                            playerCharacters[select].swipe_up_align_frame_x[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "block0":
                            playerCharacters[select].block_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "block1":
                            playerCharacters[select].block_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch0":
                            playerCharacters[select].crouch_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch1":
                            playerCharacters[select].crouch_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch2":
                            playerCharacters[select].crouch_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch3":
                            playerCharacters[select].crouch_align_frame_x[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw0":
                            playerCharacters[select].grab_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw1":
                            playerCharacters[select].grab_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw2":
                            playerCharacters[select].grab_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit0":
                            playerCharacters[select].hit_align_frame_x[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit1":
                            playerCharacters[select].hit_align_frame_x[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit2":
                            playerCharacters[select].hit_align_frame_x[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        default:
                            break;
                    }
                    break;
                case "enemy":
                    switch (action) {
                        case "sIdle":
                            enemyCharacters[select].idle_align_frame_x = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack0":
                            enemyCharacters[select].attack_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack1":
                            enemyCharacters[select].attack_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack2":
                            enemyCharacters[select].attack_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack3":
                            enemyCharacters[select].attack_align_frame_x[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack4":
                            enemyCharacters[select].attack_align_frame_x[4] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack5":
                            enemyCharacters[select].attack_align_frame_x[5] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack6":
                            enemyCharacters[select].attack_align_frame_x[6] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack7":
                            enemyCharacters[select].attack_align_frame_x[7] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack8":
                            enemyCharacters[select].attack_align_frame_x[8] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack9":
                            enemyCharacters[select].attack_align_frame_x[9] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeBack0":
                            enemyCharacters[select].swipe_back_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward0":
                            enemyCharacters[select].swipe_forward_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward1":
                            enemyCharacters[select].swipe_forward_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward2":
                            enemyCharacters[select].swipe_forward_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeDown0":
                            enemyCharacters[select].swipe_down_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeDown1":
                            enemyCharacters[select].swipe_down_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp0":
                            enemyCharacters[select].swipe_up_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp1":
                            enemyCharacters[select].swipe_up_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp2":
                            enemyCharacters[select].swipe_up_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp3":
                            enemyCharacters[select].swipe_up_align_frame_x[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "block0":
                            enemyCharacters[select].block_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "block1":
                            enemyCharacters[select].block_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch0":
                            enemyCharacters[select].crouch_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch1":
                            enemyCharacters[select].crouch_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch2":
                            enemyCharacters[select].crouch_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch3":
                            enemyCharacters[select].crouch_align_frame_x[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw0":
                            enemyCharacters[select].grab_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw1":
                            enemyCharacters[select].grab_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw2":
                            enemyCharacters[select].grab_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit0":
                            enemyCharacters[select].hit_align_frame_x[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit1":
                            enemyCharacters[select].hit_align_frame_x[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit2":
                            enemyCharacters[select].hit_align_frame_x[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch(player) {
                case "player":
                    switch (action) {
                        case "sIdle":
                            playerCharacters[select].idle_align_frame_y = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack0":
                            playerCharacters[select].attack_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack1":
                            playerCharacters[select].attack_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack2":
                            playerCharacters[select].attack_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack3":
                            playerCharacters[select].attack_align_frame_y[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack4":
                            playerCharacters[select].attack_align_frame_y[4] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack5":
                            playerCharacters[select].attack_align_frame_y[5] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack6":
                            playerCharacters[select].attack_align_frame_y[6] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack7":
                            playerCharacters[select].attack_align_frame_y[7] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack8":
                            playerCharacters[select].attack_align_frame_y[8] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "attack9":
                            playerCharacters[select].attack_align_frame_y[9] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeBack0":
                            playerCharacters[select].swipe_back_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward0":
                            playerCharacters[select].swipe_forward_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward1":
                            playerCharacters[select].swipe_forward_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeForward2":
                            playerCharacters[select].swipe_forward_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeDown0":
                            playerCharacters[select].swipe_down_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeDown1":
                            playerCharacters[select].swipe_down_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp0":
                            playerCharacters[select].swipe_up_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp1":
                            playerCharacters[select].swipe_up_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp2":
                            playerCharacters[select].swipe_up_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "swipeUp3":
                            playerCharacters[select].swipe_up_align_frame_y[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "block0":
                            playerCharacters[select].block_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "block1":
                            playerCharacters[select].block_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch0":
                            playerCharacters[select].crouch_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch1":
                            playerCharacters[select].crouch_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch2":
                            playerCharacters[select].crouch_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "crouch3":
                            playerCharacters[select].crouch_align_frame_y[3] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw0":
                            playerCharacters[select].grab_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw1":
                            playerCharacters[select].grab_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "throw2":
                            playerCharacters[select].grab_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit0":
                            playerCharacters[select].hit_align_frame_y[0] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit1":
                            playerCharacters[select].hit_align_frame_y[1] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        case "hit2":
                            playerCharacters[select].hit_align_frame_y[2] = (int) (frame_change * playerCharacters[select].size);
                            break;
                        default:
                            break;
                    }
                    break;
                case "enemy":
                    switch (action) {
                        case "sIdle":
                            enemyCharacters[select].idle_align_frame_y = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack0":
                            enemyCharacters[select].attack_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack1":
                            enemyCharacters[select].attack_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack2":
                            enemyCharacters[select].attack_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack3":
                            enemyCharacters[select].attack_align_frame_y[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack4":
                            enemyCharacters[select].attack_align_frame_y[4] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack5":
                            enemyCharacters[select].attack_align_frame_y[5] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack6":
                            enemyCharacters[select].attack_align_frame_y[6] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack7":
                            enemyCharacters[select].attack_align_frame_y[7] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack8":
                            enemyCharacters[select].attack_align_frame_y[8] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "attack9":
                            enemyCharacters[select].attack_align_frame_y[9] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeBack0":
                            enemyCharacters[select].swipe_back_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward0":
                            enemyCharacters[select].swipe_forward_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward1":
                            enemyCharacters[select].swipe_forward_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeForward2":
                            enemyCharacters[select].swipe_forward_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeDown0":
                            enemyCharacters[select].swipe_down_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeDown1":
                            enemyCharacters[select].swipe_down_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp0":
                            enemyCharacters[select].swipe_up_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp1":
                            enemyCharacters[select].swipe_up_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp2":
                            enemyCharacters[select].swipe_up_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "swipeUp3":
                            enemyCharacters[select].swipe_up_align_frame_y[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "block0":
                            enemyCharacters[select].block_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "block1":
                            enemyCharacters[select].block_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch0":
                            enemyCharacters[select].crouch_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch1":
                            enemyCharacters[select].crouch_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch2":
                            enemyCharacters[select].crouch_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "crouch3":
                            enemyCharacters[select].crouch_align_frame_y[3] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw0":
                            enemyCharacters[select].grab_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw1":
                            enemyCharacters[select].grab_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "throw2":
                            enemyCharacters[select].grab_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit0":
                            enemyCharacters[select].hit_align_frame_y[0] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit1":
                            enemyCharacters[select].hit_align_frame_y[1] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        case "hit2":
                            enemyCharacters[select].hit_align_frame_y[2] = (int) (frame_change * enemyCharacters[select].size);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initializeFrameSize(String player, int select, String action, MyFrame myFrame, int align_x) {
        int flip_x = 0;
        switch(player) {
            case "player":
                switch(action) {
                    case "sIdle":
                        playerCharacters[select].sIdleFrames.add(myFrame);
                        playerCharacters[select].idle_align_frame_x = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].idle_align_frame_y = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        break;
                    case "attack0":
                        playerCharacters[select].attackFrames_0.add(myFrame);
                        playerCharacters[select].attack_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack1":
                        playerCharacters[select].attackFrames_1.add(myFrame);
                        playerCharacters[select].attack_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack2":
                        playerCharacters[select].attackFrames_2.add(myFrame);
                        playerCharacters[select].attack_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack3":
                        playerCharacters[select].attackFrames_3.add(myFrame);
                        playerCharacters[select].attack_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack4":
                        playerCharacters[select].attackFrames_4.add(myFrame);
                        playerCharacters[select].attack_width[4] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[4] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack5":
                        playerCharacters[select].attackFrames_5.add(myFrame);
                        playerCharacters[select].attack_width[5] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[5] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack6":
                        playerCharacters[select].attackFrames_6.add(myFrame);
                        playerCharacters[select].attack_width[6] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[6] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack7":
                        playerCharacters[select].attackFrames_7.add(myFrame);
                        playerCharacters[select].attack_width[7] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[7] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack8":
                        playerCharacters[select].attackFrames_8.add(myFrame);
                        playerCharacters[select].attack_width[8] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[8] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "attack9":
                        playerCharacters[select].attackFrames_9.add(myFrame);
                        playerCharacters[select].attack_width[9] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].attack_height[9] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        break;
                    case "swipeBack0":
                        playerCharacters[select].backFrames_0.add(myFrame);
                        playerCharacters[select].swipe_back_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_back_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeForward0":
                        playerCharacters[select].forwardAttackFrames_0.add(myFrame);
                        playerCharacters[select].swipe_forward_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_forward_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeForward1":
                        playerCharacters[select].forwardAttackFrames_1.add(myFrame);
                        playerCharacters[select].swipe_forward_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_forward_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeForward2":
                        playerCharacters[select].forwardAttackFrames_2.add(myFrame);
                        playerCharacters[select].swipe_forward_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_forward_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeDown0":
                        playerCharacters[select].downAttackFrames_0.add(myFrame);
                        playerCharacters[select].swipe_down_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_down_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeDown1":
                        playerCharacters[select].downAttackFrames_1.add(myFrame);
                        playerCharacters[select].swipe_down_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_down_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeUp0":
                        playerCharacters[select].upAttackFrames_0.add(myFrame);
                        playerCharacters[select].swipe_up_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_up_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeUp1":
                        playerCharacters[select].upAttackFrames_1.add(myFrame);
                        playerCharacters[select].swipe_up_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_up_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeUp2":
                        playerCharacters[select].upAttackFrames_2.add(myFrame);
                        playerCharacters[select].swipe_up_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_up_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "swipeUp3":
                        playerCharacters[select].upAttackFrames_3.add(myFrame);
                        playerCharacters[select].swipe_up_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].swipe_up_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "crouch0":
                        playerCharacters[select].cDownIdleFrames.add(myFrame);
                        playerCharacters[select].crouch_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].crouch_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "crouch1":
                        playerCharacters[select].cIdleFrames.add(myFrame);
                        playerCharacters[select].crouch_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].crouch_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "crouch2":
                        playerCharacters[select].chargeFrames.add(myFrame);
                        playerCharacters[select].crouch_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].crouch_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "crouch3":
                        playerCharacters[select].cUpIdleFrames.add(myFrame);
                        playerCharacters[select].crouch_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].crouch_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "block0":
                        playerCharacters[select].blockFrames.add(myFrame);
                        playerCharacters[select].block_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].block_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "block1":
                        playerCharacters[select].blockToStandFrames.add(myFrame);
                        playerCharacters[select].block_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].block_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "throw0":
                        playerCharacters[select].throwFrames.add(myFrame);
                        playerCharacters[select].grab_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].grab_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        break;
                    case "throw1":
                        playerCharacters[select].throwMissFrames.add(myFrame);
                        playerCharacters[select].grab_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].grab_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "throw2":
                        playerCharacters[select].throwGrabFrames.add(myFrame);
                        playerCharacters[select].grab_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].grab_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "hit0":
                        playerCharacters[select].hitStandMedium.add(myFrame);
                        playerCharacters[select].hit_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].hit_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "hit1":
                        playerCharacters[select].hitStandHigh.add(myFrame);
                        playerCharacters[select].hit_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].hit_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    case "hit2":
                        playerCharacters[select].hitStandLow.add(myFrame);
                        playerCharacters[select].hit_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * playerCharacters[select].size);
                        playerCharacters[select].hit_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * playerCharacters[select].size);
                        initializeFrameAlign(player, select, action, align_x, true);
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch(action) {
                    case "sIdle":
                        enemyCharacters[select].sIdleFrames.add(myFrame);
                        enemyCharacters[select].idle_align_frame_x = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].idle_align_frame_y = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        break;
                    case "attack0":
                        enemyCharacters[select].attackFrames_0.add(myFrame);
                        enemyCharacters[select].attack_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack1":
                        enemyCharacters[select].attackFrames_1.add(myFrame);
                        enemyCharacters[select].attack_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack2":
                        enemyCharacters[select].attackFrames_2.add(myFrame);
                        enemyCharacters[select].attack_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack3":
                        enemyCharacters[select].attackFrames_3.add(myFrame);
                        enemyCharacters[select].attack_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[3]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack4":
                        enemyCharacters[select].attackFrames_4.add(myFrame);
                        enemyCharacters[select].attack_width[4] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[4] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[4]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack5":
                        enemyCharacters[select].attackFrames_5.add(myFrame);
                        enemyCharacters[select].attack_width[5] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[5] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[5]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack6":
                        enemyCharacters[select].attackFrames_6.add(myFrame);
                        enemyCharacters[select].attack_width[6] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[6] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[6]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack7":
                        enemyCharacters[select].attackFrames_7.add(myFrame);
                        enemyCharacters[select].attack_width[7] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[7] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[7]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack8":
                        enemyCharacters[select].attackFrames_8.add(myFrame);
                        enemyCharacters[select].attack_width[8] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[8] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[8]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "attack9":
                        enemyCharacters[select].attackFrames_9.add(myFrame);
                        enemyCharacters[select].attack_width[9] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].attack_height[9] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].attack_width[9]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeBack0":
                        enemyCharacters[select].backFrames_0.add(myFrame);
                        enemyCharacters[select].swipe_back_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_back_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_back_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeForward0":
                        enemyCharacters[select].forwardAttackFrames_0.add(myFrame);
                        enemyCharacters[select].swipe_forward_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_forward_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_forward_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeForward1":
                        enemyCharacters[select].forwardAttackFrames_1.add(myFrame);
                        enemyCharacters[select].swipe_forward_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_forward_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_forward_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeForward2":
                        enemyCharacters[select].forwardAttackFrames_2.add(myFrame);
                        enemyCharacters[select].swipe_forward_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_forward_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_forward_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeDown0":
                        enemyCharacters[select].downAttackFrames_0.add(myFrame);
                        enemyCharacters[select].swipe_down_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_down_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_down_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeDown1":
                        enemyCharacters[select].downAttackFrames_1.add(myFrame);
                        enemyCharacters[select].swipe_down_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_down_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_down_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeUp0":
                        enemyCharacters[select].upAttackFrames_0.add(myFrame);
                        enemyCharacters[select].swipe_up_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_up_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_up_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeUp1":
                        enemyCharacters[select].upAttackFrames_1.add(myFrame);
                        enemyCharacters[select].swipe_up_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_up_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_up_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeUp2":
                        enemyCharacters[select].upAttackFrames_2.add(myFrame);
                        enemyCharacters[select].swipe_up_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_up_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_up_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "swipeUp3":
                        enemyCharacters[select].upAttackFrames_3.add(myFrame);
                        enemyCharacters[select].swipe_up_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].swipe_up_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].swipe_up_width[3]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "crouch0":
                        enemyCharacters[select].cDownIdleFrames.add(myFrame);
                        enemyCharacters[select].crouch_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].crouch_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].crouch_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "crouch1":
                        enemyCharacters[select].cIdleFrames.add(myFrame);
                        enemyCharacters[select].crouch_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].crouch_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].crouch_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "crouch2":
                        enemyCharacters[select].chargeFrames.add(myFrame);
                        enemyCharacters[select].crouch_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].crouch_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].crouch_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "crouch3":
                        enemyCharacters[select].cUpIdleFrames.add(myFrame);
                        enemyCharacters[select].crouch_width[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].crouch_height[3] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].crouch_width[3]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "block0":
                        enemyCharacters[select].blockFrames.add(myFrame);
                        enemyCharacters[select].block_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].block_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].block_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "block1":
                        enemyCharacters[select].blockToStandFrames.add(myFrame);
                        enemyCharacters[select].block_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].block_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].block_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "throw0":
                        enemyCharacters[select].throwFrames.add(myFrame);
                        enemyCharacters[select].grab_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].grab_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].grab_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "throw1":
                        enemyCharacters[select].throwMissFrames.add(myFrame);
                        enemyCharacters[select].grab_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].grab_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].grab_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "throw2":
                        enemyCharacters[select].throwGrabFrames.add(myFrame);
                        enemyCharacters[select].grab_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].grab_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].grab_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "hit0":
                        enemyCharacters[select].hitStandMedium.add(myFrame);
                        enemyCharacters[select].hit_width[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].hit_height[0] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].hit_width[0]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "hit1":
                        enemyCharacters[select].hitStandHigh.add(myFrame);
                        enemyCharacters[select].hit_width[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].hit_height[1] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].hit_width[1]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    case "hit2":
                        enemyCharacters[select].hitStandLow.add(myFrame);
                        enemyCharacters[select].hit_width[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getWidth() * enemyCharacters[select].size);
                        enemyCharacters[select].hit_height[2] = (int) (BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length).getHeight() * enemyCharacters[select].size);
                        flip_x = (enemyCharacters[select].idle_align_frame_x - enemyCharacters[select].hit_width[2]) - align_x;
                        initializeFrameAlign(player, select, action, flip_x, true);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void loadDrawable(String player, int select, String myStatus, int frame) {
        switch(player) {
            case "player":
                switch (myStatus) {
                    case "sIdle":
                        if (frame >= playerCharacters[select].sIdleFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].sIdleFrames.get(frame).drawable == null) {
                            playerCharacters[select].sIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].sIdleFrames.get(frame).bytes, 0, playerCharacters[select].sIdleFrames.get(frame).bytes.length));
                            playerCharacters[select].sIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "attack0":
                        if (frame >= playerCharacters[select].attackFrames_0.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_0.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_0.get(frame).bytes, 0, playerCharacters[select].attackFrames_0.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "attack1":
                        if (frame >= playerCharacters[select].attackFrames_1.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_1.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_1.get(frame).bytes, 0, playerCharacters[select].attackFrames_1.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "attack2":
                        if (frame >= playerCharacters[select].attackFrames_2.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_2.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_2.get(frame).bytes, 0, playerCharacters[select].attackFrames_2.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "attack3":
                        if (frame >= playerCharacters[select].attackFrames_3.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_3.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_3.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_3.get(frame).bytes, 0, playerCharacters[select].attackFrames_3.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_3.get(frame).isReady = true;
                        }
                        break;
                    case "attack4":
                        if (frame >= playerCharacters[select].attackFrames_4.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_4.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_4.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_4.get(frame).bytes, 0, playerCharacters[select].attackFrames_4.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_4.get(frame).isReady = true;
                        }
                        break;
                    case "attack5":
                        if (frame >= playerCharacters[select].attackFrames_5.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_5.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_5.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_5.get(frame).bytes, 0, playerCharacters[select].attackFrames_5.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_5.get(frame).isReady = true;
                        }
                        break;
                    case "attack6":
                        if (frame >= playerCharacters[select].attackFrames_6.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_6.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_6.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_6.get(frame).bytes, 0, playerCharacters[select].attackFrames_6.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_6.get(frame).isReady = true;
                        }
                        break;
                    case "attack7":
                        if (frame >= playerCharacters[select].attackFrames_7.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_7.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_7.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_7.get(frame).bytes, 0, playerCharacters[select].attackFrames_7.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_7.get(frame).isReady = true;
                        }
                        break;
                    case "attack8":
                        if (frame >= playerCharacters[select].attackFrames_8.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_8.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_8.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_8.get(frame).bytes, 0, playerCharacters[select].attackFrames_8.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_8.get(frame).isReady = true;
                        }
                        break;
                    case "attack9":
                        if (frame >= playerCharacters[select].attackFrames_9.size()) {
                            return;
                        }
                        if (playerCharacters[select].attackFrames_9.get(frame).drawable == null) {
                            playerCharacters[select].attackFrames_9.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].attackFrames_9.get(frame).bytes, 0, playerCharacters[select].attackFrames_9.get(frame).bytes.length));
                            playerCharacters[select].attackFrames_9.get(frame).isReady = true;
                        }
                        break;
                    case "swipeBack0":
                        if(frame >= playerCharacters[select].backFrames_0.size()) {
                            return;
                        }
                        if (playerCharacters[select].backFrames_0.get(frame).drawable == null) {
                            playerCharacters[select].backFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].backFrames_0.get(frame).bytes, 0, playerCharacters[select].backFrames_0.get(frame).bytes.length));
                            playerCharacters[select].backFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward0":
                        if(frame >= playerCharacters[select].forwardAttackFrames_0.size()) {
                            return;
                        }
                        if (playerCharacters[select].forwardAttackFrames_0.get(frame).drawable == null) {
                            playerCharacters[select].forwardAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].forwardAttackFrames_0.get(frame).bytes, 0, playerCharacters[select].forwardAttackFrames_0.get(frame).bytes.length));
                            playerCharacters[select].forwardAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward1":
                        if(frame >= playerCharacters[select].forwardAttackFrames_1.size()) {
                            return;
                        }
                        if (playerCharacters[select].forwardAttackFrames_1.get(frame).drawable == null) {
                            playerCharacters[select].forwardAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].forwardAttackFrames_1.get(frame).bytes, 0, playerCharacters[select].forwardAttackFrames_1.get(frame).bytes.length));
                            playerCharacters[select].forwardAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward2":
                        if(frame >= playerCharacters[select].forwardAttackFrames_2.size()) {
                            return;
                        }
                        if (playerCharacters[select].forwardAttackFrames_2.get(frame).drawable == null) {
                            playerCharacters[select].forwardAttackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].forwardAttackFrames_2.get(frame).bytes, 0, playerCharacters[select].forwardAttackFrames_2.get(frame).bytes.length));
                            playerCharacters[select].forwardAttackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "swipeDown0":
                        if(frame >= playerCharacters[select].downAttackFrames_0.size()) {
                            return;
                        }
                        if (playerCharacters[select].downAttackFrames_0.get(frame).drawable == null) {
                            playerCharacters[select].downAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].downAttackFrames_0.get(frame).bytes, 0, playerCharacters[select].downAttackFrames_0.get(frame).bytes.length));
                            playerCharacters[select].downAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeDown1":
                        if(frame >= playerCharacters[select].downAttackFrames_1.size()) {
                            return;
                        }
                        if (playerCharacters[select].downAttackFrames_1.get(frame).drawable == null) {
                            playerCharacters[select].downAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].downAttackFrames_1.get(frame).bytes, 0, playerCharacters[select].downAttackFrames_1.get(frame).bytes.length));
                            playerCharacters[select].downAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp0":
                        if(frame >= playerCharacters[select].upAttackFrames_0.size()) {
                            return;
                        }
                        if (playerCharacters[select].upAttackFrames_0.get(frame).drawable == null) {
                            playerCharacters[select].upAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].upAttackFrames_0.get(frame).bytes, 0, playerCharacters[select].upAttackFrames_0.get(frame).bytes.length));
                            playerCharacters[select].upAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp1":
                        if(frame >= playerCharacters[select].upAttackFrames_1.size()) {
                            return;
                        }
                        if (playerCharacters[select].upAttackFrames_1.get(frame).drawable == null) {
                            playerCharacters[select].upAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].upAttackFrames_1.get(frame).bytes, 0, playerCharacters[select].upAttackFrames_1.get(frame).bytes.length));
                            playerCharacters[select].upAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp2":
                        if(frame >= playerCharacters[select].upAttackFrames_2.size()) {
                            return;
                        }
                        if (playerCharacters[select].upAttackFrames_2.get(frame).drawable == null) {
                            playerCharacters[select].upAttackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].upAttackFrames_2.get(frame).bytes, 0, playerCharacters[select].upAttackFrames_2.get(frame).bytes.length));
                            playerCharacters[select].upAttackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp3":
                        if(frame >= playerCharacters[select].upAttackFrames_3.size()) {
                            return;
                        }
                        if (playerCharacters[select].upAttackFrames_3.get(frame).drawable == null) {
                            playerCharacters[select].upAttackFrames_3.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].upAttackFrames_3.get(frame).bytes, 0, playerCharacters[select].upAttackFrames_3.get(frame).bytes.length));
                            playerCharacters[select].upAttackFrames_3.get(frame).isReady = true;
                        }
                        break;
                    case "crouch0":
                        if(frame >= playerCharacters[select].cDownIdleFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].cDownIdleFrames.get(frame).drawable == null) {
                            playerCharacters[select].cDownIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].cDownIdleFrames.get(frame).bytes, 0, playerCharacters[select].cDownIdleFrames.get(frame).bytes.length));
                            playerCharacters[select].cDownIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch1":
                        if(frame >= playerCharacters[select].cIdleFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].cIdleFrames.get(frame).drawable == null) {
                            playerCharacters[select].cIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].cIdleFrames.get(frame).bytes, 0, playerCharacters[select].cIdleFrames.get(frame).bytes.length));
                            playerCharacters[select].cIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch2":
                        if(frame >= playerCharacters[select].chargeFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].chargeFrames.get(frame).drawable == null) {
                            playerCharacters[select].chargeFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].chargeFrames.get(frame).bytes, 0, playerCharacters[select].chargeFrames.get(frame).bytes.length));
                            playerCharacters[select].chargeFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch3":
                        if(frame >= playerCharacters[select].cUpIdleFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].cUpIdleFrames.get(frame).drawable == null) {
                            playerCharacters[select].cUpIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].cUpIdleFrames.get(frame).bytes, 0, playerCharacters[select].cUpIdleFrames.get(frame).bytes.length));
                            playerCharacters[select].cUpIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "block0":
                        if(frame >= playerCharacters[select].blockFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].blockFrames.get(frame).drawable == null) {
                            playerCharacters[select].blockFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].blockFrames.get(frame).bytes, 0, playerCharacters[select].blockFrames.get(frame).bytes.length));
                            playerCharacters[select].blockFrames.get(frame).isReady = true;
                        }
                        break;
                    case "block1":
                        if(frame >= playerCharacters[select].blockToStandFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].blockToStandFrames.get(frame).drawable == null) {
                            playerCharacters[select].blockToStandFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].blockToStandFrames.get(frame).bytes, 0, playerCharacters[select].blockToStandFrames.get(frame).bytes.length));
                            playerCharacters[select].blockToStandFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw0":
                        if(frame >= playerCharacters[select].throwFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].throwFrames.get(frame).drawable == null) {
                            playerCharacters[select].throwFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].throwFrames.get(frame).bytes, 0, playerCharacters[select].throwFrames.get(frame).bytes.length));
                            playerCharacters[select].throwFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw1":
                        if(frame >= playerCharacters[select].throwMissFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].throwMissFrames.get(frame).drawable == null) {
                            playerCharacters[select].throwMissFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].throwMissFrames.get(frame).bytes, 0, playerCharacters[select].throwMissFrames.get(frame).bytes.length));
                            playerCharacters[select].throwMissFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw2":
                        if(frame >= playerCharacters[select].throwGrabFrames.size()) {
                            return;
                        }
                        if (playerCharacters[select].throwGrabFrames.get(frame).drawable == null) {
                            playerCharacters[select].throwGrabFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].throwGrabFrames.get(frame).bytes, 0, playerCharacters[select].throwGrabFrames.get(frame).bytes.length));
                            playerCharacters[select].throwGrabFrames.get(frame).isReady = true;
                        }
                        break;
                    case "hit0":
                        if(frame >= playerCharacters[select].hitStandMedium.size()) {
                            return;
                        }
                        if (playerCharacters[select].hitStandMedium.get(frame).drawable == null) {
                            playerCharacters[select].hitStandMedium.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].hitStandMedium.get(frame).bytes, 0, playerCharacters[select].hitStandMedium.get(frame).bytes.length));
                            playerCharacters[select].hitStandMedium.get(frame).isReady = true;
                        }
                        break;
                    case "hit1":
                        if(frame >= playerCharacters[select].hitStandHigh.size()) {
                            return;
                        }
                        if (playerCharacters[select].hitStandHigh.get(frame).drawable == null) {
                            playerCharacters[select].hitStandHigh.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].hitStandHigh.get(frame).bytes, 0, playerCharacters[select].hitStandHigh.get(frame).bytes.length));
                            playerCharacters[select].hitStandHigh.get(frame).isReady = true;
                        }
                        break;
                    case "hit2":
                        if(frame >= playerCharacters[select].hitStandLow.size()) {
                            return;
                        }
                        if (playerCharacters[select].hitStandLow.get(frame).drawable == null) {
                            playerCharacters[select].hitStandLow.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(playerCharacters[select].hitStandLow.get(frame).bytes, 0, playerCharacters[select].hitStandLow.get(frame).bytes.length));
                            playerCharacters[select].hitStandLow.get(frame).isReady = true;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch (myStatus) {
                    case "sIdle":
                        if (frame >= enemyCharacters[select].sIdleFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].sIdleFrames.get(frame).drawable == null) {
                            enemyCharacters[select].sIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].sIdleFrames.get(frame).bytes, 0, enemyCharacters[select].sIdleFrames.get(frame).bytes.length));
                            enemyCharacters[select].sIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "attack0":
                        if (frame >= enemyCharacters[select].attackFrames_0.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_0.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_0.get(frame).bytes, 0, enemyCharacters[select].attackFrames_0.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "attack1":
                        if (frame >= enemyCharacters[select].attackFrames_1.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_1.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_1.get(frame).bytes, 0, enemyCharacters[select].attackFrames_1.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "attack2":
                        if (frame >= enemyCharacters[select].attackFrames_2.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_2.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_2.get(frame).bytes, 0, enemyCharacters[select].attackFrames_2.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "attack3":
                        if (frame >= enemyCharacters[select].attackFrames_3.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_3.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_3.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_3.get(frame).bytes, 0, enemyCharacters[select].attackFrames_3.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_3.get(frame).isReady = true;
                        }
                        break;
                    case "attack4":
                        if (frame >= enemyCharacters[select].attackFrames_4.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_4.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_4.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_4.get(frame).bytes, 0, enemyCharacters[select].attackFrames_4.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_4.get(frame).isReady = true;
                        }
                        break;
                    case "attack5":
                        if (frame >= enemyCharacters[select].attackFrames_5.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_5.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_5.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_5.get(frame).bytes, 0, enemyCharacters[select].attackFrames_5.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_5.get(frame).isReady = true;
                        }
                        break;
                    case "attack6":
                        if (frame >= enemyCharacters[select].attackFrames_6.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_6.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_6.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_6.get(frame).bytes, 0, enemyCharacters[select].attackFrames_6.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_6.get(frame).isReady = true;
                        }
                        break;
                    case "attack7":
                        if (frame >= enemyCharacters[select].attackFrames_7.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_7.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_7.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_7.get(frame).bytes, 0, enemyCharacters[select].attackFrames_7.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_7.get(frame).isReady = true;
                        }
                        break;
                    case "attack8":
                        if (frame >= enemyCharacters[select].attackFrames_8.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_8.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_8.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_8.get(frame).bytes, 0, enemyCharacters[select].attackFrames_8.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_8.get(frame).isReady = true;
                        }
                        break;
                    case "attack9":
                        if (frame >= enemyCharacters[select].attackFrames_9.size()) {
                            return;
                        }
                        if (enemyCharacters[select].attackFrames_9.get(frame).drawable == null) {
                            enemyCharacters[select].attackFrames_9.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].attackFrames_9.get(frame).bytes, 0, enemyCharacters[select].attackFrames_9.get(frame).bytes.length));
                            enemyCharacters[select].attackFrames_9.get(frame).isReady = true;
                        }
                        break;
                    case "swipeBack0":
                        if(frame >= enemyCharacters[select].backFrames_0.size()) {
                            return;
                        }
                        if (enemyCharacters[select].backFrames_0.get(frame).drawable == null) {
                            enemyCharacters[select].backFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].backFrames_0.get(frame).bytes, 0, enemyCharacters[select].backFrames_0.get(frame).bytes.length));
                            enemyCharacters[select].backFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward0":
                        if(frame >= enemyCharacters[select].forwardAttackFrames_0.size()) {
                            return;
                        }
                        if (enemyCharacters[select].forwardAttackFrames_0.get(frame).drawable == null) {
                            enemyCharacters[select].forwardAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].forwardAttackFrames_0.get(frame).bytes, 0, enemyCharacters[select].forwardAttackFrames_0.get(frame).bytes.length));
                            enemyCharacters[select].forwardAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward1":
                        if(frame >= enemyCharacters[select].forwardAttackFrames_1.size()) {
                            return;
                        }
                        if (enemyCharacters[select].forwardAttackFrames_1.get(frame).drawable == null) {
                            enemyCharacters[select].forwardAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].forwardAttackFrames_1.get(frame).bytes, 0, enemyCharacters[select].forwardAttackFrames_1.get(frame).bytes.length));
                            enemyCharacters[select].forwardAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeForward2":
                        if(frame >= enemyCharacters[select].forwardAttackFrames_2.size()) {
                            return;
                        }
                        if (enemyCharacters[select].forwardAttackFrames_2.get(frame).drawable == null) {
                            enemyCharacters[select].forwardAttackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].forwardAttackFrames_2.get(frame).bytes, 0, enemyCharacters[select].forwardAttackFrames_2.get(frame).bytes.length));
                            enemyCharacters[select].forwardAttackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "swipeDown0":
                        if(frame >= enemyCharacters[select].downAttackFrames_0.size()) {
                            return;
                        }
                        if (enemyCharacters[select].downAttackFrames_0.get(frame).drawable == null) {
                            enemyCharacters[select].downAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].downAttackFrames_0.get(frame).bytes, 0, enemyCharacters[select].downAttackFrames_0.get(frame).bytes.length));
                            enemyCharacters[select].downAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeDown1":
                        if(frame >= enemyCharacters[select].downAttackFrames_1.size()) {
                            return;
                        }
                        if (enemyCharacters[select].downAttackFrames_1.get(frame).drawable == null) {
                            enemyCharacters[select].downAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].downAttackFrames_1.get(frame).bytes, 0, enemyCharacters[select].downAttackFrames_1.get(frame).bytes.length));
                            enemyCharacters[select].downAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp0":
                        if(frame >= enemyCharacters[select].upAttackFrames_0.size()) {
                            return;
                        }
                        if (enemyCharacters[select].upAttackFrames_0.get(frame).drawable == null) {
                            enemyCharacters[select].upAttackFrames_0.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].upAttackFrames_0.get(frame).bytes, 0, enemyCharacters[select].upAttackFrames_0.get(frame).bytes.length));
                            enemyCharacters[select].upAttackFrames_0.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp1":
                        if(frame >= enemyCharacters[select].upAttackFrames_1.size()) {
                            return;
                        }
                        if (enemyCharacters[select].upAttackFrames_1.get(frame).drawable == null) {
                            enemyCharacters[select].upAttackFrames_1.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].upAttackFrames_1.get(frame).bytes, 0, enemyCharacters[select].upAttackFrames_1.get(frame).bytes.length));
                            enemyCharacters[select].upAttackFrames_1.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp2":
                        if(frame >= enemyCharacters[select].upAttackFrames_2.size()) {
                            return;
                        }
                        if (enemyCharacters[select].upAttackFrames_2.get(frame).drawable == null) {
                            enemyCharacters[select].upAttackFrames_2.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].upAttackFrames_2.get(frame).bytes, 0, enemyCharacters[select].upAttackFrames_2.get(frame).bytes.length));
                            enemyCharacters[select].upAttackFrames_2.get(frame).isReady = true;
                        }
                        break;
                    case "swipeUp3":
                        if(frame >= enemyCharacters[select].upAttackFrames_3.size()) {
                            return;
                        }
                        if (enemyCharacters[select].upAttackFrames_3.get(frame).drawable == null) {
                            enemyCharacters[select].upAttackFrames_3.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].upAttackFrames_3.get(frame).bytes, 0, enemyCharacters[select].upAttackFrames_3.get(frame).bytes.length));
                            enemyCharacters[select].upAttackFrames_3.get(frame).isReady = true;
                        }
                        break;
                    case "crouch0":
                        if(frame >= enemyCharacters[select].cDownIdleFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].cDownIdleFrames.get(frame).drawable == null) {
                            enemyCharacters[select].cDownIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].cDownIdleFrames.get(frame).bytes, 0, enemyCharacters[select].cDownIdleFrames.get(frame).bytes.length));
                            enemyCharacters[select].cDownIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch1":
                        if(frame >= enemyCharacters[select].cIdleFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].cIdleFrames.get(frame).drawable == null) {
                            enemyCharacters[select].cIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].cIdleFrames.get(frame).bytes, 0, enemyCharacters[select].cIdleFrames.get(frame).bytes.length));
                            enemyCharacters[select].cIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch2":
                        if(frame >= enemyCharacters[select].chargeFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].chargeFrames.get(frame).drawable == null) {
                            enemyCharacters[select].chargeFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].chargeFrames.get(frame).bytes, 0, enemyCharacters[select].chargeFrames.get(frame).bytes.length));
                            enemyCharacters[select].chargeFrames.get(frame).isReady = true;
                        }
                        break;
                    case "crouch3":
                        if(frame >= enemyCharacters[select].cUpIdleFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].cUpIdleFrames.get(frame).drawable == null) {
                            enemyCharacters[select].cUpIdleFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].cUpIdleFrames.get(frame).bytes, 0, enemyCharacters[select].cUpIdleFrames.get(frame).bytes.length));
                            enemyCharacters[select].cUpIdleFrames.get(frame).isReady = true;
                        }
                        break;
                    case "block0":
                        if(frame >= enemyCharacters[select].blockFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].blockFrames.get(frame).drawable == null) {
                            enemyCharacters[select].blockFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].blockFrames.get(frame).bytes, 0, enemyCharacters[select].blockFrames.get(frame).bytes.length));
                            enemyCharacters[select].blockFrames.get(frame).isReady = true;
                        }
                        break;
                    case "block1":
                        if(frame >= enemyCharacters[select].blockToStandFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].blockToStandFrames.get(frame).drawable == null) {
                            enemyCharacters[select].blockToStandFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].blockToStandFrames.get(frame).bytes, 0, enemyCharacters[select].blockToStandFrames.get(frame).bytes.length));
                            enemyCharacters[select].blockToStandFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw0":
                        if(frame >= enemyCharacters[select].throwFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].throwFrames.get(frame).drawable == null) {
                            enemyCharacters[select].throwFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].throwFrames.get(frame).bytes, 0, enemyCharacters[select].throwFrames.get(frame).bytes.length));
                            enemyCharacters[select].throwFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw1":
                        if(frame >= enemyCharacters[select].throwMissFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].throwMissFrames.get(frame).drawable == null) {
                            enemyCharacters[select].throwMissFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].throwMissFrames.get(frame).bytes, 0, enemyCharacters[select].throwMissFrames.get(frame).bytes.length));
                            enemyCharacters[select].throwMissFrames.get(frame).isReady = true;
                        }
                        break;
                    case "throw2":
                        if(frame >= enemyCharacters[select].throwGrabFrames.size()) {
                            return;
                        }
                        if (enemyCharacters[select].throwGrabFrames.get(frame).drawable == null) {
                            enemyCharacters[select].throwGrabFrames.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].throwGrabFrames.get(frame).bytes, 0, enemyCharacters[select].throwGrabFrames.get(frame).bytes.length));
                            enemyCharacters[select].throwGrabFrames.get(frame).isReady = true;
                        }
                        break;
                    case "hit0":
                        if(frame >= enemyCharacters[select].hitStandMedium.size()) {
                            return;
                        }
                        if (enemyCharacters[select].hitStandMedium.get(frame).drawable == null) {
                            enemyCharacters[select].hitStandMedium.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].hitStandMedium.get(frame).bytes, 0, enemyCharacters[select].hitStandMedium.get(frame).bytes.length));
                            enemyCharacters[select].hitStandMedium.get(frame).isReady = true;
                        }
                        break;
                    case "hit1":
                        if(frame >= enemyCharacters[select].hitStandHigh.size()) {
                            return;
                        }
                        if (enemyCharacters[select].hitStandHigh.get(frame).drawable == null) {
                            enemyCharacters[select].hitStandHigh.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].hitStandHigh.get(frame).bytes, 0, enemyCharacters[select].hitStandHigh.get(frame).bytes.length));
                            enemyCharacters[select].hitStandHigh.get(frame).isReady = true;
                        }
                        break;
                    case "hit2":
                        if(frame >= enemyCharacters[select].hitStandLow.size()) {
                            return;
                        }
                        if (enemyCharacters[select].hitStandLow.get(frame).drawable == null) {
                            enemyCharacters[select].hitStandLow.get(frame).drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(enemyCharacters[select].hitStandLow.get(frame).bytes, 0, enemyCharacters[select].hitStandLow.get(frame).bytes.length));
                            enemyCharacters[select].hitStandLow.get(frame).isReady = true;
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void deleteDrawable(String player, int select, String myStatus, int frame) {
        if(frame != 0) {
            MyFrame previousFrame = null;
            BitmapDrawable previousBitmap = null;
            switch(player) {
                case "player":
                    switch(myStatus) {
                        case "sIdle":
                            if(frame < 0) {
                                frame = playerCharacters[select].sIdleFrames.size() - 1;
                            }
                            if(playerCharacters[select].sIdleFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].sIdleFrames.get(frame);
                            }
                            break;
                        case "attack0":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_0.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_0.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_0.get(frame);
                            }
                            break;
                        case "attack1":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_1.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_1.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_1.get(frame);
                            }
                            break;
                        case "attack2":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_2.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_2.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_2.get(frame);
                            }
                            break;
                        case "attack3":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_3.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_3.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_3.get(frame);
                            }
                            break;
                        case "attack4":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_4.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_4.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_4.get(frame);
                            }
                            break;
                        case "attack5":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_5.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_5.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_5.get(frame);
                            }
                            break;
                        case "attack6":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_6.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_6.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_6.get(frame);
                            }
                            break;
                        case "attack7":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_7.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_7.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_7.get(frame);
                            }
                            break;
                        case "attack8":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_8.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_8.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_8.get(frame);
                            }
                            break;
                        case "attack9":
                            if(frame < 0) {
                                frame = playerCharacters[select].attackFrames_9.size() - 1;
                            }
                            if(playerCharacters[select].attackFrames_9.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].attackFrames_9.get(frame);
                            }
                            break;
                        case "swipeBack0":
                            if(frame < 0) {
                                frame = playerCharacters[select].backFrames_0.size() - 1;
                            }
                            if(playerCharacters[select].backFrames_0.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].backFrames_0.get(frame);
                            }
                            break;
                        case "swipeForward0":
                            if(frame < 0) {
                                frame = playerCharacters[select].forwardAttackFrames_0.size() - 1;
                            }
                            if(playerCharacters[select].forwardAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].forwardAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeForward1":
                            if(frame < 0) {
                                frame = playerCharacters[select].forwardAttackFrames_1.size() - 1;
                            }
                            if(playerCharacters[select].forwardAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].forwardAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeForward2":
                            if(frame < 0) {
                                frame = playerCharacters[select].forwardAttackFrames_2.size() - 1;
                            }
                            if(playerCharacters[select].forwardAttackFrames_2.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].forwardAttackFrames_2.get(frame);
                            }
                            break;
                        case "swipeDown0":
                            if(frame < 0) {
                                frame = playerCharacters[select].downAttackFrames_0.size() - 1;
                            }
                            if(playerCharacters[select].downAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].downAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeDown1":
                            if(frame < 0) {
                                frame = playerCharacters[select].downAttackFrames_1.size() - 1;
                            }
                            if(playerCharacters[select].downAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].downAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeUp0":
                            if(frame < 0) {
                                frame = playerCharacters[select].upAttackFrames_0.size() - 1;
                            }
                            if(playerCharacters[select].upAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].upAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeUp1":
                            if(frame < 0) {
                                frame = playerCharacters[select].upAttackFrames_1.size() - 1;
                            }
                            if(playerCharacters[select].upAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].upAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeUp2":
                            if(frame < 0) {
                                frame = playerCharacters[select].upAttackFrames_2.size() - 1;
                            }
                            if(playerCharacters[select].upAttackFrames_2.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].upAttackFrames_2.get(frame);
                            }
                            break;
                        case "swipeUp3":
                            if(frame < 0) {
                                frame = playerCharacters[select].upAttackFrames_3.size() - 1;
                            }
                            if(playerCharacters[select].upAttackFrames_3.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].upAttackFrames_3.get(frame);
                            }
                            break;
                        case "crouch0":
                            if(frame < 0) {
                                frame = playerCharacters[select].cDownIdleFrames.size() - 1;
                            }
                            if(playerCharacters[select].cDownIdleFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].cDownIdleFrames.get(frame);
                            }
                            break;
                        case "crouch1":
                            if(frame < 0) {
                                frame = playerCharacters[select].cIdleFrames.size() - 1;
                            }
                            if(playerCharacters[select].cIdleFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].cIdleFrames.get(frame);
                            }
                            break;
                        case "crouch2":
                            if(frame < 0) {
                                frame = playerCharacters[select].chargeFrames.size() - 1;
                            }
                            if(playerCharacters[select].chargeFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].chargeFrames.get(frame);
                            }
                            break;
                        case "crouch3":
                            if(frame < 0) {
                                frame = playerCharacters[select].cUpIdleFrames.size() - 1;
                            }
                            if(playerCharacters[select].cUpIdleFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].cUpIdleFrames.get(frame);
                            }
                            break;
                        case "block0":
                            if(frame < 0) {
                                frame = playerCharacters[select].blockFrames.size() - 1;
                            }
                            if(playerCharacters[select].blockFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].blockFrames.get(frame);
                            }
                            break;
                        case "block1":
                            if(playerCharacters[select].blockToStandFrames.size() >= 3) {
                                if (frame < 0) {
                                    frame = playerCharacters[select].blockToStandFrames.size() - 1;
                                }
                                if (playerCharacters[select].blockToStandFrames.get(frame).drawable != null) {
                                    previousFrame = playerCharacters[select].blockToStandFrames.get(frame);
                                }
                            }
                            break;
                        case "throw0":
                            if(frame < 0) {
                                frame = playerCharacters[select].throwFrames.size() - 1;
                            }
                            if (playerCharacters[select].throwFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].throwFrames.get(frame);
                            }
                            break;
                        case "throw1":
                            if(frame < 0) {
                                frame = playerCharacters[select].throwMissFrames.size() - 1;
                            }
                            if (playerCharacters[select].throwMissFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].throwMissFrames.get(frame);
                            }
                            break;
                        case "throw2":
                            if(frame < 0) {
                                frame = playerCharacters[select].throwGrabFrames.size() - 1;
                            }
                            if (playerCharacters[select].throwGrabFrames.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].throwGrabFrames.get(frame);
                            }
                            break;
                        case "hit0":
                            if(frame < 0) {
                                frame = playerCharacters[select].hitStandMedium.size() - 1;
                            }
                            if (playerCharacters[select].hitStandMedium.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].hitStandMedium.get(frame);
                            }
                            break;
                        case "hit1":
                            if(frame < 0) {
                                frame = playerCharacters[select].hitStandHigh.size() - 1;
                            }
                            if (playerCharacters[select].hitStandHigh.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].hitStandHigh.get(frame);
                            }
                            break;
                        case "hit2":
                            if(frame < 0) {
                                frame = playerCharacters[select].hitStandLow.size() - 1;
                            }
                            if (playerCharacters[select].hitStandLow.get(frame).drawable != null) {
                                previousFrame = playerCharacters[select].hitStandLow.get(frame);
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case "enemy":
                    switch(myStatus) {
                        case "sIdle":
                            if(frame < 0) {
                                frame = enemyCharacters[select].sIdleFrames.size() - 1;
                            }
                            if(enemyCharacters[select].sIdleFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].sIdleFrames.get(frame);
                            }
                            break;
                        case "attack0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_0.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_0.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_0.get(frame);
                            }
                            break;
                        case "attack1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_1.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_1.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_1.get(frame);
                            }
                            break;
                        case "attack2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_2.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_2.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_2.get(frame);
                            }
                            break;
                        case "attack3":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_3.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_3.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_3.get(frame);
                            }
                            break;
                        case "attack4":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_4.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_4.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_4.get(frame);
                            }
                            break;
                        case "attack5":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_5.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_5.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_5.get(frame);
                            }
                            break;
                        case "attack6":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_6.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_6.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_6.get(frame);
                            }
                            break;
                        case "attack7":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_7.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_7.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_7.get(frame);
                            }
                            break;
                        case "attack8":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_8.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_8.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_8.get(frame);
                            }
                            break;
                        case "attack9":
                            if(frame < 0) {
                                frame = enemyCharacters[select].attackFrames_9.size() - 1;
                            }
                            if(enemyCharacters[select].attackFrames_9.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].attackFrames_9.get(frame);
                            }
                            break;
                        case "swipeBack0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].backFrames_0.size() - 1;
                            }
                            if(enemyCharacters[select].backFrames_0.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].backFrames_0.get(frame);
                            }
                            break;
                        case "swipeForward0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].forwardAttackFrames_0.size() - 1;
                            }
                            if(enemyCharacters[select].forwardAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].forwardAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeForward1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].forwardAttackFrames_1.size() - 1;
                            }
                            if(enemyCharacters[select].forwardAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].forwardAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeForward2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].forwardAttackFrames_2.size() - 1;
                            }
                            if(enemyCharacters[select].forwardAttackFrames_2.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].forwardAttackFrames_2.get(frame);
                            }
                            break;
                        case "swipeDown0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].downAttackFrames_0.size() - 1;
                            }
                            if(enemyCharacters[select].downAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].downAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeDown1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].downAttackFrames_1.size() - 1;
                            }
                            if(enemyCharacters[select].downAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].downAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeUp0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].upAttackFrames_0.size() - 1;
                            }
                            if(enemyCharacters[select].upAttackFrames_0.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].upAttackFrames_0.get(frame);
                            }
                            break;
                        case "swipeUp1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].upAttackFrames_1.size() - 1;
                            }
                            if(enemyCharacters[select].upAttackFrames_1.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].upAttackFrames_1.get(frame);
                            }
                            break;
                        case "swipeUp2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].upAttackFrames_2.size() - 1;
                            }
                            if(enemyCharacters[select].upAttackFrames_2.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].upAttackFrames_2.get(frame);
                            }
                            break;
                        case "swipeUp3":
                            if(frame < 0) {
                                frame = enemyCharacters[select].upAttackFrames_3.size() - 1;
                            }
                            if(enemyCharacters[select].upAttackFrames_3.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].upAttackFrames_3.get(frame);
                            }
                            break;
                        case "crouch0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].cDownIdleFrames.size() - 1;
                            }
                            if(enemyCharacters[select].cDownIdleFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].cDownIdleFrames.get(frame);
                            }
                            break;
                        case "crouch1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].cIdleFrames.size() - 1;
                            }
                            if(enemyCharacters[select].cIdleFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].cIdleFrames.get(frame);
                            }
                            break;
                        case "crouch2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].chargeFrames.size() - 1;
                            }
                            if(enemyCharacters[select].chargeFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].chargeFrames.get(frame);
                            }
                            break;
                        case "crouch3":
                            if(frame < 0) {
                                frame = enemyCharacters[select].cUpIdleFrames.size() - 1;
                            }
                            if(enemyCharacters[select].cUpIdleFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].cUpIdleFrames.get(frame);
                            }
                            break;
                        case "block0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].blockFrames.size() - 1;
                            }
                            if(enemyCharacters[select].blockFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].blockFrames.get(frame);
                            }
                            break;
                        case "block1":
                            if(enemyCharacters[select].blockToStandFrames.size() >= 3) {
                                if (frame < 0) {
                                    frame = enemyCharacters[select].blockToStandFrames.size() - 1;
                                }
                                if (enemyCharacters[select].blockToStandFrames.get(frame).drawable != null) {
                                    previousFrame = enemyCharacters[select].blockToStandFrames.get(frame);
                                }
                            }
                            break;
                        case "throw0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].throwFrames.size() - 1;
                            }
                            if (enemyCharacters[select].throwFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].throwFrames.get(frame);
                            }
                            break;
                        case "throw1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].throwMissFrames.size() - 1;
                            }
                            if (enemyCharacters[select].throwMissFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].throwMissFrames.get(frame);
                            }
                            break;
                        case "throw2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].throwGrabFrames.size() - 1;
                            }
                            if (enemyCharacters[select].throwGrabFrames.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].throwGrabFrames.get(frame);
                            }
                            break;
                        case "hit0":
                            if(frame < 0) {
                                frame = enemyCharacters[select].hitStandMedium.size() - 1;
                            }
                            if (enemyCharacters[select].hitStandMedium.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].hitStandMedium.get(frame);
                            }
                            break;
                        case "hit1":
                            if(frame < 0) {
                                frame = enemyCharacters[select].hitStandHigh.size() - 1;
                            }
                            if (enemyCharacters[select].hitStandHigh.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].hitStandHigh.get(frame);
                            }
                            break;
                        case "hit2":
                            if(frame < 0) {
                                frame = enemyCharacters[select].hitStandLow.size() - 1;
                            }
                            if (enemyCharacters[select].hitStandLow.get(frame).drawable != null) {
                                previousFrame = enemyCharacters[select].hitStandLow.get(frame);
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (previousFrame != null) {
                previousBitmap = (BitmapDrawable) previousFrame.drawable;
                previousBitmap.getBitmap().recycle();
                previousFrame.drawable = null;
                previousFrame.isReady = false;
            }
        }
    }

    private void alignCharacter(String player, int select, int x, int y) {
        switch(player) {
            case "player":
                switch(select) {
                    case 0:
                        Log.i("align", x + " , " + y);
                        player1.setX(player1.getX() + x);
                        player1.setY(player1.getY() + y);
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch(select) {
                    case 0:
                        enemy1.setX(enemy1.getX() + x);
                        enemy1.setY(enemy1.getY() + y);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void alignGround(String player, int select) {
        switch(player) {
            case "player":
                switch (select) {
                    case 0:
                        player1.setY(player1.getY() - playerCharacters[select].location_up);
                        playerCharacters[select].location_up = 0;
                        break;
                    default:
                        break;
                }
                break;
            case "enemy":
                switch(select) {
                    case 0:
                        enemy1.setY(enemy1.getY() - enemyCharacters[select].location_up);
                        enemyCharacters[select].location_up = 0;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void moveCharacter(String player, int select, boolean ground, int x, int y) {
        int remaining;
        switch(player) {
            case "player":
                switch(select) {
                    case 0:
                        if(x >= 0) {
                            // Moving Right
                            if(enemyCharacters[enemy].location_left <= screen_width / 2) {
                                //move character
                                if(playerCharacters[select].location_left + playerCharacters[select].idle_align_frame_x + x
                                        < enemyCharacters[enemy].location_left) {
                                    // space to move
                                    player1.setX(player1.getX() + x);
                                    playerCharacters[select].location_left += x;
                                } else {
                                    // no space or small amounts
                                    remaining = (enemyCharacters[enemy].location_left) - (playerCharacters[select].location_left + playerCharacters[select].idle_align_frame_x);
                                    player1.setX(player1.getX() + remaining);
                                    playerCharacters[select].location_left += remaining;
                                }
                            } else {
                                //move background and character possibly...
                                if(background.getX() - x <= (-1 * screen_width * screen_multiplier) + screen_width) {
                                    //end of screen
                                    background.setX((-1 * screen_width * screen_multiplier) + screen_width);
                                    if(playerCharacters[select].location_left + playerCharacters[select].idle_align_frame_x + x >=
                                            enemyCharacters[enemy].location_left) {
                                        //move to enemy
                                        remaining = enemyCharacters[enemy].location_left - (playerCharacters[select].location_left + playerCharacters[select].idle_align_frame_x);
                                        playerCharacters[select].location_left += remaining;
                                        player1.setX(player1.getX() + remaining);
                                    } else {
                                        playerCharacters[select].location_left += x;
                                        player1.setX(player1.getX() + x);
                                    }
                                } else {
                                    //space left
                                    if(enemyCharacters[enemy].location_left - x <= screen_width / 2) {
                                        //enemy in middle
                                        remaining = enemyCharacters[enemy].location_left - (screen_width / 2);
                                        enemyCharacters[enemy].location_left -= remaining;
                                        enemy1.setX(enemy1.getX() - remaining);
                                        background.setX(background.getX() - remaining);

                                        if(playerCharacters[select].location_left + x >= screen_width / 2) {
                                            remaining = (screen_width / 2) - playerCharacters[select].location_left ;
                                            playerCharacters[select].location_left += remaining;
                                            player1.setX(player1.getX() + remaining);
                                        } else {
                                            playerCharacters[select].location_left += x;
                                            player1.setX(player1.getX() + x);
                                        }
                                    } else {
                                        enemyCharacters[enemy].location_left -= x;
                                        enemy1.setX(enemy1.getX() - x);
                                        background.setX(background.getX() - x);

                                        if(playerCharacters[select].location_left + x >= screen_width / 2) {
                                            remaining = (screen_width / 2) - playerCharacters[select].location_left ;
                                            playerCharacters[select].location_left += remaining;
                                            player1.setX(player1.getX() + remaining);
                                        } else {
                                            playerCharacters[select].location_left += x;
                                            player1.setX(player1.getX() + x);
                                        }
                                    }
                                }
                            }
                            /*if (background.getX() - x <= (-1 * screen_width * screen_multiplier) + screen_width) {
                                remaining = (int) (background.getX() - ((-1 * screen_width * screen_multiplier) + screen_width));
                                remaining = x - remaining;
                                background.setX((-1 * screen_width * screen_multiplier) + screen_width);
                                if(remaining != 0 && playerCharacters[select].location_left + remaining >= screen_width - (screen_margin + playerCharacters[select].idle_align_frame_x)) {
                                    remaining = ((screen_width - (screen_margin + playerCharacters[select].idle_align_frame_x)) - playerCharacters[select].location_left);
                                    player1.setX(player1.getX() + remaining);
                                    playerCharacters[select].location_left = (screen_width - (playerCharacters[select].idle_align_frame_x + screen_margin));
                                } else {
                                    player1.setX(player1.getX() + remaining);
                                    playerCharacters[select].location_left += remaining;
                                }
                            } else {
                                background.setX(background.getX() - x);
                            }*/
                        } else if (x <= 0) {
                            // Moving Left
                            if(enemyCharacters[enemy].location_left + enemyCharacters[enemy].idle_align_frame_x
                                    >= (screen_width - screen_margin)) {
                                //enemy is at edge
                                if(playerCharacters[select].location_left + x <= screen_margin) {
                                    remaining = screen_margin - playerCharacters[select].location_left;
                                    playerCharacters[select].location_left += remaining;
                                    player1.setX(player1.getX() + remaining);
                                } else {
                                    playerCharacters[select].location_left += x;
                                    player1.setX(player1.getX() + x);
                                }
                            } else {
                                if(background.getX() - x >= 0) {
                                    // left screen edge
                                    background.setX(0);
                                    if(playerCharacters[select].location_left + x <= screen_margin) {
                                        remaining = screen_margin - playerCharacters[select].location_left;
                                        playerCharacters[select].location_left += remaining;
                                        player1.setX(player1.getX() + remaining);
                                    } else {
                                        playerCharacters[select].location_left += x;
                                        player1.setX(player1.getX() + x);
                                    }
                                } else {
                                    if(enemyCharacters[enemy].location_left + enemyCharacters[enemy].idle_align_frame_x - x
                                            >= screen_width - screen_margin) {
                                        remaining = (screen_width - screen_margin) - (enemyCharacters[enemy].location_left + enemyCharacters[enemy].idle_align_frame_x);
                                        enemyCharacters[enemy].location_left += remaining;
                                        enemy1.setX(enemy1.getX() + remaining);
                                        background.setX(background.getX() + remaining);

                                        if(playerCharacters[select].location_left + x <= screen_margin) {
                                            remaining = screen_margin - playerCharacters[select].location_left;
                                            playerCharacters[select].location_left += remaining;
                                            player1.setX(player1.getX() + remaining);
                                        } else {
                                            playerCharacters[select].location_left += x;
                                            player1.setX(player1.getX() + x);
                                        }

                                    } else {
                                        enemyCharacters[enemy].location_left -= x;
                                        enemy1.setX(enemy1.getX() - x);
                                        background.setX(background.getX() - x);

                                        if(playerCharacters[select].location_left + x <= screen_margin) {
                                            remaining = screen_margin - playerCharacters[select].location_left;
                                            playerCharacters[select].location_left += remaining;
                                            player1.setX(player1.getX() + remaining);
                                        } else {
                                            playerCharacters[select].location_left += x;
                                            player1.setX(player1.getX() + x);
                                        }
                                    }
                                }
                            }
                            /*
                            if (playerCharacters[select].location_left + x <= screen_margin) {

                                remaining = screen_margin - playerCharacters[select].location_left;
                                player1.setX(player1.getX() + remaining);
                                remaining = x - remaining;
                                playerCharacters[select].location_left = screen_margin;
                                if(remaining != 0 && background.getX() - remaining >= 0) {
                                    background.setX(0);
                                } else {
                                    background.setX(background.getX() - remaining);
                                }
                            } else {
                                player1.setX(player1.getX() + x);
                                playerCharacters[select].location_left += x;

                            }
                            */
                        } else {
                            //Neither
                        }
                        if(ground) {
                            player1.setY(player1.getY() + y);
                            playerCharacters[select].location_up += y;
                        } else {
                            // only move background if we are the lowest //
                            background.setY(background.getY() - y);
                        }
                        //player1.setY(player1.getY() + y);
                        //playerCharacters[select].location_up += y;
                        /*
                        player1.setX(player1.getX() + x);
                        player1.setY(player1.getY() + y);
                        playerCharacters[select].location_left += x;
                        playerCharacters[select].location_up += y;
                        if(player1.getX() <= screen_margin) {
                            player1.setX(screen_margin);
                        } else if (player1.getX() >= screen_width - (screen_margin + playerCharacters[select].idle_align_frame_x)) {
                            player1.setX(screen_width - (playerCharacters[select].idle_align_frame_x + screen_margin));
                        }
                        */
                        break;
                }
                break;
        }
    }

    private void runAnimation() {
        runPlayer1 = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runPlayer1);
                int duration = 0;
                deleteDrawable("player", 0, playerCharacters[0].lastStatus, playerCharacters[0].lastFrame);
                if(playerCharacters[0].blocking) {
                    switch(playerCharacters[0].status) {
                        case "sIdle":
                            playerCharacters[0].status = "block0";
                            playerCharacters[0].currentFrame = 0;
                            break;
                        default:
                            break;
                    }
                }
                if(playerCharacters[0].immediate || (playerCharacters[0].nextStatus != null && playerCharacters[0].currentFrame == playerCharacters[0].nextFrame)) {
                    playerCharacters[0].immediate = false;
                    playerCharacters[0].status = playerCharacters[0].nextStatus;
                    playerCharacters[0].nextStatus = null;
                    playerCharacters[0].currentFrame = 0;
                }
                if(playerCharacters[0].currentFrame == 0) {
                    switch(playerCharacters[0].lastStatus) {
                        case "attack0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[0], 0 - playerCharacters[0].attack_align_frame_y[0]);
                            break;
                        case "attack1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[1], 0 - playerCharacters[0].attack_align_frame_y[1]);
                            break;
                        case "attack2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[2], 0 - playerCharacters[0].attack_align_frame_y[2]);
                            break;
                        case "attack3":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[3], 0 - playerCharacters[0].attack_align_frame_y[3]);
                            break;
                        case "attack4":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[4], 0 - playerCharacters[0].attack_align_frame_y[4]);
                            break;
                        case "attack5":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[5], 0 - playerCharacters[0].attack_align_frame_y[5]);
                            break;
                        case "attack6":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[6], 0 - playerCharacters[0].attack_align_frame_y[6]);
                            break;
                        case "attack7":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[7], 0 - playerCharacters[0].attack_align_frame_y[7]);
                            break;
                        case "attack8":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[8], 0 - playerCharacters[0].attack_align_frame_y[8]);
                            break;
                        case "attack9":
                            alignCharacter("player", 0, 0 - playerCharacters[0].attack_align_frame_x[9], 0 - playerCharacters[0].attack_align_frame_y[9]);
                            break;
                        case "swipeBack0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_back_align_frame_x[0], 0 - playerCharacters[0].swipe_back_align_frame_y[0]);
                            break;
                        case "swipeForward0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_forward_align_frame_x[0], 0 - playerCharacters[0].swipe_forward_align_frame_y[0]);
                            break;
                        case "swipeForward1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_forward_align_frame_x[1], 0 - playerCharacters[0].swipe_forward_align_frame_y[1]);
                            break;
                        case "swipeForward2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_forward_align_frame_x[2], 0 - playerCharacters[0].swipe_forward_align_frame_y[2]);
                            break;
                        case "swipeDown0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_down_align_frame_x[0], 0 - playerCharacters[0].swipe_down_align_frame_y[0]);
                            break;
                        case "swipeDown1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_down_align_frame_x[1], 0 - playerCharacters[0].swipe_down_align_frame_y[0]);
                            break;
                        case "swipeUp0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_up_align_frame_x[0], 0 - playerCharacters[0].swipe_up_align_frame_y[0]);
                            break;
                        case "swipeUp1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_up_align_frame_x[1], 0 - playerCharacters[0].swipe_up_align_frame_y[1]);
                            break;
                        case "swipeUp2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_up_align_frame_x[2], 0 - playerCharacters[0].swipe_up_align_frame_y[2]);
                            break;
                        case "swipeUp3":
                            alignCharacter("player", 0, 0 - playerCharacters[0].swipe_up_align_frame_x[3], 0 - playerCharacters[0].swipe_up_align_frame_y[3]);
                            break;
                        case "block0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].block_align_frame_x[0], 0 - playerCharacters[0].block_align_frame_y[0]);
                            break;
                        case "block1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].block_align_frame_x[1], 0 - playerCharacters[0].block_align_frame_y[1]);
                            break;
                        case "crouch0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].crouch_align_frame_x[0], 0 - playerCharacters[0].crouch_align_frame_y[0]);
                            break;
                        case "crouch1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].crouch_align_frame_x[1], 0 - playerCharacters[0].crouch_align_frame_y[1]);
                            break;
                        case "crouch2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].crouch_align_frame_x[2], 0 - playerCharacters[0].crouch_align_frame_y[2]);
                            break;
                        case "crouch3":
                            alignCharacter("player", 0, 0 - playerCharacters[0].crouch_align_frame_x[3], 0 - playerCharacters[0].crouch_align_frame_y[3]);
                            break;
                        case "throw0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].grab_align_frame_x[0], 0 - playerCharacters[0].grab_align_frame_y[0]);
                            break;
                        case "throw1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].grab_align_frame_x[1], 0 - playerCharacters[0].grab_align_frame_y[1]);
                            break;
                        case "throw2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].grab_align_frame_x[2], 0 - playerCharacters[0].grab_align_frame_y[2]);
                            break;
                        case "hit0":
                            alignCharacter("player", 0, 0 - playerCharacters[0].hit_align_frame_x[0], 0 - playerCharacters[0].hit_align_frame_y[0]);
                            break;
                        case "hit1":
                            alignCharacter("player", 0, 0 - playerCharacters[0].hit_align_frame_x[1], 0 - playerCharacters[0].hit_align_frame_y[1]);
                            break;
                        case "hit2":
                            alignCharacter("player", 0, 0 - playerCharacters[0].hit_align_frame_x[2], 0 - playerCharacters[0].hit_align_frame_y[2]);
                            break;
                        default:
                            break;
                    }
                }
                switch(playerCharacters[0].status) {
                    case "sIdle":
                        player1.setImageDrawable(playerCharacters[0].sIdleFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].sIdleFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].idle_align_frame_x;
                            player1.getLayoutParams().height = playerCharacters[0].idle_align_frame_y;
                            Log.d("idle", player1.getWidth() + " , " + player1.getHeight());
                            playerCharacters[0].totalFrames = playerCharacters[0].sIdleFrames.size();
                        }
                        break;
                    case "attack0":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_0.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[0],
                                    playerCharacters[0].attack_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).move_y);
                        if(playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).attack) {
                            if(player1.getX() +
                                    playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).hit_x +
                                    playerCharacters[0].attackFrames_0.get(playerCharacters[0].currentFrame).hit_width >= enemy1.getX()) {
                                enemyCharacters[0].immediate = true;
                                enemyCharacters[0].nextStatus = "hit0";
                            }
                        }
                        break;
                    case "attack1":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_1.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[1],
                                    playerCharacters[0].attack_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).move_y);
                        if(playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).attack) {
                            if(player1.getX() +
                                    playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).hit_x +
                                    playerCharacters[0].attackFrames_1.get(playerCharacters[0].currentFrame).hit_width >= enemy1.getX()) {
                                enemyCharacters[0].immediate = true;
                                enemyCharacters[0].nextStatus = "hit1";
                            }
                        }
                        break;
                    case "attack2":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_2.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[2],
                                    playerCharacters[0].attack_align_frame_y[2]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).move_y);
                        if(playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).attack) {
                            if(player1.getX() +
                                    playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).hit_x +
                                    playerCharacters[0].attackFrames_2.get(playerCharacters[0].currentFrame).hit_width >= enemy1.getX()) {
                                enemyCharacters[0].immediate = true;
                                enemyCharacters[0].nextStatus = "hit2";
                            }
                        }
                        break;
                    case "attack3":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[3];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[3];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_3.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[3],
                                    playerCharacters[0].attack_align_frame_y[3]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).move_x,
                            playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).move_y);
                        if(playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).attack) {
                            if(player1.getX() +
                                    playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).hit_x +
                                    playerCharacters[0].attackFrames_3.get(playerCharacters[0].currentFrame).hit_width >= enemy1.getX()) {
                                enemyCharacters[0].immediate = true;
                                enemyCharacters[0].nextStatus = "hit1";
                            }
                        }
                        break;
                    case "attack4":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[4];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[4];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_4.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[4],
                                    playerCharacters[0].attack_align_frame_y[4]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).move_y);
                        if(playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).attack) {
                            if(player1.getX() +
                                    playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).hit_x +
                                    playerCharacters[0].attackFrames_4.get(playerCharacters[0].currentFrame).hit_width >= enemy1.getX()) {
                                enemyCharacters[0].immediate = true;
                                enemyCharacters[0].nextStatus = "hit0";
                            }
                        }
                        break;
                    case "attack5":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_5.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_5.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[5];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[5];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_5.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[5],
                                    playerCharacters[0].attack_align_frame_y[5]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_5.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_5.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "attack6":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_6.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_6.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[6];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[6];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_6.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[6],
                                    playerCharacters[0].attack_align_frame_y[6]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_6.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_6.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "attack7":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_7.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_7.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[7];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[7];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_7.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[7],
                                    playerCharacters[0].attack_align_frame_y[7]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_7.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_7.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "attack8":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_8.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_8.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[8];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[8];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_8.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[8],
                                    playerCharacters[0].attack_align_frame_y[8]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_8.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_8.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "attack9":
                        player1.setImageDrawable(playerCharacters[0].attackFrames_9.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].attackFrames_9.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].attack_width[9];
                            player1.getLayoutParams().height = playerCharacters[0].attack_height[9];
                            playerCharacters[0].totalFrames = playerCharacters[0].attackFrames_9.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].attack_align_frame_x[9],
                                    playerCharacters[0].attack_align_frame_y[9]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].attackFrames_9.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].attackFrames_9.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "crouch0":
                        player1.setImageDrawable(playerCharacters[0].cDownIdleFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].cDownIdleFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].crouch_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].crouch_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].cDownIdleFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].crouch_align_frame_x[0],
                                    playerCharacters[0].crouch_align_frame_y[0]);
                        }
                        break;
                    case "crouch1":
                        player1.setImageDrawable(playerCharacters[0].cIdleFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].cIdleFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].crouch_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].crouch_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].cIdleFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].crouch_align_frame_x[1],
                                    playerCharacters[0].crouch_align_frame_y[1]);
                        }
                        break;
                    case "crouch2":
                        player1.setImageDrawable(playerCharacters[0].chargeFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].chargeFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].crouch_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].crouch_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].chargeFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].crouch_align_frame_x[2],
                                    playerCharacters[0].crouch_align_frame_y[2]);
                        }
                        break;
                    case "crouch3":
                        player1.setImageDrawable(playerCharacters[0].cUpIdleFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].cUpIdleFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].crouch_width[3];
                            player1.getLayoutParams().height = playerCharacters[0].crouch_height[3];
                            playerCharacters[0].totalFrames = playerCharacters[0].cUpIdleFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].crouch_align_frame_x[3],
                                    playerCharacters[0].crouch_align_frame_y[3]);
                        }
                        break;
                    case "swipeBack0":
                        player1.setImageDrawable(playerCharacters[0].backFrames_0.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].backFrames_0.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_back_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_back_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].backFrames_0.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_back_align_frame_x[0],
                                    playerCharacters[0].swipe_back_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].backFrames_0.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].backFrames_0.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeForward0":
                        player1.setImageDrawable(playerCharacters[0].forwardAttackFrames_0.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].forwardAttackFrames_0.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_forward_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_forward_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].forwardAttackFrames_0.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_forward_align_frame_x[0],
                                    playerCharacters[0].swipe_forward_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].forwardAttackFrames_0.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].forwardAttackFrames_0.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeForward1":
                        player1.setImageDrawable(playerCharacters[0].forwardAttackFrames_1.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].forwardAttackFrames_1.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_forward_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_forward_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].forwardAttackFrames_1.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_forward_align_frame_x[1],
                                    playerCharacters[0].swipe_forward_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].forwardAttackFrames_1.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].forwardAttackFrames_1.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeForward2":
                        player1.setImageDrawable(playerCharacters[0].forwardAttackFrames_2.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].forwardAttackFrames_2.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_forward_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_forward_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].forwardAttackFrames_2.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_forward_align_frame_x[2],
                                    playerCharacters[0].swipe_forward_align_frame_y[2]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].forwardAttackFrames_2.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].forwardAttackFrames_2.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeDown0":
                        player1.setImageDrawable(playerCharacters[0].downAttackFrames_0.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].downAttackFrames_0.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_down_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_down_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].downAttackFrames_0.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_down_align_frame_x[0],
                                    playerCharacters[0].swipe_down_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].downAttackFrames_0.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].downAttackFrames_0.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeDown1":
                        player1.setImageDrawable(playerCharacters[0].downAttackFrames_1.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].downAttackFrames_1.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_down_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_down_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].downAttackFrames_1.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_down_align_frame_x[1],
                                    playerCharacters[0].swipe_down_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].downAttackFrames_1.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].downAttackFrames_1.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeUp0":
                        player1.setImageDrawable(playerCharacters[0].upAttackFrames_0.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].upAttackFrames_0.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_up_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_up_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].upAttackFrames_0.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_up_align_frame_x[0],
                                    playerCharacters[0].swipe_up_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, false, playerCharacters[0].upAttackFrames_0.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].upAttackFrames_0.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeUp1":
                        player1.setImageDrawable(playerCharacters[0].upAttackFrames_1.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].upAttackFrames_1.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_up_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_up_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].upAttackFrames_1.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_up_align_frame_x[1],
                                    playerCharacters[0].swipe_up_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, false, playerCharacters[0].upAttackFrames_1.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].upAttackFrames_1.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeUp2":
                        player1.setImageDrawable(playerCharacters[0].upAttackFrames_2.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].upAttackFrames_2.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_up_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_up_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].upAttackFrames_2.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_up_align_frame_x[2],
                                    playerCharacters[0].swipe_up_align_frame_y[2]);
                        }
                        moveCharacter("player", 0, false, playerCharacters[0].upAttackFrames_2.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].upAttackFrames_2.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "swipeUp3":
                        player1.setImageDrawable(playerCharacters[0].upAttackFrames_3.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].upAttackFrames_3.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].swipe_up_width[3];
                            player1.getLayoutParams().height = playerCharacters[0].swipe_up_height[3];
                            playerCharacters[0].totalFrames = playerCharacters[0].upAttackFrames_3.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].swipe_up_align_frame_x[3],
                                    playerCharacters[0].swipe_up_align_frame_y[3]);
                        }
                        moveCharacter("player", 0, false, playerCharacters[0].upAttackFrames_3.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].upAttackFrames_3.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "block0":
                        player1.setImageDrawable(playerCharacters[0].blockFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].blockFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].block_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].block_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].blockFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].block_align_frame_x[0],
                                    playerCharacters[0].block_align_frame_y[0]);
                        }
                        break;
                    case "block1":
                        player1.setImageDrawable(playerCharacters[0].blockToStandFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].blockToStandFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].block_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].block_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].blockToStandFrames.size();
                            alignCharacter("player", 0,
                                    playerCharacters[0].block_align_frame_x[1],
                                    playerCharacters[0].block_align_frame_y[1]);
                        }
                        break;
                    case "throw0":
                        player1.setImageDrawable(playerCharacters[0].throwFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].throwFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].grab_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].grab_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].throwFrames.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].grab_align_frame_x[0],
                                    playerCharacters[0].grab_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].throwFrames.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].throwFrames.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "throw1":
                        player1.setImageDrawable(playerCharacters[0].throwMissFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].throwMissFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].grab_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].grab_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].throwMissFrames.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].grab_align_frame_x[1],
                                    playerCharacters[0].grab_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].throwMissFrames.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].throwMissFrames.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "throw2":
                        player1.setImageDrawable(playerCharacters[0].throwGrabFrames.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].throwGrabFrames.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].grab_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].grab_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].throwGrabFrames.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].grab_align_frame_x[2],
                                    playerCharacters[0].grab_align_frame_y[2]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].throwGrabFrames.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].throwGrabFrames.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "hit0":
                        player1.setImageDrawable(playerCharacters[0].hitStandMedium.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].hitStandMedium.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].hit_width[0];
                            player1.getLayoutParams().height = playerCharacters[0].hit_height[0];
                            playerCharacters[0].totalFrames = playerCharacters[0].hitStandMedium.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].hit_align_frame_x[0],
                                    playerCharacters[0].hit_align_frame_y[0]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].hitStandMedium.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].hitStandMedium.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "hit1":
                        player1.setImageDrawable(playerCharacters[0].hitStandHigh.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].hitStandHigh.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].hit_width[1];
                            player1.getLayoutParams().height = playerCharacters[0].hit_height[1];
                            playerCharacters[0].totalFrames = playerCharacters[0].hitStandHigh.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].hit_align_frame_x[1],
                                    playerCharacters[0].hit_align_frame_y[1]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].hitStandHigh.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].hitStandHigh.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    case "hit2":
                        player1.setImageDrawable(playerCharacters[0].hitStandLow.get(playerCharacters[0].currentFrame).drawable);
                        duration = playerCharacters[0].hitStandLow.get(playerCharacters[0].currentFrame).duration;
                        if(playerCharacters[0].currentFrame == 0) {
                            player1.getLayoutParams().width = playerCharacters[0].hit_width[2];
                            player1.getLayoutParams().height = playerCharacters[0].hit_height[2];
                            playerCharacters[0].totalFrames = playerCharacters[0].hitStandLow.size();
                            alignGround("player", 0);
                            alignCharacter("player", 0,
                                    playerCharacters[0].hit_align_frame_x[2],
                                    playerCharacters[0].hit_align_frame_y[2]);
                        }
                        moveCharacter("player", 0, true, playerCharacters[0].hitStandLow.get(playerCharacters[0].currentFrame).move_x,
                                playerCharacters[0].hitStandLow.get(playerCharacters[0].currentFrame).move_y);
                        break;
                    default:
                        break;
                }
                playerCharacters[0].lastStatus = playerCharacters[0].status;
                playerCharacters[0].lastFrame = playerCharacters[0].currentFrame;
                if (playerCharacters[0].currentFrame + 1 >= playerCharacters[0].totalFrames) {
                    playerCharacters[0].immediate = false;
                    playerCharacters[0].nextStatus = null;
                    switch(playerCharacters[0].status) {
                        case "block0":
                            if(!playerCharacters[0].blocking) {
                                playerCharacters[0].status = "block1";
                                playerCharacters[0].currentFrame = 0;
                            } else {
                                playerCharacters[0].currentFrame = playerCharacters[0].blockFrames.size() - 1;
                                playerCharacters[0].lastStatus = "";
                            }
                            break;
                        case "throw0":
                            playerCharacters[0].status = "throw1";
                            playerCharacters[0].currentFrame = 0;
                            break;
                        case "crouch0":
                            if(!longPress) {
                                playerCharacters[0].status = "crouch2";
                                playerCharacters[0].currentFrame = 0;
                            } else {
                                playerCharacters[0].status = "crouch1";
                                playerCharacters[0].currentFrame = 0;
                            }
                            break;
                        case "crouch1":
                            playerCharacters[0].status = "crouch2";
                            playerCharacters[0].currentFrame = 0;
                            break;
                        case "crouch2":
                            playerCharacters[0].status = "crouch3";
                            playerCharacters[0].currentFrame = 0;
                            break;
                        case "swipeForward0":
                            playerCharacters[0].status = "swipeForward1";
                            playerCharacters[0].currentFrame = 0;
                            break;
                        default:
                            playerCharacters[0].status = "sIdle";
                            playerCharacters[0].currentFrame = 0;
                            break;
                    }
                } else {
                    playerCharacters[0].currentFrame++;
                }
                handler.postDelayed(runPlayer1, duration);
                loadDrawable("player", 0, playerCharacters[0].status, playerCharacters[0].currentFrame);
            }
        };
        runEnemy1 = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runEnemy1);
                int duration = 0;
                deleteDrawable("enemy", 0, enemyCharacters[0].lastStatus, enemyCharacters[0].lastFrame);
                if(enemyCharacters[0].immediate || (enemyCharacters[0].nextStatus != null && enemyCharacters[0].currentFrame == enemyCharacters[0].nextFrame)) {
                    enemyCharacters[0].immediate = false;
                    enemyCharacters[0].status = enemyCharacters[0].nextStatus;
                    enemyCharacters[0].nextStatus = null;
                    enemyCharacters[0].currentFrame = 0;
                }
                if(enemyCharacters[0].currentFrame == 0) {
                    switch (enemyCharacters[0].lastStatus) {
                        case "hit0":
                            alignCharacter("enemy", 0, 0 - enemyCharacters[0].hit_align_frame_x[0], 0 - enemyCharacters[0].hit_align_frame_y[0]);
                            break;
                        case "hit1":
                            alignCharacter("enemy", 0, 0 - enemyCharacters[0].hit_align_frame_x[1], 0 - enemyCharacters[0].hit_align_frame_y[1]);
                            break;
                        case "hit2":
                            alignCharacter("enemy", 0, 0 - enemyCharacters[0].hit_align_frame_x[2], 0 - enemyCharacters[0].hit_align_frame_y[2]);
                            break;
                        default:
                            break;
                    }
                }
                switch(enemyCharacters[0].status) {
                    case "sIdle":
                        enemy1.setImageDrawable(enemyCharacters[0].sIdleFrames.get(enemyCharacters[0].currentFrame).drawable);
                        duration = enemyCharacters[0].sIdleFrames.get(enemyCharacters[0].currentFrame).duration;
                        if (enemyCharacters[0].currentFrame == 0) {
                            enemy1.getLayoutParams().width = enemyCharacters[0].idle_align_frame_x;
                            enemy1.getLayoutParams().height = enemyCharacters[0].idle_align_frame_y;
                            enemyCharacters[0].totalFrames = enemyCharacters[0].sIdleFrames.size();
                        }
                        break;
                    case "hit0":
                        enemy1.setImageDrawable(enemyCharacters[0].hitStandMedium.get(enemyCharacters[0].currentFrame).drawable);
                        duration = enemyCharacters[0].hitStandMedium.get(enemyCharacters[0].currentFrame).duration;
                        if (enemyCharacters[0].currentFrame == 0) {
                            enemy1.getLayoutParams().width = enemyCharacters[0].hit_width[0];
                            enemy1.getLayoutParams().height = enemyCharacters[0].hit_height[0];
                            enemyCharacters[0].totalFrames = enemyCharacters[0].hitStandMedium.size();
                            alignGround("enemy", 0);
                            alignCharacter("enemy", 0,
                                    enemyCharacters[0].hit_align_frame_x[0],
                                    enemyCharacters[0].hit_align_frame_y[0]);
                        }
                        moveCharacter("enemy", 0, true, enemyCharacters[0].hitStandMedium.get(enemyCharacters[0].currentFrame).move_x,
                                enemyCharacters[0].hitStandMedium.get(enemyCharacters[0].currentFrame).move_y);
                        break;
                    case "hit1":
                        enemy1.setImageDrawable(enemyCharacters[0].hitStandHigh.get(enemyCharacters[0].currentFrame).drawable);
                        duration = enemyCharacters[0].hitStandHigh.get(enemyCharacters[0].currentFrame).duration;
                        if (enemyCharacters[0].currentFrame == 0) {
                            enemy1.getLayoutParams().width = enemyCharacters[0].hit_width[1];
                            enemy1.getLayoutParams().height = enemyCharacters[0].hit_height[1];
                            enemyCharacters[0].totalFrames = enemyCharacters[0].hitStandHigh.size();
                            alignGround("enemy", 0);
                            alignCharacter("enemy", 0,
                                    enemyCharacters[0].hit_align_frame_x[1],
                                    enemyCharacters[0].hit_align_frame_y[1]);
                        }
                        moveCharacter("enemy", 0, true, enemyCharacters[0].hitStandHigh.get(enemyCharacters[0].currentFrame).move_x,
                                enemyCharacters[0].hitStandHigh.get(enemyCharacters[0].currentFrame).move_y);
                        break;
                    case "hit2":
                        enemy1.setImageDrawable(enemyCharacters[0].hitStandLow.get(enemyCharacters[0].currentFrame).drawable);
                        duration = enemyCharacters[0].hitStandLow.get(enemyCharacters[0].currentFrame).duration;
                        if (enemyCharacters[0].currentFrame == 0) {
                            enemy1.getLayoutParams().width = enemyCharacters[0].hit_width[2];
                            enemy1.getLayoutParams().height = enemyCharacters[0].hit_height[2];
                            enemyCharacters[0].totalFrames = enemyCharacters[0].hitStandLow.size();
                            alignGround("enemy", 0);
                            alignCharacter("enemy", 0,
                                    enemyCharacters[0].hit_align_frame_x[2],
                                    enemyCharacters[0].hit_align_frame_y[2]);
                        }
                        moveCharacter("enemy", 0, true, enemyCharacters[0].hitStandLow.get(enemyCharacters[0].currentFrame).move_x,
                                enemyCharacters[0].hitStandLow.get(enemyCharacters[0].currentFrame).move_y);
                        break;
                    default:
                        break;
                }
                enemyCharacters[0].lastStatus = enemyCharacters[0].status;
                enemyCharacters[0].lastFrame = enemyCharacters[0].currentFrame;
                if (enemyCharacters[0].currentFrame + 1 >= enemyCharacters[0].totalFrames) {
                    enemyCharacters[0].immediate = false;
                    enemyCharacters[0].nextStatus = null;
                    switch(enemyCharacters[0].status) {
                        default:
                            enemyCharacters[0].status = "sIdle";
                            enemyCharacters[0].currentFrame = 0;
                            break;
                    }
                } else {
                    enemyCharacters[0].currentFrame++;
                }
                handler.postDelayed(runEnemy1, duration);
                loadDrawable("enemy", 0, enemyCharacters[0].status, enemyCharacters[0].currentFrame);
            }
        };
        handler.post(runPlayer1);
        handler.post(runEnemy1);
        /*
        Thread important = new Thread() {
            @Override
            public void run() {
                while(true) {
                    player1.setImageDrawable(playerCharacters[0].sIdleFrames.get(playerCharacters[0].currentFrame).drawable);
                    if(playerCharacters[0].currentFrame + 1 >= playerCharacters[0].sIdleFrames.size())  {
                        playerCharacters[0].currentFrame = 0;
                    } else {
                        playerCharacters[0].currentFrame++;
                    }
                    loadFrames("player", 0, "sIdle", playerCharacters[0].currentFrame);
                }
            }
        };
        important.setPriority(Thread.MAX_PRIORITY);
        important.start();
        */
    }

    private void initializeTouch() {
        gestureDetector = new GestureDetector(this, new OnSwipeListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                Log.d("Fight", "onDown , " + event.getPointerCount());
                initialX = event.getRawX();
                initialY = event.getRawY();
                moving = false;
                longPress = false;
                two_finger = false;
                two_swipe = false;
                return true;
            }

            @Override
            public void onLongPress(MotionEvent event) {
                Log.d("Fight", "onLongPress");
                longPress = true;
                switch(playerCharacters[active].status) {
                    case "sIdle":
                        if (playerCharacters[active].cDownIdleFrames != null) {
                            playerCharacters[active].immediate = true;
                            playerCharacters[active].nextStatus = "crouch0";
                        }
                        break;
                }
            }

            @Override
            public boolean onSwipe(Direction direction) {
                if (!two_finger && !two_swipe) {
                    Log.d("Fight", "onSwipe");
                    if (direction == Direction.up) {
                        Log.d("Fight", "onSwipe: up");
                        switch (playerCharacters[active].status) {
                            case "sIdle":
                            case "swipeBack0":
                            case "swipeForward0":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                            case "attack0":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[0];
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                            case "attack1":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[1];
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                            case "attack2":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[2];
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                            case "attack3":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[3];
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                            case "attack4":
                                if (playerCharacters[active].upAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[4];
                                    playerCharacters[active].nextStatus = "swipeUp0";
                                }
                                break;
                        }
                    }
                    if (direction == Direction.right) {
                        Log.d("Fight", "onSwipe: right");
                        switch (playerCharacters[active].status) {
                            case "sIdle":
                            case "swipeBack0":
                                if (playerCharacters[active].forwardAttackFrames_0 != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "swipeForward0";
                                }
                                break;
                            case "swipeForward1":
                                if (playerCharacters[active].forwardAttackFrames_2 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_forward_frame_change[1];
                                    playerCharacters[active].nextStatus = "swipeForward2";
                                }
                                break;
                        }
                    }
                    if (direction == Direction.down) {
                        Log.d("Fight", "onSwipe: down");
                        switch (playerCharacters[active].status) {
                            case "sIdle":
                            case "swipeBack0":
                            case "swipeForward0":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "swipeUp3":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_up_frame_change[3];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack0":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[0];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack1":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[1];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack2":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[2];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack3":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[3];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack4":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[4];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack5":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[5];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack6":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[6];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack7":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[7];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack8":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[8];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                            case "attack9":
                                if (playerCharacters[active].downAttackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[9];
                                    playerCharacters[active].nextStatus = "swipeDown0";
                                }
                                break;
                        }
                    }
                    if (direction == Direction.left) {
                        Log.d("Fight", "onSwipe: left");
                        switch (playerCharacters[active].status) {
                            case "sIdle":
                            case "swipeForward0":
                                if (playerCharacters[active].backFrames_0 != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "swipeBack0";
                                }
                                break;
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Fight", "Stop");
        handler.removeCallbacks(runPlayer1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Fight", "Pause");
        handler.removeCallbacks(runPlayer1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Fight", "Destroy");
        handler.removeCallbacks(runPlayer1);
        handler = null;
        playerCharacters = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();
        handler.post(runPlayer1);
    }

    public void onFragmentInteraction(Uri uri) {
        Log.d("Fragment", "Interaction");
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("Touch", "event!");
            gestureDetector.onTouchEvent(event);
            if(event.getPointerCount() == 2) {
                if (!two_finger) {
                    two_finger = true;
                    twoInitialX = event.getX();
                    twoInitialY = event.getY();
                    playerCharacters[active].blocking = true;
                } else {
                    if(!two_swipe) {
                        if(Math.abs(twoInitialX - event.getX()) >= threshold || Math.abs(twoInitialY - event.getY()) >= threshold) {
                            two_swipe = true;
                            playerCharacters[active].blocking = false;
                            switch (playerCharacters[active].status) {
                                case "sIdle":
                                case "block0":
                                case "block1":
                                    if (playerCharacters[active].throwFrames != null) {
                                        playerCharacters[active].immediate = true;
                                        playerCharacters[active].nextStatus = "throw0";
                                        playerCharacters[active].lastStatus = playerCharacters[active].status;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (!moving && (Math.abs(initialX - event.getRawX()) >= threshold || Math.abs(initialY - event.getRawY()) >= threshold)) {
                        moving = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Touch", "action up");
                    if (!moving && !longPress && !two_finger && !two_swipe) {
                        switch (playerCharacters[active].status) {
                            case "sIdle":
                            case "swipeBack0":
                            case "swipeForward0":
                                if (playerCharacters[active].attackFrames_0 != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "attack0";
                                }
                                break;
                            case "swipeForward1":
                                if(playerCharacters[active].attackFrames_0 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_forward_frame_change[1];
                                    playerCharacters[active].nextStatus = "attack0";
                                }
                                break;
                            case "swipeUp0":
                                if(playerCharacters[active].upAttackFrames_1 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_up_frame_change[0];
                                    playerCharacters[active].nextStatus = "swipeUp1";
                                }
                                break;
                            case "swipeUp1":
                                if(playerCharacters[active].upAttackFrames_2 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_up_frame_change[1];
                                    playerCharacters[active].nextStatus = "swipeUp2";
                                }
                                break;
                            case "swipeUp2":
                                if(playerCharacters[active].upAttackFrames_3 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].swipe_up_frame_change[2];
                                    playerCharacters[active].nextStatus = "swipeUp3";
                                }
                                break;
                            case "attack0":
                                if (playerCharacters[active].attackFrames_1 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[0];
                                    playerCharacters[active].nextStatus = "attack1";
                                }
                                break;
                            case "attack1":
                                if (playerCharacters[active].attackFrames_2 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[1];
                                    playerCharacters[active].nextStatus = "attack2";
                                }
                                break;
                            case "attack2":
                                if (playerCharacters[active].attackFrames_3 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[2];
                                    playerCharacters[active].nextStatus = "attack3";
                                }
                                break;
                            case "attack3":
                                if (playerCharacters[active].attackFrames_4 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[3];
                                    playerCharacters[active].nextStatus = "attack4";
                                }
                                break;
                            case "attack4":
                                if (playerCharacters[active].attackFrames_5 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[4];
                                    playerCharacters[active].nextStatus = "attack5";
                                }
                                break;
                            case "attack5":
                                if (playerCharacters[active].attackFrames_6 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[5];
                                    playerCharacters[active].nextStatus = "attack6";
                                }
                                break;
                            case "attack6":
                                if (playerCharacters[active].attackFrames_7 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[6];
                                    playerCharacters[active].nextStatus = "attack7";
                                }
                                break;
                            case "attack7":
                                if (playerCharacters[active].attackFrames_8 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[7];
                                    playerCharacters[active].nextStatus = "attack8";
                                }
                                break;
                            case "attack8":
                                if (playerCharacters[active].attackFrames_9 != null) {
                                    playerCharacters[active].nextFrame = playerCharacters[active].attack_frame_change[8];
                                    playerCharacters[active].nextStatus = "attack9";
                                }
                                break;
                        }
                    } else if (longPress) {
                        longPress = false;
                        switch (playerCharacters[active].status) {
                            case "crouch0":
                            case "crouch1":
                                if (playerCharacters[active].chargeFrames != null) {
                                    playerCharacters[active].immediate = true;
                                    playerCharacters[active].nextStatus = "crouch2";
                                }
                                break;
                            default:
                                break;
                        }
                    } else if (two_finger) {
                        playerCharacters[active].blocking = false;
                    }
                    moving = false;
                    longPress = false;
                    two_finger = false;
                    two_swipe = false;
            }
            return true;
        }
    };
}