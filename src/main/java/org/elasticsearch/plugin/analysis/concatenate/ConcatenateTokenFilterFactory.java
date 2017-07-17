package org.elasticsearch.plugin.analysis.concatenate;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.IndexSettings;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

public class ConcatenateTokenFilterFactory extends AbstractTokenFilterFactory {
    private String tokenSeparator = null;
    private int incrementGap = 100;

    public ConcatenateTokenFilterFactory(IndexSettings indexSettings, Environment environment,
                                         String name, Settings settings) {
        super(indexSettings, name, settings);
        // the token_separator is defined in the ES configuration file
        tokenSeparator = settings.get("token_separator");
        incrementGap = settings.getAsInt("increment_gap", 100);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new ConcatenateFilter(Version.LATEST, tokenStream, tokenSeparator, incrementGap);
    }
}
