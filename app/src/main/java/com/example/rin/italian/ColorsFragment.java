package com.example.rin.italian;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorsFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0); //back to the beginning
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseMedia();
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlayer.start();
                    break;
            }
        }
    };

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BroadcastReceiver myNoisyAudioStreamReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                mediaPlayer.pause();
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMedia();
        }
    };


    public ColorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.word_list, container, false);
        final Activity activity = getActivity();

        ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Red", "Rosso", R.drawable.red, R.raw.red));
        words.add(new Word("White", "Bianco", R.drawable.white, R.raw.white));
        words.add(new Word("Yellow", "Giallo", R.drawable.yellow, R.raw.yellow));
        words.add(new Word("Blue", "Azzurro", R.drawable.blue, R.raw.blue));
        words.add(new Word("Green", "Verde", R.drawable.green, R.raw.green));
        words.add(new Word("Brown", "Marrone", R.drawable.brown, R.raw.brown));
        words.add(new Word("Pink", "Rosa", R.drawable.pink, R.raw.pink));
        words.add(new Word("Orange", "Arancione", R.drawable.orange, R.raw.orange));
        words.add(new Word("Purple", "Violetto", R.drawable.purple, R.raw.purple));
        words.add(new Word("Grey", "Grigio", R.drawable.grey, R.raw.grey));

        final WordAdapter wordsAdapter = new WordAdapter(activity, words, R.color.category_colors);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(wordsAdapter);

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                releaseMedia();

                int requestResult = audioManager.requestAudioFocus(
                        audioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer = MediaPlayer.create(activity, wordsAdapter.getItem(position).getAudioId());

                    activity.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(completionListener);
                }
            }
        });

        return rootView;
    }


    private void releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(audioListener);
            getActivity().unregisterReceiver(myNoisyAudioStreamReceiver);
        }
    }

    @Override
    public void onStop() {
        super.onStop(); //You must always call the superclass first!
        releaseMedia();
    }
}


