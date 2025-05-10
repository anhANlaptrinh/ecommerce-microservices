const searchInput = document.querySelector('.search');
const categories_menu = document.querySelector('.categories-menu');
const cart = document.querySelector('.cart');
const header_bottom = document.querySelector('.header-bottom');
const menu_bar = document.querySelector('.menu-bar');
const menu_close = document.querySelector('.menu-close');
const menu_mobile_nav = document.querySelector('.menu-mobile-nav');
const back_to_top = document.querySelector('.back-to-top');
//generateSearchKey
const messages = [
  'Bàn phím akko',
  'Bàn phím keychron',
  'Tai nghe gaming',
  'Ghế gaming',
  'Màn hình',
];

let currentMessageIndex = 0;
let currentCharIndex = 0;
const typingSpeed = 100;
let placeholder = '';
let listProducts = [];
let listCategories = [];
let listBlogs = [];
function initApp() {
  //Fetch dữ liệu từ file json
  const request1 = fetch('http://localhost:8888/api/products').then((response) =>
    response.json()
  );
  const request2 = fetch('http://localhost:8888/api/categories').then((response) =>
    response.json()
  );
  const request3 = fetch('./data/Blogs.json').then((response) =>
    response.json()
  );
  Promise.all([request1, request2, request3])
    .then(([data1, data2, data3]) => {
      listProducts = data1;
      listCategories = data2;
      listBlogs = data3;
      generateSearchKey(); //Tạo animation placeholder
      generateCategories(listCategories); // render list danh mục sản phẩm
      generatefeaturedCategoriesList(listCategories); // render danh mục sản phẩm nổi bật
      generateProductList(listProducts); //render list sản phẩm
      generateCollections(listProducts); // render bộ sưu tập sản phẩm
      generateBlogs(listBlogs); //render blogs
      toastMessage();
      loadCartToHTML();
    })
    .catch((error) => {
      console.error(error);
    });
}
//Xu ly header top fixed và back to top
window.addEventListener('scroll', () => {
  const currentScroll = window.pageYOffset;
  if (currentScroll > 150) {
    header_bottom.classList.add('sticky');
    back_to_top.classList.add('active');
  } else {
    header_bottom.classList.remove('sticky');
    back_to_top.classList.remove('active');
  }
});
$('.back-to-top').click(function () {
  $('html, body').animate(
    {
      scrollTop: 0,
    },
    800
  );
  return false;
});
//Dong mo danh sach san pham
categories_menu.addEventListener('click', function () {
  const categories_content = document.querySelector('.categories-content');
  categories_content.classList.toggle('show');
});

//Dong mo gio hang
cart.addEventListener('click', function () {
  const mini_cart = document.querySelector('.mini-cart');
  mini_cart.classList.toggle('show');
});

//menu-mobile toggle
menu_bar.addEventListener('click', function () {
  menu_bar.classList.toggle('hidden');
  menu_close.classList.toggle('hidden');
  menu_mobile_nav.classList.toggle('show');
});
menu_close.addEventListener('click', function () {
  menu_bar.classList.toggle('hidden');
  menu_close.classList.toggle('hidden');
  menu_mobile_nav.classList.toggle('show');
});

/**
 * Hàm này dùng để format giá từ number -> tiền tệ VN
 * @param number
 * @returns formatted_number
 */
function formatVND(number) {
  let formatted_number = new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  })
    .format(number)
    .replaceAll('.', ',');
  return formatted_number;
}

function numberValidation(n) {
  if (isNaN(n)) {
    return false;
  } else {
    return true;
  }
}
/**
 * Hàm này dùng để show toast message mỗi khi thêm sp vào giỏ
 */
function toastMessage() {
  const btn_carts = document.querySelectorAll('.btn-cart');
  btn_carts.forEach((btn) => {
    btn.addEventListener('click', () => {
      Toastify({
        //Gọi thư viện
        text: 'Đã thêm vào giỏ hàng',
        style: {
          background: 'linear-gradient(to right, #00b09b, #96c93d)',
        },
        duration: 3000,
      }).showToast();
    });
  });
}
/**
 * Tạo animation placeholder cho input tìm kiếm
 */
