import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.nzby.coursekotlin.UserApplication
import com.nzby.coursekotlin.dao.CartItemDao
import com.nzby.coursekotlin.dao.ProductDao
import com.nzby.coursekotlin.models.CartItemWithProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(
    private val cartItemDao: CartItemDao,
    application: Application
) : AndroidViewModel(application) {
    private val appContext = application as UserApplication
    val userId = appContext.userId

    // LiveData для отслеживания списка товаров в корзине
    private val _cartItems = MutableLiveData<List<CartItemWithProduct>>()
    val cartItems: LiveData<List<CartItemWithProduct>> get() = _cartItems

    init {
        fetchCartItems()
    }

    // Функция для загрузки данных в корутине
    private fun fetchCartItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = cartItemDao.getCartItemsWithProduct(userId!!)
                withContext(Dispatchers.Main) {
                    _cartItems.value = items
                }
            } catch (e: Exception) {
                e.printStackTrace() // Логируем ошибку
            }
        }
    }
}


