package ogd.rwbyrisingstorms.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import ogd.rwbyrisingstorms.async.AsyncContentResolver;
import ogd.rwbyrisingstorms.async.IEntityCreator;
import ogd.rwbyrisingstorms.async.QueryBuilder;
import ogd.rwbyrisingstorms.async.SimpleQueryBuilder;

/**
 * Created by luismeneses on 7/12/17.
 */

public abstract class Manager<T> {

    private final Context context;

    private final IEntityCreator<T> creator;

    private final int loaderID;

    private final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }

    protected void executeSimpleQuery(Uri uri,
                                      QueryBuilder.IQueryListener<T> listener) {
        SimpleQueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, creator, listener);
    }
}
