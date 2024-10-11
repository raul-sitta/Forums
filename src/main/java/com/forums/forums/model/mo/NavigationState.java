package com.forums.forums.model.mo;

// Il model object NavigationState è stato pensato per essere implementato solo lato cookies
// (e non nel database). Ha la funzione di contenere le informazioni riguardo allo stato di navigazione
// degli utenti, per consentire una navigazione pulita e coerente nel sito (ad esempio per tornare nella pagina
// di topic corretta quando si esce dalla visualizzazione dei post di uno specifico topic).
public class NavigationState {

    // Questo parametro indica l'ID del topic che l'utente sta visualizzando (se l'utente non sta
    // visualizzando alcun topic sarà uguale a null)
    private Long topicID;

    // Questo parametro indica l'ID della pagina di topic che l'utente sta visualizzando (es. pagina 1 di 2, ecc..)
    private Long topicsCurrentPageIndex;

    // Questo parametro indica se le pagine di topic che l'utente sta visualizzando siano risultato di una ricerca
    // tramite filtro di topic o meno.
    private Boolean topicsSearchResultFlag;

    // Questo parametro indica l'ID della pagina di post che l'utente sta visualizzando (es. pagina 1 di 2, ecc..)
    private Long postsCurrentPageIndex;

    public Long getTopicID() {
        return topicID;
    }

    public void setTopicID(Long topicID) {
        this.topicID = topicID;
    }

    public Long getTopicsCurrentPageIndex() {
        return topicsCurrentPageIndex;
    }

    public void setTopicsCurrentPageIndex(Long topicsCurrentPageIndex) {
        this.topicsCurrentPageIndex = topicsCurrentPageIndex;
    }

    public Boolean getTopicsSearchResultFlag() {
        return topicsSearchResultFlag;
    }

    public void setTopicsSearchResultFlag(Boolean topicsSearchResultFlag) {
        this.topicsSearchResultFlag = topicsSearchResultFlag;
    }

    public Long getPostsCurrentPageIndex() {
        return postsCurrentPageIndex;
    }

    public void setPostsCurrentPageIndex(Long postsCurrentPageIndex) {
        this.postsCurrentPageIndex = postsCurrentPageIndex;
    }
}
