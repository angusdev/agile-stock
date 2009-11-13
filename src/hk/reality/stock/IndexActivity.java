package hk.reality.stock;

import hk.reality.stock.service.fetcher.IndexesUpdateTask;
import hk.reality.stock.view.IndexAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class IndexActivity extends BaseStockActivity {
    public static final String TAG = "IndexActivity";
    public static final int DIALOG_ERR_DOWNLOAD = 410;
    public static final int DIALOG_ERR_PARSE = 411;    
    private IndexAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview);
        
        adapter = new IndexAdapter(this);
        setListAdapter(adapter);
        
        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setText(R.string.msg_loading);
        
        Log.i(TAG, "start index activity");
        IndexesUpdateTask task = new IndexesUpdateTask(this);
        task.execute();
    }
    
    public IndexAdapter getIndexAdapter() {
        return adapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu, menu);
        menu.getItem(0).setIcon(R.drawable.ic_menu_rotate);
        menu.getItem(1).setIcon(R.drawable.ic_menu_help);        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh:
            IndexesUpdateTask task = new IndexesUpdateTask(this);
            task.execute();
            return true;
        default:
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_ERR_PARSE:
            final AlertDialog quoteErrDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.msg_error_stock)
                .setMessage(R.string.msg_error_stock_details)
                .setPositiveButton(R.string.ok_label, new OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }                    
                })
                .setCancelable(true)
                .create();
            return quoteErrDialog;
        case DIALOG_ERR_DOWNLOAD:
            final AlertDialog quoteUpdateErrDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.msg_error_download)
                .setPositiveButton(R.string.ok_label, new OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }                    
                })
                .setMessage(R.string.msg_error_download_details)
                .setCancelable(true)
                .create();
            return quoteUpdateErrDialog;
        default:
        }
        return super.onCreateDialog(id);
    }

}
