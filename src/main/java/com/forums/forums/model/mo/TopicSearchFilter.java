package com.forums.forums.model.mo;

import java.sql.Timestamp;

// Il model object "TopicSearchFilter" è stato pensato per essere implementato solo lato cookies
// (e non nel database). Ha la funzione di contenere i parametri del filtro di ricerca dei topic,
// permettendo così all'utente di visualizzare solo i topic di proprio interesse.
// N.B. tutti i parametri sono opzionali: possono assumere valore null nel caso in cui un
// utente non abbia preferenze per quanto riguarda il parametro specifico
// (quando l'utente navigherà per la prima volta nella sezione "Supporto" il filtro sarà impostato
// con tutti i parametri a null).
public class TopicSearchFilter {
    private String title;
    private String authorName;
    private String categoryName;
    private Timestamp moreRecentThan;
    private Timestamp olderThan;
    private Boolean isAnonymous;
    private Boolean sortNewestFirst;

    public Boolean getSortNewestFirst() {
        return sortNewestFirst;
    }

    public void setSortNewestFirst(Boolean sortNewestFirst) {
        this.sortNewestFirst = sortNewestFirst;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Timestamp getMoreRecentThan() {
        return moreRecentThan;
    }

    public void setMoreRecentThan(Timestamp moreRecentThan) {
        this.moreRecentThan = moreRecentThan;
    }

    public Timestamp getOlderThan() {
        return olderThan;
    }

    public void setOlderThan(Timestamp olderThan) {
        this.olderThan = olderThan;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }
}