function generateSearchKey() {
  if (currentMessageIndex === messages.length) currentMessageIndex = 0; //Khi duyệt hết mảng thì quay lại từ đầu

  const message = messages[currentMessageIndex]; //message hiện tại

  if (currentCharIndex < message.length) {
    placeholder += message.charAt(currentCharIndex);
    searchInput.setAttribute('placeholder', placeholder); //generate từng kí tự
    currentCharIndex++; //kí tự tiếp theo
    setTimeout(generateSearchKey, typingSpeed); //Gọi đệ quy để generate kí tự tiếp theo
  } else {
    // Sau mỗi message generate xong
    setTimeout(() => {
      currentCharIndex = 0; //đặt lại kí tự đầu tiên
      currentMessageIndex++; //message tiếp theo
      placeholder = ''; //đặt lại placeholder
      searchInput.setAttribute('placeholder', placeholder);
      generateSearchKey(); //Gọi đệ quy
    }, 1000);
  }
}

function generateCategories(listCategories) {
  const categories_list = document.querySelector('.categories-list');
  listCategories.forEach((item) => {
    categories_list.innerHTML += `
    <li class="categories-item">
    <a href="store.html?idCategory=${item.id}"> ${item.name} </a>
   </li>
    `;
  });
}

/**
 * Hàm này để lấy tổng sản phẩm trong một danh mục sản phẩm
 * @param id
 * @returns sum
 */
function getCountProductsOfCategories(id) {
  let sum = 0;
  listProducts.forEach((p) => {
    if (p.category == id) {
      sum++;
    }
  });
  return sum;
}

/**
 * Hàm này dùng để render danh mục sản phẩm nổi bật
 * @param listCategories
 */
function generatefeaturedCategoriesList(listCategories) {
  const featured_categories_list = document.querySelector(
    '.featured-categories-list'
  );
  if (featured_categories_list) {
    listCategories.forEach((item) => {
      featured_categories_list.innerHTML += `
        <div class="category-item">
        <div class="category-item-info">
          <h4 class="category-item-name">
            <a href="store.html?idCategory=${item.id}"> ${item.name} </a>
          </h4>
          <div class="total-items">${getCountProductsOfCategories(
            item.id
          )} sản phẩm</div>
          <a href="" class="shop-btn">+ Xem thêm</a>
        </div>
        <div class="category-item-thumb">
          <a href="store.html?idCategory=${item.id}">
            <img
              src="${item.img}"
              alt="${item.name}"
            />
          </a>
        </div>
      </div>`;
    });
  }
}
/**
 * Hàm này dùng để render list sản phẩm
 * @param listProducts
 */
function generateProductList(listProducts) {
  const productList = document.querySelector(
    '.product-list .owl-stage-outer .owl-stage'
  );
  if (productList) {
    const listShow = listProducts.slice(0, 5);
    listShow.forEach((item) => {
      productList.innerHTML += `<div class="owl-item">
      <div class="product-item">
      <div class="product-thumb">
      <a href="product-detail.html?id=${item.id}">
      <img
      src="${item.img}"
      alt="product-name"
      />
      </a>
      </div>
      <div class="product-caption">
      <div class="manufacture-product">
      <a href="product-detail.html?id=${item.id}">${item.brand}</a>
      </div>
      <div class="product-name">
      <a href="product-detail.html?id=${item.id}">
      <h4>
      ${item.name}
      </h4>
      </a>
      </div>
      <div class="price-box">
            <span class="regular-price ${
              item.price_old ? 'sale' : ''
            }">${formatVND(item.price)}</span>
            <span class="old-price">${
              item.price_old ? formatVND(item.price_old) : ''
            }</span>
            </div>
            <button class="btn-cart" onclick="addToCart(${
              item.id
            })" type="button">
            Thêm vào giỏ
          </button>
          </div>
          </div>
    </div>`;
    });
    //Sử dụng thư viên owl-carousel
    $(document.querySelector('.product-list')).owlCarousel({
      loop: true,
      margin: 30,
      singleItem: true,
      items: 4,
      dots: false,
      nav: true,
      navText: [
        '<i class="bi bi-arrow-left"></i>',
        '<i class="bi bi-arrow-right"></i>',
      ],
      navContainer: '.box-title .custom-nav-best-seller',
      responsive: {
        0: {
          items: 2,
        },
        600: {
          items: 2,
        },
        1000: {
          items: 4,
        },
      },
    });
  }
}

