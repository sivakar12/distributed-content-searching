package snutella.queryresults;

import java.util.Set;

public interface QueryResultsListener {
    void resultsChanged(Set<QueryResultItem> items);
}
