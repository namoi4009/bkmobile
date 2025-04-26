using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class StickerManager : MonoBehaviour
{
    [SerializeField] private GameObject stickerPrefab;
    [SerializeField] private Transform canvasTransform;
    [SerializeField] private List<Sprite> availableStickerSprites;

    private List<GameObject> activeStickers = new List<GameObject>();

    private void Start()
    {
        if (canvasTransform == null)
            canvasTransform = GameObject.Find("Canvas").transform;
    }

    public void CreateNewSticker(int stickerIndex)
    {
        if (stickerPrefab == null || availableStickerSprites == null)
        {
            Debug.LogError("Sticker prefab or available stickers not set!");
            return;
        }

        if (stickerIndex < 0 || stickerIndex >= availableStickerSprites.Count)
        {
            Debug.LogWarning("Invalid sticker index: " + stickerIndex);
            return;
        }

        // Instantiate a new sticker
        GameObject newSticker = Instantiate(stickerPrefab, canvasTransform);

        // Position the sticker in the center of the screen
        RectTransform rectTransform = newSticker.GetComponent<RectTransform>();
        rectTransform.anchoredPosition = Vector2.zero;

        // Set the sticker sprite
        Image stickerImage = newSticker.GetComponent<Image>();
        if (stickerImage != null)
        {
            stickerImage.sprite = availableStickerSprites[stickerIndex];
        }

        // Add draggable component if it doesn't exist
        if (newSticker.GetComponent<StickerController>() == null)
        {
            newSticker.AddComponent<StickerController>();
        }

        // Add to active stickers list
        activeStickers.Add(newSticker);
    }

    public void DeleteLastSticker()
    {
        if (activeStickers.Count > 0)
        {
            int lastIndex = activeStickers.Count - 1;
            GameObject stickerToDelete = activeStickers[lastIndex];
            activeStickers.RemoveAt(lastIndex);
            Destroy(stickerToDelete);
        }
    }

    public void ClearAllStickers()
    {
        foreach (GameObject sticker in activeStickers)
        {
            Destroy(sticker);
        }
        activeStickers.Clear();
    }
}
