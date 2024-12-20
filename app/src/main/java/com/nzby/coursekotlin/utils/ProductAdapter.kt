import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.databinding.ItemProductBinding
import com.nzby.coursekotlin.models.Product

class ProductAdapter(private val onAddToCart: (Product, Int) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productList = listOf<Product>()

    // Метод для обновления данных
    fun submitList(products: List<Product>) {
        productList = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            product.image?.let { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                 binding.imageViewProduct.setImageBitmap(bitmap)
            } ?: run {
                // Если изображения нет, ставим заглушку
                binding.imageViewProduct.setImageResource(R.drawable.placeholder_image)
            }
            // Устанавливаем имя и цену продукта
            binding.textViewProductName.text = product.name
            binding.textViewProductPrice.text = "Price: ${product.price}"

            // Привязываем кнопку "Add to Cart"
            binding.buttonAddToCart.setOnClickListener {
                // Передаем продукт и количество (например, 1) в метод добавления в корзину
                onAddToCart(product, 1)
            }
        }
    }
}
