package cc.foxtail.funkey.childAdd;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import cc.foxtail.funkey.ChildModel;
import cc.foxtail.funkey.MainFragment;
import cc.foxtail.funkey.data.Child;

public class ChildAddPresenter implements ChildAddContract.Presenter {

    private ChildModel childModel;
    private ChildAddContract.View view;
    private boolean update;
    private String updateKey;

    private boolean nickNameChecked;

    public ChildAddPresenter() {
        this.childModel = new ChildModel();
    }

    @Override
    public void setView(ChildAddContract.View view) {
        this.view = view;
    }

    @Override
    public void uploadImages(Uri photoUri) {
        view.showProgressDialog("등록중 . . ");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://big9-project.appspot.com").child("profileImages");
        if (photoUri != null) {

            UploadTask uploadTask = storageReference.child(generateTempFilename()).putFile(photoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    view.setProfileImageUrl(taskSnapshot.getDownloadUrl().toString());

                    if (update)
                        updateChildInfo();
                    else
                        uploadToDatabase();

                    view.dismissProgressDialog();
                }
            });

        } else {
            if (update)
                updateChildInfo();
            else
                uploadToDatabase();

            view.dismissProgressDialog();
        }
    }

    @Override
    public void updateChildInfo() {
        Child child = view.getChildData();
        childModel.updateUserData(updateKey, child);

        view.popBackStack();
    }

    @Override
    public void uploadToDatabase() {
        Child child = view.getChildData();
        childModel.addUserData(child);

        view.popBackStack();
    }

    public void setUpdateKey(String key) {
        updateKey = key;
    }

    public void setUpdateMode(boolean value) {
        update = value;
    }

    public void setNickNameChecked(boolean value) {
        nickNameChecked = value;
    }

    private String generateTempFilename() {
        return UUID.randomUUID().toString();
    }
}
