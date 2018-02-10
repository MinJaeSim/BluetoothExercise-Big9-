package cc.foxtail.funkey.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cc.foxtail.funkey.R;
import cc.foxtail.funkey.data.User;

public class FriendDeleteDialogFragment extends DialogFragment {
    private List<String> friendList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_child_delete_dialog, null);

        friendList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final String documentKey = getArguments().getString("KEY");
        String name = getArguments().getString("NAME");

        TextView textView = view.findViewById(R.id.delete_dialog_text_view);
        Button cancelButton = view.findViewById(R.id.delete_dialog_cancel_button);
        final Button okButton = view.findViewById(R.id.delete_dialog_ok_button);
        okButton.setVisibility(View.GONE);

        textView.setText(name + " 어린이를 친구에서 삭제하시곘습니까 ?");

        DocumentReference documentReference = firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                friendList = documentSnapshot.toObject(User.class).getFriendList();
                okButton.setVisibility(View.VISIBLE);

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendList.contains(documentKey)) {
                    friendList.remove(documentKey);
                    firebaseFirestore.collection("User").document(firebaseAuth.getCurrentUser().getUid()).update(
                            "friendList", friendList
                    );
                } else {
                    Toast.makeText(getActivity(),"이미 삭제된 친구입니다.",Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public static FriendDeleteDialogFragment newInstance(String key, String userName) {
        FriendDeleteDialogFragment friendDeleteDialogFragment = new FriendDeleteDialogFragment();

        Bundle args = new Bundle();
        args.putString("KEY", key);
        args.putString("NAME", userName);

        friendDeleteDialogFragment.setArguments(args);

        return friendDeleteDialogFragment;
    }
}
