using UnityEngine;

public class ModelInteractionController : MonoBehaviour
{
    public Transform modelParent;
    private Vector3 initialPosition, initialScale;
    private Quaternion initialRotation;

    void Start()
    {
        initialPosition = modelParent.position;
        initialRotation = modelParent.rotation;
        initialScale = modelParent.localScale;
    }

    void Update()
    {
        // Rotate
        if (Input.GetMouseButton(0))
        {
            float rotationSpeed = 100f;
            float rotX = Input.GetAxis("Mouse X") * rotationSpeed * Time.deltaTime;
            float rotY = Input.GetAxis("Mouse Y") * rotationSpeed * Time.deltaTime;
            modelParent.Rotate(Vector3.up, -rotX, Space.World);
            modelParent.Rotate(Vector3.right, rotY, Space.World);
        }

        // Zoom
        float scroll = Input.GetAxis("Mouse ScrollWheel");
        modelParent.localScale += Vector3.one * scroll;

        // Pan (Right Click)
        if (Input.GetMouseButton(1))
        {
            float panSpeed = 0.01f;
            float panX = Input.GetAxis("Mouse X") * panSpeed;
            float panY = Input.GetAxis("Mouse Y") * panSpeed;
            modelParent.Translate(new Vector3(-panX, -panY, 0));
        }
    }

    public void ResetInteraction()
    {
        modelParent.position = initialPosition;
        modelParent.rotation = initialRotation;
        modelParent.localScale = initialScale;
    }
}
