using UnityEngine;

public class NavBarController : MonoBehaviour
{
    [SerializeField] private GameObject navBar;
    [SerializeField] private GameObject triggerNavBar;

    private void Start()
    {
        // Hide NavBar at the beginning
        navBar.SetActive(false);
        triggerNavBar.SetActive(true);
    }

    public void ToggleNavBar()
    {
        navBar.SetActive(!navBar.activeSelf);
    }

    public void ShowNavBar()
    {
        navBar.SetActive(true);
    }

    public void HideNavBar()
    {
        navBar.SetActive(false);
    }
}
