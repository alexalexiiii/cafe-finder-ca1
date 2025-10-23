package org.wit.placemark.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.placemark.models.CafeModel

interface CafeListener {
    fun onCafeClick(cafe: CafeModel)
}
class CafeAdapter(private var cafes: List<CafeModel>,
                  private val listener: CafeListener) :
    RecyclerView.Adapter<CafeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardCafeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val cafe = cafes[holder.adapterPosition]
        holder.bind(cafes, listener)
    }

    override fun getItemCount(): Int = cafes.size

    class MainHolder(private val binding: CardCafeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cafe: List<CafeModel>, listener: CafeListener) {

            binding.cafeName.text = cafe.name
            binding.cafeFavourite.text = "Favourite: ${cafe.favouriteMenuItem}"
            binding.cafeRating.text = "Rating: ${cafe.rating} ⭐"
            binding.cafeLocation.text = "📍 ${cafe.location}"

            if (cafe.image.isNotEmpty()) {
                binding.cafeImage.setImageURI(Uri.parse(cafe.image))
            } else {
                binding.cafeImage.setImageResource(R.drawable.ic_placeholder) // fallback image
            }

            // Handle click
            binding.root.setOnClickListener { listener.onPlacemarkClick(cafe) }
        }

    }
