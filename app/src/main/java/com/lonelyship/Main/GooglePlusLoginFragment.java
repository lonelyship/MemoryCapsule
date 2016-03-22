package com.lonelyship.Main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * GooglePlus登入Fragment
 */
public class GooglePlusLoginFragment extends Fragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    public static final int GOOGLE_PLUS_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "GooglePlusLoginFragment";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    public static GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;


    public GooglePlusLoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_googleplus_login, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vInitial();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "[onConnected]");
        mSignInClicked = false;
        Toast.makeText(getActivity(), "Google Plus 登入連線成功", Toast.LENGTH_LONG).show();

        //Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
        //      .setResultCallback(this);

        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        //updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "[onConnectionSuspended]");
        mGoogleApiClient.connect();
        //updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "[onConnectionFailed]"+connectionResult.getErrorMessage()+"code"+connectionResult.getErrorCode());
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(),
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                //resolveSignInError();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked

                Log.w(TAG, "[onClick][btn_sign_in]");
                signInWithGplus();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "[GooglePlusFragment][onActivityResult]" + requestCode + " " + + resultCode);
        if (requestCode == GOOGLE_PLUS_SIGN_IN) {
            if (resultCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void vInitial() {
        btnSignIn = (SignInButton) getView().findViewById(R.id.btn_sign_in);


        // Button click listeners
        btnSignIn.setOnClickListener(this);

        // Initializing google plus api client
        //mGoogleApiClient = GooglePlusApiManager.getInstance().oInitialGoogleApiClient(getContext(), m_ConnectionCallbacks, m_OnConnectionFailedListener);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {

        if (!mGoogleApiClient.isConnected()) {
            resolveSignInError();
            mSignInClicked = true;
        } else {
            getProfileInformation();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                Log.w(TAG, "[resolveSignInError]mConnectionResult.hasResolution()");
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), GOOGLE_PLUS_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "[resolveSignInError]IntentSender.SendIntentException e");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

//    /**
//     * Sign-out from google
//     * */
//    private void signOutFromGplus() {
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
//            //updateUI(false);
//        }
//    }
//
//    /**
//     * Revoking access from google
//     * */
//    private void revokeGplusAccess() {
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
//                    .setResultCallback(new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(Status arg0) {
//                            Log.e(TAG, "User access revoked!");
//                            mGoogleApiClient.connect();
////                            updateUI(false);
//                        }
//
//                    });
//        }
//    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     * */
//    private void updateUI(boolean isSignedIn) {
//        if (isSignedIn) {
//            btnSignIn.setVisibility(View.GONE);
//            btnSignOut.setVisibility(View.VISIBLE);
//            btnRevokeAccess.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);
//        } else {
//            btnSignIn.setVisibility(View.VISIBLE);
//            btnSignOut.setVisibility(View.GONE);
//            btnRevokeAccess.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
//        }
//    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.w(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                Intent it = new Intent(getActivity(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MainActivity.BUNDEL_KEY_LOGIN_TYPE, "Google");
                bundle.putString(MainActivity.BUNDEL_KEY_NAME, personName);
                bundle.putString(MainActivity.BUNDEL_KEY_EMAIL, email);
                bundle.putString(MainActivity.BUNDEL_KEY_URL, personPhotoUrl);
                it.putExtras(bundle);
                startActivity(it);

            } else {
                Toast.makeText(getActivity(),
                        "查無資料!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
