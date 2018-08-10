package com.simplexorg.searchfragment.search;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.simplexorg.searchfragment.R;
import com.simplexorg.searchfragment.decorator.SearchSuggestionDecorator.OnSuggestionClickListener;
import com.simplexorg.searchfragment.search.SearchSuggestionAdapter.SearchSuggestionViewHolder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static com.simplexorg.searchfragment.search.SearchSuggestionAdapter.SAVED_SUGGESTIONS;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchSuggestionAdapterUnitTest {
    private SearchSuggestionAdapter mAdapter;

    @Before
    public void setup() {
        mAdapter = new SearchSuggestionAdapter();
    }

    /**
     * Check exception to see if it can be ignored.
     * @param ex the exception.
     */
    private void checkException(NullPointerException ex) {
        boolean ignoreException = false;
        for (StackTraceElement trace : ex.getStackTrace()) {
            if (trace.toString().contains("notifyDataSetChanged")) {
                ignoreException = true;
            }
        }
        if (!ignoreException) {
            throw ex;
        }
    }

    @Test
    public void test_onBindViewHolder() {
        List<String> suggestions = new ArrayList<>();
        int position = 0;
        suggestions.add("one");
        try {
            mAdapter.setSuggestions(suggestions);
        } catch (NullPointerException ex) {
            checkException(ex);
        }

        View itemView = mock(View.class);
        TextView suggestionText = mock(TextView.class);
        when(suggestionText.getText()).thenReturn(suggestions.get(position));
        when(itemView.findViewById(R.id.search_id_text)).thenReturn(suggestionText);
        SearchSuggestionViewHolder viewHolder = new SearchSuggestionViewHolder(itemView);

        mAdapter.onBindViewHolder(viewHolder, position);
        verify(suggestionText).setText(suggestions.get(position));

        ArgumentCaptor<OnClickListener> argumentCaptor = ArgumentCaptor.forClass(OnClickListener.class);
        verify(itemView).setOnClickListener(argumentCaptor.capture());

        OnClickListener clickListener = argumentCaptor.getValue();
        clickListener.onClick(itemView); // Should not throw null pointer exception.

        OnSuggestionClickListener onSuggestionClickListener = mock(OnSuggestionClickListener.class);
        mAdapter.setOnSuggestionClickListener(onSuggestionClickListener);

        clickListener.onClick(itemView);

        verify(onSuggestionClickListener).onSuggestionClick(suggestions.get(position));
    }

    @Test
    public void test_onSaveInstanceState() {
        Bundle outState = mock(Bundle.class);
        mAdapter.onSaveInstanceState(outState);

        verify(outState).putStringArrayList(eq(SAVED_SUGGESTIONS), anyObject());
    }

    @Test
    public void test_onRestoreSavedState() {
        Bundle savedInstanceState = mock(Bundle.class);
        try {
            mAdapter.onRestoreSavedState(savedInstanceState);
        } catch(NullPointerException ex) {
            checkException(ex);
        }
        verify(savedInstanceState).getStringArrayList(SAVED_SUGGESTIONS);
    }
}
