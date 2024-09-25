package com.forums.forums.model.dao;
import com.forums.forums.model.mo.*;

import java.sql.Timestamp;
import java.util.List;

public interface FAQDAO {
    public FAQ create(
            String question,
            String answer,
            Timestamp creationTimestamp,
            User author
    );
    public void update(FAQ faq);
    public void delete(FAQ faq);
    public List<FAQ> getAll();
}
