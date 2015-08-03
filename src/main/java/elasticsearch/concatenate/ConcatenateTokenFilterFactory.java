package org.elasticsearch.index.analysis.concatenate;

import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;

import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;

import org.apache.lucene.util.Version;

public class ConcatenateTokenFilterFactory extends AbstractTokenFilterFactory {

    private String tokenSeparator = null;
    private int incrementGap = 100;
    
    @Inject 
    public ConcatenateTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        // the token_separator is defined in the ES configuration file
        tokenSeparator = settings.get("token_separator");
	incrementGap = settings.getAsInt("increment_gap", 100);
    }

    @Override 
    public TokenStream create(TokenStream tokenStream) {
        return new ConcatenateFilter(Version.LATEST, tokenStream, tokenSeparator, incrementGap);
    }

}
