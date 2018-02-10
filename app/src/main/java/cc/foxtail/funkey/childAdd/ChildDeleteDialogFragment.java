package cc.foxtail.funkey.childAdd;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.foxtail.funkey.ChildModel;
import cc.foxtail.funkey.R;

public class ChildDeleteDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_child_delete_dialog, null);
        final String documentKey = getArguments().getString("KEY");
        String name = getArguments().getString("NAME");

        TextView textView = view.findViewById(R.id.delete_dialog_text_view);
        Button cancelButton = view.findViewById(R.id.delete_dialog_cancel_button);
        Button okButton = view.findViewById(R.id.delete_dialog_ok_button);

        textView.setText(name + " 어린이의 정보를 삭제하시겠습니까?");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChildModel().deleteChildData(documentKey);
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


    public static ChildDeleteDialogFragment newInstance(String key, String userName) {
        ChildDeleteDialogFragment commentDialogFragment = new ChildDeleteDialogFragment();

        Bundle args = new Bundle();
        args.putString("KEY", key);
        args.putString("NAME", userName);

        commentDialogFragment.setArguments(args);

        return commentDialogFragment;
    }
}
