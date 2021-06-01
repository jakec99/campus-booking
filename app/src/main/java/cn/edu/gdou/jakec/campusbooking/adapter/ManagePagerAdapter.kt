package cn.edu.gdou.jakec.campusbooking.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.edu.gdou.jakec.campusbooking.*

const val TODO_PAGE_INDEX = 0
const val ROOM_PAGE_INDEX = 1
const val USER_PAGE_INDEX = 2

class ManagePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        TODO_PAGE_INDEX to { ManageTodoFragment() },
        ROOM_PAGE_INDEX to { ManageStudyFragment() },
        USER_PAGE_INDEX to { ManageStudyRoomApplyFrament() },
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}