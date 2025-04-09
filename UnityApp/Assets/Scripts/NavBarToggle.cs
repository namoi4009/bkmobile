using UnityEngine;

public class NavBarToggle : MonoBehaviour
{
    public GameObject navBar;

    public void ToggleNavBar()
    {
        navBar.SetActive(!navBar.activeSelf);
    }
}