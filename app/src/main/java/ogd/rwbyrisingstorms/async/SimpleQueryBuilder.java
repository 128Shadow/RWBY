package ogd.rwbyrisingstorms.async;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor>{

    private IEntityCreator<T> mCreator;
    //private ISimpleQueryListener<T> mListener;

    private QueryBuilder.IQueryListener mListener;


    public SimpleQueryBuilder(IEntityCreator<T> creator, QueryBuilder.IQueryListener listener) {
        mCreator = creator;
        mListener = listener;
    }



    public interface ISimpleQueryListener<T> {
        public void handleResults(List<T> results);
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        QueryBuilder.IQueryListener<T> listener) {
        SimpleQueryBuilder queryBuilder = new SimpleQueryBuilder(creator,
                listener);
    }

    public void kontinue(Cursor cursor) {
        mListener.handleResults(cursor);
        /*
        List<T> instances = new ArrayList<T>();
        if (cursor.moveToFirst())
        {
            do
            {
                T instance = mCreator.create(cursor);
                instances.add(instance);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        mListener.handleResults(instances);
        */
    }

}
