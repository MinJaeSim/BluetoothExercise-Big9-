package cc.foxtail.funkey.childAdd;


import android.net.Uri;

import cc.foxtail.funkey.data.Child;

public interface ChildAddContract {

    interface View {
        void showDialog(String message);

        void dismissProgressDialog();

        void showProgressDialog(String message);

        Child getChildData();

        void popBackStack();

        void setProfileImageUrl(String imageUrl);
    }

    interface Presenter {

        void setView(ChildAddContract.View view);

        void uploadImages(Uri photoUri);

        void updateChildInfo();

        void uploadToDatabase();
    }
}
