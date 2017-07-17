package org.elasticsearch.plugin.analysis.concatenate;

import java.util.Collections;
import java.util.Map;

import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class ConcatenatePlugin extends Plugin implements AnalysisPlugin {
    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return Collections.singletonMap(ConcatenateFilter.FILTER_NAME, ConcatenateTokenFilterFactory::new);
    }
}
