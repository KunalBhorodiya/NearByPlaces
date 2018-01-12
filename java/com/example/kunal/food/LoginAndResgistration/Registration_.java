package com.example.kunal.food.LoginAndResgistration;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunal.food.Profile.Category_;
import com.example.kunal.food.Profile.Home_;
import com.example.kunal.food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class Registration_ extends Fragment implements View.OnClickListener{

    private EditText res_email, res_pass;
    private Button register;
    private TextView goLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    Fragment category_ = new Category_();

    public Registration_() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_, container, false);

        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //Fragment fragment = new Home_();
            //Required_Fragment(fragment);
            getActivity().startActivity(new Intent(getActivity(), Home_.class));
        }

        res_email = view.findViewById(R.id.res_email);
        res_pass = view.findViewById(R.id.res_pass);
        register = view.findViewById(R.id.register);
        goLogin = view.findViewById(R.id.go_login);


        register.setOnClickListener(this);
        goLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if(view == goLogin){
            Fragment login = new Login();
            Required_Fragment(login);
        }else if(view == register){
            RegisterUser();
        }

    }

    private void RegisterUser() {

        String us_email = res_email.getText().toString().trim();
        String us_pass = res_pass.getText().toString().trim();

        if(TextUtils.isEmpty(us_email)){
            Toast.makeText(getActivity(), "Email is empty...", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(us_pass)){
            Toast.makeText(getActivity(), "Password is empty...", Toast.LENGTH_SHORT).show();
            return;
        }else {
            progressDialog.setMessage("Please wait user is Registering...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(us_email, us_pass)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                res_email.setText("");
                                res_pass.setText("");
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "User Registered Successfully...", Toast.LENGTH_SHORT).show();
                                 Required_Fragment(category_);
                                //getActivity().startActivity(new Intent(getActivity(), Category.class));
                            }else {
//                                    FirebaseAuthException exception = (FirebaseAuthException) task.getException();
  //                                  Toast.makeText(getActivity(), "" + exception.getMessage(), Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "User Not Registered. Please try again...", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }

    private void Required_Fragment(Fragment fragment) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}