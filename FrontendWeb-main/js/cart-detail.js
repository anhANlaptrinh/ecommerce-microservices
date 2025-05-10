function initCartDetail() {
  fetch("http://localhost:8888/api/products")
    .then((response) => response.json())
    .then((response) => {
      listProducts = response;
      loadCartDetail();
    });
}
//Format number sang VND
function formatVND(number) {
  let formatted_number = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  })
    .format(number)
    .replaceAll('.', ',');
  return formatted_number;
}

function getProductById(id) {
  return listProducts.find((item) => item.id == id);
}
/**
 * Xoá sản phẩm khỏi giỏ hàng
 * @param id 
 */
function deleteCart(productId) {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    alert("Bạn cần đăng nhập để thao tác giỏ hàng!");
    return;
  }

  fetch(`http://localhost:8888/api/cart/items/${productId}`, {
    method: 'DELETE',
    headers: {
      'X-USER-ID': userId
    }
  })
    .then(res => {
      if (!res.ok) throw new Error("Không thể xóa sản phẩm");
      return res.json();
    })
    .then(cart => {
      if (cart.items.length === 0) {
        return fetch('http://localhost:8888/api/cart', {
          method: 'DELETE',
          headers: {
            'X-USER-ID': userId
          }
        });
      }
    })
    .finally(() => {
      loadCartDetail();    // <-- Bạn thiếu cái này
      loadCartToHTML();
    })
    .catch(err => {
      console.error("❌ Lỗi khi xóa sản phẩm:", err);
      alert("Không thể xóa sản phẩm. Thử lại!");
    });
}

/**
 * Hàm này dùng để render lại giao diện chi tiết giỏ hàng
 */
function loadCartDetail() {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    alert("Bạn cần đăng nhập để xem giỏ hàng!");
    return;
  }

  fetch(`http://localhost:8888/api/cart`, {
    method: 'GET',
    headers: {
      'X-USER-ID': userId
    }
  })
    .then(response => {
      if (!response.ok) throw new Error("Không thể tải giỏ hàng");
      return response.json();
    })
    .then(cart => {
      const table_cart = document.querySelector('.table-cart');
      const title_number_cart = document.querySelector('.title-number-cart');
      const summary_total_number = document.querySelector('.summary-total-number');

      const count = cart.items.reduce((sum, item) => sum + item.quantity, 0);
      let sum = 0;
      table_cart.innerHTML = '';
      title_number_cart.innerHTML = `Bạn đang có <strong> ${count} sản phẩm </strong> trong giỏ hàng`;

      cart.items.forEach(item => {
        const product = getProductById(item.productId);
        if (!product) return;

        sum += item.quantity * product.price;

        let newHTML = `
        <div class="media-line-item line-item">
          <div class="media-left">
            <div class="item-img">
              <a href="/product-detail.html?id=${product.id}">
                <img src="${product.img}" alt="${product.name}" />
              </a>
            </div>
            <div class="item-remove" onclick="confirmDelete(${product.id})">
              <i class="bi bi-x"></i>
            </div>
          </div>
          <div class="media-right">
            <div class="item-info">
              <a href="/product-detail.html?id=${product.id}">${product.name}</a>
            </div>
            <div class="item-price">
              <span>${formatVND(product.price)}</span>
              <del>${product.price_old ? formatVND(product.price_old) : ''}</del>
            </div>
          </div>
          <div class="media-total">
            <div class="item-total-price">
              <span>${formatVND(item.quantity * product.price)}</span>
            </div>
            <div class="item-qty">
              <div class="qty">
                <button type="button" onclick="minusQuantity(${product.id})" class="btn-qty">-</button>
                <input type="text" class="quantity-input" value="${item.quantity}" min="1" />
                <button type="button" onclick="plusQuantity(${product.id})" class="btn-qty">+</button>
              </div>
            </div>
          </div>
        </div>`;
        table_cart.innerHTML += newHTML;
      });

      summary_total_number.textContent = formatVND(sum);
    })
    .catch(err => {
      console.error("❌ Lỗi khi load cart detail:", err);
    });
}

/**
 * Hàm này để xác nhận trước khi xoá khỏi giỏ hàng
 * @param id 
 */
function confirmDelete(id) {
  const btn_modal = document.getElementById('btnModal');
  btn_modal.click(); //Thực hiện click button modal để hiện modal của bootstrap
  const btn_delete = document.getElementById('btn-delete'); //Đây là nút xoá trong modal
  btn_delete.onclick = function () {
    deleteCart(id); //Thực hiện xoá sản phẩm khỏi giỏ hàng
  };
}
/**
 * Hàm này để tăng số lượng sp trong giỏ hàng
 * @param id
 */
function minusQuantity(productId) {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    alert("Bạn cần đăng nhập!");
    return;
  }

  fetch(`http://localhost:8888/api/cart/items/${productId}/decrease`, {
    method: 'PUT',
    headers: {
      'X-USER-ID': userId
    }
  })
  .then(res => {
    if (!res.ok) throw new Error("Không thể giảm số lượng");
    return res.json();
  })
  .then(() => {
    loadCartDetail();
    loadCartToHTML();
  })
  .catch(err => {
    console.error("❌ Lỗi khi giảm số lượng:", err);
  });
}

/**
 * Hàm này để giảm số lượng sp trong giỏ hàng
 * @param id
 */
function plusQuantity(productId) {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    alert("Bạn cần đăng nhập!");
    return;
  }

  fetch('http://localhost:8888/api/cart/items', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-USER-ID': userId
    },
    body: JSON.stringify({
      productId: productId,
      quantity: 1
    })
  })
    .then(res => res.json())
    .then(() => {
      loadCartDetail();
      loadCartToHTML();
    })
    .catch(err => {
      console.error("❌ Lỗi khi tăng số lượng:", err);
    });
}

initCartDetail();
