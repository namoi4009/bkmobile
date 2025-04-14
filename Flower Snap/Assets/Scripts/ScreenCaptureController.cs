using System.Collections;
using System.IO;
using UnityEngine;
using UnityEngine.UI;
using System.Diagnostics;
using Debug = UnityEngine.Debug;

public class ScreenCaptureController : MonoBehaviour
{
    [SerializeField] private Button captureButton;
    [SerializeField] private Image previewImage;
    [SerializeField] private GameObject captureFlashEffect;
    [SerializeField] private string desktopSavePath = "Screenshots"; // Folder on desktop to save screenshots

    private void Start()
    {
        if (captureButton != null)
            captureButton.onClick.AddListener(CaptureScreen);

        if (captureFlashEffect != null)
            captureFlashEffect.SetActive(false);
    }

    public void CaptureScreen()
    {
        StartCoroutine(CaptureScreenCoroutine());
    }

    private IEnumerator CaptureScreenCoroutine()
    {
        // Wait for the end of the frame so we're sure everything is rendered
        yield return new WaitForEndOfFrame();

        // Optional: Show a flash effect
        if (captureFlashEffect != null)
        {
            captureFlashEffect.SetActive(true);
            yield return new WaitForSeconds(0.1f);
            captureFlashEffect.SetActive(false);
        }

        // Capture the screen
        Texture2D screenTexture = new Texture2D(Screen.width, Screen.height, TextureFormat.RGB24, false);
        screenTexture.ReadPixels(new Rect(0, 0, Screen.width, Screen.height), 0, 0);
        screenTexture.Apply();

        // Convert to PNG
        byte[] bytes = screenTexture.EncodeToPNG();

        // Save to device gallery
        SaveToGallery(bytes);

        // Show in preview if needed
        if (previewImage != null)
        {
            Sprite sprite = Sprite.Create(screenTexture, new Rect(0, 0, screenTexture.width, screenTexture.height), new Vector2(0.5f, 0.5f));
            previewImage.sprite = sprite;
        }

        // Clean up to prevent memory leaks
        Destroy(screenTexture);
    }

    private void SaveToGallery(byte[] imageBytes)
    {
        string fileName = "CapturedImage_" + System.DateTime.Now.ToString("yyyyMMdd_HHmmss") + ".png";

#if UNITY_ANDROID
        // Save to gallery using Android's media scanner
        string path = Path.Combine(Application.persistentDataPath, fileName);
        File.WriteAllBytes(path, imageBytes);

        // Notify gallery
        AndroidJavaClass classPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject objActivity = classPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        AndroidJavaClass classUri = new AndroidJavaClass("android.net.Uri");
        AndroidJavaObject objIntent = new AndroidJavaObject("android.content.Intent", new object[2]
            { "android.intent.action.MEDIA_SCANNER_SCAN_FILE", classUri.CallStatic<AndroidJavaObject>("parse", "file://" + path) });
        objActivity.Call("sendBroadcast", objIntent);
#elif UNITY_EDITOR || UNITY_STANDALONE
        // For Unity Editor or standalone builds, save to a more accessible location
        string desktopPath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.Desktop);
        string directoryPath = Path.Combine(desktopPath, desktopSavePath);
        
        // Create directory if it doesn't exist
        if (!Directory.Exists(directoryPath))
        {
            Directory.CreateDirectory(directoryPath);
        }
        
        string fullPath = Path.Combine(directoryPath, fileName);
        File.WriteAllBytes(fullPath, imageBytes);
        
        Debug.Log("Screenshot saved to: " + fullPath);
        
        // Open the folder where the screenshot was saved (Windows, macOS, Linux)
        OpenInFileExplorer(directoryPath);
#else
        // For other platforms, just save to persistent data path
        string path = Path.Combine(Application.persistentDataPath, fileName);
        File.WriteAllBytes(path, imageBytes);
        Debug.Log("Image saved to: " + path);
#endif
    }

    private void OpenInFileExplorer(string path)
    {
#if UNITY_EDITOR_WIN || UNITY_STANDALONE_WIN
        Process.Start("explorer.exe", path);
#elif UNITY_EDITOR_OSX || UNITY_STANDALONE_OSX
        Process.Start("open", path);
#elif UNITY_EDITOR_LINUX || UNITY_STANDALONE_LINUX
        Process.Start("xdg-open", path);
#endif
    }
}
