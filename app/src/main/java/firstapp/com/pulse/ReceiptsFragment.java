package firstapp.com.pulse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static firstapp.com.pulse.EditProfileActivity.getEmailString;

public class ReceiptsFragment extends Fragment {

    DatabaseReference databaseReceipts = FirebaseDatabase.getInstance().getReference("Profile");
    ListView listViewTotal;
    TextView noreceipts;
    List<Receipt> ReceiptList;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String email = user.getEmail();
    String email_string = getEmailString(email);

    DatabaseReference myRef = databaseReceipts.child(email_string).child("Receipts");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.receipts_fragment, container, false);


            noreceipts = (TextView) v.findViewById(R.id.noreceipts);
            listViewTotal = v.findViewById(R.id.list1);
            ReceiptList = new ArrayList<>();


            return v;
       
        }


        public void onStart() {

            final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Fetching data...");
            mProgressDialog.show();
            super.onStart();

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ReceiptList.clear();

                    for (DataSnapshot ReceiptSnapshot : dataSnapshot.getChildren()) {

                        noreceipts.setVisibility(View.GONE);
                        Receipt receipt = ReceiptSnapshot.getValue(Receipt.class);
                        mProgressDialog.dismiss();
                        if(receipt.getTimeDate() != null) {
                            ReceiptList.add(receipt);
                        }
                    }
                if(getActivity() != null) {
                    ReceiptList adapter = new ReceiptList(getActivity(), ReceiptList);
                    listViewTotal.setAdapter(adapter);
                }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                }
            });
        }
    }




       
    

