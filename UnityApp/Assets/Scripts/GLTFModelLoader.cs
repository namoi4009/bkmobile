using UnityEngine;
using UnityEngine.Networking;
using UnityGLTF;
using System.Collections;
using System.IO;

public class GLTFModelLoader : MonoBehaviour
{
    public Transform ModelParent;
    private GameObject currentModel;
    private GLTFSceneImporter importer;

    // Hàm public gọi để load model
    public void LoadModel(string modelName)
    {
        StartCoroutine(LoadGLBModelCoroutine(modelName));
    }

    private IEnumerator LoadGLBModelCoroutine(string modelName)
    {
        yield return UnloadPreviousModel();
        yield return StartCoroutine(LoadGLBFile(modelName));

        if (importer == null)
        {
            Debug.LogError("Không thể khởi tạo importer. Dừng load model!");
            yield break;
        }

        yield return importer.LoadSceneAsync();

        if (importer.LastLoadedScene == null)
        {
            Debug.LogError("Model không load được hoặc LastLoadedScene = null!");
            yield break;
        }

        currentModel = importer.LastLoadedScene;
        currentModel.transform.SetParent(ModelParent, false);

        currentModel.transform.localPosition = Vector3.zero;
        currentModel.transform.localRotation = Quaternion.identity;
        currentModel.transform.localScale = Vector3.one;

        Debug.Log("Load model thành công: " + modelName);
    }

    private IEnumerator LoadGLBFile(string modelName)
    {
        string fileNameWithPath = System.IO.Path.Combine(Application.streamingAssetsPath, "Models", modelName);

#if UNITY_ANDROID && !UNITY_EDITOR
        using (UnityWebRequest www = UnityWebRequest.Get(fileNameWithPath))
        {
            yield return www.SendWebRequest();
            if (www.result != UnityWebRequest.Result.Success)
            {
                Debug.LogError("Không thể tải glb từ StreamingAssets: " + www.error);
                yield break;
            }

            // Chuyển byte[] -> MemoryStream
            byte[] glbData = www.downloadHandler.data;
            MemoryStream glbStream = new MemoryStream(glbData);

            // Tạo importer cho file .glb
            importer = new GLTFSceneImporter(
                new GLBStream(glbStream, glbStream),
                new ImportOptions()
            );
        }
#else
        if (!File.Exists(fileNameWithPath))
        {
            Debug.LogError("File không tồn tại: " + fileNameWithPath);
            yield break;
        }

        importer = new GLTFSceneImporter(fileNameWithPath, new ImportOptions());
        yield return null;
#endif
    }

    private IEnumerator UnloadPreviousModel()
    {
        if (importer != null)
        {
            importer.Dispose();
            importer = null;
        }

        if (currentModel != null)
        {
            Destroy(currentModel);
            currentModel = null;
        }

        yield return null;
    }

    public void ResetModel()
    {
        if (currentModel != null)
        {
            currentModel.transform.localPosition = Vector3.zero;
            currentModel.transform.localRotation = Quaternion.identity;
            currentModel.transform.localScale = Vector3.one;
        }
    }
}
