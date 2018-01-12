package com.example.kunal.food.LoginAndResgistration;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunal.food.Profile.Category_;
import com.example.kunal.food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment implements View.OnClickListener{

    private EditText lo_email, lo_pass;
    private Button lo_login, logout;
    private TextView forgot_CreateNew;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    Fragment fragment = new Category_();
    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //getActivity().startActivity(new Intent(getActivity(), Category.class));
            Required_Fragment(fragment);
        }

        progressDialog = new ProgressDialog(getActivity());
        lo_email = view.findViewById(R.id.lo_email);
        lo_pass = view.findViewById(R.id.lo_pass);
        lo_login = view.findViewById(R.id.lo_login);
        forgot_CreateNew = view.findViewById(R.id.forgot_pass);


        lo_login.setOnClickListener(this);
        forgot_CreateNew.setOnClickListener(this);

        logout = getActivity().findViewById(R.id.logout_M);
        logout.setVisibility(View.INVISIBLE);

        return view;
    }

    private void Required_Fragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK){
                    getActivity().finish();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view == lo_login){
            SignIn();
        }else if(view == forgot_CreateNew){
            Fragment registration = new Registration_();
            Required_Fragment(registration);
        }

    }

    private void SignIn() {

        String us_email = lo_email.getText().toString();
        String us_pass = lo_pass.getText().toString();

        if(TextUtils.isEmpty(us_email)){
            Toast.makeText(getActivity(), "Email is empty...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(us_pass)){
            Toast.makeText(getActivity(), "Password is empty...", Toast.LENGTH_SHORT).show();
        }else {

            progressDialog.setMessage("Login Please Wait...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(us_email, us_pass)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Login Successfully....", Toast.LENGTH_SHORT).show();
                                Required_Fragment(fragment);
                                //getActivity().startActivity(new Intent(getActivity(), Category.class));

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Getting some Error in Login Please try again...!!!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }

    }
}
