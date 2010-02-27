package hk.reality.stock.service.fetcher;


public class QuoteFetcherFactory {
    public static QuoteFetcher getQuoteFetcher(String ts) {
        return new Money18QuoteFetcher(ts);
    }
}
