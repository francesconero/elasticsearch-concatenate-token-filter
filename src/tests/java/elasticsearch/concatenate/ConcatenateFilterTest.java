package elasticsearch.concatenate;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.test.ESTokenStreamTestCase;

import java.io.IOException;
import java.io.StringReader;

import static org.elasticsearch.test.ESTestCase.createTestAnalysis;

public class ConcatenateFilterTest extends ESTokenStreamTestCase {

    public void testConcatenateFilter() throws IOException
    {
        Settings settings = Settings.builder()
                .put("index.analysis.filter.my_filter.type", "concatenate")
                .build();

        ESTestCase.TestAnalysis analysis = createTestAnalysis(new Index("test", "_na_"), settings, new ConcatenatePlugin());
        TokenFilterFactory filter = analysis.tokenFilter.get("my_filter");
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("next to"));
        TokenStream tokenStream = filter.create(tokenizer);
        BaseTokenStreamTestCase.assertTokenStreamContents(tokenStream, new String[] { "next to" });

    }

    public void testConcatenateFilterTokenSeparator() throws IOException
    {
        Settings settings = Settings.builder()
                .put("index.analysis.filter.my_filter.type", "concatenate")
                .put("index.analysis.filter.my_filter.token_separator", "")
                .build();

        ESTestCase.TestAnalysis analysis = createTestAnalysis(new Index("test", "_na_"), settings, new ConcatenatePlugin());
        TokenFilterFactory filter = analysis.tokenFilter.get("my_filter");
        Tokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("公园 附近 的 酒店"));
        TokenStream tokenStream = filter.create(tokenizer);
        BaseTokenStreamTestCase.assertTokenStreamContents(tokenStream, new String[] { "公园附近的酒店" });

        settings = Settings.builder()
                .put("index.analysis.filter.my_filter.type", "concatenate")
                .put("index.analysis.filter.my_filter.token_separator", "_")
                .build();

        analysis = createTestAnalysis(new Index("test", "_na_"), settings, new ConcatenatePlugin());
        filter = analysis.tokenFilter.get("my_filter");
        tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("hotels next to the park"));
        tokenStream = filter.create(tokenizer);
        BaseTokenStreamTestCase.assertTokenStreamContents(tokenStream, new String[] { "hotels_next_to_the_park" });

    }
}