using UnityEngine;
using UnityEngine.EventSystems;

public class ModelInteractionController : MonoBehaviour
{
    [Tooltip("Gán Transform cha chứa các model (ví dụ: ModelContainer) hoặc một Transform điều khiển chung.")]
    public Transform interactionTarget;

    [Header("Tốc độ Tương tác")]
    public float rotationSpeed = 10f;
    public float zoomSpeed = 0.5f;
    public float panSpeed = 0.5f;

    [Header("Giới hạn Zoom")]
    public float minZoomScale = 0.3f;
    public float maxZoomScale = 4.0f;

    private Vector3 initialPosition;
    private Quaternion initialRotation;
    private Vector3 initialScale;

    private Vector3 touchStartPos; // Có thể không cần nữa nếu pan dùng lastPanPosition
    private Vector2 lastPanPosition;
    private float initialTouchDistance;
    private Vector3 initialTargetScale;

    // Biến cờ để theo dõi xem có nên bỏ qua input model không
    private bool isInteractingWithUI = false;

    void Start()
    {
        if (interactionTarget == null)
        {
            Debug.LogError("Interaction Target chưa được gán trong ModelInteractionController! Vui lòng gán Transform.");
            this.enabled = false;
            return;
        }
        StoreInitialTransform();
    }

    void StoreInitialTransform()
    {
        initialPosition = interactionTarget.localPosition;
        initialRotation = interactionTarget.localRotation;
        initialScale = interactionTarget.localScale;
    }

    void Update()
    {
        if (interactionTarget == null) return;

        // --- KIỂM TRA INPUT TRÊN UI ---
        // Reset cờ mỗi frame
        isInteractingWithUI = false;

        // Kiểm tra cho Touch Input
        if (Input.touchCount > 0)
        {
            // Kiểm tra từng ngón tay chạm
            for (int i = 0; i < Input.touchCount; i++)
            {
                // QUAN TRỌNG: Phải truyền fingerId cho touch input
                if (EventSystem.current.IsPointerOverGameObject(Input.GetTouch(i).fingerId))
                {
                    isInteractingWithUI = true;
                    break; // Chỉ cần một ngón tay chạm UI là đủ để bỏ qua tương tác model
                }
            }
        }
        // Kiểm tra cho Mouse Input (khi không có touch)
        else if (Input.mousePresent) // Chỉ kiểm tra chuột nếu có chuột và không có chạm
        {
            // Dùng IsPointerOverGameObject() không có đối số cho chuột
            if (EventSystem.current.IsPointerOverGameObject())
            {
                isInteractingWithUI = true;
            }
        }

        // --- XỬ LÝ INPUT ---
        // Chỉ xử lý input cho model nếu không tương tác với UI
        if (!isInteractingWithUI)
        {
            HandleTouchInput(); // Xử lý chạm cho model

            // --- Input dự phòng cho Editor/PC (sử dụng chuột) ---
#if UNITY_EDITOR || UNITY_STANDALONE || UNITY_WEBGL
            if (Input.touchCount == 0 && Input.mousePresent)
            {
                HandleMouseInput(); // Xử lý chuột cho model
            }
#endif
        }
        else
        {
            // Optional: Reset trạng thái nếu đang chạm UI để tránh hành vi không mong muốn khi chuyển đổi
            // Ví dụ: lastPanPosition = Vector2.zero; // hoặc null tùy cách bạn xử lý
        }
    }

