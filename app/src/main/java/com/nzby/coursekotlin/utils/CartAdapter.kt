import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nzby.coursekotlin.R
import com.nzby.coursekotlin.models.CartItem
import com.nzby.coursekotlin.models.CartItemWithProduct

class CartAdapter(
    private val cartItems: List<CartItemWithProduct>,
    private val onRemoveClick: (CartItemWithProduct) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        Log.d("CartAdapter", "Binding item at position $position: ${cartItem.productName}")
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.cart_item_image)
        private val itemName: TextView = itemView.findViewById(R.id.cart_item_name)
        private val itemDescription: TextView = itemView.findViewById(R.id.cart_item_description)
        private val itemPrice: TextView = itemView.findViewById(R.id.cart_item_price)
        private val itemQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        private val removeButton: ImageView = itemView.findViewById(R.id.cart_item_remove)

        fun bind(cartItem: CartItemWithProduct) {
            itemName.text = cartItem.productName
            itemDescription.text = cartItem.productDescription
            itemPrice.text = "₽${cartItem.productPrice}"
            itemQuantity.text = "Кол-во: ${cartItem.cartQuantity}"
            // Загружаем изображение, если оно есть
            cartItem.productImage?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                itemImage.setImageBitmap(bitmap)
            }

            // Обработчик клика на кнопку удаления
            removeButton.setOnClickListener {
                onRemoveClick(cartItem)
            }
        }
    }
}