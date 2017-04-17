package ca.ualberta.benchmark.query;

/**
 * Created by nouhadziri on 2017-04-16.
 */
public class RelationQuery extends KeywordQuery {
    private final String relation;

    public RelationQuery(String id, String keyword, String relation) {
        super(id, keyword);
        this.relation = relation;
    }

    @Override
    public String toString() {
        return String.format("%s relation:\"%s\"", super.toString(), relation);
    }

    public String getRelation() {
        return relation;
    }
}
