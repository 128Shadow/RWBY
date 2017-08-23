package ogd.rwbyrisingstorms.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ogd.rwbyrisingstorms.contracts.DeckContract;
import ogd.rwbyrisingstorms.managers.TypedCursor;

public class QueryBuilder implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = QueryBuilder.class.getSimpleName();

    private Context mContext;
    private Uri mUri;
    private String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;
    private int mLoaderId;
    private IQueryListener mListener;

    public static interface IQueryListener<T> {

        public void handleResults(Cursor cursor);

        public void closeResults();

    }

    private QueryBuilder(Context context,
                               Uri uri,
                               String[] projection,
                               String selection,
                               String[] selectionArgs,
                               String sortOrder,
                               int loaderId,
                               IQueryListener listener)
    {
        mContext = context;
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mLoaderId = loaderId;
        mListener = listener;
    }

    // TODO complete the implementation of this

    public static <T> void executeQuery(Activity activity,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        String sortOrder,
                                        int loaderId,
                                        IQueryListener listener)
    {
        QueryBuilder queryBuilder = new QueryBuilder(activity,
                uri, projection, selection, selectionArgs, sortOrder, loaderId, listener);

        activity.getLoaderManager().restartLoader(loaderId, null, queryBuilder);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //AsyncContentResolver contentResolver = new AsyncContentResolver(view.getContentResolver());
        //return new CursorLoader(view, DeckContract.CONTENT_URI, null, null, null, null);
        if (id == mLoaderId) {
            return new CursorLoader(mContext, mUri, mProjection, mSelection, mSelectionArgs, mSortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //ListView cartListView = (ListView) view.findViewById(android.R.id.list);
        //SimpleCursorAdapter curAdapter = (SimpleCursorAdapter) cartListView.getAdapter();
        //curAdapter.swapCursor(cursor);
        if (loader.getId() == mLoaderId) {
            mListener.handleResults(cursor);
        }
        Log.i("QB", "LF");
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == mLoaderId) {
            mListener.closeResults();
        }
        Log.i("QB", "LR");
    }
}
