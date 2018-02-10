package cc.foxtail.funkey.childAdd;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.firestore.Query;

import cc.foxtail.funkey.FireStoreAdapter;
import cc.foxtail.funkey.R;
import cc.foxtail.funkey.childAdd.ChildViewHolder;
import cc.foxtail.funkey.data.Child;


public class ChildAdapter extends FireStoreAdapter<ChildViewHolder> {

    public ChildAdapter(Query query) {
        super(query);
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ChildViewHolder(inflater.inflate(R.layout.list_item_new_child, parent, false));
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {
        holder.bindDate(getSnapshot(position).toObject(Child.class),getSnapshot(position).getId());
    }

    @Override
    public void setQuery(Query query) {
        super.setQuery(query);
    }
}
