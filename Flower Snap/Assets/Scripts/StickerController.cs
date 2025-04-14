using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class StickerController : MonoBehaviour, IDragHandler, IBeginDragHandler, IEndDragHandler
{
    private RectTransform rectTransform;
    private Vector2 lastTouchPosition;
    private bool isDragging = false;

    private void Awake()
    {
        rectTransform = GetComponent<RectTransform>();
    }

    public void OnBeginDrag(PointerEventData eventData)
    {
        lastTouchPosition = eventData.position;
        isDragging = true;
    }

    public void OnDrag(PointerEventData eventData)
    {
        if (isDragging)
        {
            Vector2 currentTouchPosition = eventData.position;
            Vector2 difference = currentTouchPosition - lastTouchPosition;

            Vector3 newPosition = rectTransform.position + new Vector3(difference.x, difference.y, 0);
            rectTransform.position = newPosition;

            lastTouchPosition = currentTouchPosition;
        }
    }

    public void OnEndDrag(PointerEventData eventData)
    {
        isDragging = false;
    }
}
