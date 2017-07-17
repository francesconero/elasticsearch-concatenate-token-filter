package org.elasticsearch.plugin.analysis.concatenate;

import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.IndexAnalyzers;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.test.ESTokenStreamTestCase;
import org.elasticsearch.test.IndexSettingsModule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class ConcatenateFilterTest extends ESTokenStreamTestCase {
    private static String[] validTokenSeparators = new String[] {"-", "_", "+", ";", "::"};

    private Settings.Builder getSettingsPerTokenSeparator(String tokenSeparator) {
        Settings.Builder settingsBuilder = Settings.builder()
            .put(IndexMetaData.SETTING_VERSION_CREATED, org.elasticsearch.Version.CURRENT)
            .put("index.analysis.filter.concatenate.type", "concatenate")
            .put("index.analysis.filter.concatenate.token_separator", tokenSeparator)
            .put("index.analysis.filter.custom_stop.type", "stop")
            .putArray("index.analysis.filter.custom_stop.stopwords", "and", "is", "the")
            .put("index.analysis.analyzer.stop_concatenate.tokenizer", "standard")
            .putArray("index.analysis.analyzer.stop_concatenate.filter", "custom_stop", "concatenate")
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir().toString());
        return settingsBuilder;
    }

    private NamedAnalyzer getNamedAnalyzerWithDefaultIncrementGap(String tokenSeparator) throws IOException {
        Settings settings = getSettingsPerTokenSeparator(tokenSeparator).build();
        IndexSettings idxSettings = IndexSettingsModule.newIndexSettings("test", settings);
        AnalysisModule analysisModule = new AnalysisModule(new Environment(settings), Collections.singletonList(
            new AnalysisPlugin() {
                @Override
                public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
                    return Collections.singletonMap(ConcatenateFilter.FILTER_NAME, ConcatenateTokenFilterFactory::new);
                }
            }
        ));
        IndexAnalyzers indexAnalyzers = analysisModule.getAnalysisRegistry().build(idxSettings);
        return indexAnalyzers.get("stop_concatenate");
    }

    public NamedAnalyzer getNamedAnalyzerWithNewIncrementGap(String tokenSeparator, Integer incrementGap) throws IOException {
        Settings settings = getSettingsPerTokenSeparator(tokenSeparator)
            .put("index.analysis.filter.concatenate.increment_gap", incrementGap)
            .build();
        IndexSettings idxSettings = IndexSettingsModule.newIndexSettings("test", settings);
        AnalysisModule analysisModule = new AnalysisModule(new Environment(settings), Collections.singletonList(
            new AnalysisPlugin() {
                @Override
                public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
                    return Collections.singletonMap(ConcatenateFilter.FILTER_NAME, ConcatenateTokenFilterFactory::new);
                }
            }
        ));
        IndexAnalyzers indexAnalyzers = analysisModule.getAnalysisRegistry().build(idxSettings);
        return indexAnalyzers.get("stop_concatenate");
    }

    @Test
    public void testForAllSeparatorsWithDefaultIncrementGap() throws IOException {
        String input = "my name is bond, james bond";
        String inputWithoutStopWordsAndPunctuation = "my name bond james bond";
        for (String tokenSeparator : validTokenSeparators) {
            String expectedOutput = String.join(tokenSeparator, inputWithoutStopWordsAndPunctuation.split("\\s+"));

            assertTokenStreamContents(
                getNamedAnalyzerWithDefaultIncrementGap(tokenSeparator).tokenStream("test", input),
                new String[]{expectedOutput}
            );

            // Repeat one more time to make sure that token filter is reinitialized correctly
            assertTokenStreamContents(
                getNamedAnalyzerWithDefaultIncrementGap(tokenSeparator).tokenStream("test", input),
                new String[]{expectedOutput}
            );
        }
    }

    @Test
    public void testForAllSeparatorsWithNewIncrementGap() throws IOException {
        String input = "my name is bond, james bond";
        String inputWithoutStopWordsAndPunctuation = "my name bond james bond";
        for (String tokenSeparator : validTokenSeparators) {
            String expectedOutput = String.join(tokenSeparator, inputWithoutStopWordsAndPunctuation.split("\\s+"));

            assertTokenStreamContents(
                getNamedAnalyzerWithNewIncrementGap(tokenSeparator, 300).tokenStream("test", input),
                new String[]{expectedOutput}
            );

            // Repeat one more time to make sure that token filter is reinitialized correctly
            assertTokenStreamContents(
                getNamedAnalyzerWithNewIncrementGap(tokenSeparator, 300).tokenStream("test", input),
                new String[]{expectedOutput}
            );
        }
    }
}
