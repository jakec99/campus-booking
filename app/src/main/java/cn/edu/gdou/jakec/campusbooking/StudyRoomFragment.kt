package cn.edu.gdou.jakec.campusbooking

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.data.StudyDatabase
import cn.edu.gdou.jakec.campusbooking.data.StudySeat
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudyRoomBinding

class StudyRoomFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: StudyRoomViewModel
    private lateinit var binding: FragmentStudyRoomBinding

    private lateinit var layout: ViewGroup
    private lateinit var seatMapLayout: LinearLayout
    private lateinit var rowLayout: LinearLayout
    private lateinit var view: TextView
    private lateinit var selectView: TextView
    private var selectId = ""
    private var seatClickable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_study_room, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudyDatabase.getInstance(application).studySeatDao
        val viewModelFactory = StudyRoomViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(StudyRoomViewModel::class.java)

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
                seatClickable = true
                binding.button.isEnabled = true
                binding.refreshButton.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.seats.observe(viewLifecycleOwner, {
            it?.let {
                setSeats(it, viewModel.getRoom().xCount, viewModel.getRoom().yCount)
            }
        })

        viewModel.orderId.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(StudyRoomFragmentDirections
                    .actionStudyRoomFragmentToStudyOrderFragment(it))
            }
        })

        binding.refreshButton.setOnClickListener {
            seatClickable = false
            binding.button.isEnabled = false
            binding.refreshButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.refreshRoom()
        }

        binding.button.setOnClickListener {
            seatClickable = false
            binding.button.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.createOrder(selectId)
        }

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.info -> {
                    true
                }
                R.id.schedule -> {
                    findNavController().navigate(StudyRoomFragmentDirections.actionStudyRoomFragmentToStudyScheduleFragment(
                        viewModel.getRoom()))
                    true
                }
                else -> true
            }
        }

        binding.button.isEnabled = false
        binding.refreshButton.isEnabled = false

        val room = StudyRoomFragmentArgs.fromBundle(requireArguments()).room
        viewModel.setRoom(room)
        viewModel.refreshRoom()

        return binding.root
    }

    private fun createOrder(selectId: String, roomId: String) {
        if (selectId != "") {
            seatClickable = false
            binding.button.isEnabled = false
            binding.refreshButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.createOrder(selectId)
        }
    }

    private fun setSeats(seat: List<StudySeat>, xCount: Int, yCount: Int) {
        seatMapLayout = LinearLayout(MyApplication.context)
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
                createSeat(seat[count], count++)
            }

//            create last empty column
            createEmptySeat(50)
        }

//        Create last empty row
        createRow()
        createEmptySeat(200)

        seatClickable = true
        binding.button.isEnabled = true
        binding.refreshButton.isEnabled = true
        binding.progressBar.visibility = View.GONE
    }

    private fun createEmptySeat(size: Int) {
        val layoutParams = LinearLayout.LayoutParams(size, size)
        view = TextView(MyApplication.context)
        view.layoutParams = layoutParams
        rowLayout.addView(view)
    }

    private fun createSeat(seat: StudySeat, count: Int) {

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
                view.tag = listOf(seat.id, seat.name, "unavailable")
            } else if (seat.isOccupied) {
//                occupied
                view.setBackgroundResource(R.drawable.ic_seat_occupied)
                view.setTextColor(requireContext().getColor(R.color.red_800))
                view.tag = listOf(seat.id, seat.name, "occupied")
            } else if (!seat.isOccupied) {
//                standard
                view.setBackgroundResource(R.drawable.ic_seat_standard)
                view.setTextColor(requireContext().getColor(R.color.light_blue_800))
                view.tag = listOf(seat.id, seat.name, "standard")
            }
        }

        if (seat.type == "none") {
            view.gravity = Gravity.CENTER
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
            view.setBackgroundResource(R.drawable.ic_seat_empty)
        }

        rowLayout.addView(view)
    }

    private fun createRow() {
        rowLayout = LinearLayout(MyApplication.context)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        seatMapLayout.addView(rowLayout)
    }

    override fun onClick(view: View) {
        if (seatClickable) {
            val tag = view.tag as? List<*>
            if (tag != null) {
                when {
                    tag[2] == "unavailable" -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.the_selected_seat_is_unavailable), Toast.LENGTH_SHORT
                        ).show()
                    }
                    tag[2] == "occupied" -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.the_selected_seat_is_occupied), Toast.LENGTH_SHORT
                        ).show()
                    }
                    tag[2] == "standard" -> {
                        val name = tag[1]
                        selectId = tag[0] as String
                        select(view as TextView, name as String)
                    }
                }
            }
        }
    }

    private fun select(view: TextView, name: String) {
        cancelSelect()

        val text = getString(R.string.booking) + " " + name
        view.setBackgroundResource(R.drawable.ic_seat_select)
        binding.button.text = text
        binding.button.isEnabled = true

        selectView = view
    }

    private fun cancelSelect() {
        if (this::selectView.isInitialized) {
            selectView.setBackgroundResource(R.drawable.ic_seat_standard)
        }
        binding.button.text = getString(R.string.select_a_seat)
        binding.button.isEnabled = false
    }

}