package hk.reality.stock.service.fetcher;

import hk.reality.stock.IndexActivity;
import hk.reality.stock.R;
import hk.reality.stock.model.Index;
import hk.reality.stock.service.Money18Service;
import hk.reality.stock.service.exception.DownloadException;
import hk.reality.stock.service.exception.ParseException;
import hk.reality.stock.view.IndexAdapter;
import hk.reality.util.ActivityHelper;
import hk.reality.util.NetworkDetector;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class IndexesUpdateTask extends AsyncTask<Void, Integer, Boolean> {
    public static final String TAG = "IndexesUpdateTask";
    private IndexActivity activity;
    private List<Index> results;

    private Error error;
    enum Error {
        ERROR_NO_NET, ERROR_DOWNLOAD, ERROR_PARSE, ERROR_UNKNOWN
    }
    
    public IndexesUpdateTask(IndexActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Void ... ignored) {
        Log.i(TAG, "running indexes update in background");
        if (!NetworkDetector.hasValidNetwork(activity)) {
            error = Error.ERROR_NO_NET;
            return Boolean.FALSE;
        }

        // run this before fetching quote
        // this fetch the proper timestamp modifier for money18.on.cc
        String ts = Money18Service.getInstance().getTimestamp();
        IndexesFetcher fetcher = IndexesFetcherFactory.getIndexesFetcher(activity, ts);
        
        try {
            results = fetcher.fetch();
            return Boolean.TRUE;
        } catch (DownloadException de) {
            Log.e(TAG, "error downloading stock", de);
            error = Error.ERROR_DOWNLOAD;
            return Boolean.FALSE;
        } catch (ParseException pe) {
            Log.e(TAG, "error parsing code", pe);
            error = Error.ERROR_PARSE;
            return Boolean.FALSE;
        } catch (RuntimeException re) {
            Log.e(TAG, "unexpected error while update stock quote", re);
            error = Error.ERROR_UNKNOWN;
            return Boolean.FALSE;
        } 
    }

    private void updateIndexes(List<Index> indexes) {
        IndexAdapter adapter = activity.getIndexAdapter();
        adapter.clear();
        for(Index i : indexes) {
            adapter.add(i);
        }
        adapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onPreExecute() {
        activity.getParent().setProgressBarVisibility(true);
        activity.getParent().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected void onCancelled() {
        activity.getParent().setProgressBarVisibility(false);
        activity.getParent().setProgressBarIndeterminateVisibility(false);
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
        activity.getParent().setProgressBarVisibility(false);
        activity.getParent().setProgressBarIndeterminateVisibility(false);
        if (result) {
            Log.i(TAG, "update success, number of results ..." + results.size());
            updateIndexes(results);
        } else {
            switch (error) {
            case ERROR_NO_NET:
                Toast.makeText(activity, R.string.msg_no_network, Toast.LENGTH_LONG).show();
                break;
            case ERROR_DOWNLOAD:
                ActivityHelper.showDialogQuitely(activity, 
                        IndexActivity.DIALOG_ERR_DOWNLOAD);
                break;
            case ERROR_PARSE:
                ActivityHelper.showDialogQuitely(activity,
                        IndexActivity.DIALOG_ERR_PARSE);
                break;
            case ERROR_UNKNOWN:
                ActivityHelper.showDialogQuitely(activity,
                        IndexActivity.DIALOG_ERR_UNEXPECTED);
                break;
            default:
                break;
            }
        }
    }
}
