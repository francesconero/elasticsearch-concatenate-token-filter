package org.elasticsearch.index.analysis.concatenate;

import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class ConcatenatePlugin extends Plugin implements AnalysisPlugin
{
    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters()
    {
        return singletonMap("concatenate", ConcatenateTokenFilterFactory::new);
        // module.addTokenFilter("concatenate", ConcatenateTokenFilterFactory.class);
    }
}
