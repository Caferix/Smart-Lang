package com.scu.smartlang.presentation.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.presentation.viewmodel.SocialViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendRequestsFragment extends Fragment implements FriendRequestsAdapter.OnRequestActionListener {

    private SocialViewModel socialViewModel;
    private RecyclerView recyclerView;
    private FriendRequestsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socialViewModel = new ViewModelProvider(requireActivity()).get(SocialViewModel.class);

        recyclerView = view.findViewById(R.id.rv_friend_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendRequestsAdapter(new java.util.ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // LiveData ile istekleri gözlemle
        socialViewModel.getIncomingRequests().observe(getViewLifecycleOwner(), requests -> {
            if (requests != null) {
                adapter.updateList(requests);
            }
        });

        loadRequests();
    }

    private void loadRequests() {
        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                socialViewModel.fetchIncomingRequests(uid);
            }
        });
    }

    @Override
    public void onAccept(String requestId) {
        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                socialViewModel.acceptFriendRequest(requestId, uid);
                Toast.makeText(getContext(), "İstek kabul edildi", Toast.LENGTH_SHORT).show();
                loadRequests(); // Listeyi yenile
            }
        });
    }

    @Override
    public void onViewProfile(String userId) {
        Bundle args = new Bundle();
        args.putString("userId", userId);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_friendRequestsFragment_to_navigation_profile, args);
    }
}
