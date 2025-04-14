using UnityEngine;
using UnityEngine.UI;

public class StickerButtonManager : MonoBehaviour
{
    [SerializeField] private StickerManager stickerManager;
    [SerializeField] private Button[] stickerButtons;
    [SerializeField] private Button deleteButton;

    private void Start()
    {
        if (stickerManager == null)
            stickerManager = FindObjectOfType<StickerManager>();

        // Set up sticker buttons
        for (int i = 0; i < stickerButtons.Length; i++)
        {
            int index = i; // Capture the index for the lambda
            stickerButtons[i].onClick.AddListener(() => OnStickerButtonClicked(index));
        }

        // Set up delete button if available
        if (deleteButton != null)
            deleteButton.onClick.AddListener(OnDeleteButtonClicked);
    }

    private void OnStickerButtonClicked(int index)
    {
        stickerManager.CreateNewSticker(index);
    }

    private void OnDeleteButtonClicked()
    {
        stickerManager.DeleteLastSticker();
    }
}
