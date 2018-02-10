package cc.foxtail.funkey.friend;


import cc.foxtail.funkey.data.Child;
import cc.foxtail.funkey.dialog.FriendDeleteDialogFragment;

public interface OnFriendClickListener {
    void onClick(String key, Child child);
    void onLongClick(FriendDeleteDialogFragment dialog);
}
