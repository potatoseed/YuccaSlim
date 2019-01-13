package com.yuccaworld.yuccaslim;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.yuccaworld.yuccaslim.billing.BillingManager;
import com.yuccaworld.yuccaslim.skulist.AcquireFragment;
import com.yuccaworld.yuccaslim.utilities.SlimUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterationFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @com.mobsandgeeks.saripaar.annotation.Email(messageResId = R.string.email_validation)
    private EditText Email;

    @ConfirmEmail
    private EditText confirmEmail;

    @Password(min = 6, scheme = Password.Scheme.NUMERIC, messageResId = R.string.validation_password_number)
    private EditText passwordInput;

    @ConfirmPassword
    private EditText passwordConfirm;

    private Button purchaseButton;



    private OnFragmentInteractionListener mListener;

    public RegisterationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterationFragment newInstance(String param1, String param2) {
        RegisterationFragment fragment = new RegisterationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.registration_fragment, container, false);
        Email = root.findViewById(R.id.editTextMail);
        confirmEmail = root.findViewById(R.id.editTextConfrimEmail);
        passwordInput = root.findViewById(R.id.editTextPassword);
        passwordConfirm = root.findViewById(R.id.editTextPasswordConfirm);
        purchaseButton = root.findViewById(R.id.buttonPurchase);
        if (!SlimUtils.gUserRegistrationEmail.isEmpty()){
            Email.setText(SlimUtils.gUserRegistrationEmail);
            confirmEmail.setText(SlimUtils.gUserRegistrationEmail);

        }
        if (!SlimUtils.gRegistrationPassword.isEmpty()) {
            passwordInput.setText(SlimUtils.gRegistrationPassword);
            passwordConfirm.setText(SlimUtils.gRegistrationPassword);
        }

        return root;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
