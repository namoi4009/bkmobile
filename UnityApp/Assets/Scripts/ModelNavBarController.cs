using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class ModelNavBarController : MonoBehaviour
{
    public GLTFModelLoader modelLoader;
    public GameObject buttonPrefab;
    public Transform contentHolder;

    string[] models = { "cherry_blossom_branch.glb", "lilies.glb", "lotus.glb", "orchid.glb", "rose.glb", "sunflower.glb", "tulip.glb" };
    string[] modelDisplayNames = {  "Cherry", "Lilies", "Lotus", "Orchid", "Rose", "Sunflower", "Tulip" };
    void Start()
    {
        for (int i = 0; i < models.Length; i++)
        {
            string modelFile = models[i];
            string displayName = modelDisplayNames[i];

            GameObject button = Instantiate(buttonPrefab, contentHolder);
            button.GetComponentInChildren<TextMeshProUGUI>().text = displayName;

            button.GetComponent<Button>().onClick.AddListener(() => modelLoader.LoadModel(modelFile));
        }
    }

}
