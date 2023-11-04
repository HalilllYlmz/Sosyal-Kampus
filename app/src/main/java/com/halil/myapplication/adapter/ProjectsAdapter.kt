package com.halil.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.halil.myapplication.R
import com.halil.myapplication.model.ProjectModal
import de.hdodenhof.circleimageview.CircleImageView

class ProjectsAdapter(private val context: Context, private val projectsArray: ArrayList<ProjectModal>) : RecyclerView.Adapter<ProjectsAdapter.ProjectHolder>() {

    class ProjectHolder(itemView: View) : ViewHolder(itemView) {
        val profileImageView: CircleImageView = itemView.findViewById(R.id.profileImageView)
        val studentNameText: TextView = itemView.findViewById(R.id.studentNameText)
        val projectNameText: TextView = itemView.findViewById(R.id.projectNameText)
        val projectYearText: TextView = itemView.findViewById(R.id.projectYearText)
        val projectImageView: ImageView = itemView.findViewById(R.id.projectImage)
        val expandButton: ImageView = itemView.findViewById(R.id.expandButton)
        val expandedLayout: ConstraintLayout = itemView.findViewById(R.id.expandedLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.project_items, parent, false)
        return ProjectHolder(view)
    }

    override fun getItemCount(): Int {
        return projectsArray.size
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
        Glide.with(context).load(projectsArray[position].studentImageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
            R.drawable.people).into(holder.profileImageView)
        Glide.with(context).load(projectsArray[position].projectImage).fitCenter().placeholder(R.drawable.loading_animation).error(
            R.drawable.people).into(holder.projectImageView)
        holder.studentNameText.text = projectsArray[position].studentName
        holder.projectYearText.text = "Yıl : ${projectsArray[position].projectYear}"
        holder.projectNameText.text = "Proje Adı : ${projectsArray[position].projectName}"

        val expanded = projectsArray[position].expanded
        holder.expandedLayout.visibility = if (expanded) View.VISIBLE else View.GONE

        holder.expandButton.setOnClickListener {
            projectsArray[position].expanded = !projectsArray[position].expanded
            notifyItemChanged(position)
        }

    }

}