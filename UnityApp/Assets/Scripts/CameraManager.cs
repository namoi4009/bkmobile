using UnityEngine;
using UnityEngine.UI;

public class CameraManager : MonoBehaviour
{
    public RawImage cameraView;
    private WebCamTexture webCamTexture;
    private int currentCamIndex = 0;

    void Start()
    {
        StartCamera();
    }

    public void SwitchCamera()
    {
        currentCamIndex++;
        currentCamIndex %= WebCamTexture.devices.Length;
        StartCamera();
    }

    void StartCamera()
    {
        if (webCamTexture != null)
            webCamTexture.Stop();

        WebCamDevice device = WebCamTexture.devices[currentCamIndex];
        webCamTexture = new WebCamTexture(device.name);
        cameraView.texture = webCamTexture;
        webCamTexture.Play();
    }

    public Texture2D CaptureImage()
    {
        Texture2D photo = new Texture2D(webCamTexture.width, webCamTexture.height);
        photo.SetPixels(webCamTexture.GetPixels());
        photo.Apply();
        return photo;
    }
}
