package hk.reality.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PriceFormatter {
    private static final DecimalFormat stockPriceFormat = new DecimalFormat("#,##0.000");
    private static final DecimalFormat indexPriceFormat = new DecimalFormat("#,##0.00");
    private static final DecimalFormat percentFormat = new DecimalFormat("#,##0.00%");

    public static String forStockPrice(double value) {
        return stockPriceFormat.format(value);
    }
    
    public static String forStockPrice(BigDecimal value) {
        return stockPriceFormat.format(value);
    }

    public static String forIndexPrice(double value) {
        return indexPriceFormat.format(value);
    }
    
    public static String forIndexPrice(BigDecimal value) {
        return indexPriceFormat.format(value);
    }

    public static String forPercent(double value) {
        return percentFormat.format(value);
    }
    public static String forPercent(BigDecimal value) {
        return percentFormat.format(value);
    }
}
