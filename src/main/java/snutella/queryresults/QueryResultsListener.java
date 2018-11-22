package snutella.queryresults;

import java.util.List;

public interface QueryResultsListener {
    void resultsChanged(List<QueryResultItem> items);
}
