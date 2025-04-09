using UnityEngine;
using UnityEngine.UI;
using System.IO;

public class CaptureImage : MonoBehaviour
{
    public CameraManager cameraManager;

    public void CaptureAndSave()
    {
        Texture2D capturedPhoto = cameraManager.CaptureImage();
        byte[] bytes = capturedPhoto.EncodeToPNG();
        string path = Path.Combine(Application.persistentDataPath, $"Captured_{System.DateTime.Now.Ticks}.png");
        File.WriteAllBytes(path, bytes);

        Debug.Log($"Saved Image: {path}");
    }
}
