package com.simplexorg.searchfragment.decorator;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.simplexorg.searchfragment.R;
import com.simplexorg.searchfragment.search.BaseSearchView;
import com.simplexorg.searchfragment.search.SearchData;
import com.simplexorg.searchfragment.search.SearchSuggestionAdapter;
import com.simplexorg.searchfragment.search.SearchSuggestionSupplier;
import com.simplexorg.searchfragment.search.SearchSuggestionSupplier.OnSuggestionObtainedListener;
import com.simplexorg.searchfragment.view.SearchEditText;

import java.util.List;

public class SearchSuggestionDecorator extends SearchDecorator implements OnSuggestionObtainedListener {
    private static final String TAG = SearchSuggestionDecorator.class.getSimpleName();
    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    private OnSuggestionClickListener mOnSuggestionClickListener;
    private SearchSuggestionSupplier mSearchSuggestionSupplier;

    private CardView mCardView;
    private RelativeLayout mRecyclerListParent;
    private RecyclerView mRecyclerList;
    private SearchSuggestionAdapter mSuggestionAdapter;
    private SearchEditText mSearchText;

    public SearchSuggestionDecorator(BaseSearchView searchBar) {
        super(searchBar);
    }

    @Override
    public void attach(View view) {
        mSearchBar.attach(view);

        mSearchText = view.findViewById(R.id.search_id_search);
        RelativeLayout root = view.findViewById(R.id.search_id_root);
        mCardView = view.findViewById(R.id.search_id_card_view);

        View suggestionView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.search_suggestion_list, root);
        mRecyclerList = suggestionView.findViewById(R.id.search_id_list);
        mRecyclerListParent = suggestionView.findViewById(R.id.search_id_suggest_root);
    }

    @Override
    public void gainFocus() {
        mSearchBar.gainFocus();
        mSearchText.setFocusableInTouchMode(true);
    }

    @Override
    public void removeFocus() {
        mSearchBar.removeFocus();
        mSearchText.setFocusableInTouchMode(false);
    }

    @Override
    public void initDisplay() {
        mSearchBar.initDisplay();

        if (mSuggestionAdapter == null) {
            mSuggestionAdapter = new SearchSuggestionAdapter();
            mSuggestionAdapter.setOnSuggestionClickListener(mOnSuggestionClickListener);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mRecyclerList.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerList.setLayoutManager(layoutManager);
            mRecyclerList.setAdapter(mSuggestionAdapter);

            LayoutParams params = (LayoutParams) mRecyclerListParent.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, mCardView.getId());
            mRecyclerListParent.setLayoutParams(params);
        }
    }

    public void updateSuggestions(SearchData searchData) {
        if (mSearchSuggestionSupplier != null) {
            mSearchSuggestionSupplier.getSuggestion(searchData, this);
        }
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener onSuggestionClickListener) {
        mOnSuggestionClickListener = onSuggestionClickListener;
    }

    public void setSearchSuggestionSupplier(SearchSuggestionSupplier searchSuggestionSupplier) {
        mSearchSuggestionSupplier = searchSuggestionSupplier;
    }

    @Override
    public void onSuggestionObtained(List<String> suggestions) {
        if (mSuggestionAdapter != null) {
            mSuggestionAdapter.setSuggestions(suggestions);
        }
    }
}