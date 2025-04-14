using UnityEngine;
using UnityEngine.UI;
using TMPro;
using System.Collections.Generic; // Cần thiết để sử dụng Dictionary

public class ModelNavBarController : MonoBehaviour
{
    public GameObject buttonPrefab;
    public Transform contentHolder;
    public Transform modelContainer;

    string[] modelObjectNames = {
        "cherry_blossom_branch",
        "lotus",
        "orchid",
        "rose",
        "tulip"
    };

    string[] modelDisplayNames = { "Cherry", "Lotus", "Orchid", "Rose", "Tulip" };

    private Dictionary<string, GameObject> modelReferences = new Dictionary<string, GameObject>();

    void Start()
    {
        if (modelContainer == null)
        {
            Debug.LogError("ModelContainer chưa được gán trong Inspector!");
            return;
        }

        // --- Khởi tạo trạng thái Model ---
        // Lấy tham chiếu và ẩn tất cả các model con trong modelContainer
        foreach (Transform childModel in modelContainer)
        {
            // Lưu tham chiếu vào Dictionary bằng tên của GameObject
            if (!modelReferences.ContainsKey(childModel.name))
            {
                modelReferences.Add(childModel.name, childModel.gameObject);
            }
            childModel.gameObject.SetActive(false); // Ẩn model đi lúc bắt đầu
        }

        // --- Hiển thị Model Mặc định ---
        string defaultModelName = "cherry_blossom_branch";
        ShowModel(defaultModelName); // Gọi hàm để hiển thị model mặc định

        // --- Tạo các Nút Bấm ---
        // Đảm bảo số lượng tên đối tượng khớp với số lượng tên hiển thị
        if (modelObjectNames.Length != modelDisplayNames.Length)
        {
            Debug.LogError("Số lượng phần tử trong modelObjectNames và modelDisplayNames không khớp!");
            return;
        }

        for (int i = 0; i < modelObjectNames.Length; i++)
        {
            string objectName = modelObjectNames[i];
            string displayName = modelDisplayNames[i];

            // Kiểm tra xem model có thực sự tồn tại trong Dictionary không trước khi tạo nút
            if (!modelReferences.ContainsKey(objectName))
            {
                Debug.LogWarning($"Không tìm thấy GameObject có tên '{objectName}' trong ModelContainer. Bỏ qua việc tạo nút cho model này.");
                continue;
            }

            // Tạo nút từ Prefab
            GameObject button = Instantiate(buttonPrefab, contentHolder);

            // Gán tên hiển thị cho nút
            TextMeshProUGUI buttonText = button.GetComponentInChildren<TextMeshProUGUI>();
            if (buttonText != null)
            {
                buttonText.text = displayName;
            }
            else
            {
                Debug.LogWarning($"Không tìm thấy TextMeshProUGUI trong prefab của nút: {buttonPrefab.name}");
            }

            // Gán sự kiện onClick cho nút
            Button buttonComponent = button.GetComponent<Button>();
            if (buttonComponent != null)
            {
                string nameToShowOnClick = objectName;
                buttonComponent.onClick.AddListener(() => ShowModel(nameToShowOnClick));
            }
            else
            {
                Debug.LogWarning($"Không tìm thấy Button component trong prefab của nút: {buttonPrefab.name}");
            }
        }
    }

    // Hàm để hiển thị model được chọn và ẩn các model khác
    public void ShowModel(string modelName)
    {
        // Kiểm tra xem tên model có hợp lệ không
        if (!modelReferences.ContainsKey(modelName))
        {
            Debug.LogError($"Cố gắng hiển thị model không tồn tại: {modelName}");
            return;
        }

        // Duyệt qua tất cả các model đã lưu tham chiếu
        foreach (var kvp in modelReferences)
        {
            // Nếu là model cần hiển thị thì SetActive(true), ngược lại SetActive(false)
            kvp.Value.SetActive(kvp.Key == modelName);
        }

        Debug.Log($"Đã hiển thị model: {modelName}");
    }
}