package com.halil.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.halil.myapplication.R
import com.halil.myapplication.databinding.FragmentCreateAccFirstBinding

class CreateAccFirstFragment : Fragment() {

    private lateinit var fragmentCreateAccFirstBinding: FragmentCreateAccFirstBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentCreateAccFirstBinding = FragmentCreateAccFirstBinding.inflate(layoutInflater)


        with(fragmentCreateAccFirstBinding) {

            classLayout.visibility = View.GONE

            val listItems = listOf<String>("Öğrenci", "Yönetici")
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, listItems)
            dropDownMenu.setAdapter(adapter)

            dropDownMenu.onItemClickListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if  (position == 0) {
                        classLayout.visibility = View.VISIBLE
                        val classItems = listOf<String>("Yazilim Mühendisliği 1. Sınıf", "Yazilim Mühendisliği 2. Sınıf", "Yazilim Mühendisliği 3. Sınıf", "Yazilim Mühendisliği 4. Sınıf")
                        val classAdapter = ArrayAdapter(requireContext(), R.layout.list_item, classItems)
                        classDownMenu.setAdapter(classAdapter)
                    } else {
                        classLayout.visibility = View.GONE
                    }
                }
            }

            nextButton.setOnClickListener {
                if (dropDownMenu.text.toString() == "Öğrenci") {
                    if (nameText.text.isNullOrEmpty() || surnameText.text.isNullOrEmpty() ||
                        numberText.text.isNullOrEmpty() || dropDownMenu.text.isNullOrEmpty() ||
                        passwordText.text.isNullOrEmpty() || classDownMenu.text.isNullOrEmpty()
                    ) {
                        Toast.makeText(requireContext(), "Boş alanları doldurunuz!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val createAccSecondFragment = CreateAccSecondFragment()
                        val bundle = Bundle()
                        bundle.putString("Name",nameText.text.toString())
                        bundle.putString("Surname", surnameText.text.toString())
                        bundle.putString("Number", numberText.text.toString())
                        bundle.putString("State", dropDownMenu.text.toString())
                        bundle.putString("Class", classDownMenu.text.toString())
                        bundle.putString("Password", passwordText.text.toString())
                        createAccSecondFragment.arguments = bundle
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.frameLayout, createAccSecondFragment, null).commit()
                    }
                } else {
                    if (nameText.text.isNullOrEmpty() || surnameText.text.isNullOrEmpty() ||
                        numberText.text.isNullOrEmpty() || dropDownMenu.text.isNullOrEmpty() ||
                        passwordText.text.isNullOrEmpty()
                    ) {
                        Toast.makeText(requireContext(), "Boş alanları doldurunuz!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        val createAccSecondFragment = CreateAccSecondFragment()
                        val bundle = Bundle()
                        bundle.putString("Name",nameText.text.toString())
                        bundle.putString("Surname", surnameText.text.toString())
                        bundle.putString("Number", numberText.text.toString())
                        bundle.putString("State", dropDownMenu.text.toString())
                        bundle.putString("Class", "")
                        bundle.putString("Password", passwordText.text.toString())
                        createAccSecondFragment.arguments = bundle
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.frameLayout, createAccSecondFragment, null).commit()
                    }
                }

            }


            return fragmentCreateAccFirstBinding.root
        }

    }


}