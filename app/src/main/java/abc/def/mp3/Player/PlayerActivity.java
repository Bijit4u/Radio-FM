package abc.def.mp3.Player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import abc.def.mp3.R;
import abc.def.mp3.Utils.MusicUtils;
import abc.def.mp3.Utils.Tools;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initToolbar();
        initView();
        findsong();
    }

    TextView txtName, txtArtist, name2;

    private void initView() {
        txtName = findViewById(R.id.txtName);
        name2 = findViewById(R.id.name);
        txtArtist = findViewById(R.id.txtArtist);
    }

    SharedPreferences sharedPreferences;

    void findsong() {
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        name2.setText(name);
        txtName.setText(name);
        String artist = sharedPreferences.getString("artist", "");
        txtArtist.setText(artist);
        String path = sharedPreferences.getString("path", "");
        String duration = sharedPreferences.getString("duration", "");
        String displayName = sharedPreferences.getString("displayName", "");
        initComponent(path);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorAccent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private ImageButton bt_play;
    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private MusicUtils utils;

    private void initComponent(String filePath) {
        parent_view = findViewById(R.id.parent_view);
        seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
        bt_play = (ImageButton) findViewById(R.id.bt_play);
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                bt_play.setImageResource(R.drawable.ic_play_arrow);
            }
        });

        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mp.setDataSource(Environment.getExternalStorageDirectory()+filePath.split("0")[1]);
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
            bt_play.setImageResource(R.drawable.ic_pause);
            mHandler.post(mUpdateTimeTask);
        } catch (Exception e) {
            Log.d("error",e.toString());
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }


        utils = new MusicUtils();
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
    }

    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mp.isPlaying()) {
                    mp.pause();
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mp.start();
                    bt_play.setImageResource(R.drawable.ic_pause);
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_repeat: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_shuffle: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_prev: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_next: {
                toggleButtonColor((ImageButton) v);
                Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
            seek_song_progressbar.setProgress(progress);
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }

}
