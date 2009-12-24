package hk.reality.stock.service.fetcher;

import hk.reality.stock.service.Money18Service;
import android.content.Context;

public class IndexesFetcherFactory {
    public static IndexesFetcher getIndexesFetcher(Context context) {
        return new Money18IndexesFetcher(context, Money18Service.getInstance());
    }
}
