package com.mridx.radioprogram.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mridx.radioprogram.R;
import com.mridx.radioprogram.fragment.AbutFrag;
import com.mridx.radioprogram.fragment.ContactFrag;
import com.mridx.radioprogram.fragment.HomeFrag;
import com.mridx.radioprogram.fragment.ProgramsFrag;
import com.mridx.radioprogram.helper.MediaHelper;
import com.mridx.radioprogram.helper.PermissionHelper;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class HomeUI extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    //public static MediaHandler mediaHandler;

    String[] extensions = { "mp3", "m4a", "MP3", "M4A" };
    public static ArrayList<String> AllSongsUri = new ArrayList<>();
    public static ArrayList<String> AllSongsName = new ArrayList<>();

    public static MediaPlayer mediaPlayer;
    public static MediaHelper mediaHelper;

    public static int currentPlayingPosition;
    public static String currentPlayingLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_ui);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.btmNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        mediaHelper = new MediaHelper();

        //mediaHandler = new MediaHandler(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        //checkForPermission();

        //loadFragment(new ProgramsFrag());

        if (new PermissionHelper().check(this)) {
            //loadFragment(new ProgramsFrag());
            startDirectoryChooser();
        } else {
            new PermissionHelper().getOrCheck(this);
        }

    }



    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.radioProgram:
                //loadFragment(new ProgramsFrag());
                break;
            case R.id.aboutUs:
                //loadFragment(new AbutFrag());
                break;
            case R.id.contactUs:
                //loadFragment(new ContactFrag());
                break;
            default:
                    loadFragment(new ProgramsFrag());
                    break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (new PermissionHelper().check(this)) {
                startHome();
            } else {
                new PermissionHelper().getOrCheck(this);
            }
        } else if (requestCode == 105) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                handleDirectoryChoice(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            } else {
                // Nothing selected
                showNothingSelected();
            }
        }
    }

    private void showNothingSelected() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("You haven't selected any directory. \nSelect one or exit ! ");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Select", (dialog, which) -> {
            alertDialog.dismiss();
            startDirectoryChooser();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", (dialog, which) -> {
            alertDialog.dismiss();
            finish();
        });
        alertDialog.show();
    }

    private void handleDirectoryChoice(String stringExtra) {
        Log.d("kaku", "handleDirectoryChoice: " + stringExtra);
        loadmp3(stringExtra);
        loadFragment(new ProgramsFrag());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startHome();
            } else {
                new PermissionHelper().getOrCheck(this);
            }
        }
    }

    private void startHome() {
        //loadFragment(new ProgramsFrag());
        startDirectoryChooser();
    }

    public void startDirectoryChooser() {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

// REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        startActivityForResult(chooserIntent, 105);
    }



    private void loadmp3(final String homedir) {

        class loadSongs extends AsyncTask<Void, Void, Integer> {

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... voids) {

                Drawable drawable = getDrawable(R.drawable.image);
                assert drawable != null;
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

                File file = new File(homedir);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                loadmp3(f.getAbsolutePath());
                            } else {
                                for (int i = 0; i < extensions.length; i++) {
                                    if (f.getAbsolutePath().endsWith(extensions[i])) {
                                        AllSongsUri.add(f.getAbsolutePath());
                                        String song = f.getName().replace("." + extensions[i], "");
                                        AllSongsName.add(song);
                                        //mediaMetadataRetriever.setDataSource(f.getAbsolutePath());
                                        /*String m = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                        //Log.d("t", m);
                                        artist.add(m);*/
                                        /*byte[] art = mediaMetadataRetriever.getEmbeddedPicture();
                                        if (art != null) {
                                            albumart.add(art);
                                        } else {
                                            albumart.add(bitMapData);
                                        }*/

                                    }
                                }
                            }
                        }
                    }
                }
                return AllSongsUri.size();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                //splashImage.setVisibility(View.GONE);
                if (integer > 0) {
                    //mainUITitle.setVisibility(View.VISIBLE);
                    /*AllSongsAdapter allSongsAdapter = new AllSongsAdapter(MainUI.this, AllSongsUri, AllSongsName, artist, albumart);
                    GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    mainUISongsHolder.setLayoutManager(linearLayoutManager);
                    mainUISongsHolder.setAdapter(allSongsAdapter);*/
                    //mainUISongsHolder.smoothScrollToPosition(0);
                    loadFragment(new ProgramsFrag());
                } else {
                    //nosongs.setVisibility(View.VISIBLE);
                    showAlert();
                }
                //getSongList();
                //getSupportActionBar().show();
                //nowPlayingMainUI.setVisibility(View.VISIBLE);
            }
        }

        loadSongs loadSongs = new loadSongs();
        loadSongs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("No Songs found ! \nSelect another directory");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Select", (dialog, which) -> {
            alertDialog.dismiss();
            startDirectoryChooser();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", (dialog, which) -> {
            alertDialog.dismiss();
            finish();
        });
        alertDialog.show();
    }

}
