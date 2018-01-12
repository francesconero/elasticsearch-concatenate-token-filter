package elasticsearch.concatenate;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.IndexSettings;

public class ConcatenateTokenFilterFactory extends AbstractTokenFilterFactory {

    private String tokenSeparator = null;
    private int incrementGap = 100;

    @Inject
    public ConcatenateTokenFilterFactory(IndexSettings indexSettings, String name, Settings settings) {
        super(indexSettings, name, settings);
        tokenSeparator = settings.get("token_separator", null);
        incrementGap = settings.getAsInt("increment_gap", 100);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ConcatenateFilter(tokenStream, tokenSeparator, incrementGap);
    }
}
