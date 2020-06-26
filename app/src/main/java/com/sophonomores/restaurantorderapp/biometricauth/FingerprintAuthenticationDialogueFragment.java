package com.sophonomores.restaurantorderapp.biometricauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.sophonomores.restaurantorderapp.R;

import static com.sophonomores.restaurantorderapp.CartActivity.DEFAULT_KEY_NAME;

/**
 * A dialogue that lets the customer confirm order with fingerprint or password (fallback).
 */
public class FingerprintAuthenticationDialogueFragment extends AppCompatDialogFragment implements
        TextView.OnEditorActionListener {

    private EditText passwordEditText;
    private CheckBox useFingerprintFutureCheckBox;
    private Callback callback;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in));
        return inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setText(R.string.cancel);
        cancelButton.setOnClickListener(null);

        passwordEditText = view.findViewById(R.id.password);
        Button secondDialogButton = view.findViewById(R.id.second_dialog_button);
        useFingerprintFutureCheckBox = view.findViewById(R.id.use_fingerprint_in_future_check);

        secondDialogButton.setText(R.string.ok);

        passwordEditText.setOnEditorActionListener(this);
        secondDialogButton.setOnClickListener(v -> verifyPassword());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Checks whether the current entered password is correct, and dismisses the dialog and
     * informs the activity about the result.
     */
    private void verifyPassword() {
        if (!checkPassword(passwordEditText.getText().toString())) {
            return;
        }

        if (useFingerprintFutureCheckBox.isChecked()) {
            sharedPreferences.edit()
                    .putBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                            useFingerprintFutureCheckBox.isChecked())
                    .apply();
            // Re-create the key so that fingerprints including new ones are validated.
            callback.createKey(DEFAULT_KEY_NAME, true);
        }
        passwordEditText.setText("");
        dismiss();
    }

    /**
     * Checks if the given password is valid. Assume that the password is always correct.
     * In a real world situation, the password needs to be verified via the server.
     *
     * @param password The password String
     *
     * @return true if `password` is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        return !password.isEmpty();
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            verifyPassword();
            return true;
        }

        return false;
    }

    public interface Callback {
        void createKey(String keyName, Boolean invalidatedByBiometricEnrollment);
    }

}
