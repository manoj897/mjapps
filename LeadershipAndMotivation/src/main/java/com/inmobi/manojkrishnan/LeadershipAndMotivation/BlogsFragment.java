package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.DownloadBlogsImageService;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Parser;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.blogData;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by manoj.krishnan on 5/24/16.
 */
public class BlogsFragment  extends android.support.v4.app.Fragment {
    private ViewGroup mContainer;
    private  FeedItemAdapter mFeedAdapter;
    private GridView mGridView;
    private ArrayList<FeedDataBlogs.FeedItem> mFeedItems;
    private KeyValueStore mKeyValueStore;
    private KeyValueStore mKeyValueStore4BlogsInit;
    private static NetworkResponse mResponse;
    private RecyclerView mRecycleView;
    View.OnClickListener clickListener;
    private ArrayList<Bitmap> mBitMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_recycler_view, container, false);
        mRecycleView = (RecyclerView) view.findViewById(R.id.cardList);
        mRecycleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(llm);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mKeyValueStore = KeyValueStore.getInstance(BlogsFragment.this.getContext().getApplicationContext(), "BlogImages");
        mKeyValueStore4BlogsInit = KeyValueStore.getInstance(BlogsFragment.this.getContext().getApplicationContext(), "BlogsInit");
        super.onActivityCreated(savedInstanceState);
        if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
            if(!mKeyValueStore4BlogsInit.getBoolean("init",false)) {
                Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network and launch again - Blogs",
                        Toast.LENGTH_LONG).show();
                return ;
            }
        }

        if(!mKeyValueStore.getBoolean("BlogImagesInit",false)) {
            mKeyValueStore.putBoolean("blogImageDownloaded", true);
        }

        if(!mKeyValueStore4BlogsInit.getBoolean("init",false)){
            Log.d("Blogs","Fetching from Network======");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            String sleepTime = "few";
            runner.execute(sleepTime);
            //Todo - cache the network data into file instead of preference

        }else {
            Log.d("Blogs","Using cached value======");
            Parser.parseBlogGrammar(mKeyValueStore4BlogsInit.getString("blogsGrammar",null));
            mFeedItems = FeedDataBlogs.generateFeedItems();
            //Download Blog Images parallely
            boolean didBlogImagesDownloaded = false;
            try {
                File filePath = BlogsFragment.this.getContext().getFileStreamPath("blogspicsdownloaded.txt");
                FileInputStream fi = new FileInputStream(filePath);
                if ( fi != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(fi);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }
                    fi.close();
                    didBlogImagesDownloaded = Boolean.valueOf(stringBuilder.toString());
                    Log.d("Blogs","shouldDownloadBlogsImages returned"+ didBlogImagesDownloaded);
                }
                fi.close();
            } catch (Exception ex) {
                Log.e("failed to read blogspicsdownloaded file", ex.getMessage());
            }
            if(!didBlogImagesDownloaded){
                Log.d("Blogs","Downloading Images parallely======");
                ArrayList<FeedDataBlogs.FeedItem> users = mFeedItems;
                String imgUrl = null;
                int i=-1;
                for (FeedDataBlogs.FeedItem user:
                        users) {
                    i++;
                    imgUrl = user.getThumbnail();
                    Intent downloadBlogsImageServiceIntent = new Intent(BlogsFragment.this.getContext(), DownloadBlogsImageService.class);
                    downloadBlogsImageServiceIntent.putExtra("blogImagesCounter",i);
                    downloadBlogsImageServiceIntent.putExtra("blogImageUrl",imgUrl);
                    BlogsFragment.this.getContext().startService(downloadBlogsImageServiceIntent);
                }
                Intent downloadBlogsImageServiceIntent = new Intent(BlogsFragment.this.getContext(), DownloadBlogsImageService.class);
                downloadBlogsImageServiceIntent.putExtra("complete",true);
                BlogsFragment.this.getContext().startService(downloadBlogsImageServiceIntent);
                mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems,false);
                mRecycleView.setAdapter(mFeedAdapter);
                clickListener = new mItemClickListener();
            }else {
                Log.d("Blogs","Using Downloaded Images======");
                Bitmap thumbnail = null;
                int i =0;
                mBitMap = new ArrayList<>();
                for (i=0;i<mFeedItems.size();i++) {
                    try {
                        File filePath = BlogsFragment.this.getContext().getFileStreamPath("blogspics"+i+".png");
                        FileInputStream fi = new FileInputStream(filePath);
                        thumbnail = BitmapFactory.decodeStream(fi);
                        mBitMap.add(thumbnail);
                        fi.close();
                        Log.d("Blogs","Downloaded Images populated======");
                    } catch (Exception ex) {
                        Log.e("getThumbnail() on internal storage", ex.getMessage());
                    }
                }

                mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems,true);
                mRecycleView.setAdapter(mFeedAdapter);
                clickListener = new mItemClickListener();
            }



            //Start downloading images
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



    public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.FeedItemHolder> {
        private Context context;
        private ArrayList<FeedDataBlogs.FeedItem> users;
        private boolean useCachedImage;

        public FeedItemAdapter(Context context, ArrayList<FeedDataBlogs.FeedItem> users,boolean shouldUseCachedImages) {
            super();
            this.context = context;
            this.users = users;
            this.useCachedImage = shouldUseCachedImages;
        }

        @Override
        public FeedItemAdapter.FeedItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.content_blog, parent, false);
            itemView.setOnClickListener(clickListener);
            return new FeedItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FeedItemAdapter.FeedItemHolder holder, int position) {
            FeedDataBlogs .FeedItem feed = users.get(position);
            TextView textViewName = holder.title;
            ImageView imageView = holder.big_image;
            TextView textViewReadMore = holder.readMore;
            textViewReadMore.setText("Read More...");
            textViewName.setText(feed.gettitle());

            if(useCachedImage){
                Bitmap thumbnail = null;
                try {
                    imageView.setImageBitmap(mBitMap.get(position));
                } catch (Exception ex) {
                    Log.e("getThumbnail() on internal storage", ex.getMessage());
                }
            }else{
                if(position == 1)
                    Glide.with(BlogsFragment.this.getContext().getApplicationContext())
                            .load(feed.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().override(250,150).priority(Priority.IMMEDIATE)
                            .into(imageView);
                else
                    Glide.with(BlogsFragment.this.getContext().getApplicationContext())
                            .load(feed.getThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().override(250,150)
                            .into(imageView);
            }


        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class FeedItemHolder extends RecyclerView.ViewHolder {
            protected ImageView big_image;
            protected TextView title;
            protected TextView readMore;

            public FeedItemHolder(View v) {
                super(v);
                big_image = (ImageView) v.findViewById(R.id.big_image);
                title = (TextView) v.findViewById(R.id.blogtitle);
                readMore = (TextView) v.findViewById(R.id.ReadMore);
            }
        }
    }


    private class mItemClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Log.d("testBlog","Inside the Click listener");
            try {
                /*if (!NetworkUtils.isNetworkAvailable(BlogsFragment.this.getActivity())) {
                    Toast.makeText(BlogsFragment.this.getActivity(), "Please connect to network to Read the full blog!", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                int position = mRecycleView.getChildLayoutPosition(v);
                FeedDataBlogs.FeedItem inst = mFeedItems.get(position);
                blogData data = new blogData(inst.getContent(),inst.getBigImage());
                Intent intentBlog = new Intent(BlogsFragment.this.getActivity(), BlogShowCaseActivity.class);
                intentBlog.putExtra("BlogItem", data);
                intentBlog.putExtra("BlogImage", "blogspics"+position+".png");
                intentBlog.putExtra("BlogContent", "blogscontent"+position+".txt");
                intentBlog.putExtra("BlogTitle",inst.gettitle());
                startActivity(intentBlog);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    /*public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
    }*/




   /* public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }*/

    /*private class LongOperation extends AsyncTask<MyTaskParams, Void, Bitmap> {
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
    }*/


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
                mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems,false);
                mRecycleView.setAdapter(mFeedAdapter);
                if(clickListener != null)
                    clickListener =  new mItemClickListener();
                mRecycleView.setOnClickListener(clickListener);
                mKeyValueStore4BlogsInit.putBoolean("init", true);
                mKeyValueStore4BlogsInit.putString("blogsGrammar",mResponse.getResponse());
                //Download all the blogsImages
                boolean didBlogImagesDownloaded = false;
                /*try {
                    File filePath = BlogsFragment.this.getContext().getFileStreamPath("blogspicsdownloaded.txt");
                    FileInputStream fi = new FileInputStream(filePath);
                    if ( fi != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(fi);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }
                        fi.close();
                        shouldDownloadBlogsImages = Boolean.valueOf(stringBuilder.toString());
                    }
                    fi.close();
                } catch (Exception ex) {
                    Log.e("failed to read blogspicsdownloaded file", ex.getMessage());
                }*/
                if(didBlogImagesDownloaded) {
                    Log.d("Blogs","Downloading Images parallely======");
                    ArrayList<FeedDataBlogs.FeedItem> users = mFeedItems;
                    String imgUrl = null;
                    int i = -1;
                    for (FeedDataBlogs.FeedItem user :
                            users) {
                        i++;
                        imgUrl = user.getThumbnail();
                        Intent downloagBlogsImageServiceIntent = new Intent(BlogsFragment.this.getContext(), DownloadBlogsImageService.class);
                        downloagBlogsImageServiceIntent.putExtra("blogImagesCounter", i);
                        downloagBlogsImageServiceIntent.putExtra("blogImageUrl", imgUrl);
                        BlogsFragment.this.getContext().startService(downloagBlogsImageServiceIntent);
                    }
                    Intent downloadBlogsImageServiceIntent = new Intent(BlogsFragment.this.getContext(), DownloadBlogsImageService.class);
                    downloadBlogsImageServiceIntent.putExtra("complete", true);
                    BlogsFragment.this.getContext().startService(downloadBlogsImageServiceIntent);
                }

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
