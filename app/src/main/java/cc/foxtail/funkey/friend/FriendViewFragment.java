package cc.foxtail.funkey.friend;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.List;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.StarFragment;
import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.data.User;
import cc.foxtail.funkey.dialog.FriendAddDialogFragment;
import cc.foxtail.funkey.dialog.FriendDeleteDialogFragment;
import cc.foxtail.funkey.navigation.NavigationActivity;

public class FriendViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private User user;
    private List<String> friendList;
    private FriendAdapter friendAdapter;
    private FragmentManager fm;
    private MenuItem searchItem;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_friend_view, container, false);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.friend_recycler_view);
        imageView = view.findViewById(R.id.find_friend_image_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        fm = getActivity().getSupportFragmentManager();

        DocumentReference documentReference = firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid());

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    user = snapshot.toObject(User.class);
                    friendList = user.getFriendList();
                    readFriend();
                }
            }
        });

        Query query = null;

        friendAdapter = new FriendAdapter(query, new OnFriendClickListener() {
            @Override
            public void onClick(String key, Child child) {
                String title = child.getUserName() + "의 정보";
                StarFragment starFragment = StarFragment.newInstance(title, child.getUserName(), child.getProfileImageUrl(), child.getTotalStarCount());
                fm.beginTransaction().replace(R.id.content_fragment, starFragment).addToBackStack(null).commit();
            }

            @Override
            public void onLongClick(FriendDeleteDialogFragment dialog) {
                dialog.show(fm, "dialog");
            }
        }) {
            @Override
            protected void onDataChanged() {
//                if (searchItem.isActionViewExpanded())
//                    searchItem.collapseActionView();
                // Show/hide content if the query returns empty.

                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {

            }
        };
        recyclerView.setAdapter(friendAdapter);
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_bluetooth).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(true);
        searchItem = menu.findItem(R.id.menu_search);
        searchItem.setEnabled(true);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                System.out.println("TEST LIST SIZE : " + friendList.size());
                friendAdapter.clearList();
                for (String key : friendList) {
                    FirebaseFirestore.getInstance().collection("Children").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            friendAdapter.addUser(documentSnapshot);
                        }
                    });
                }

                if (friendList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }

                friendAdapter.setOnFriendClickListener(new OnFriendClickListener() {
                    @Override
                    public void onClick(String documentKey, Child child) {
//                        RecordFragment recordFragment = RecordFragment.newInstance(child);
                        String title = child.getUserName() + "의 정보";
                        StarFragment starFragment = StarFragment.newInstance(title, child.getUserName(), child.getProfileImageUrl(), child.getTotalStarCount());
                        fm.beginTransaction().replace(R.id.content_fragment, starFragment).addToBackStack(null).commit();
                    }

                    @Override
                    public void onLongClick(FriendDeleteDialogFragment dialog) {
                        dialog.show(fm, "dialog");
                    }
                });

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        System.out.println("Friend Resume");
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        ((NavigationActivity) getActivity()).showToolbarTextView(getResources().getString(R.string.friend_list));

        super.onResume();
    }

    private void readFriend() {
        if (searchItem.isActionViewExpanded()) {
            searchItem.collapseActionView();
            return;
        }
        if (friendList.size() != 0) {
            friendAdapter.clearList();
            for (String key : friendList) {
                FirebaseFirestore.getInstance().collection("Children").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        friendAdapter.addUser(documentSnapshot);
                    }
                });
            }
            imageView.setVisibility(View.GONE);
        } else if (friendList.size() == 0) {
            friendAdapter.clearList();
            recyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void filter(String friendName) {
        friendAdapter.clearList();
        Query query = FirebaseFirestore.getInstance().collection("Children").whereEqualTo("userName", friendName);
        friendAdapter.setQuery(query);

        friendAdapter.setOnFriendClickListener(new OnFriendClickListener() {
            @Override
            public void onClick(String documentKey, Child child) {
                FriendAddDialogFragment dialog = FriendAddDialogFragment.newInstance(documentKey, child.getUserName());
                dialog.show(fm, "dialog");
            }

            @Override
            public void onLongClick(FriendDeleteDialogFragment dialog) {

            }
        });
    }
}
