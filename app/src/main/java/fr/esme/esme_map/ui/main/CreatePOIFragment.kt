package fr.esme.esme_map.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import fr.esme.esme_map.R

class CreatePOIFragment : Fragment() {

    companion object {
        fun newInstance() = CreatePOIFragment()
    }

    private lateinit var viewModel: CreatePOIViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.create_poi_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var poi = activity?.intent?.getParcelableExtra<LatLng>("LATLNG")

        viewModel = ViewModelProvider(this).get(CreatePOIViewModel::class.java)

    }

}