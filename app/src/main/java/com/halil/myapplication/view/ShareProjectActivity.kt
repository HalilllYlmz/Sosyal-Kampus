package com.halil.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.halil.myapplication.R
import com.halil.myapplication.databinding.ActivityShareProjectBinding
import com.halil.myapplication.utils.GetFileName
import java.util.*
import kotlin.collections.HashMap

class ShareProjectActivity : AppCompatActivity() {

    private var studentImage = ""
    private var studentName = ""
    private var projectName = ""
    private var projectYear = ""
    private var projectImage = ""

    private var profileImageUri: Uri? = null
    private var projectImageUri: Uri? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageProfileLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageProjectLauncher: ActivityResultLauncher<Intent>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var binding: ActivityShareProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.shareProgress.visibility = View.GONE

        //registerLauncher()

        imageProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if  (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                profileImageUri = intentFromResult?.data
                profileImageUri?.let {
                    binding.circleImageView.setImageURI(profileImageUri)
                    binding.addImageView.visibility = View.GONE
                }
            }
        }

        imageProfile()

        imageProjectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if  (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                projectImageUri = intentFromResult?.data
                projectImageUri?.let {
                    val getFileName = GetFileName(this@ShareProjectActivity)
                    binding.projectImageText.setText(getFileName.fileName(projectImageUri!!))
                }
            }
        }

        imageProject()


        with(binding) {
            val listItems = listOf("2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")
            val adapter = ArrayAdapter(this@ShareProjectActivity, R.layout.list_item, listItems)
            yearDropDownMenu.setAdapter(adapter)

            profileImage.setOnClickListener {
                if  (ContextCompat.checkSelfPermission(this@ShareProjectActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if  (ActivityCompat.shouldShowRequestPermissionRationale(this@ShareProjectActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(it, "Galeriye erişim için izin verin.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    imageProfileLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                }
            }

            projectImageText.setOnClickListener {
                if  (ContextCompat.checkSelfPermission(this@ShareProjectActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if  (ActivityCompat.shouldShowRequestPermissionRationale(this@ShareProjectActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(it, "Galeriye erişim için izin verin.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    imageProjectLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                }
            }

            shareButton.setOnClickListener {
                if (projectImageUri == null || profileImageUri == null || nameSurnameText.text.isNullOrEmpty() || projectNameText.text.isNullOrEmpty()
                    || yearDropDownMenu.text.isNullOrEmpty()) {
                    Toast.makeText(this@ShareProjectActivity,"Boş alanları doldurunuz. Resimleri unutmayınız.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ShareProjectActivity,"Bu işlem biraz zaman alabilir. Lütfen bekleyiniz..", Toast.LENGTH_LONG).show()
                    shareProgress.visibility = View.VISIBLE
                    studentName = nameSurnameText.text.toString()
                    projectName = projectNameText.text.toString()
                    projectYear = yearDropDownMenu.text.toString()
                    uploadProfileImage(profileImageUri!!, projectImageUri!!)
                }
            }

        }
    }


    private fun imageProfile() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if  (result) {
                imageProfileLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            } else {
                Toast.makeText(this@ShareProjectActivity, "Galeriye erişim için izin veriniz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun imageProject() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if  (result) {
                imageProjectLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            } else {
                Toast.makeText(this@ShareProjectActivity, "Galeriye erişim için izin veriniz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProject(dataHashMap: HashMap<String, Any>) {
        firestore.collection("Projects").add(dataHashMap).addOnSuccessListener {
            val intent = Intent(this@ShareProjectActivity, ProjectsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@ShareProjectActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImage(profileImageURI: Uri, projectImageURI: Uri) : String {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("projects/$imageName")
        imageReference.putFile(profileImageURI).addOnSuccessListener {
            val uploadImageReference = storage.reference.child("projects").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                studentImage = downloadUrl
            }
        }.addOnFailureListener {
            Toast.makeText(this@ShareProjectActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            uploadProjectImage(projectImageURI)
        }
        return studentImage
    }

    private fun uploadProjectImage(projectImageURI: Uri) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("projects/$imageName")
        imageReference.putFile(projectImageURI).addOnSuccessListener {
            val uploadImageReference = storage.reference.child("projects").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                val dataHash : HashMap<String,Any> = HashMap()
                dataHash["studentImage"] = studentImage
                dataHash["studentName"] = studentName
                dataHash["projectName"] = projectName
                dataHash["projectYear"] = projectYear
                dataHash["projectImage"] = downloadUrl
                saveProject(dataHash)
            }
        }.addOnFailureListener {
            Toast.makeText(this@ShareProjectActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }



}