using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

public class ButtonHandler : MonoBehaviour
{
    [SerializeField] Button pressMe;
    [SerializeField] TextMeshProUGUI content;
    private int count = 0;

    // Start is called before the first frame update
    void Start()
    {
        if (content) content.SetText(count.ToString());
    }

    public void onButtonClick()
    {
        count++;
        content.SetText(count.ToString());
    }

    public void onButtonReset()
    {
        count = 0;
        content.SetText(count.ToString());
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
