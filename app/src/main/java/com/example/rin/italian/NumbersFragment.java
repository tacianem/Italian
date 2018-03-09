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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumbersFragment extends Fragment {

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

    private IntentFilter noisyIntent = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
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

    public NumbersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.word_list, container, false);
        final Activity activity = getActivity();

        ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("One", "Uno", R.drawable.one, R.raw.one));
        words.add(new Word("Two", "Due", R.drawable.two, R.raw.two));
        words.add(new Word("Three", "Tre", R.drawable.three, R.raw.three));
        words.add(new Word("Four", "Quattro", R.drawable.four, R.raw.four));
        words.add(new Word("Five", "Cinque", R.drawable.five, R.raw.five));
        words.add(new Word("Six", "Sei", R.drawable.six, R.raw.six));
        words.add(new Word("Seven", "Sette", R.drawable.seven, R.raw.seven));
        words.add(new Word("Eight", "Otto", R.drawable.eight, R.raw.eight));
        words.add(new Word("Nine", "Nove", R.drawable.nine, R.raw.nine));
        words.add(new Word("Ten", "Dieci", R.drawable.ten, R.raw.ten));

        final WordAdapter wordsAdapter = new WordAdapter(activity, words, R.color.category_numbers);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(wordsAdapter);

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC); //volume responds to cellphone volume buttons

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                releaseMedia();

                int requestResult = audioManager.requestAudioFocus(
                        audioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer = MediaPlayer.create(activity, wordsAdapter.getItem(position).getAudioId());

                    activity.registerReceiver(myNoisyAudioStreamReceiver, noisyIntent);

                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(completionListener);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop(); //You must always call the superclass first!
        releaseMedia();
    }

    private void releaseMedia() {
        Log.v("NumbersFragment", "Releasing Media Player");

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(audioListener);
            getActivity().unregisterReceiver(myNoisyAudioStreamReceiver);
        }
    }

}






