package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;


/**
 * Created by manoj.krishnan on 5/31/16.
 */
public class AboutMeFragment  extends android.support.v4.app.Fragment implements View.OnClickListener {
    private TextView mTxtView;
    private ImageView fb;
    private ImageView chrome;
    private ImageView youtube;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section_aboutme, container, false);
        mTxtView= (TextView)view.findViewById(R.id.AboutMe);
        mTxtView.setText("        James Vineeth is a Leadership Coach and a Key Note Speaker. He coaches Entrepreneurs and individuals on Leadership, Growth & Self Mastery through practical tools from Psychology, Management and his own Behavioral Research Work.\n" +
                "\n" +
                "        James is well recognized for his high impact behavioral insights which are unique and easy to remember. He has a track record of enabling and empowering people to achieve breakthrough results in their career.\n" +
                "\n" +
                "        MJ Learning Labs aims at providing practical behavioral solutions for people to hack growth, break limiting patterns and live life to the fullest.\n" +
                "\n" +
                "        Through his Workshops, James has trained and Coached thousands of people on Leadership, Transformation, Career Planning, Relationship Building and other behavioral aspects of life. His Purpose and Vision in life is to not only help everyone to Dream; But to help them follow their Dreams!" +
                "\n" +
                "\n" +
                "\n" +
                "www.jamesvineeth.com");
        fb = (ImageView)view.findViewById(R.id.facebook);
        chrome = (ImageView)view.findViewById(R.id.chrome);
        youtube = (ImageView)view.findViewById(R.id.youtube);
fb.setOnClickListener(this);
        chrome.setOnClickListener(this);
        youtube.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (!NetworkUtils.isNetworkAvailable(AboutMeFragment.this.getActivity())) {
            Toast.makeText(AboutMeFragment.this.getActivity(), "Please connect to network and Click on any Social networking site", Toast.LENGTH_LONG).show();
            return;
        }
        switch (i) {

            case R.id.facebook:
                try {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/658805267557550"));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Jamesvineeth"));
                    startActivity(intent);
                }
                break;
            case R.id.chrome:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jamesvineeth.com/"));
                startActivity(intent);
                break;
            case R.id.youtube:
                try {
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/channel/UCbkIZtYaorPiNspW7K_rE_Q"));
                    startActivity(youtubeIntent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;

        }
    }
}