package hk.reality.stock.service.fetcher;

import hk.reality.stock.R;
import hk.reality.stock.model.Index;
import hk.reality.stock.service.exception.DownloadException;
import hk.reality.stock.service.exception.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Money18IndexesFetcher extends BaseIndexesFetcher {
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";
    private static final String TAG = "Money18IndexesFetcher";

    private Context context;
    private String timestamp;
    
    public Money18IndexesFetcher(Context context, String timestamp) {
        this.context = context;
        this.timestamp = timestamp;
    }

    @Override
    public List<Index> fetch() throws DownloadException, ParseException {
        List<Index> indexes = new ArrayList<Index>();
        indexes.add(getHsi());
        indexes.addAll(getWorldIndexes());
        return indexes;
    }

    private Index getHsi() throws ParseException, DownloadException {
        try {
            Index hsi = new Index();
            HttpGet req = new HttpGet(getHSIURL());
            req.addHeader(new BasicHeader("Referer", getReferer()));
    
            HttpResponse resp = getClient().execute(req);
            String content = EntityUtils.toString(resp.getEntity());
            JSONObject json = preprocessJson(content);
            
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            Date updateTime = formatter.parse(json.getString("ltt"));
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(updateTime);
            hsi.setUpdatedAt(updatedAt);
            
            BigDecimal value = new BigDecimal(json.getString("value"));
            BigDecimal change = new BigDecimal(json.getString("difference"));
            
            hsi.setName(getContext().getString(R.string.msg_hsi));
            hsi.setValue(value);
            hsi.setChange(change);
            hsi.setChangePercent(change.divide(value, MathContext.DECIMAL64));
    
            return hsi;
        } catch (org.apache.http.ParseException pe) {
            throw new ParseException("error parsing http data", pe);
        } catch (JSONException je) {
            throw new ParseException("error parsing http data", je);
        } catch (IOException ie) {
            throw new DownloadException("error parsing http data", ie);
        } catch (java.text.ParseException e) {
            throw new ParseException("error parsing json data", e);
        }
    }
    
    private String getHSIURL() {
        String url = String.format("http://money18.on.cc/js/real/index/HSI_r.js?t=%s", 
                timestamp);
        Log.d(TAG, "HSIURL: " + url);
        return url;
    }
    
    private String getWorldIndexURL() {
        String url = String.format("http://money18.on.cc/js/daily/worldidx/worldidx_b.js?t=%s", 
                timestamp);
        Log.d(TAG, "WorldIndexURL: " + url);
        return url;
    }
    
    private JSONObject preprocessJson(String content) throws JSONException {
        int pos = content.indexOf('{');
        String result = StringUtils.substring(content, pos);
        JSONObject json = new JSONObject(result);
        return json;
    }
    
    private List<Index> getWorldIndexes()  throws ParseException, DownloadException {
        try {
            HttpGet req = new HttpGet(getWorldIndexURL());
            req.addHeader(new BasicHeader("Referer", getReferer()));
    
            HttpResponse resp = getClient().execute(req);
            String content = EntityUtils.toString(resp.getEntity(), "Big5");
            return getWorldIndexesFromJson(content);
        } catch (org.apache.http.ParseException pe) {
            throw new ParseException("error parsing http data", pe);
        } catch (JSONException je) {
            throw new ParseException("error parsing http data", je);
        } catch (IOException ie) {
            throw new DownloadException("error parsing http data", ie);
        }
    }
    
    private List<Index> getWorldIndexesFromJson(String content) throws JSONException {
        List<Index> indexes = new ArrayList<Index>();
        int start = content.indexOf('{');
        while (start > 0) {
            int end = content.indexOf(";", start);
            String result = StringUtils.substring(content, start, end);
            JSONObject json = new JSONObject(result);
            String name = json.getString("Name");
            String value = json.getString("Point");
            String diff = json.getString("Difference");
            Index index = new Index();
            index.setName(name);
            index.setValue(new BigDecimal(value));
            
            if (diff != null && !StringUtils.equalsIgnoreCase(diff, "null")) {
                index.setChange(new BigDecimal(diff));
            } else {
                index.setChange(null);
            }

            if (diff != null && !StringUtils.equalsIgnoreCase(diff, "null")){
                double valueD = json.getDouble("Point");
                double diffD = json.getDouble("Difference");
                index.setChangePercent(new BigDecimal(diffD/(valueD-diffD)));
            } else {
                index.setChangePercent(null);
            }

            indexes.add(index);
            start = content.indexOf('{', end);
        }
        return indexes;
    }

    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(Context context) {
        this.context = context;
    }

    private String getReferer() {
        return "http://money18.on.cc/info/liveinfo_idx.html&refer=refresh";
    }
}
