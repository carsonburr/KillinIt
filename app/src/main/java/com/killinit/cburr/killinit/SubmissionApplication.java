package com.killinit.cburr.killinit;

import android.app.Application;
import android.util.Log;

import com.simperium.Simperium;
import com.simperium.client.Bucket;
import com.simperium.client.BucketNameInvalid;

/**
 * Created by Owner on 10/24/2015.
 */
public class SubmissionApplication extends Application {

    private Simperium mSimperium;
    private Bucket<Submission> mSubmissionBucket;

    @Override
    public void onCreate() {
        super.onCreate();

        mSimperium = Simperium.newClient(BuildConfig.SIMPERIUM_APP, BuildConfig.SIMPERIUM_KEY, this);

        try {
            mSubmissionBucket = mSimperium.bucket("submission", new Submission.Schema());
        } catch (BucketNameInvalid bucketNameInvalid) {
            Log.i("KillinIt", "Could not create bucket");
        }
    }

    // Getters
    public Simperium getSimperium() {
        return mSimperium;
    }

    public Bucket<Submission> getmSubmissionBucket() {
        return mSubmissionBucket;
    }
}
