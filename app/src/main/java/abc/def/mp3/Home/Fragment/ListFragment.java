package abc.def.mp3.Home.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import abc.def.mp3.Home.Adapter.SongsListAdapter;
import abc.def.mp3.Home.HomeActivity;
import abc.def.mp3.Home.Modal.SongsDetails;
import abc.def.mp3.Player.PlayerActivity;
import abc.def.mp3.R;


public class ListFragment extends Fragment {
    View view;
    Context context;
    RecyclerView rvLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        context = getActivity().getApplicationContext();
        initView();
        initToolbar();
        return view;

    }
    Toolbar toolbar;
    private void initToolbar() {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
    }

    SharedPreferences sharedPreferences;

    void initView() {
        sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        rvLists = view.findViewById(R.id.rvLists);
        LinearLayoutManager ly =
                new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rvLists.setLayoutManager(ly);
        String path = sharedPreferences.getString("path", "");
        if (path.equalsIgnoreCase("")) {
            //   openFolder();
        } else {
            //   loadSongsFromDevice("/storage/");
        }

        loadAllSongs();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String Fpath = data.getDataString();
            Toast.makeText(context, Fpath, Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
        }
    }

    int PICK_FOLDER_REQUEST_CODE = 101;

    void openFolder() {
        Toast.makeText(context, "Please select Folder to play Mp3", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
    }

    ArrayList<SongsDetails> songsDetails;
    SongsListAdapter adapter;
    void loadAllSongs() {
        songsDetails = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songtTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songAetist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int path = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int name = songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);

            do {
                String currentTitle = songCursor.getString(songtTitle);
                String songArtist = songCursor.getString(songAetist);
                String data = songCursor.getString(path);
                String songDuration = songCursor.getString(duration);
                String displaName = songCursor.getString(name);
                SongsDetails songs = new SongsDetails(currentTitle, songArtist, data, songDuration, displaName);
                songsDetails.add(songs);

                Log.d("currentTitle", currentTitle);


            } while (songCursor.moveToNext());
            adapter= new SongsListAdapter(context,songsDetails);
            rvLists.setAdapter(adapter);
            if(songsDetails.size()>0){
                adapter.setOnItemClickListener(new SongsListAdapter.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(View view, SongsDetails obj, int position, ImageView ivProfile, TextView name) {
                        //for now playing display in homepage I used sharedPreferences not intent putExtras :)
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",obj.getName());
                        editor.putString("artist", obj.getArtist());
                        editor.putString("path", obj.getPath());
                        editor.putString("duration", obj.getDuration());
                        editor.putString("displayName", obj.getDisplayName());
                        editor.apply();
                        Intent a= new Intent(context, PlayerActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), (View)ivProfile, "profile");
                        startActivity(a,options.toBundle());
                        //((HomeActivity) getActivity()).loadFragment(new PlayerFragment(),"player");
                    }
                });
            }
        }
    }

    private void loadSongsFromDevice(String storage) {
        ArrayList<HashMap<String, String>> songList = getPlayListByPath(storage);
        if (songList != null) {
            Toast.makeText(context, songList.size() + " Songs Found", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < songList.size(); i++) {
                String fileName = songList.get(i).get("file_name");
                String filePath = songList.get(i).get("file_path");
                Log.d("filesMp3", " name =" + fileName + " path = " + filePath);
            }
        } else {
            Toast.makeText(context, "No Songs Found", Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<HashMap<String, String>> getPlayListByPath(String rootPath) {
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayListByPath(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayListByPath(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}
