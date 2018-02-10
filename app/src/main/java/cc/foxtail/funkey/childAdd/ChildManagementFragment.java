package cc.foxtail.funkey.childAdd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.navigation.NavigationActivity;
import cc.foxtail.funkey.R;

import static cc.foxtail.funkey.navigation.NavigationActivity.soundId;
import static cc.foxtail.funkey.navigation.NavigationActivity.soundPool;


public class ChildManagementFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_management, container, false);
        setHasOptionsMenu(true);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.add_user_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
                recyclerView.getAdapter().notifyDataSetChanged();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, new ChildAddFragment()).addToBackStack(null).commit();
            }
        });

        recyclerView = view.findViewById(R.id.user_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        String uid = FirebaseAuth.getInstance().getUid();
        Query query = FirebaseFirestore.getInstance().collection("Children").whereEqualTo("uid", uid);


        ChildAdapter childAdapter = new ChildAdapter(query) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {

            }
        };
        recyclerView.setAdapter(childAdapter);

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_bluetooth).setVisible(false);
//        menu.findItem(R.id.menu_rank).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }

    @Override
    public void onResume() {
        ((NavigationActivity) getActivity()).showToolbarTextView(getResources().getString(R.string.register_child));
        ((NavigationActivity) getActivity()).setNavigationBackListener();
        super.onResume();
    }
}
