package cc.foxtail.funkey.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cc.foxtail.funkey.R;

public class InfoDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int layoutId = getArguments().getInt("LAYOUT_ID", R.layout.fragment_bmi_info_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public static DialogFragment newInstance(int layoutId) {
        DialogFragment dialogFragment = new InfoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("LAYOUT_ID", layoutId);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
