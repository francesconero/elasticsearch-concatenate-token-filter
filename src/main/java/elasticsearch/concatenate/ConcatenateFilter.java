package org.elasticsearch.index.analysis.concatenate;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public final class ConcatenateFilter extends TokenFilter {

    private final static String DEFAULT_TOKEN_SEPARATOR = " ";

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private String tokenSeparator = null;
    private int incrementGap = 100;
    private StringBuilder builder = new StringBuilder();
    private AttributeSource.State previousState = null;
    private boolean recheckPrevious = false;

    public ConcatenateFilter(Version matchVersion, TokenStream input, String tokenSeparator, int incrementGap) {
        super(input);
        this.tokenSeparator = tokenSeparator!=null ? tokenSeparator : DEFAULT_TOKEN_SEPARATOR;
        this.incrementGap = incrementGap;
    }

    public ConcatenateFilter(Version matchVersion, TokenStream input, String tokenSeparator) {
        super(input);
        this.tokenSeparator = tokenSeparator!=null ? tokenSeparator : DEFAULT_TOKEN_SEPARATOR;
        this.incrementGap = 100;
    }

    @Override
    public boolean incrementToken() throws IOException {
        boolean empty = false;
        builder.setLength(0);

	if(recheckPrevious) {
	   restoreState(previousState);
	   // append the term of the current token
           builder.append(termAtt.buffer(), 0, termAtt.length());
           recheckPrevious = false;
	}

        while (input.incrementToken()) {
	    if(posIncrAtt.getPositionIncrement() <= incrementGap) {
                if (builder.length()>0) {
                    // append the token separator
                    builder.append(tokenSeparator);
                }
                // append the term of the current token
                builder.append(termAtt.buffer(), 0, termAtt.length());
            } else {
		// we have found a new element in the array, the next token should start from this one
                recheckPrevious = true;
		previousState = captureState();
	        break;
	    }
	}

        if (builder.length()>0) {
            termAtt.setEmpty().append(builder);
	    if(!recheckPrevious) {
	        empty = true;
	    }
        }

        return empty;
    }

}
