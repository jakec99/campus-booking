package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.data.StudyDatabase
import cn.edu.gdou.jakec.campusbooking.data.StudySeat
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyRoomBinding

class ManageStudyRoomFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: ManageStudyRoomViewModel
    private lateinit var binding: FragmentManageStudyRoomBinding

    private lateinit var layout: ViewGroup
    private lateinit var seatMapLayout: LinearLayout
    private lateinit var rowLayout: LinearLayout
    private lateinit var view: TextView
    private var seatClickable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_manage_study_room, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudyDatabase.getInstance(application).studySeatDao
        val viewModelFactory = ManageStudyRoomViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ManageStudyRoomViewModel::class.java)

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.title.observe(viewLifecycleOwner, {
            it?.let {
                binding.appBar.title = it
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.seatForCode.observe(viewLifecycleOwner, {
            it?.let {
                val text = "s" + it.id
                findNavController().navigate(ManageStudyRoomFragmentDirections
                    .actionManageStudyRoomFragmentToCodeFragment(it.name,
                        binding.appBar.title as String,
                        text))
            }
        })

        viewModel.seats.observe(viewLifecycleOwner, {
            it?.let {
                setSeats(it, viewModel.getRoom().xCount, viewModel.getRoom().yCount)
                seatClickable = true
                binding.refreshButton.isEnabled = true
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.order.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(ManageStudyRoomFragmentDirections
                    .actionManageStudyRoomFragmentToManageStudyOrderFragment(it))
            }
        })

        binding.refreshButton.setOnClickListener {
            binding.refreshButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.refreshRoom()
        }

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    findNavController().navigate(ManageStudyRoomFragmentDirections
                        .actionManageStudyRoomFragmentToManageStudyRoomEditFragment(viewModel.getRoom().id))
                    true
                }
                R.id.schedule -> {
                    findNavController().navigate(ManageStudyRoomFragmentDirections.actionManageStudyRoomFragmentToManageStudyScheduleFragment(
                        viewModel.getRoom()))
                    true
                }
                else -> true
            }
        }

        val room = ManageStudyRoomFragmentArgs.fromBundle(requireArguments()).room
        viewModel.setRoom(room)
        viewModel.refreshRoom()


        return binding.root
    }


    private fun setSeats(seat: List<StudySeat>, xCount: Int, yCount: Int) {
        seatMapLayout = LinearLayout(requireContext())
        seatMapLayout.orientation = LinearLayout.VERTICAL
        seatMapLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layout = binding.seatView
        layout.removeAllViews()
        layout.addView(seatMapLayout)

        var count = 0

//        Create first empty row
        createRow()
        createEmptySeat(50)

        for (i in 1..yCount) {
            createRow()

//            Create first empty column
            createEmptySeat(50)
            for (j in 1..xCount) {
                createSeat(seat[count], count++, j, i)
            }

//            create last empty column
            createEmptySeat(50)
        }

//        Create last empty row
        createRow()
        createEmptySeat(200)
    }

    private fun createEmptySeat(size: Int) {
        val layoutParams = LinearLayout.LayoutParams(size, size)
        view = TextView(MyApplication.context)
        view.layoutParams = layoutParams
        rowLayout.addView(view)
    }

    private fun createSeat(seat: StudySeat, count: Int, x: Int, y: Int) {

        val size = 120
        val margin = 10

        val layoutParams = LinearLayout.LayoutParams(size, size)

        view = TextView(MyApplication.context)
        layoutParams.setMargins(margin, margin, margin, margin)
        view.layoutParams = layoutParams

//        The view has an onclick listener only when it's type is seat
        if (seat.type == "seat") {
            view.gravity = Gravity.CENTER
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            view.text = seat.name
            view.id = count
            view.setOnClickListener(this)

            if (!seat.isEnabled) {
//                unavailable
                view.setBackgroundResource(R.drawable.ic_seat_unavailable)
                view.tag = listOf("unavailable", seat.id, seat.name)
            } else if (seat.isOccupied) {
//                occupied
                view.setBackgroundResource(R.drawable.ic_seat_occupied)
                view.setTextColor(requireContext().getColor(R.color.red_800))
                view.tag = listOf("occupied", seat.id, seat.name)
            } else if (!seat.isOccupied) {
//                standard
                view.setBackgroundResource(R.drawable.ic_seat_standard)
                view.setTextColor(requireContext().getColor(R.color.light_blue_800))
                view.tag = listOf("standard", seat.id, seat.name)
            }
        }

        if (seat.type == "none") {
            view.gravity = Gravity.CENTER
            view.setBackgroundResource(R.drawable.ic_seat_layout)
            view.tag = listOf("none", x.toString(), y.toString())
            view.setOnClickListener(this)
        }

        rowLayout.addView(view)
    }

    private fun createRow() {
        rowLayout = LinearLayout(MyApplication.context)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        seatMapLayout.addView(rowLayout)
    }

    override fun onClick(v: View) {
        if (seatClickable) {
            val tag = v.tag as? List<*>
            if (tag != null) {
                when {
                    tag[0] == "unavailable" -> {
                        val id = tag[1] as String
                        showMenu(v, R.menu.study_seat_unavailable_popup_menu, id, 0, 0)
                    }
                    tag[0] == "occupied" -> {
                        val id = tag[1] as String
                        showMenu(v, R.menu.study_seat_occupied_popup_menu, id, 0, 0)
                    }
                    tag[0] == "standard" -> {
                        val id = tag[1] as String
                        showMenu(v, R.menu.study_seat_standard_popup_menu, id, 0, 0)
                    }
                    tag[0] == "none" -> {
                        val x = tag[1] as String
                        val y = tag[2] as String
                        showMenu(v, R.menu.study_seat_none_popup_menu, "", x.toInt(), y.toInt())
                    }
                }
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, id: String, x: Int, y: Int) {
        val popup = PopupMenu(activity, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it!!.itemId) {
                R.id.code -> {
                    viewModel.getSeat(id)
                }
                R.id.enable -> {
                    viewModel.enable(id)
                }
                R.id.disable -> {
                    viewModel.disable(id)
                }
                R.id.status -> {
                    viewModel.getOrder(id)
                }
                R.id.create -> {
                    findNavController().navigate(ManageStudyRoomFragmentDirections
                        .actionManageStudyRoomFragmentToManageStudySeatCreateFragment(
                            viewModel.getRoom().id, x, y
                        ))
                }
                R.id.edit -> {
                    findNavController().navigate(ManageStudyRoomFragmentDirections
                        .actionManageStudyRoomFragmentToManageStudySeatEditFragment(id))
                }
            }
            true
        }

        popup.show()
    }


}