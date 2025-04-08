using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class SceneChanger : MonoBehaviour
{
    [Header("Setting Scene")]
    [Tooltip("Insert Scene's Name")]
    public string targetScene;

    // Load scene khi click
    public void LoadScene()
    {
        if (!string.IsNullOrEmpty(targetScene))
        {
            if (targetScene != null)
            {
                SceneManager.LoadScene(targetScene);
            }
        }
        else
        {
            Debug.LogError("Destination Scene Not Found");
        }
    }
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
