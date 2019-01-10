package com.example.julien.appdrone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;


public class MyProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private String userEmail = "user email";

    public MyProfileFragment() {
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile,container,false);

        userEmail = getActivity().getIntent().getStringExtra("googleUserEmail");
        TextView emailTextView = view.findViewById(R.id.userEmail);
        emailTextView.setText(userEmail);

        return view;
    }

    private void signOut(View view) {
        // Firebase sign out
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        Toast.makeText(getActivity(), "NOT yet implemented",Toast.LENGTH_SHORT).show();
        // TODO: find way of signout from different activity. Idea: send flag to LoginActivity forcing it to relog by telling it we're coming from MyProfileFragment and not from startup

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}