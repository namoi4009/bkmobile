using UnityEngine;
using UnityEngine.EventSystems;

public class StickerInteractable : MonoBehaviour, IPointerDownHandler, IDragHandler, IScrollHandler
{
    // Sticker đang được chọn
    public static GameObject selectedSticker;

    public void OnPointerDown(PointerEventData eventData)
    {
        selectedSticker = gameObject;
        transform.SetAsLastSibling();
    }

    public void OnDrag(PointerEventData eventData)
    {
        transform.position += (Vector3)eventData.delta;
    }

    public void OnScroll(PointerEventData eventData)
    {
        float scaleChange = eventData.scrollDelta.y * 0.1f;
        transform.localScale += Vector3.one * scaleChange;
    }
}
