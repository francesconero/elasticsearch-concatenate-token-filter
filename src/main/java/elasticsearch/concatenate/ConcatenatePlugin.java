package org.elasticsearch.index.analysis.concatenate;

import java.util.Collection;
import org.elasticsearch.index.analysis.AnalysisModule;

import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.plugins.Plugin;

public class ConcatenatePlugin extends Plugin {
    @Override
    public String name() {
        return "analysis-concatenate";
    }

    @Override
    public String description() {
        return "Plugin that provides a Token Filter that recombines all of the tokens in a token stream back into one.";
    }

    public void onModule(AnalysisModule module) {
        module.addTokenFilter("concatenate", ConcatenateTokenFilterFactory.class);
    }
}
