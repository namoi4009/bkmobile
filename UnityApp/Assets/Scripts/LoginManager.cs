using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class LoginManager : MonoBehaviour
{
    public InputField usernameInput;
    public InputField passwordInput;
    public Button loginButton;
    public TextMeshProUGUI errorText;

    private string validUser = "nam123";
    private string validPass = "nam123";

    private bool isLoggingIn = false;

    public void OnLoginButtonClicked()
    {
        if (isLoggingIn) return; // prevent spam click

        if (usernameInput.text == validUser && passwordInput.text == validPass)
        {
            isLoggingIn = true;
            errorText.color = Color.green;
            errorText.SetText("Login successfully!");
            StartCoroutine(DelayedLoadScene());
        }
        else
        {
            errorText.color = Color.red;
            errorText.SetText("Invalid Username or Password. Please try again!");
        }
    }

    private IEnumerator DelayedLoadScene()
    {
        yield return new WaitForSeconds(1f);
        SceneManager.LoadScene("HomeScene");
    }

    // Start is called before the first frame update
    void Start()
    {
        isLoggingIn = false;
        loginButton.onClick.AddListener(OnLoginButtonClicked);
        errorText.SetText("");
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