/**
 * Hàm này dùng để render list bộ sưu tập sản phẩm
 * @param listProducts
 */
function generateCollections(listProducts) {
  const collections = document.querySelector('.collections-list');
  if (collections) {
    const listShow = listProducts.slice(0, 10);
    listShow.forEach((item) => {
      collections.innerHTML += `<li class="collections-item">
      <div class="product-item">
        <div class="product-thumb">
          <a href="product-detail.html?id=${item.id}">
            <img
              src="${item.img}"
              alt="product-name"
            />
          </a>
        </div>
        <div class="product-caption">
          <div class="manufacture-product">
            <a hrefproduct-detail.html?id=${item.id}">${item.brand}</a>
          </div>
          <div class="product-name">
            <a href="product-detail.html?id=${item.id}">
              <h4>
                ${item.name}
              </h4>
            </a>
          </div>
          <div class="price-box">
            <span class="regular-price ${
              item.price_old ? 'sale' : ''
            }">${formatVND(item.price)}</span>
            <span class="old-price">${
              item.price_old ? formatVND(item.price_old) : ''
            }</span>
          </div>
          <button class="btn-cart" onclick="addToCart(${
            item.id
          })" type="button">
            Thêm vào giỏ
          </button>
        </div>
      </div>
    </li>`;
    });
  }
}
/**
 * Hàm này dùng để render blogs
 * @param listBlogs 
 */
function generateBlogs(listBlogs) {
  const blog_list = document.querySelector('.blog-list');
  const blog_main = document.querySelector('.blog-main');
  let blogMain = listBlogs.slice(0, 1)[0]; //Lấy blog mới nhất làm blog chính
  let blogList = listBlogs.slice(1, 5);// Lấy 4 blog tiếp theo để show ra giao diện
  //render HTML
  if (blog_list && blog_main) {
    blog_main.innerHTML = `
   <div class="blog-image">
   <a href="blog-detail.html?id=${blogMain.id}">
     <img src="${blogMain.img}" alt="${blogMain.title}" />
   </a>
 </div>
 <div class="blog-title">
   <h4>
     <a href="blog-detail.html?id=${blogMain.id}"> ${blogMain.title}</a>
   </h4>
 </div>
 <div class="blog-date">${blogMain.date}</div>
   `;
    blogList.forEach((item) => {
      blog_list.innerHTML += `
     <li class="blog-item">
     <div class="blog-item-image">
       <a href="blog-detail.html?id=${item.id}">
         <img src="${item.img}" alt="${item.title}" />
       </a>
     </div>
     <div class="blog-item-detail">
       <div class="blog-item-title">
         <a href="blog-detail.html?id=${item.id}"> ${item.title}</a>
       </div>
       <div class="blog-item-date">${item.date}</div>
     </div>
   </li>
     `;
    });
  }
}

function getProductById(id) {
  return listProducts.find((item) => item.id == id);
}
/**
 * Hàm này để thêm sản phẩm vào giỏ hàng
 * @param productId
 */
