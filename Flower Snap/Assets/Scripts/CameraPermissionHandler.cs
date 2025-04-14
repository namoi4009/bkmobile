using UnityEngine;
using UnityEngine.UI; // Needed if you have UI elements like buttons
using System.Collections;

#if PLATFORM_ANDROID
using UnityEngine.Android;
#endif

public class CameraPermissionHandler : MonoBehaviour
{
    [SerializeField] private GameObject permissionScreen; // UI Panel asking for permission
    [SerializeField] private GameObject mainCameraObject; // The GameObject holding your main camera or AR camera
    // Optional: Add a button reference if you want to disable it while requesting
    // [SerializeField] private Button requestPermissionButton;

    private bool isRequestingPermission = false; // Flag to prevent multiple requests

#if PLATFORM_ANDROID
    private PermissionCallbacks permissionCallbacks;
#endif

    void Awake()
    {
#if PLATFORM_ANDROID
        // Initialize the callbacks
        permissionCallbacks = new PermissionCallbacks();
        permissionCallbacks.PermissionGranted += OnPermissionGranted;
        permissionCallbacks.PermissionDenied += OnPermissionDenied;
        permissionCallbacks.PermissionDeniedAndDontAskAgain += OnPermissionDeniedAndDontAskAgain;
#endif
    }

    void Start()
    {
        // Initial check when the scene starts
        CheckCameraPermission();
    }

    // Public method to be called by a UI Button (e.g., "Grant Permission" button on permissionScreen)
    public void RequestCameraPermissionOnClick()
    {
        RequestPermission();
    }

    private void CheckCameraPermission()
    {
        Debug.Log("Checking Camera Permission...");
#if PLATFORM_ANDROID
        if (Permission.HasUserAuthorizedPermission(Permission.Camera))
        {
            Debug.Log("Camera Permission Already Granted.");
            ActivateCamera();
        }
        else
        {
            Debug.Log("Camera Permission Not Granted. Showing Permission Screen.");
            // Show the screen explaining why permission is needed
            ShowPermissionRequestUI();
            // Note: We don't request immediately here. We wait for user interaction (button press).
        }
#else
        // For platforms other than Android (like Editor), assume permission is granted
        Debug.Log("Platform is not Android, activating camera directly.");
        ActivateCamera();
#endif
    }

    private void RequestPermission()
    {
#if PLATFORM_ANDROID
        if (!Permission.HasUserAuthorizedPermission(Permission.Camera) && !isRequestingPermission)
        {
            Debug.Log("Requesting Camera Permission...");
            isRequestingPermission = true;
            // Optional: Disable the request button temporarily
            // if (requestPermissionButton != null) requestPermissionButton.interactable = false;

            Permission.RequestUserPermission(Permission.Camera, permissionCallbacks);
        }
        else if (Permission.HasUserAuthorizedPermission(Permission.Camera))
        {
            Debug.Log("Permission was already granted when request was attempted.");
            ActivateCamera(); // Already granted, ensure camera is active
        }
        else
        {
            Debug.Log("Already requesting permission...");
        }
#else
        Debug.Log("Platform is not Android, skipping request.");
        ActivateCamera(); // Activate directly on non-Android
#endif
    }

    private void ActivateCamera()
    {
        Debug.Log("Activating Camera...");
        if (permissionScreen != null) permissionScreen.SetActive(false);
        if (mainCameraObject != null) mainCameraObject.SetActive(true);
        isRequestingPermission = false; // Reset flag
        // Optional: Re-enable button if it was disabled
        // if (requestPermissionButton != null) requestPermissionButton.interactable = true;
    }

    private void ShowPermissionRequestUI()
    {
        Debug.Log("Showing Permission Request UI...");
        if (permissionScreen != null) permissionScreen.SetActive(true);
        if (mainCameraObject != null) mainCameraObject.SetActive(false);
        isRequestingPermission = false; // Reset flag
        // Optional: Ensure request button is interactable
        // if (requestPermissionButton != null) requestPermissionButton.interactable = true;
    }

#if PLATFORM_ANDROID
    // --- Callback Handlers ---

    private void OnPermissionGranted(string permissionName)
    {
        Debug.Log($"Permission Granted: {permissionName}");
        if (permissionName == Permission.Camera)
        {
            ActivateCamera();
        }
        isRequestingPermission = false;
        // Optional: Re-enable button if it was disabled
        // if (requestPermissionButton != null) requestPermissionButton.interactable = true;
    }

    private void OnPermissionDenied(string permissionName)
    {
        Debug.LogWarning($"Permission Denied: {permissionName}. Showing request UI again.");
        if (permissionName == Permission.Camera)
        {
            // User denied, but can be asked again. Keep the explanation screen visible.
            ShowPermissionRequestUI();
        }
        isRequestingPermission = false;
        // Optional: Re-enable button
        // if (requestPermissionButton != null) requestPermissionButton.interactable = true;
    }

    private void OnPermissionDeniedAndDontAskAgain(string permissionName)
    {
        Debug.LogError($"Permission Denied and Don't Ask Again: {permissionName}. User must grant permission in settings.");
        if (permissionName == Permission.Camera)
        {
            // User denied permanently. Keep the explanation screen visible.
            // You might want to add text explaining they need to go to app settings.
            ShowPermissionRequestUI();
            // Optionally disable the request button as it won't work anymore
            // if (requestPermissionButton != null) requestPermissionButton.interactable = false;

            // You could add functionality here to guide the user to settings. Example:
            // ShowSettingsButton(); // A new button to open app settings
        }
        isRequestingPermission = false;
    }

    // --- Optional: Native Settings Opener ---
    /*
    public void OpenAppSettings()
    {
        try
        {
            using (var unityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            using (AndroidJavaObject currentActivity = unityClass.GetStatic<AndroidJavaObject>("currentActivity"))
            {
                string packageName = currentActivity.Call<string>("getPackageName");

                using (var uriClass = new AndroidJavaClass("android.net.Uri"))
                using (AndroidJavaObject uriObject = uriClass.CallStatic<AndroidJavaObject>("fromParts", "package", packageName, null))
                using (var intentObject = new AndroidJavaObject("android.content.Intent", "android.settings.APPLICATION_DETAILS_SETTINGS", uriObject))
                {
                    intentObject.Call<AndroidJavaObject>("addCategory", "android.intent.category.DEFAULT");
                    intentObject.Call<AndroidJavaObject>("setFlags", 0x10000000); // Intent.FLAG_ACTIVITY_NEW_TASK
                    currentActivity.Call("startActivity", intentObject);
                }
            }
        }
        catch (System.Exception ex)
        {
            Debug.LogError("Error opening app settings: " + ex.Message);
        }
    }
    */

    void OnDestroy()
    {
        // Clean up callbacks when the object is destroyed
        if (permissionCallbacks != null)
        {
            permissionCallbacks.PermissionGranted -= OnPermissionGranted;
            permissionCallbacks.PermissionDenied -= OnPermissionDenied;
            permissionCallbacks.PermissionDeniedAndDontAskAgain -= OnPermissionDeniedAndDontAskAgain;
        }
    }
#endif
}