const API_BASE_URL = 'https://api-gateway.myjenkins.click';
function initStore() {
  const request1 = fetch(`${API_BASE_URL}/api/products`).then((response) =>
    response.json()
  );
  const request2 = fetch(`${API_BASE_URL}/api/categories`).then((response) =>
    response.json()
  );
  Promise.all([request1, request2])
    .then(([data1, data2]) => {
      listProducts = data1;
      listCategories = data2;
      generateStoreProduct(listProducts);
      generateFilterBrand(listProducts);
      generateFilterCategory(listCategories);
      toastMessage();
    })
    .catch((error) => {
      console.error(error);
    });
}
//Add toast animation
function toastMessage() {
  const btn_carts = document.querySelectorAll('.btn-cart');
  btn_carts.forEach((btn) => {
    btn.addEventListener('click', () => {
      Toastify({
        text: 'Đã thêm vào giỏ hàng',
        style: {
          background: 'linear-gradient(to right, #00b09b, #96c93d)',
        },
        duration: 3000,
      }).showToast();
    });
  });
}
function generateStoreProduct(listProducts) {
  const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
  });
  let idCategory = params.idCategory;
  let nameBrand = params.nameBrand;
  const product_list_title = document.querySelector('.product-list-title');
  const product_list_store = document.querySelector('.product-list-store');
  let listFiltered = listProducts.filter(
    (item) => item.category == idCategory || item.brand == nameBrand
  );
  if ((idCategory || nameBrand) && listFiltered.length > 0) {
    // product_list_title.textContent =
    listFiltered.forEach((item) => {
      product_list_store.innerHTML += `
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
          </div>`;
    });
  } else if (product_list_store) {
    listProducts.forEach((item) => {
      product_list_store.innerHTML += `
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
        </div>`;
    });
  }
}

function generateFilterBrand(listProducts) {
  const checkbox_list_brand = document.querySelector('.checkbox-list-brand');
  const brands = Object.getOwnPropertyNames(
    Object.groupBy(listProducts, ({ brand }) => brand)
  );
  brands.forEach((item, index) => {
    checkbox_list_brand.innerHTML += `
        <li>
                            <input
                              type="checkbox"
                              name="brand-filter"
                              id="data-brand-${index}"
                            />
                            <label for="data-brand-${index}">${item}</label>
                          </li>`;
  });
}

function generateFilterCategory(listCategories) {
  const checkbox_list_category = document.querySelector(
    '.checkbox-list-category'
  );
  listCategories.forEach((item, index) => {
    checkbox_list_category.innerHTML += `
        <li>
                            <input
                              type="checkbox"
                              name="category-filter"
                              id="data-category-${index}"
                            />
                            <label for="data-category-${index}">${item.name}</label>
                          </li>`;
  });
}

document.getElementById('filter-button').addEventListener('click', () => {
  const selectedBrands = Array.from(document.querySelectorAll('input[name="brand-filter"]:checked')).map(el => el.nextElementSibling.textContent.trim());
  const selectedCategories = Array.from(document.querySelectorAll('input[name="category-filter"]:checked')).map(el => el.nextElementSibling.textContent.trim());
  const priceRanges = Array.from(document.querySelectorAll('input[name="price-filter"]:checked')).map(el => el.id);

  let minPrice = null, maxPrice = null;
  if (priceRanges.includes('price-range-0')) {
    minPrice = 0; maxPrice = 1000000;
  } else if (priceRanges.includes('price-range-1')) {
    minPrice = 1000000; maxPrice = 2000000;
  } else if (priceRanges.includes('price-range-2')) {
    minPrice = 2000000; maxPrice = 3000000;
  } else if (priceRanges.includes('price-range-3')) {
    minPrice = 3000000; maxPrice = 4000000;
  } else if (priceRanges.includes('price-range-4')) {
    minPrice = 4000000; maxPrice = 999999999;
  }

  // Tạo query string
  const queryParams = new URLSearchParams();
  selectedBrands.forEach(brand => queryParams.append('brands', brand));
  selectedCategories.forEach(cat => {
    // Chuyển tên thành ID nếu cần (ở đây bạn có thể ánh xạ theo listCategories)
    const found = listCategories.find(c => c.name === cat);
    if (found) queryParams.append('categories', found.id);
  });
  if (minPrice !== null) queryParams.append('minPrice', minPrice);
  if (maxPrice !== null) queryParams.append('maxPrice', maxPrice);

  fetch(`https://api-gateway.myjenkins.click/api/products/filter?${queryParams.toString()}`)
    .then(res => res.json())
    .then(data => {
      const container = document.querySelector('.product-list-store');
      container.innerHTML = ''; // clear cũ
      generateStoreProduct(data); // dùng lại hàm render
    });
});

initStore();
