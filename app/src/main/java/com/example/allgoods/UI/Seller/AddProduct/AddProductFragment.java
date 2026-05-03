package com.example.allgoods.UI.Seller.AddProduct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.allgoods.R;
import com.example.allgoods.databinding.FragmentAddProductBinding;
import com.example.allgoods.model.ProductModel;
import com.example.allgoods.utils.Category;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductFragment extends Fragment {
    private FragmentAddProductBinding binding;
    private AddProductViewModel viewModel;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private List<Uri> selectedImageUris = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri uri = getImageUri(imageBitmap);
                selectedImageUris.add(uri);
                updatePhotoNamesUI();
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                if (result.getData().getClipData() != null) {
                    // multiple photos
                    int count = result.getData().getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        selectedImageUris.add(result.getData().getClipData().getItemAt(i).getUri());
                    }
                } else if (result.getData().getData() != null) {
                    // one photo
                    selectedImageUris.add(result.getData().getData());
                }
                updatePhotoNamesUI();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AddProductViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategorySpinner();
        setupAllPickers();
        observeViewModel();

        binding.btnUpload.setOnClickListener(v -> showImagePickerDialog());
        binding.btnAddProduct.setOnClickListener(v -> collectDataAndSave());
    }

    private void observeViewModel() {
        viewModel.uploadStatus.observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (status.equals("Success")) {
                    Toast.makeText(requireContext(), "Product Added Successfully", Toast.LENGTH_LONG).show();
                    clearFields();
                } else {
                    Toast.makeText(requireContext(), "Error: " + status, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "Product_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private void collectDataAndSave() {
        String title = binding.etProductTitle.getText().toString().trim();
        String categoryStr = binding.spinnerCategory.getText().toString().trim();
        String price = binding.etProductPrice.getText().toString().trim();
        String desc = binding.etProductDesc.getText().toString().trim();


        if (title.isEmpty()) { Toast.makeText(requireContext(), "Title Required", Toast.LENGTH_SHORT).show(); return; }
        if (categoryStr.isEmpty()) { Toast.makeText(requireContext(), "Category Required", Toast.LENGTH_SHORT).show(); return; }
        if (price.isEmpty()) { Toast.makeText(requireContext(), "Price Required", Toast.LENGTH_SHORT).show(); return; }
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(requireContext(), "Please Select Images", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Integer> sizesMap = new HashMap<>();
        sizesMap.put("XS", getPickerValue(binding.pickerXS.getRoot()));
        sizesMap.put("S", getPickerValue(binding.pickerS.getRoot()));
        sizesMap.put("M", getPickerValue(binding.pickerM.getRoot()));
        sizesMap.put("L", getPickerValue(binding.pickerL.getRoot()));
        sizesMap.put("XL", getPickerValue(binding.pickerXL.getRoot()));
        sizesMap.put("XXL", getPickerValue(binding.pickerXXL.getRoot()));

        Category categoryEnum;
        try {
            categoryEnum = Category.valueOf(categoryStr.toUpperCase().replace("-", ""));
        } catch (Exception e) {
            categoryEnum = Category.TSHIRT;
        }

        String adminId = FirebaseAuth.getInstance().getUid();

        List<String> emptyUrls = new ArrayList<>();

        ProductModel sellerProduct = new ProductModel(
                title,
                emptyUrls,
                Double.parseDouble(price),
                desc,
                categoryEnum,
                adminId,
                sizesMap
        );

        Toast.makeText(requireContext(), "Uploading... Please wait", Toast.LENGTH_SHORT).show();
        viewModel.saveProduct(sellerProduct, selectedImageUris);
    }

    private int getPickerValue(View includeView) {
        TextView tvQuantity = includeView.findViewById(R.id.tvQuantity);
        return Integer.parseInt(tvQuantity.getText().toString());
    }

    private void setupAllPickers() {
        setupSinglePicker(binding.pickerXS.getRoot(), "XS");
        setupSinglePicker(binding.pickerS.getRoot(), "S");
        setupSinglePicker(binding.pickerM.getRoot(), "M");
        setupSinglePicker(binding.pickerL.getRoot(), "L");
        setupSinglePicker(binding.pickerXL.getRoot(), "XL");
        setupSinglePicker(binding.pickerXXL.getRoot(), "XXL");
    }

    private void setupSinglePicker(View includeView, String sizeLabel) {
        TextView label = includeView.findViewById(R.id.tvSizeLabel);
        TextView tvQuantity = includeView.findViewById(R.id.tvQuantity);
        View btnPlus = includeView.findViewById(R.id.btnPlus);
        View btnMinus = includeView.findViewById(R.id.btnMinus);

        label.setText(sizeLabel);
        tvQuantity.setText("0");

        btnPlus.setOnClickListener(v -> {
            int current = Integer.parseInt(tvQuantity.getText().toString());
            tvQuantity.setText(String.valueOf(current + 1));
        });

        btnMinus.setOnClickListener(v -> {
            int current = Integer.parseInt(tvQuantity.getText().toString());
            if (current > 0) {
                tvQuantity.setText(String.valueOf(current - 1));
            }
        });
    }

    private void setupCategorySpinner() {
        String[] categories = { "Hoodie", "Pants", "T-Shirt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.layout_category_item, R.id.tvCategoryName, categories);
        binding.spinnerCategory.setAdapter(adapter);

        binding.spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            binding.spinnerCategory.setHint("");
        });


        binding.spinnerCategory.post(() -> {
            int offset = -binding.spinnerCategory.getHeight();
            binding.spinnerCategory.setDropDownVerticalOffset(offset);
        });
        binding.spinnerCategory.setDropDownBackgroundResource(android.R.color.white);
    }

    private void showImagePickerDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_upload_options, null);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheet.dismiss());

        view.findViewById(R.id.layoutCamera).setOnClickListener(v -> {
            cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.layoutGallery).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            galleryLauncher.launch(Intent.createChooser(intent, "Select Pictures"));
            bottomSheet.dismiss();
        });

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void clearFields() {
        binding.etProductTitle.setText("");
        binding.etProductPrice.setText("");
        binding.etProductDesc.setText("");
        binding.etPhotoName.setText("");
        binding.ImagesNumber.setText("0");
        binding.spinnerCategory.setText(null);
        binding.spinnerCategory.setHint(R.string.category_hint);
        binding.spinnerCategory.clearFocus();
        selectedImageUris.clear();
        setupAllPickers();
    }


    private void updatePhotoNamesUI() {
        if (selectedImageUris.isEmpty()) {
            binding.etPhotoName.setText("");
            binding.ImagesNumber.setText("0");
            return;
        }

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < selectedImageUris.size(); i++) {
            names.append(getFileName(selectedImageUris.get(i)));
            if (i < selectedImageUris.size() - 1) {
                names.append(", ");
            }
        }

        binding.etPhotoName.setText(names.toString());
        binding.ImagesNumber.setText(String.valueOf(selectedImageUris.size()));
    }
}