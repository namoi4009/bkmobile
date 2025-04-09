using UnityEngine;
using UnityGLTF;
using System.Collections;

public class GLTFModelLoader : MonoBehaviour
{
    public Transform ModelParent;
    private GameObject currentModel;

    public void LoadModel(string modelName)
    {
        if (currentModel != null) Destroy(currentModel);
        StartCoroutine(LoadGLBCoroutine(modelName));
    }

    IEnumerator LoadGLBCoroutine(string modelName)
    {
        string path = System.IO.Path.Combine(Application.streamingAssetsPath, "Models", modelName);
        GLTFSceneImporter importer = new GLTFSceneImporter(path, new ImportOptions());
        yield return importer.LoadSceneAsync();
        currentModel = importer.LastLoadedScene;
        currentModel.transform.SetParent(ModelParent, false);

        currentModel.transform.localPosition = Vector3.zero;
        currentModel.transform.localRotation = Quaternion.identity;

        currentModel.transform.localScale = Vector3.one * 1f;
    }


    public void ResetModel()
    {
        currentModel.transform.localPosition = Vector3.zero;
        currentModel.transform.localRotation = Quaternion.identity;
        currentModel.transform.localScale = Vector3.one;
    }
}
