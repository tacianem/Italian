package com.example.rin.italian;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rin on 21/09/17.
 */

public class WordAdapter extends ArrayAdapter<Word> {

    private int background;

    public WordAdapter(Activity context, ArrayList<Word> words, int background) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0. >> BY UDACITY
        super(context, 0, words);
        this.background = background;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        final Word currentWord = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID italian_text
        TextView textViewItalian = listItemView.findViewById(R.id.italian_text);
        // Get the italian translation from the current Word object and
        // set this text on the name TextView
        textViewItalian.setText(currentWord.getItalianTranslation());

        // Find the TextView in the list_item.xml layout with the ID english_text
        TextView textViewEnglish = listItemView.findViewById(R.id.english_text);
        // Get the englishtranslation from th e current Word object and
        // set this text on the number TextView
        textViewEnglish.setText(currentWord.getEnglishTranslation());


        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        ImageView iconView = listItemView.findViewById(R.id.icon);

        if (currentWord.hasImage()) {
            // Get the image resource ID from the current AndroidFlavor object and
            // set the image to iconView
            iconView.setImageResource(currentWord.getImageResourceId());
            iconView.setVisibility(View.VISIBLE); //As views are reused, it could be gone at this time and then we need to set it back to visible!!!!
        } else {
            iconView.setVisibility(View.GONE);
        }

        //LinearLayout numbersLinear = (LinearLayout) getContext().findViewById(R.id.linearList);??!
        View linearList = listItemView.findViewById(R.id.linearList);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), background);
        // Set the background color of the text container View
        linearList.setBackgroundColor(color);


        ImageView play = listItemView.findViewById(R.id.play);
        play.setBackgroundColor(color);

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;

    }

}


