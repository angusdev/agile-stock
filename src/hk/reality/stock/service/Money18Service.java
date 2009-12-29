package hk.reality.stock.service;

import hk.reality.util.WebUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;

import android.util.Log;

public class Money18Service {
    public static final String TAG = "Money18Service";
    
    private static Money18Service instance;
    private static String cachedReplacement = null;
    
    public synchronized static Money18Service getInstance() {
        if (instance == null) {
            instance = new Money18Service();
        }
        return instance;
    }

    /**
     * get a processed timestamp for money18.on.cc
     * @return
     */
    public String getTimestamp() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong Kong"));
        String ts = cal.getTime().getTime() + "";
        return modifyTimestamp(ts, "([0-9]{10})([0-9])([0-9]{2})", findReplacement());
    }
    
    private String modifyTimestamp(String ts, String pattern, String replacement) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(ts);
        if (m.find()) {
            return m.group(1) + replacement + m.group(3);
        } else {
            return ts;
        }
    }
    
    private synchronized String findReplacement() {
        if (cachedReplacement != null) {
            Log.d(TAG, "use cached m18-lib.js");
            return cachedReplacement;
        }

        try {
            Log.d(TAG, "reading latest m18-lib.js");
            String lib = WebUtil.fetch("http://money18.on.cc/js/m18-lib.js");
            if (!StringUtils.isEmpty(lib)) {
                Pattern p = Pattern.compile("\"([0-9])\"\\+a\\.substring");
                Matcher m = p.matcher(lib);
                if (m.find()) {
                    cachedReplacement = m.group(1);
                    Log.i(TAG, "replacement=" + cachedReplacement);
                    return cachedReplacement;
                } else {
                    Log.w(TAG, "replacement not found");
                }
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "http error", e);
        } catch (IOException e) {
            Log.e(TAG, "failed loading page", e);
        } catch (RuntimeException re) {
            Log.e(TAG, "unexpected error", re);
        }
        return "3";        
    }
    
    /**
     * clear cache
     */
    public synchronized static void clearCache() {
        cachedReplacement = null;
    }
}
