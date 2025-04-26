using UnityEngine;

public class CapturePictureController : MonoBehaviour
{
    [Header("References")]
    [SerializeField] private CameraPermissionHandler permissionHandler;
    [SerializeField] private NavBarController navBarController;
    [SerializeField] private StickerController stickerController;
    [SerializeField] private ScreenCaptureController captureController;
    [SerializeField] private CameraSwitcher cameraSwitcher;

    [Header("UI Elements")]
    [SerializeField] private GameObject mainCamera;
    [SerializeField] private GameObject permissionScreen;
    [SerializeField] private GameObject triggerNavBar;
    [SerializeField] private GameObject navBar;

    private void Start()
    {
        // Initialize components if not assigned
        if (permissionHandler == null)
            permissionHandler = GetComponent<CameraPermissionHandler>();

        if (navBarController == null)
            navBarController = GetComponent<NavBarController>();

        if (stickerController == null)
            stickerController = GetComponentInChildren<StickerController>();

        if (captureController == null)
            captureController = GetComponent<ScreenCaptureController>();

        if (cameraSwitcher == null)
            cameraSwitcher = GetComponent<CameraSwitcher>();

        // Check camera permission at start
        //permissionHandler.CheckCameraPermission();
    }

    // This can be called from the UI button in the permission screen
    //public void RequestCameraPermission()
    //{
    //    permissionHandler.RequestCameraPermission();
    //}

    // This can be called from the TriggerNavBar button
    public void ToggleNavBar()
    {
        navBarController.ToggleNavBar();
    }

    // This can be called from the CaptureButton
    public void CaptureImage()
    {
        captureController.CaptureScreen();
    }

    // This can be called from the SwitchCameraButton
    public void SwitchCamera()
    {
        cameraSwitcher.SwitchCamera();
    }
}