function addToCart(productId) {
  // 1. Gửi request xác thực (cookie đã kèm theo)
  fetch('http://localhost:8888/api/auth/hello', {
    method: 'GET',
    credentials: 'include'
  })
  .then(res => {
    if (!res.ok) throw new Error('Chưa đăng nhập hoặc token hết hạn');
    return res.json();
  })
  .then(data => {
    const userId = data.userId;
    const input_quantity = document.getElementById('quantity');
    const quantity = input_quantity ? Number(input_quantity.value) : 1;

    // 2. Gọi API thêm sản phẩm vào giỏ hàng
    return fetch('http://localhost:8888/api/cart/items', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-USER-ID': userId
      },
      body: JSON.stringify({
        productId: productId,
        quantity: quantity
      })
    });
  })
  .then(res => {
    if (!res.ok) throw new Error('Thêm vào giỏ thất bại');
    return res.json();
  })
  .then(() => {
    loadCartToHTML(); // Cập nhật giỏ
  })
  .catch(err => {
    console.error(err);
    alert("Vui lòng đăng nhập để thêm vào giỏ hàng!");
    window.location.href = 'login.html';
  });
}

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
        // Nếu giỏ trống, gọi API xoá giỏ luôn
        return fetch('http://localhost:8888/api/cart', {
          method: 'DELETE',
          headers: {
            'X-USER-ID': userId
          }
        });
      }
    })
    .finally(() => {
      loadCartToHTML();
    })
    .catch(err => {
      console.error("❌ Lỗi khi xóa sản phẩm:", err);
      alert("Không thể xóa sản phẩm. Thử lại!");
    });
}

/**
 * Hàm này dùng để render ra giao diện giỏ hàng
 */
function loadCartToHTML() {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    document.querySelector('.cart-count').textContent = 0;
    document.querySelector('.cart-list').textContent = 'Bạn chưa đăng nhập';
    document.querySelector('.total-price').textContent = '0đ';
    return;
  }

  fetch('http://localhost:8888/api/cart', {
    headers: {
      'X-USER-ID': userId
    }
  })
    .then(response => {
      if (!response.ok) throw new Error('Không thể lấy giỏ hàng');
      return response.json();
    })
    .then(cart => {
      const cart_count = document.querySelector('.cart-count');
      const cart_list = document.querySelector('.cart-list');
      const total_price = document.querySelector('.total-price');

      cart_count.textContent = cart.items.reduce((sum, item) => sum + item.quantity, 0);
      cart_list.innerHTML = '';
      let sum = 0;

      if (cart.items.length === 0) {
        cart_list.textContent = 'Bạn chưa thêm sản phẩm';
      } else {
        cart.items.forEach(item => {
          const product = getProductById(item.productId); // lấy từ listProducts đã fetch sẵn
          if (!product) return;

          sum += product.price * item.quantity;

          cart_list.innerHTML += `
            <li class="cart-item">
              <div class="cart-image">
                <a href="product-detail.html?id=${product.id}">
                  <img src="${product.img}" />
                </a>
              </div>
              <div class="cart-info">
                <h4><a href="product-detail.html?id=${product.id}">${product.name}</a></h4>
                <span>${item.quantity} x <span>${formatVND(product.price)}</span></span>
              </div>
              <div class="del-icon" onclick="deleteCart(${product.id})">
                <i class="bi bi-x-circle"></i>
              </div>
            </li>`;
        });
      }
      total_price.textContent = formatVND(sum);
    })
    .catch(error => {
      console.error('❌ Lỗi khi load giỏ hàng:', error);
      document.querySelector('.cart-list').textContent = 'Không thể hiển thị giỏ hàng';
    });
}

