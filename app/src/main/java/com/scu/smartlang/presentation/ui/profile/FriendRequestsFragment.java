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
import com.scu.smartlang.presentation.viewmodel.ProfileViewModel;
import com.scu.smartlang.presentation.viewmodel.SocialViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendRequestsFragment extends Fragment implements FriendRequestsAdapter.OnRequestActionListener {

    private SocialViewModel socialViewModel;
    private ProfileViewModel profileViewModel;
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
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        recyclerView = view.findViewById(R.id.rv_friend_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendRequestsAdapter(new java.util.ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        socialViewModel.getIncomingRequests().observe(getViewLifecycleOwner(), requests -> {
            if (requests != null) {
                adapter.updateList(requests);
            }
        });

        socialViewModel.getFriendshipActionCompleted().observe(getViewLifecycleOwner(), aVoid -> {
            Toast.makeText(getContext(), "İşlem tamamlandı.", Toast.LENGTH_SHORT).show();
            loadRequests();
        });

        loadRequests();

        // Reset notification count when viewing this screen
        profileViewModel.resetUnreadNotificationsCount();
    }

    private void loadRequests() {
        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                socialViewModel.fetchIncomingRequests(uid);
            }
        });
    }

    @Override
    public void onAccept(String requestId, String requesterUid) {
        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                socialViewModel.acceptFriendRequest(requestId, uid, requesterUid);
            }
        });
    }

    @Override
    public void onReject(String requestId) {
        socialViewModel.getCurrentUserId().thenAccept(uid -> {
            if (uid != null) {
                socialViewModel.rejectFriendRequest(requestId, uid);
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
