using UnityEngine;
using UnityEngine.Android;

public class CameraPermission : MonoBehaviour
{
    public GameObject permissionScreen;

    void Start()
    {
#if UNITY_ANDROID
        if (!Permission.HasUserAuthorizedPermission(Permission.Camera))
        {
            Permission.RequestUserPermission(Permission.Camera);
            permissionScreen.SetActive(true);
        }
        else
        {
            permissionScreen.SetActive(false);
        }
#endif
    }

    void OnApplicationFocus(bool hasFocus)
    {
#if UNITY_ANDROID
        if (hasFocus)
        {
            permissionScreen.SetActive(!Permission.HasUserAuthorizedPermission(Permission.Camera));
        }
#endif
    }
}
