package com.killinit.cburr.killinit;

import com.simperium.client.Bucket;
import com.simperium.client.BucketObject;
import com.simperium.client.BucketSchema;
import com.simperium.client.Query;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Owner on 10/24/2015.
 */
public class Submission extends BucketObject {

    private static final String IMAGE_URL_PROPERTY = "url";
    private static final String USER_ID_PROPERTY = "uuid";
    private static final String TIME_STAMP_PROPERTY = "time";
    private static final String USERNAME_PROPERTY = "user";
    private static final String CAPTION_PROPERTY = "caption";
    private static final String ORDER_PROPERTY = "order";

    private Submission(String key, JSONObject properties) {
        super(key, properties);
    }

    public static class Schema extends BucketSchema<Submission> {

        public static final String BUCKET_NAME = "submission";

        public Schema() {
            autoIndex();
        }

        public String getRemoteName() {
            return BUCKET_NAME;
        }

        @Override
        public Submission build(String key, JSONObject properties) {
            return new Submission(key, properties);
        }

        @Override
        public void update(Submission submission, JSONObject properties) {
            submission.updateProperties(properties);
            android.util.Log.d("KillinIt", "Updated properties: " + submission);
        }
    }

    public static Query<Submission> queryAll(Bucket<Submission> bucket) {
        Query<Submission> query = bucket.query();
        query.order(TIME_STAMP_PROPERTY);
        return query;
    }

    private void updateProperties(JSONObject properties) {
        this.setProperties(properties);
    }

    public String getURL() {
        return getProperties().optString(IMAGE_URL_PROPERTY, "");
    }

    public String getUUID() {
        return getProperties().optString(USER_ID_PROPERTY, "");
    }

    public String getTime() {
        return getProperties().optString(TIME_STAMP_PROPERTY, "");
    }

    public String getUser() {
        return getProperties().optString(USERNAME_PROPERTY, "");
    }

    public String getCaption() {
        return getProperties().optString(CAPTION_PROPERTY, "");
    }

    public int getOrder() {
        return getProperties().optInt(ORDER_PROPERTY);
}

    public void setURL(String url) {
        setProperty(IMAGE_URL_PROPERTY, url);
    }

    public void setOrder(int order) {
        setProperty(ORDER_PROPERTY, order);
        android.util.Log.d("Killin' It", Integer.toString(order));
    }

    public void setCaption(String caption) {
        setProperty(CAPTION_PROPERTY, caption);
    }

    public void setTime(long time) {
        setProperty(TIME_STAMP_PROPERTY, -time);
    }
}
