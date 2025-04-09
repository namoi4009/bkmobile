using UnityEngine;
using UnityEngine.UI;
using System.Collections.Generic;

public class StickerNavBarManager : MonoBehaviour
{
    public GameObject stickerButtonPrefab; // Prefab cho nút Sticker
    public Transform stickerButtonContainer; // Content của Vertical Scroll View
    public StickerSpawner stickerSpawner; // Script để spawn sticker thật sự vào màn hình
    public List<Sprite> stickerSprites; // Danh sách sticker images sẽ được load vào Scroll View

    private bool isShown = false;

    void Start()
    {
        // Ban đầu tắt Scroll View
        stickerButtonContainer.parent.parent.gameObject.SetActive(false);
        PopulateStickers();
    }

    // Spawn ra danh sách sticker button khi khởi động
    void PopulateStickers()
    {
        foreach (var stickerSprite in stickerSprites)
        {
            GameObject stickerBtn = Instantiate(stickerButtonPrefab, stickerButtonContainer);
            stickerBtn.GetComponent<Image>().sprite = stickerSprite;

            // Gắn event click cho nút
            stickerBtn.GetComponent<Button>().onClick.AddListener(() => OnStickerClicked(stickerSprite));
        }
    }

    void OnStickerClicked(Sprite stickerSprite)
    {
        // Spawn sticker thật lên màn hình
        stickerSpawner.SpawnSticker(stickerSprite);
    }

    // Nút TriggerNavBar gọi hàm này để bật tắt Scroll View
    public void ToggleStickerBar()
    {
        isShown = !isShown;
        stickerButtonContainer.parent.parent.gameObject.SetActive(isShown);
    }
}
