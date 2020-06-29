package com.sophonomores.restaurantorderapp.biometricauth;

import android.util.Log;

import com.sophonomores.restaurantorderapp.R;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

public class BiometricAuth {

    private static final String TAG = "CartActivity";

    private AppCompatActivity activity;
    private BiometricPrompt biometricPrompt;
    private boolean canAuthenticate;

    public BiometricAuth(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setup(Runnable r) {
        try {
            createBiometricPrompt(r);
            canAuthenticate = true;
        } catch (Exception e) {
            canAuthenticate = false;
        }
    }

    public boolean canAuthenticate() {
        return canAuthenticate && BiometricManager.from(
                activity.getApplication()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void authenticate(String title, String subTitle) {
        biometricPrompt.authenticate(createPromptInfo(title, subTitle));
    }

    private BiometricPrompt.PromptInfo createPromptInfo(String title, String subTitle) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle("Pay with " + subTitle)
                .setDescription(activity.getString(R.string.prompt_info_subtitle))
                .setConfirmationRequired(false)
                .setNegativeButtonText("Cancel")
                .build();
        return promptInfo;
    }

    public void createBiometricPrompt(Runnable runnable) {
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d(TAG, errorCode + ": " + errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {}
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Authentication failed for an unknown reason");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "Authentication was successful");
                runnable.run();
            }
        };

        biometricPrompt = new BiometricPrompt(activity, executor, callback);
    }
}
