package firstapp.com.pulse;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {
    Button changePassword, deleteAccount, customerSupport;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        changePassword = (Button) v.findViewById(R.id.changepwactivity);
        deleteAccount = (Button) v.findViewById(R.id.deleteMyUser);
        customerSupport = (Button) v.findViewById(R.id.customerSupportButton);
        changePassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePassword.class));
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DeleteUser.class));
            }
        });
        customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomerSupport.class));
            }
        });


        return v;
    }




}
