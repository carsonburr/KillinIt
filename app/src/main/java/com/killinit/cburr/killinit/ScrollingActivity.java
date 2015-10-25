package com.killinit.cburr.killinit;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.simperium.client.Bucket;
import com.simperium.client.BucketObjectMissingException;
import com.squareup.picasso.Picasso;

public class ScrollingActivity extends AppCompatActivity
    implements Bucket.Listener<Submission>{

    private SubmissionAdapter mAdapter;
    private Bucket<Submission> mSubmissionBucket;
    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.submission_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the TodoAdapter
        mAdapter = new SubmissionAdapter();
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        //listView.setOnItemClickListener(this);

        extras = getIntent().getExtras();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void createSubmission(Bundle extras) {
        if (extras != null) {
            String url = extras.getString("url");
            String caption = extras.getString("caption");
            Submission sub = mSubmissionBucket.newObject();
            sub.setURL(url);
            sub.setCaption(caption);
            sub.setTime(System.currentTimeMillis());
            sub.save();
        }
    }

    public void clearContent(View view) {
        mSubmissionBucket.reset();
        //createSubmission(extras);
    }

    public void submitContent(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);

        /*Submission sub = mSubmissionBucket.newObject();
        int picselect = (int)Math.floor(Math.random()*2);
        if (picselect == 0) { sub.setURL("http://i.ytimg.com/vi/lzWq_RLMNTM/maxresdefault.jpg"); }
        else if (picselect == 1) { sub.setURL("https://images-na.ssl-images-amazon.com/images/I/61qTg2tjkDL._UX250_.jpg"); }
        sub.setCaption("Killin' it");
        sub.setTime(System.currentTimeMillis());
        sub.save();*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        SubmissionApplication app = (SubmissionApplication) getApplication();

        mSubmissionBucket = app.getmSubmissionBucket();

        if (mSubmissionBucket != null) {
            mSubmissionBucket.addListener(this);
            mSubmissionBucket.start();
            refreshSubmissions(mSubmissionBucket);
        }


        createSubmission(extras);
    }

    @Override
    protected  void onPause() {
        if (mSubmissionBucket != null) {
            mSubmissionBucket.removeListener(this);
            mSubmissionBucket.stop();
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshSubmissions(final Bucket<Submission> submissions) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.requeryBucket(submissions);
            }
        });
    }

    class SubmissionAdapter extends CursorAdapter {
        SubmissionAdapter() {
            super (ScrollingActivity.this, null, false);
        }

        public void requeryBucket(Bucket<Submission> submissions) {
            swapCursor(Submission.queryAll(submissions).execute());
        }

        public Submission getItem(int position) {
            Bucket.ObjectCursor<Submission> cursor = (Bucket.ObjectCursor<Submission>) super.getItem(position);
            return cursor.getObject();
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Bucket.ObjectCursor<Submission> bucketCursor = (Bucket.ObjectCursor<Submission>) cursor;
            final Submission submission = bucketCursor.getObject();

            SubmissionRowHolder viewHolder = (SubmissionRowHolder) view.getTag(R.id.submission_row_holder);

            Spannable caption = new SpannableString(submission.getCaption());

            if (TextUtils.isEmpty(caption)) {
                caption = emptyCaption();
            }

            viewHolder.labelView.setText(caption);
            Picasso.with(context)
                    .load(submission.getURL())
                    .into(viewHolder.imageView);
        }

        public View newView(Context context, Cursor cusor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.submission_row, parent, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView textView = (TextView) view.findViewById(R.id.label);
            SubmissionRowHolder holder = new SubmissionRowHolder(textView, imageView);
            view.setTag(R.id.submission_row_holder, holder);
            return view;
        }



        private final class SubmissionRowHolder {
            public final TextView labelView;
            public final ImageView imageView;

            public SubmissionRowHolder(TextView tv, ImageView iv) {
                labelView = tv;
                imageView = iv;
            }
        }
    }

    private SpannableString emptyCaption() {
        SpannableString caption = new SpannableString((getString(R.string.empty_caption_title)));
        int length = caption.length();
        caption.setSpan(new StyleSpan(Typeface.ITALIC), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        caption.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.empty_task_text_color)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return caption;
    }

    @Override
    public void onSaveObject(Bucket<Submission> submissions, Submission submission) {
        refreshSubmissions(submissions);
    }

    @Override
    public void onDeleteObject(Bucket<Submission> submissions, Submission submission) {
        refreshSubmissions(submissions);
    }

    @Override
    public void onBeforeUpdateObject(Bucket<Submission> bucket, Submission submission) {
        // noop
    }

    @Override
    public void onNetworkChange(Bucket<Submission> submissions, Bucket.ChangeType changeType, String simperiumKey) {
        refreshSubmissions(submissions);
    }
}
