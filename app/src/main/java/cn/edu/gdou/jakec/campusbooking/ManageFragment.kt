package cn.edu.gdou.jakec.campusbooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.edu.gdou.jakec.campusbooking.adapter.*
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageBinding
import com.google.android.material.tabs.TabLayoutMediator

class ManageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentManageBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.pager

        viewPager.adapter = ManagePagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.appBar)

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root

    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            TODO_PAGE_INDEX -> R.drawable.tab_event
            ROOM_PAGE_INDEX -> R.drawable.tab_study
            USER_PAGE_INDEX -> R.drawable.tab_me
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            TODO_PAGE_INDEX -> getString(R.string.todo)
            ROOM_PAGE_INDEX -> getString(R.string.room)
            USER_PAGE_INDEX -> getString(R.string.user)
            else -> null
        }
    }

}