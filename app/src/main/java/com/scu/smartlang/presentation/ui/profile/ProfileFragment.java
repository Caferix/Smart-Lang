package com.scu.smartlang.presentation.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scu.smartlang.R;
import com.scu.smartlang.domain.model.Friend;
import com.scu.smartlang.domain.model.User;
import com.scu.smartlang.presentation.ui.auth.AuthResultState;
import com.scu.smartlang.presentation.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView tvName, tvLevel, tvXp;
    private ProgressBar pbXp;
    private RecyclerView rvFriends, rvIncomingRequests;
    private Button btnSendFriendRequest;

    private FriendsAdapter friendsAdapter;
    private FriendRequestsAdapter requestsAdapter;

    private String viewUserId = null;
    private String currentUid = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewUserId = getArguments().getString("userId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvLevel = view.findViewById(R.id.tv_level_label);
        tvXp = view.findViewById(R.id.tv_xp_label);
        pbXp = view.findViewById(R.id.pb_profile_xp);
        rvFriends = view.findViewById(R.id.rv_friends);
        rvIncomingRequests = view.findViewById(R.id.rv_incoming_requests);
        btnSendFriendRequest = view.findViewById(R.id.btn_send_friend_request);

        setupRecyclerViews();
        observeViewModel();

        userViewModel.fetchUserProfile();
        userViewModel.clearNotificationBadge();
    }

    private void setupRecyclerViews() {
        friendsAdapter = new FriendsAdapter(new ArrayList<>());
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(friendsAdapter);

        requestsAdapter = new FriendRequestsAdapter(new ArrayList<>(), requestId -> {
            if (currentUid != null) {
                userViewModel.acceptFriendRequest(requestId, currentUid);
                Toast.makeText(getContext(), "İstek kabul edildi", Toast.LENGTH_SHORT).show();
            }
        });
        rvIncomingRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIncomingRequests.setAdapter(requestsAdapter);
    }

    private void observeViewModel() {
        userViewModel.getUserProfile().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AuthResultState.Success) {
                User user = ((AuthResultState.Success) state).getUser();
                if (user != null) {
                    currentUid = user.getUid();

                    // viewUserId null ise kendi profilimiz
                    if (viewUserId == null) {
                        viewUserId = currentUid;
                    }

                    // Görüntülenecek profili çek
                    if (viewUserId.equals(currentUid)) {
                        // Kendi profilimiz
                        updateUI(user);
                        btnSendFriendRequest.setVisibility(View.GONE);
                        rvIncomingRequests.setVisibility(View.VISIBLE);
                        userViewModel.fetchFriends(currentUid);
                        userViewModel.fetchIncomingRequests(currentUid);
                    } else {
                        // Başka kullanıcı — ayrıca fetch et
                        userViewModel.fetchUserById(viewUserId); // Yeni metot
                        btnSendFriendRequest.setVisibility(View.VISIBLE);
                        btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest());
                        rvIncomingRequests.setVisibility(View.GONE);
                        userViewModel.fetchFriends(viewUserId);
                    }
                }
            }
        });

        // Başka kullanıcı profilini observe et
        userViewModel.getViewedUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && !user.getUid().equals(currentUid)) {
                updateUI(user);
            }
        });

        userViewModel.getFriends().observe(getViewLifecycleOwner(), friendsList -> {
            if (friendsList != null) {
                List<FriendsAdapter.FriendModel> models = new ArrayList<>();
                for (Friend f : friendsList) {
                    models.add(new FriendsAdapter.FriendModel(
                            f.getUserName() != null ? f.getUserName() : f.getUid(),
                            f.getLevel()
                    ));
                }
                friendsAdapter.updateList(models);
            }
        });

        userViewModel.getIncomingRequests().observe(getViewLifecycleOwner(), requests -> {
            if (requests != null && !requests.isEmpty()) {
                requestsAdapter.updateList(requests);
                rvIncomingRequests.setVisibility(View.VISIBLE);
            } else {
                rvIncomingRequests.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(User user) {
        tvName.setText(user.getUserName() != null ? user.getUserName() : "Kullanıcı");

        int currentLevel = user.getLevel();
        int totalXp = user.getXp();
        int requiredXp = 100 + (currentLevel - 1) * 25;

        int previousLevelsTotalXp = 0;
        for (int i = 1; i < currentLevel; i++) {
            previousLevelsTotalXp += (100 + (i - 1) * 25);
        }

        int xpInCurrentLevel = totalXp - previousLevelsTotalXp;
        if (xpInCurrentLevel < 0) xpInCurrentLevel = 0;

        tvLevel.setText("Level " + currentLevel);
        tvXp.setText(xpInCurrentLevel + " / " + requiredXp + " XP");

        pbXp.setMax(requiredXp);
        pbXp.setProgress(xpInCurrentLevel);
    }

    private void sendFriendRequest() {
        if (currentUid != null && viewUserId != null && !currentUid.equals(viewUserId)) {
            userViewModel.sendFriendRequest(currentUid, viewUserId);
            Toast.makeText(getContext(), "İstek gönderildi", Toast.LENGTH_SHORT).show();
            btnSendFriendRequest.setEnabled(false);
            btnSendFriendRequest.setText("İstek Gönderildi");
        }
    }
}