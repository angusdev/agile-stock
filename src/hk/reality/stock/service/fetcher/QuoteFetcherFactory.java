package hk.reality.stock.service.fetcher;

import hk.reality.stock.service.Money18Service;

public class QuoteFetcherFactory {
    public static QuoteFetcher getQuoteFetcher() {
        return new Money18QuoteFetcher(Money18Service.getInstance());
    }
}
