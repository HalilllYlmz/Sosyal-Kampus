package com.halil.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.myapplication.R
import com.halil.myapplication.adapter.ProjectsAdapter
import com.halil.myapplication.databinding.ActivityProjectsBinding
import com.halil.myapplication.model.ProjectModal

class ProjectsActivity : AppCompatActivity() {

    private lateinit var adapter: ProjectsAdapter
    private lateinit var projectModal: ProjectModal
    private lateinit var projectArray: ArrayList<ProjectModal>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var activityProjectsBinding: ActivityProjectsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProjectsBinding = ActivityProjectsBinding.inflate(layoutInflater)
        setContentView(activityProjectsBinding.root)

        projectArray = ArrayList()
        firestore = FirebaseFirestore.getInstance()

        with(activityProjectsBinding) {
            val listItems = listOf<String>("Tümü","2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")
            val adapter = ArrayAdapter(this@ProjectsActivity, R.layout.list_item, listItems)
            yearDropDownMenu.setAdapter(adapter)

            getProjects("Tümü")

            backButton.setOnClickListener {
                val intent = Intent(this@ProjectsActivity, BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            yearDropDownMenu.onItemClickListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    progressBar.visibility = View.VISIBLE
                    projectRecycler.visibility = View.INVISIBLE
                    projectArray.clear()
                    getProjects(listItems[position])
                }
            }

        }

    }

    private fun getProjects(filter: String) {
        if (filter == "Tümü") {
            firestore.collection("Projects").get().addOnSuccessListener {documents ->
                if (documents != null) {
                    for (document in documents) {
                        projectModal = ProjectModal(document.data["studentImage"].toString(),
                            document.data["studentName"].toString(),
                            document.data["projectName"].toString(),
                            document.data["projectYear"].toString(),
                            document.data["projectImage"].toString(), false)
                        projectArray.add(projectModal)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@ProjectsActivity, "Veriler indirilemedi. ${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                activityProjectsBinding.progressBar.visibility = View.GONE
                activityProjectsBinding.projectRecycler.visibility = View.VISIBLE
                adapter = ProjectsAdapter(this@ProjectsActivity, projectArray)
                activityProjectsBinding.projectRecycler.adapter = adapter
            }
        } else {
            firestore.collection("Projects").whereEqualTo("projectYear", filter).get().addOnSuccessListener {documents ->
                if (documents != null) {
                    for (document in documents) {
                        projectModal = ProjectModal(document.data["studentImage"].toString(),
                            document.data["studentName"].toString(),
                            document.data["projectName"].toString(),
                            document.data["projectYear"].toString(),
                            document.data["projectImage"].toString(), false)
                        projectArray.add(projectModal)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@ProjectsActivity, "Veriler indirilemedi. ${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                activityProjectsBinding.progressBar.visibility = View.GONE
                activityProjectsBinding.projectRecycler.visibility = View.VISIBLE
                adapter = ProjectsAdapter(this@ProjectsActivity, projectArray)
                activityProjectsBinding.projectRecycler.adapter = adapter
            }
        }

    }





}