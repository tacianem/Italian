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
public class PhrasesFragment extends Fragment {

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


    public PhrasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.word_list, container, false);
        final Activity activity = getActivity();

        ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("I'm a woman", "Io sonno una donna", R.raw.woman));
        words.add(new Word("The boy eats an apple", "Il ragazzo mangia la mela", R.raw.boy));
        words.add(new Word("You're welcome", "Prego!", R.raw.welcome));
        words.add(new Word("Thank you very much", "Grazie mille", R.raw.thanks));
        words.add(new Word("It's awful", "Mi fa cagare!", R.raw.awful));
        words.add(new Word("'Thank God :B'", "Meno male!", R.raw.okgood));
        words.add(new Word("Hello!", "Ciao/salve", R.raw.hello));
        words.add(new Word("How are you?", "Come stai?", R.raw.howru));
        words.add(new Word("My name is...", "Mi chiamo...", R.raw.myname));
        words.add(new Word("Hello (on telephone)", "Pronto?", R.raw.pronto));

        final WordAdapter wordsAdapter = new WordAdapter(activity, words, R.color.category_phrases);
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
