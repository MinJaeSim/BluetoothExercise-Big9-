package cc.foxtail.funkey.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import cc.foxtail.funkey.OnHeightInputDialogListener;
import cc.foxtail.funkey.R;

public class HeightInputDialogFragment extends DialogFragment {

    private OnHeightInputDialogListener onHeightInputDialogListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_height_input_dialog, null);

        TextView okButton = view.findViewById(R.id.dialog_ok_button);
        TextView cancelButton = view.findViewById(R.id.dialong_cancel_button);

        final EditText fatherEditTextView = view.findViewById(R.id.father_height_edit_text);
        final EditText motherEditTextView = view.findViewById(R.id.mother_height_edit_text);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fatherHeight = !Objects.equals(fatherEditTextView.getText().toString(), "") ? Integer.parseInt(fatherEditTextView.getText().toString()) : 0;
                int motherHeight = !Objects.equals(motherEditTextView.getText().toString(), "") ? Integer.parseInt(motherEditTextView.getText().toString()) : 0;

                onHeightInputDialogListener.onResult((fatherHeight + motherHeight) / 2);
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

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }


    public void setOnHeightInputDialogListener(OnHeightInputDialogListener onHeightInputDialogListener) {
        this.onHeightInputDialogListener = onHeightInputDialogListener;
    }
}
