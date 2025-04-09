using UnityEngine;

public class StickerDeleteButton : MonoBehaviour
{
    public void DeleteSelectedSticker()
    {
        if (StickerInteractable.selectedSticker != null)
        {
            Destroy(StickerInteractable.selectedSticker);
            StickerInteractable.selectedSticker = null;
        }
    }
}
