package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.FeedDataWallpapers;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.feeders.Grammar;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkHandler;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkResponse;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.Parser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by manoj.krishnan on 5/24/16.
 */
public class WallpaperFragment extends ListFragment {

    private ViewGroup mContainer;
    private ListView mListView;
    private TextView mTextView;
    private BaseAdapter mFeedAdapter;
    private ArrayList<FeedDataWallpapers.FeedItem> mFeedItems;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private volatile static Picasso sPicasso;
    private KeyValueStore mKeyValueStore;
    private KeyValueStore mKeyValueStore4WallpaperInit;
    private static NetworkResponse mResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);
        /*View view = inflater.inflate(R.layout.fragment_section_wallpapers, container, false);
        mListView = (ListView) view.findViewById(R.id.listView_wallpapers);*/

        startPicasso(WallpaperFragment.this.getContext());
        return listFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(WallpaperFragment.this.getActivity())) {
            Toast.makeText(WallpaperFragment.this.getActivity(), "Please connect to network and launch again",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mKeyValueStore4WallpaperInit = KeyValueStore.getInstance(WallpaperFragment.this.getContext().getApplicationContext(), "WallpaperInit");
        if(!mKeyValueStore4WallpaperInit.getBoolean("init",false) && (null == mKeyValueStore4WallpaperInit.getString("wallpaperGrammar",null))){
            Log.d("Wallpaper","Fetching from network======");
            AsyncTaskRunner runner = new AsyncTaskRunner();
            String sleepTime = "few";
            runner.execute(sleepTime);
        } else {
            Log.d("Wallpaper","Using cached value======");
            mKeyValueStore = KeyValueStore.getInstance(WallpaperFragment.this.getActivity().getApplicationContext(), "QuotesCounter");
            if(mKeyValueStore.getInt("counter",0) == 0) {
                mKeyValueStore.putInt("counter", 1);
                Log.d("WallpaperFragment", "count when first time " + mKeyValueStore.getInt("counter", 1));

            }
            Parser.parseWallpaperGrammar(WallpaperFragment.this.getContext(),mKeyValueStore4WallpaperInit.getString("wallpaperGrammar",null),mKeyValueStore.getInt("counter",1));
            mFeedItems = FeedDataWallpapers.generateFeedItems(mKeyValueStore.getInt("counter", 1));
            mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
            setListAdapter(mFeedAdapter);
        }
        //getListView().setOnItemLongClickListener(mItemLongCick);

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

    private class FeedItemAdapter extends ArrayAdapter<FeedDataWallpapers.FeedItem> {
        private Context context;
        private ArrayList<FeedDataWallpapers.FeedItem> users;
        private LayoutInflater layoutInflater;

        class ContentViewHolder {
            /*TextView title;
            TextView subtitle;
            TextView time_tt;
            TextView description_tt;
            ImageView thumb_image;*/ ImageView big_image;
            //ImageView bottom_img;
        }

        public FeedItemAdapter(Context context, ArrayList<FeedDataWallpapers.FeedItem> users) {
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

            WrapperView wView = null;
            if (null == convertView) {
                convertView = layoutInflater.inflate(R.layout.content_wallpaper, parent, false);
                wView = new WrapperView (convertView);
                convertView.setTag(wView);
                Log.d("test", "RowView is null=============");
            } else {
                wView = (WrapperView) convertView.getTag();
            }
            final ImageView imgNeedsToBeAccessedInner = wView.getImage();
                Log.d("test", "RowView is not null");
            FeedDataWallpapers.FeedItem feed = users.get(position);
            wView.getImage().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("testmotivation", "Inside click");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!checkPermission()) {
                            requestPermission();
                        }

                    }
                   // Get access to the URI for the bitmap
                    Uri bmpUri = getLocalBitmapUri(imgNeedsToBeAccessedInner);

                    if (bmpUri != null) {
                        // Construct a ShareIntent with link to image
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpeg");
                        // Launch sharing dialog for image
                        startActivity(Intent.createChooser(shareIntent, "Share Image"));

                    }
                }
            });
           // ContentViewHolder holder = (ContentViewHolder) rowView.getTag();
            //holder.big_image.setImageBitmap(null);

           /* int parentWidth = ((LinearLayout) holder.big_image.getParent()).getWidth();
            int parentHeight = ((LinearLayout) holder.big_image.getParent()).getHeight();*/




                /*Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                 bitmap= decodeSampledBitmapFromResource(getResources(), context.getResources().getIdentifier(feed.getBigImage(), "drawable", context.getPackageName()),parentWidth , parentHeight)
                }
            });
            t.start();*/

            //new LongOperation(holder.big_image).execute(new MyTaskParams(parentWidth, parentHeight, feed.getBigImage(), context));

            //holder.big_image.setImageBitmap(bitmap);


            //Picasso.with(context).load(feed.getImage()).resize(500,400).into(wView.getImage());
            if(position < 3)
                Glide.with(WallpaperFragment.this)
                        .load(feed.getImage()).diskCacheStrategy(DiskCacheStrategy.RESULT).override(500,400).crossFade().priority(Priority.IMMEDIATE)
                        .into(wView.getImage());

            else
                Glide.with(WallpaperFragment.this)
                        .load(feed.getImage()).diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade()
                        .into(wView.getImage());
            //holder.big_image.set

            return convertView;


        }


    }

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
    private static void startPicasso(Context context) {
        Picasso picasso = sPicasso;
        if (null == picasso) {
            synchronized (MainActivity.class) {
                picasso = sPicasso;
                if (null == picasso) {
                    sPicasso = new Picasso.Builder(context.getApplicationContext()).build();
                }
            }
        }
    }



    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(WallpaperFragment.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(WallpaperFragment.this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(WallpaperFragment.this.getActivity(), "Write permission allows us to share data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(WallpaperFragment.this.getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(getListView(),"Permission Granted, Now you can share photos.",Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getListView(), "Permission Denied, You cannot share photos.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(Environment.getExternalStorageDirectory()+ File.separator +"share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public class WrapperView
    {
        private View base;
        private ImageView img;

        public WrapperView(View base)
        {
            this.base = base;
        }

        public final ImageView getImage()
        {
            if (img == null)
            {
                img = (ImageView) base.findViewById(R.id.big_image);
            }
            return (img);
        }
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp = null;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                mKeyValueStore = KeyValueStore.getInstance(WallpaperFragment.this.getContext().getApplicationContext(), "QuotesCounter");
                if(mKeyValueStore.getInt("counter",0) == 0) {
                    mKeyValueStore.putInt("counter", 1);
                    Log.d("Grammar", "count when first time " + mKeyValueStore.getInt("counter", 1));
                }

                NetworkHandler hdlr = new NetworkHandler();
                mResponse = hdlr.connect("http://motivationpics.s3-ap-southeast-1.amazonaws.com/grammar/wallpapergrammar.txt");
                if(mResponse != null) {
                    Parser.parseWallpaperGrammar(WallpaperFragment.this.getContext(),mResponse.getResponse(), mKeyValueStore.getInt("counter", 1));
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
                mKeyValueStore4WallpaperInit.putBoolean("init", true);
                mKeyValueStore4WallpaperInit.putString("wallpaperGrammar",mResponse.getResponse());
                mFeedItems = FeedDataWallpapers.generateFeedItems(mKeyValueStore.getInt("counter", 1));
                mFeedAdapter = new FeedItemAdapter(getActivity(), mFeedItems);
                setListAdapter(mFeedAdapter);
            }
            else
                mKeyValueStore4WallpaperInit.putBoolean("init",false);

        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(WallpaperFragment.this.getContext(),
                    "ProgressDialog",
                    "Wait for few seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
