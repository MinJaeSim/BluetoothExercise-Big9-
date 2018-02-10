package cc.foxtail.funkey;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import cc.foxtail.funkey.data.Child;

public class ChildModel {
    private FirebaseFirestore firebaseFirestore;
    private Child tempChild;


    public ChildModel() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void deleteChildData(String key) {
        firebaseFirestore.collection("Children").document(key).delete();
    }

    public void addUserData(Child child) {
        firebaseFirestore.collection("Children").add(child);
    }

    public void updateUserData(final String key, final Child child) {
        DocumentReference docRef = firebaseFirestore.collection("Children").document(key);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        tempChild = document.toObject(Child.class);
                        List<Integer> scoreList = tempChild.getStageScore();

                        if (child.getHeight() != tempChild.getHeight() || child.getWeight() != tempChild.getWeight()) {
                            for (int i = 30; i <= 40; i++)
                                scoreList.set(i, 0);
                        }
                        tempChild = child;
                        tempChild.setStageScore(scoreList);

                    } else {
                        Log.d("ChildModel", "No such document");
                    }
                } else {
                    Log.d("ChildModel", "get failed with ", task.getException());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                firebaseFirestore.collection("Children").document(key).set(tempChild);
            }
        });


    }
}
