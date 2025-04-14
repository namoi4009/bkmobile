using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class CameraSwitcher : MonoBehaviour
{
    [SerializeField] private Button switchCameraButton;
    [SerializeField] private WebCamTexture frontCameraTexture;
    [SerializeField] private WebCamTexture backCameraTexture;
    [SerializeField] private RawImage cameraDisplay;

    private bool usingFrontCamera = false;
    private WebCamTexture activeCamera;

    private void Start()
    {
        if (switchCameraButton != null)
            switchCameraButton.onClick.AddListener(SwitchCamera);

        // Initialize camera
        InitializeCamera();
    }

    private void InitializeCamera()
    {
        WebCamDevice[] devices = WebCamTexture.devices;

        if (devices.Length == 0)
        {
            Debug.LogError("No camera detected on this device");
            return;
        }

        // Find front and back cameras
        string frontCameraName = "";
        string backCameraName = "";

        foreach (WebCamDevice device in devices)
        {
            if (device.isFrontFacing)
                frontCameraName = device.name;
            else
                backCameraName = device.name;
        }

        // Default to back camera if available
        if (!string.IsNullOrEmpty(backCameraName))
        {
            backCameraTexture = new WebCamTexture(backCameraName, Screen.width, Screen.height);
            activeCamera = backCameraTexture;
            usingFrontCamera = false;
        }
        else if (!string.IsNullOrEmpty(frontCameraName))
        {
            frontCameraTexture = new WebCamTexture(frontCameraName, Screen.width, Screen.height);
            activeCamera = frontCameraTexture;
            usingFrontCamera = true;
        }

        if (activeCamera != null)
        {
            activeCamera.Play();
            cameraDisplay.texture = activeCamera;

            // Adjust aspect ratio
            float ratio = (float)activeCamera.width / (float)activeCamera.height;
            cameraDisplay.GetComponent<RectTransform>().sizeDelta = new Vector2(Screen.width, Screen.width / ratio);
        }
    }

    public void SwitchCamera()
    {
        if (activeCamera != null)
            activeCamera.Stop();

        usingFrontCamera = !usingFrontCamera;

        if (usingFrontCamera)
        {
            if (frontCameraTexture == null)
            {
                WebCamDevice[] devices = WebCamTexture.devices;
                foreach (WebCamDevice device in devices)
                {
                    if (device.isFrontFacing)
                    {
                        frontCameraTexture = new WebCamTexture(device.name, Screen.width, Screen.height);
                        break;
                    }
                }
            }

            activeCamera = frontCameraTexture;
        }
        else
        {
            if (backCameraTexture == null)
            {
                WebCamDevice[] devices = WebCamTexture.devices;
                foreach (WebCamDevice device in devices)
                {
                    if (!device.isFrontFacing)
                    {
                        backCameraTexture = new WebCamTexture(device.name, Screen.width, Screen.height);
                        break;
                    }
                }
            }

            activeCamera = backCameraTexture;
        }

        if (activeCamera != null)
        {
            activeCamera.Play();
            cameraDisplay.texture = activeCamera;

            // Adjust aspect ratio
            float ratio = (float)activeCamera.width / (float)activeCamera.height;
            cameraDisplay.GetComponent<RectTransform>().sizeDelta = new Vector2(Screen.width, Screen.width / ratio);
        }
    }

    private void OnDestroy()
    {
        if (frontCameraTexture != null && frontCameraTexture.isPlaying)
            frontCameraTexture.Stop();

        if (backCameraTexture != null && backCameraTexture.isPlaying)
            backCameraTexture.Stop();
    }
}
