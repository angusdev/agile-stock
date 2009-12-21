package hk.reality.stock;

import hk.reality.util.VersionUtils;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class MainActivity extends TabActivity {    
    public static final String TAB_STOCK = "stock";
    public static final String TAB_INDEX = "index";
    public static final String TAB_QUICK = "quick";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        TabHost host = getTabHost();        
        Drawable moneySelector = getResources().getDrawable(R.drawable.money);
        host.addTab(host
                .newTabSpec(TAB_STOCK)
                .setIndicator(getResources().getString(R.string.tab_stock), moneySelector)
                .setContent(new Intent(this, PortfolioActivity.class)));

        Drawable idxSelector = getResources().getDrawable(R.drawable.index);        
        host.addTab(host
                .newTabSpec(TAB_INDEX)
                .setIndicator(getResources().getString(R.string.tab_index), idxSelector)
                .setContent(new Intent(this, IndexActivity.class)));

//        Drawable quickSelector = getResources().getDrawable(R.drawable.quick);        
//        host.addTab(host
//                .newTabSpec(TAB_QUICK)
//                .setIndicator(getResources().getString(R.string.tab_quick), quickSelector)
//                .setContent(new Intent(this, QuickStockActivity.class)));
        
        checkUpdate();
    }
    

    private void checkUpdate() {
        AsyncTask<Void, Void, Void> updateTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... arg0) {
                VersionUtils.checkAndShowUpdate(MainActivity.this, 
                        MainActivity.this.getCurrentFocus(),
                        getResources().getString(R.string.update_url), 
                        getResources().getString(R.string.update_package),
                        getResources().getString(R.string.update_available), 
                        getResources().getString(R.string.update_download), 
                        getResources().getString(R.string.update_later), 
                        getResources().getString(R.string.update_ignore));
                return null;
            }  
        };
        updateTask.execute();
    }
}
