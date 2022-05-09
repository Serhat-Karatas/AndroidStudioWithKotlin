package com.serhat.kotlinlandmarks

import  android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.serhat.kotlinlandmarks.databinding.RecycleRowBinding

                                                                                     //görünüm tutucu. xml i bağlama ,textviewin içinde ne yazacağın yazma
class LandmarkAdapter(val landmarkList : ArrayList<Landmark>) : RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder>() {

    class LandmarkHolder(val binding : RecycleRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandmarkHolder {     //view holder ilk oluşturulduğunda ne olacak
        val binding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)         //layout ile bağlama işleme
        return LandmarkHolder(binding)
    }

    override fun onBindViewHolder(holder: LandmarkHolder, position: Int) {       //bağlandıktan sonra ne olacak? Hangi textde ne yazacak
        holder.binding.recyclerViewTextView.text = landmarkList.get(position).name
        holder.itemView.setOnClickListener {
                                       //main activitynin contexti
            val intent = Intent(holder.itemView.context,DetailsActivity::class.java)
            intent.putExtra("landmark",landmarkList.get(position))                           //intent büyük veriyi yollamada iyi değildir. Bitmap kullanman gerekebilir
            //MySingleton.selectedLandmark = landmarkList.get(position))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {           //Kaç tane oluşturacağız.
        return landmarkList.size
    }

}