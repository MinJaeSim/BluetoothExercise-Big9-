package cc.foxtail.funkey.friend;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.FireStoreAdapter;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.Child;

public class FriendAdapter extends FireStoreAdapter<FriendViewHolder> {

    private OnFriendClickListener onFriendClickListener;

    public FriendAdapter(Query query, OnFriendClickListener onFriendClickListener) {
        super(query);
        this.onFriendClickListener = onFriendClickListener;
    }


    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FriendViewHolder(inflater.inflate(R.layout.list_item_new_child, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.setOnFriendClickListener(onFriendClickListener);
        if (getSnapshot(position).exists())
            holder.bindDate(getSnapshot(position).toObject(Child.class), getSnapshot(position).getId());
    }

    public void addUser(DocumentSnapshot documentSnapshot) {
        getmSnapshots().add(documentSnapshot);
        notifyDataSetChanged();
    }

    public void clearList() {
        getmSnapshots().clear();
    }

    public void setOnFriendClickListener(OnFriendClickListener onFriendClickListener) {
        this.onFriendClickListener = onFriendClickListener;
    }
}