    void HandleTouchInput()
    {
        // Logic xử lý chạm 1 ngón, 2 ngón như trước...
        // PAN (1 ngón)
        if (Input.touchCount == 1)
        {
            Touch touch = Input.GetTouch(0);
            // ... (logic pan giữ nguyên) ...
            switch (touch.phase)
            {
                case TouchPhase.Began:
                    lastPanPosition = touch.position;
                    break;
                case TouchPhase.Moved:
                    Vector2 deltaPosition = touch.position - lastPanPosition;
                    Vector3 right = Camera.main.transform.right * -deltaPosition.x * panSpeed * Time.deltaTime;
                    Vector3 up = Camera.main.transform.up * -deltaPosition.y * panSpeed * Time.deltaTime;
                    interactionTarget.Translate(right + up, Space.World);
                    lastPanPosition = touch.position;
                    break;
                case TouchPhase.Ended:
                case TouchPhase.Canceled:
                    break;
            }
        }
        // XOAY và ZOOM (bằng 2 ngón)
        else if (Input.touchCount == 2)
        {
            Touch touchZero = Input.GetTouch(0);
            Touch touchOne = Input.GetTouch(1);
            // ... (logic zoom và xoay giữ nguyên) ...
            if (touchZero.phase == TouchPhase.Began || touchOne.phase == TouchPhase.Began)
            {
                initialTouchDistance = Vector2.Distance(touchZero.position, touchOne.position);
                initialTargetScale = interactionTarget.localScale;
            }
            else if (touchZero.phase == TouchPhase.Moved || touchOne.phase == TouchPhase.Moved)
            {
                // ZOOM
                float currentTouchDistance = Vector2.Distance(touchZero.position, touchOne.position);
                if (!Mathf.Approximately(initialTouchDistance, 0))
                {
                    float scaleFactor = currentTouchDistance / initialTouchDistance;
                    Vector3 newScale = initialTargetScale * scaleFactor;
                    newScale.x = Mathf.Clamp(newScale.x, minZoomScale, maxZoomScale);
                    newScale.y = Mathf.Clamp(newScale.y, minZoomScale, maxZoomScale);
                    newScale.z = Mathf.Clamp(newScale.z, minZoomScale, maxZoomScale);
                    interactionTarget.localScale = newScale;
                }

                // XOAY
                Vector2 prevDir = (touchZero.position - touchZero.deltaPosition) - (touchOne.position - touchOne.deltaPosition);
                Vector2 currDir = touchZero.position - touchOne.position;
                float angle = Vector2.SignedAngle(prevDir, currDir);
                interactionTarget.Rotate(Vector3.up, angle * rotationSpeed * 0.1f, Space.World);
            }
        }
        else // No touches or > 2 touches
        {
            // Reset any relevant state if needed
            // lastPanPosition = Vector2.zero; // Hoặc null
        }
    }

    void HandleMouseInput()
    {
        // Logic xử lý chuột như trước...
        // Rotate (Chuột trái)
        if (Input.GetMouseButton(0))
        {
            float mouseRotationSpeed = 1000f;
            float rotX = Input.GetAxis("Mouse X") * mouseRotationSpeed * Time.deltaTime;
            float rotY = Input.GetAxis("Mouse Y") * mouseRotationSpeed * Time.deltaTime;
            interactionTarget.Rotate(Vector3.up, -rotX, Space.World);
            interactionTarget.Rotate(Vector3.right, rotY, Space.World);
        }

        // Zoom (Lăn chuột)
        float scroll = Input.GetAxis("Mouse ScrollWheel");
        if (Mathf.Abs(scroll) > 0.01f)
        {
            float scrollZoomSpeed = 1000f;
            Vector3 currentScale = interactionTarget.localScale;
            Vector3 scaleChange = Vector3.one * scroll * scrollZoomSpeed * Time.deltaTime;
            Vector3 newScale = currentScale + scaleChange;
            newScale.x = Mathf.Clamp(newScale.x, minZoomScale, maxZoomScale);
            newScale.y = Mathf.Clamp(newScale.y, minZoomScale, maxZoomScale);
            newScale.z = Mathf.Clamp(newScale.z, minZoomScale, maxZoomScale);
            interactionTarget.localScale = newScale;
        }

        // Pan (Chuột phải hoặc chuột giữa)
        if (Input.GetMouseButton(2) || Input.GetMouseButton(1))
        {
            float mousePanSpeed = 1.0f;
            Vector3 right = Camera.main.transform.right * -Input.GetAxis("Mouse X") * mousePanSpeed * Time.deltaTime;
            Vector3 up = Camera.main.transform.up * -Input.GetAxis("Mouse Y") * mousePanSpeed * Time.deltaTime;
            interactionTarget.Translate(right + up, Space.World);
        }
        else
        {
            // Reset mouse-specific state if needed
        }
    }

    public void ResetInteraction()
    {
        if (interactionTarget != null)
        {
            interactionTarget.localPosition = initialPosition;
            interactionTarget.localRotation = initialRotation;
            interactionTarget.localScale = initialScale;
            Debug.Log($"Interaction reset cho target: {interactionTarget.name}");
            // Reset cả trạng thái touch/mouse khi reset model
            isInteractingWithUI = false;
            // lastPanPosition = Vector2.zero; // Hoặc null
        }
    }
}