package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataBlogs;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataWallpapers;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.BlogShowCaseActivity;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Parser;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.blogData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by manoj.krishnan on 5/24/16.
 */
public class BlogsFragment  extends android.support.v4.app.Fragment {
    private ViewGroup mContainer;
    private BaseAdapter mFeedAdapter;
    private GridView mGridView;
    private ArrayList<FeedDataBlogs.FeedItem> mFeedItems;
    private KeyValueStore mKeyValueStore;
    private KeyValueStore mKeyValueStore4BlogsInit;
    private static NetworkResponse mResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_section_blogs, container, false);
        mContainer = (ViewGroup) view.findViewById(R.id.container);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mKeyValueStore4BlogsInit = KeyValueStore.getInstance(BlogsFragment.this.getContext().getApplicationContext(), "BlogsInit");
        super.onActivityCreated(savedInstanceState);
        if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
            if(!mKeyValueStore4BlogsInit.getBoolean("init",false)) {
                Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network and launch again - Blogs",
                        Toast.LENGTH_LONG).show();
                return ;
            }
        }

        if(!mKeyValueStore4BlogsInit.getBoolean("init",false)){
            Log.d("Blogs","Fetching from Network======");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            String sleepTime = "few";
            runner.execute(sleepTime);
        }else {
            Log.d("Blogs","Using cached value======");
            Parser.parseBlogGrammar(mKeyValueStore4BlogsInit.getString("blogsGrammar",null));
            mFeedItems = FeedDataBlogs.generateFeedItems();
            mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
            mGridView.setAdapter(mFeedAdapter);
            mGridView.setOnItemClickListener(mItemClickListener);
        }

    }


    final public class MyTaskParams {
        int width;
        int height;
        String image;
        Context ctxt;

        MyTaskParams(int width, int height, String image, Context ctxt) {
            this.width = width;
            this.height = height;
            this.image = image;
            this.ctxt = ctxt;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public String getImage() {
            return image;
        }

        public Context getContext() {
            return ctxt;
        }

    }

    private class FeedItemAdapter extends ArrayAdapter<FeedDataBlogs.FeedItem> {
        private Context context;
        private ArrayList<FeedDataBlogs .FeedItem> users;
        private LayoutInflater layoutInflater;

        class ContentViewHolder {
            TextView title;
            TextView content;
            ImageView thumb_image;
            ImageView big_image;
            //ImageView bottom_img;
        }

        public FeedItemAdapter(Context context, ArrayList<FeedDataBlogs.FeedItem> users) {
            super(context, R.layout.listitem, R.id.title, users);
            this.context = context;
            this.users = users;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return users.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (null == rowView) {
                rowView = layoutInflater.inflate(R.layout.content_blog, parent, false);
                ContentViewHolder viewHolder = new ContentViewHolder();
                viewHolder.big_image = (ImageView) rowView.findViewById(R.id.big_image);
                viewHolder.title = (TextView) rowView.findViewById(R.id.blogtitle);
                rowView.setTag(viewHolder);

                Log.d("test", "RowView is null=============");
            } else
                Log.d("test", "RowView is not null");


            FeedDataBlogs .FeedItem feed = users.get(position);
            ContentViewHolder holder = (ContentViewHolder) rowView.getTag();
            //Picasso.with(BlogsFragment.this.getContext()).load(feed.getThumbnail()).resize(200,200).into(holder.big_image);
            if(position == 1)
                Glide.with(BlogsFragment.this.getContext().getApplicationContext())
                        .load(feed.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().override(150,100).priority(Priority.IMMEDIATE)
                        .into(holder.big_image);
            else
                Glide.with(BlogsFragment.this.getContext().getApplicationContext())
                        .load(feed.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().override(150,100)
                        .into(holder.big_image);

            holder.title.setText(feed.gettitle());

            return rowView;


        }


    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.d("testBlog","Inside the Click listener");
            try {
                if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
                    Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network to Read the full blog!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FeedDataBlogs.FeedItem inst = mFeedItems.get(position);
                blogData data = new blogData(inst.getContent(),inst.getBigImage());
                Intent intentBlog = new Intent(BlogsFragment.this.getActivity(), BlogShowCaseActivity.class);
                intentBlog.putExtra("BlogItem", data);
                startActivity(intentBlog);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };







    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d("LOG LOG", "" + reqWidth+" "+reqHeight+" "+width+" "+height+" "+inSampleSize);

        return inSampleSize;
    }




    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private class LongOperation extends AsyncTask<MyTaskParams, Void, Bitmap> {
        ImageView imgView;

        public LongOperation(ImageView imgView) {
            this.imgView = imgView;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imgView.setImageBitmap(result);
        }

        @Override
        protected Bitmap doInBackground(MyTaskParams... params) {

            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), params[0].getContext().getResources().getIdentifier(params[0].getImage(), "drawable", params[0].getContext().getPackageName()), 100, 100);
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp = null;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                NetworkHandler hdlr = new NetworkHandler();
                mResponse = hdlr.connect("http://motivationblogs.s3-ap-southeast-1.amazonaws.com/blogsGrammar.txt");
                if(mResponse != null) {
                    Parser.parseBlogGrammar(mResponse.getResponse());
                    resp = "success";
                }

            }catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            if(result.equalsIgnoreCase("success")) {
                mFeedItems = FeedDataBlogs.generateFeedItems();
                mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
                mGridView.setAdapter(mFeedAdapter);
                mGridView.setOnItemClickListener(mItemClickListener);
                mKeyValueStore4BlogsInit.putBoolean("init", true);
                mKeyValueStore4BlogsInit.putString("blogsGrammar",mResponse.getResponse());
            }
            else
                mKeyValueStore4BlogsInit.putBoolean("init",false);

        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BlogsFragment.this.getContext(),
                    "ProgressDialog",
                    "Wait for few seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
