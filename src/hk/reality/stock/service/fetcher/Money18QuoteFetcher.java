package hk.reality.stock.service.fetcher;

import static hk.reality.stock.service.fetcher.Utils.preprocessJson;
import hk.reality.stock.model.StockDetail;
import hk.reality.stock.service.exception.DownloadException;
import hk.reality.stock.service.exception.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Money18QuoteFetcher extends BaseQuoteFetcher {
    private static final String TAG = "Money18QuoteFetcher";
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm";
    private String timestamp;
    
    public Money18QuoteFetcher(String timestamp){
        this.timestamp = timestamp;
    }

    @Override
    public StockDetail fetch(String quote) throws DownloadException, ParseException {
        StockDetail d = new StockDetail();
        String content = null;

        String openUrl = getOpenUrl(quote);
        HttpGet openReq = new HttpGet(openUrl);
        try {
            openReq.addHeader("Referer", openUrl);
            HttpResponse resp = getClient().execute(openReq);
            content = EntityUtils.toString(resp.getEntity());
            JSONObject json = preprocessJson(content);
            String preClosePriceStr = json.getString("preCPrice");
            BigDecimal preClose = new BigDecimal(preClosePriceStr);
            
            String updateUrl = getUpdateUrl(quote);
            HttpGet req = new HttpGet(updateUrl);
            req.addHeader("Referer", updateUrl);

            resp = getClient().execute(req);
            content = EntityUtils.toString(resp.getEntity());
            json = preprocessJson(content);

            d.setPrice(new BigDecimal(json.getString("np")));
            d.setChangePrice(d.getPrice().subtract(preClose));
            
            d.setChangePricePercent(preClose.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : d.getChangePrice().divide(preClose, MathContext.DECIMAL64));
            d.setDayHigh(new BigDecimal(json.getString("dyh")));
            d.setDayLow(new BigDecimal(json.getString("dyl")));
            d.setQuote(quote);
            d.setSourceUrl(getUrl(quote));
            
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            Date updateTime = formatter.parse(json.getString("ltt"));
            Calendar updatedAt = Calendar.getInstance();
            updatedAt.setTime(updateTime);
            d.setUpdatedAt(updatedAt);
            d.setVolume(new BigDecimal(json.getString("vol")).toPlainString());
            
            return d;
        } catch (ClientProtocolException e) {
            openReq.abort();
            throw new DownloadException("protocol exception", e);
        } catch (IOException e) {
            openReq.abort();
            throw new DownloadException("download stock error", e);
        } catch (JSONException e) {
            openReq.abort();
            throw new ParseException("unexpected return value," +
                    " content = " + content, e);
        } catch (java.text.ParseException e) {
            openReq.abort();
            throw new ParseException("failed to parse date format," +
            		" content = " + content, e);
        }
    }

    @Override
    public String getUrl(String quote) {
        return String.format("http://money18.on.cc/info/liveinfo_quote.html?symbol=%s", quote);
    }
    
    private String getOpenUrl(String quote) {
        String url = String.format("http://money18.on.cc/js/daily/quote/%s_d.js?t=%s", 
                quote, timestamp); 
        Log.d(TAG, "open url: " + url);
        return url;
    }
    
    private String getUpdateUrl(String quote) {
        String url = String.format("http://money18.on.cc/js/real/quote/%s_r.js?t=%s", 
                quote, 
                timestamp);
        Log.d(TAG, "update url: " + url);
        return url;
    }

}