document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.querySelector(".frm-login");
  const registerForm = document.querySelector(".frm-register");
  const showRegisterLink = document.getElementById("show-register");
  const showLoginLink = document.getElementById("show-login");
  const title = document.querySelector(".section-title");

  // Chuyển từ form đăng nhập sang form đăng ký
  if (showRegisterLink) {
    showRegisterLink.addEventListener("click", function () {
      loginForm.style.display = "none";
      registerForm.style.display = "block";
      title.textContent = "Đăng ký";
    });
  }

  // Chuyển từ form đăng ký sang form đăng nhập
  if (showLoginLink) {
    showLoginLink.addEventListener("click", function () {
      registerForm.style.display = "none";
      loginForm.style.display = "block";
      title.textContent = "Đăng nhập";
    });
  }

  if (loginForm) {
    loginForm.addEventListener("submit", function (event) {
      event.preventDefault(); // Ngăn chặn reload trang
  
      const email = document.getElementById("email").value;
      const password = document.getElementById("password").value;
  
      // Kiểm tra nếu là tài khoản admin
      if (email === "admin@gmail.com" && password === "admin123") {
        window.location.href = "dashboard.html"; // Chuyển trang nếu đúng
      } else {
        localStorage.removeItem("username");
        // Gửi yêu cầu đến backend để xác thực với các tài khoản khác
        fetch("http://localhost:8888/api/auth/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({ username: email, password: password })
        })
        .then(response => response.json())
        .then(data => {
          console.log("👉 Kết quả trả về từ backend login:", data);
          if (data.token) {
            console.log("✅ Nhận được token:", data.token);
            // Lấy tên người dùng từ email (phần trước dấu @)
            const username = email.split('@')[0];
            localStorage.setItem("username", username);
  
            // Cập nhật giao diện và thay đổi chữ "Đăng nhập" thành tên người dùng
            updateLoginText(username);
  
            // Chuyển đến trang chính sau khi đăng nhập
            //window.location.href = "index.html";
          } else {
            console.warn("⚠ Không có token trả về, dữ liệu:", data);
            const errorMessage = document.getElementById("validate-password");
            errorMessage.textContent = "Email hoặc mật khẩu không đúng!";
          }
        })
        .catch(error => {
          console.error("Có lỗi xảy ra khi xác thực:", error);
          const errorMessage = document.getElementById("validate-password");
          errorMessage.textContent = "Lỗi kết nối với server!";
        });
      }
    });
  }  

  // Xử lý đăng ký
  if (registerForm) {
    registerForm.addEventListener("submit", function (event) {
      event.preventDefault();

      const email = document.getElementById("register-email").value;
      const password = document.getElementById("register-password").value;

      fetch("http://localhost:8888/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: email, password: password, role: "USER" }),
      })
      .then(response => response.json())
      .then(data => {
        if (data.success) {
          alert("Đăng ký thành công!");
          loginForm.style.display = "block";
          registerForm.style.display = "none";
        } else {
          alert(data.message || "Đã xảy ra lỗi khi đăng ký!");
        }
      })
      .catch(error => {
        console.error("Có lỗi xảy ra khi đăng ký:", error);
      });
    });
  }
});

document.addEventListener("DOMContentLoaded", function () {
  fetch("http://localhost:8888/api/auth/hello", {
    method: "GET",
    credentials: "include"
  })
  .then(response => {
      if (!response.ok) {
        throw new Error("Unauthorized");
      }
      return response.json();
  })
  .then(data => {
    if (data.username) {
      const username = data.username.split('@')[0];
      localStorage.setItem("username", username);
      localStorage.setItem("userId", parseInt(data.userId));
      updateLoginText(username);  // Hiển thị tên người dùng
    } else {
      localStorage.removeItem("authToken");
      localStorage.removeItem("username");
      localStorage.removeItem("userId");
      updateLoginText("Đăng nhập");  // Hiển thị lại "Đăng nhập" nếu có lỗi
    }
  })
  .catch(error => {
    console.error("Lỗi khi lấy thông tin người dùng:", error);
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    updateLoginText("Đăng nhập");  // Hiển thị lại "Đăng nhập" nếu có lỗi
  });
});

// Hàm để cập nhật giao diện với tên người dùng hoặc chữ "Đăng nhập"
function updateLoginText(username) {
  const loginElements = document.querySelectorAll('.login');

  loginElements.forEach(element => {
    const loginText = element.querySelector('span') || element;

    if (loginText) {
      if (username && username !== "Đăng nhập") {
        loginText.textContent = "Name: " + username;
      } else {
        loginText.textContent = "Đăng nhập";
      }
    }
  });
}


initApp();
