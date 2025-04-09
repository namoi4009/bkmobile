using UnityEngine;
using UnityEngine.UI;

public class StickerSpawner : MonoBehaviour
{
    public GameObject stickerPrefab;
    public Transform stickerContainer;

    public void SpawnSticker(Sprite stickerSprite)
    {
        GameObject sticker = Instantiate(stickerPrefab, stickerContainer);
        sticker.GetComponent<Image>().sprite = stickerSprite;
    }
}
