package com.scu.smartlang.presentation.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.SocialViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private SocialViewModel socialViewModel;
    private TextInputEditText etSearch;
    private RecyclerView rvResults;
    private SearchResultAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);

        etSearch = view.findViewById(R.id.et_search);
        rvResults = view.findViewById(R.id.rv_search_results);

        adapter = new SearchResultAdapter(new ArrayList<>(), userId -> {
            // ProfileFragment'e git
            Bundle args = new Bundle();
            args.putString("userId", userId);
            Navigation.findNavController(view).navigate(R.id.action_navigation_search_to_otherUserFragment, args);

        });

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResults.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    socialViewModel.searchUsers(s.toString());
                } else {
                    adapter.updateList(new ArrayList<>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        socialViewModel.getSearchResults().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                adapter.updateList(users);
            }
        });
    }
}
