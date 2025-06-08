package io.java.betterreads.search;

import java.util.List;

public class SearchResult {

    private int numFound;

    private List<SearchResultBook> docs;

    public List<SearchResultBook> getDocs() {
        return docs;
    }

    public void setDocs(List<SearchResultBook> docs) {
        this.docs = docs;
    }

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }
}
