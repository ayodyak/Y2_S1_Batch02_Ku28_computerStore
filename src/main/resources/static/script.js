// REMOVE ALL DUMMY DATA AND STATE ARRAYS
// All data will be fetched from backend

// DOM Elements
const searchInput = document.getElementById('search-input');
const partsTableBody = document.getElementById('parts-table-body');
const purchaseRequestsList = document.getElementById('purchase-requests-list');
const partModal = document.getElementById('part-modal');
const purchaseModal = document.getElementById('purchase-modal');
const confirmModal = document.getElementById('confirm-modal');
const partForm = document.getElementById('part-form');
const purchaseForm = document.getElementById('purchase-form');

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // First get authenticated user info (managerId etc.) then initialize app
    fetch('/api/auth-check', { credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error('Not authenticated');
            return res.json();
        })
        .then(auth => {
            // store auth info globally for other functions
            window._auth = auth || {};
            initializeEventListeners();
            fetchAndRenderParts();
            // Fetch suppliers first, then requests
            fetchAndPopulateSuppliers(null, fetchAndRenderPurchaseRequests);
            fetchAndRenderLowStock();
        })
        .catch(err => {
            console.error('Auth check failed:', err);
            // If not authenticated redirect to login
            // window.location.href = '/login'; // keep commented to avoid unexpected redirects during dev
            initializeEventListeners();
            fetchAndRenderParts();
            fetchAndPopulateSuppliers(null, fetchAndRenderPurchaseRequests);
            fetchAndRenderLowStock();
        });
});

// Fetch parts from backend and render
function fetchAndRenderParts() {
    partsTableBody.innerHTML = '<tr><td colspan="9">Loading...</td></tr>';
    fetch('/api/parts', { credentials: 'include' })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
            }
            return res.json();
        })
        .then(parts => {
            console.log('Fetched parts:', parts);
            window._parts = parts;
            renderPartsTable(parts);
            updateStatistics(parts);
        })
        .catch(err => {
            partsTableBody.innerHTML = `<tr><td colspan="9" style="color:red;">Error loading parts: ${err.message}</td></tr>`;
            console.error('Error fetching parts:', err);
        });
}

// Fetch purchase requests from backend and render
function fetchAndRenderPurchaseRequests() {
    purchaseRequestsList.innerHTML = '<p>Loading...</p>';
    fetch('/api/purchase-requests', { credentials: 'include' })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
            }
            return res.json();
        })
        .then(requests => {
            console.log('Fetched purchase requests:', requests);
            window._purchaseRequests = requests;
            renderPurchaseRequests(requests);
            // --- Check open requests for manager and update UI ---
            const managerId = (window._auth && window._auth.employeeId) ? window._auth.employeeId : null;
            const MAX_OPEN_REQUESTS = 5;
            const openRequests = managerId ? requests.filter(r => r.managerId === managerId && r.status && !['COMPLETED', 'REJECTED'].includes(r.status.toUpperCase())).length : 0;
            const limitMsgDiv = document.getElementById('purchase-request-limit-msg');
            const newBtn = document.getElementById('new-purchase-request-btn');
            if (openRequests >= MAX_OPEN_REQUESTS) {
                limitMsgDiv.textContent = 'Manager has reached the maximum number of open purchase requests.';
                limitMsgDiv.style.display = 'block';
                if (newBtn) newBtn.disabled = true;
            } else {
                limitMsgDiv.textContent = '';
                limitMsgDiv.style.display = 'none';
                if (newBtn) newBtn.disabled = false;
            }
        })
        .catch(err => {
            purchaseRequestsList.innerHTML = `<p style="color:red;">Error loading requests: ${err.message}</p>`;
            console.error('Error fetching purchase requests:', err);
        });
}

// Fetch low stock items and update alert
function fetchAndRenderLowStock() {
    const alert = document.getElementById('low-stock-alert');
    alert.style.display = 'none';
    fetch('/api/parts/low-stock', { credentials: 'include' })
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(`HTTP ${res.status}: ${text}`); });
            }
            return res.json();
        })
        .then(lowStockItems => {
            console.log('Fetched low stock:', lowStockItems);
            updateLowStockAlert(lowStockItems);
        })
        .catch(err => {
            alert.style.display = 'block';
            document.getElementById('low-stock-message').innerHTML = `<span style='color:red;'>Error loading low stock: ${err.message}</span>`;
            console.error('Error fetching low stock:', err);
        });
}

// Event Listeners
function initializeEventListeners() {
    // Tab switching
    document.querySelectorAll('.tab-trigger').forEach(trigger => {
        trigger.addEventListener('click', function() {
            switchTab(this.dataset.tab);
        });
    });

    // Search functionality
    searchInput.addEventListener('input', function() {
        renderPartsTable();
    });

    // Button event listeners
    document.getElementById('add-part-btn').addEventListener('click', function() {
        openPartModal();
    });

    document.getElementById('new-purchase-request-btn').addEventListener('click', function() {
        openPurchaseModal();
    });

    // Form submissions
    partForm.addEventListener('submit', function(e) {
        e.preventDefault();
        handlePartFormSubmit();
    });



    purchaseForm.addEventListener('submit', function(e) {
        e.preventDefault();
        handlePurchaseFormSubmit();
    });

    // Purchase part selection change
    document.getElementById('purchase-part').addEventListener('change', function() {
        updateEstimatedCost();
    });

    document.getElementById('purchase-quantity').addEventListener('input', function() {
        updateEstimatedCost();
    });

    // Modal close on background click
    window.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal')) {
            closeAllModals();
        }
    });
}

// Tab Management
function switchTab(tabName) {
    // Update tab triggers
    document.querySelectorAll('.tab-trigger').forEach(trigger => {
        trigger.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');

    // Update tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`${tabName}-tab`).classList.add('active');
}

// Parts Table Management
function renderPartsTable(parts) {
    const searchTerm = searchInput.value.toLowerCase();
    const filteredParts = parts.filter(part => {
        return part.name.toLowerCase().includes(searchTerm) ||
            part.brand.toLowerCase().includes(searchTerm) ||
            part.category.toLowerCase().includes(searchTerm) ||
            String(part.partId).toLowerCase().includes(searchTerm);
    });
    partsTableBody.innerHTML = '';
    filteredParts.forEach(part => {
        const row = document.createElement('tr');
        let status, statusClass;
        if (part.stockLevel === 0) {
            status = 'Out of Stock';
            statusClass = 'status-out-of-stock';
        } else if (part.stockLevel <= part.reorderLevel) {
            status = 'Low Stock';
            statusClass = 'status-low-stock';
        } else {
            status = 'In Stock';
            statusClass = 'status-in-stock';
        }
        row.innerHTML = `
            <td>${part.partId}</td>
            <td>${part.name}</td>
            <td>${part.brand}</td>
            <td>${part.category}</td>
            <td>$${Number(part.price).toFixed(2)}</td>
            <td>${part.stockLevel}</td>
            <td>${part.reorderLevel}</td>
            <td><span class="${statusClass}">${status}</span></td>
            <td>
                <div class="flex gap-2">
                    <button class="btn btn-sm btn-outline" onclick="editPart(${part.partId})">Edit</button>
                    <button class="btn btn-sm btn-outline" onclick="createPurchaseRequestForPart(${part.partId})">Order</button>
                    <button class="btn btn-sm btn-destructive" onclick="confirmDeletePart(${part.partId})">Delete</button>
                </div>
            </td>
        `;
        partsTableBody.appendChild(row);
    });
}



// Parts Management
let currentEditingPart = null;

function openPartModal(partId = null) {
    currentEditingPart = partId;
    const modal = document.getElementById('part-modal');
    const title = document.getElementById('part-modal-title');
    if (partId) {
        fetch(`/api/parts/${partId}`, { credentials: 'include' })
            .then(res => res.json())
            .then(part => {
                title.textContent = 'Edit Part';
                document.getElementById('part-name').value = part.name;
                document.getElementById('part-brand').value = part.brand;
                document.getElementById('part-category').value = part.category;
                document.getElementById('part-price').value = part.price;
                document.getElementById('part-stock').value = part.stockLevel;
                document.getElementById('part-reorder').value = part.reorderLevel;
                modal.classList.add('active');
            });
    } else {
        title.textContent = 'Add New Part';
        partForm.reset();
        modal.classList.add('active');
    }
}

function closePartModal() {
    document.getElementById('part-modal').classList.remove('active');
    currentEditingPart = null;
    partForm.reset();
}

function handlePartFormSubmit() {
    const formData = {
        name: document.getElementById('part-name').value,
        brand: document.getElementById('part-brand').value,
        category: document.getElementById('part-category').value,
        price: parseFloat(document.getElementById('part-price').value),
        stockLevel: parseInt(document.getElementById('part-stock').value),
        reorderLevel: parseInt(document.getElementById('part-reorder').value)
    };
    let method = 'POST';
    let url = '/api/parts';
    if (currentEditingPart) {
        method = 'PUT';
        url = `/api/parts/${currentEditingPart}`;
    }
    fetch(url, {
        method: method,
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => {
            if (!res.ok) throw new Error('Failed to save part');
            closePartModal();
            fetchAndRenderParts();
            fetchAndRenderLowStock();
        })
        .catch(err => console.error('Error saving part:', err));
}

function editPart(partId) {
    openPartModal(partId);
}

function confirmDeletePart(partId) {
    fetch(`/api/parts/${partId}`, { credentials: 'include' })
        .then(res => res.json())
        .then(part => {
            showConfirmModal(
                `Are you sure you want to delete "${part.name}"? This action cannot be undone.`,
                () => deletePart(partId)
            );
        });
}

function deletePart(partId) {
    fetch(`/api/parts/${partId}`, { method: 'DELETE', credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error('Failed to delete part');
            fetchAndRenderParts();
            fetchAndRenderLowStock();
            closeConfirmModal();
        })
        .catch(err => console.error('Error deleting part:', err));
}

// Purchase Request Management
let _suppliers = [];

function fetchAndPopulateSuppliers(selectedSupplierId = null, callback) {
    fetch('/api/suppliers', { credentials: 'include' })
        .then(res => res.json())
        .then(suppliers => {
            _suppliers = suppliers;
            window._suppliers = suppliers;
            const select = document.getElementById('purchase-supplier');
            if (select) {
                select.innerHTML = '<option value="">Select supplier</option>';
                suppliers.forEach(supplier => {
                    const option = document.createElement('option');
                    option.value = supplier.supplierId;
                    option.textContent = supplier.name;
                    if (selectedSupplierId && String(supplier.supplierId) === String(selectedSupplierId)) {
                        option.selected = true;
                    }
                    select.appendChild(option);
                });
            }
            if (callback) callback();
        });
}

function openPurchaseModal(selectedPartId = null) {
    populatePurchasePartSelect();
    fetchAndPopulateSuppliers();
    if (selectedPartId) {
        document.getElementById('purchase-part').value = selectedPartId;
        updateEstimatedCost();
    }
    document.getElementById('purchase-modal').classList.add('active');
}

function closePurchaseModal() {
    document.getElementById('purchase-modal').classList.remove('active');
    purchaseForm.reset();
    document.getElementById('estimated-cost').textContent = '$0.00';
}

function populatePurchasePartSelect() {
    const select = document.getElementById('purchase-part');
    select.innerHTML = '<option value="">Select part</option>';
    (window._parts || []).forEach(part => {
        const option = document.createElement('option');
        option.value = part.partId;
        option.textContent = `${part.name} (${part.brand}) - $${Number(part.price).toFixed(2)}`;
        select.appendChild(option);
    });
}

function updateEstimatedCost() {
    const partId = document.getElementById('purchase-part').value;
    const quantity = parseInt(document.getElementById('purchase-quantity').value) || 0;
    const part = (window._parts || []).find(p => String(p.partId) === String(partId));
    if (part && quantity > 0) {
        const cost = part.price * quantity;
        document.getElementById('estimated-cost').textContent = `$${cost.toFixed(2)}`;
        return;
    }
    document.getElementById('estimated-cost').textContent = '$0.00';
}

function handlePurchaseFormSubmit() {
    const partId = document.getElementById('purchase-part').value;
    const quantity = parseInt(document.getElementById('purchase-quantity').value);
    const supplierId = document.getElementById('purchase-supplier').value;
    const part = (window._parts || []).find(p => String(p.partId) === String(partId));
    if (!part || !supplierId) return;
    // Do not set managerId here; server will map authenticated user to manager if missing
    const now = new Date();
    const requestDate = now.toISOString();
    const status = 'pending';
    const totalAmount = part.price * quantity;
    const items = [
        {
            partId: part.partId,
            quantity: quantity,
            unitPrice: part.price,
            status: 'pending',
            supplierId: supplierId // Ensure supplierId is included in each item
        }
    ];
    const formData = {
        // managerId intentionally omitted so server derives it from authenticated user
        supplierId: parseInt(supplierId),
        requestDate: requestDate,
        status: status,
        totalAmount: totalAmount,
        items: items
    };
    fetch('/api/purchase-requests', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => {
            if (!res.ok) return res.text().then(text => { throw new Error(text); });
            closePurchaseModal();
            fetchAndRenderPurchaseRequests();
            // Clear any previous limit message
            document.getElementById('purchase-request-limit-msg').style.display = 'none';
            document.getElementById('purchase-request-limit-msg').textContent = '';
        })
        .catch(err => {
            const limitMsgDiv = document.getElementById('purchase-request-limit-msg');
            limitMsgDiv.textContent = err.message;
            limitMsgDiv.style.display = 'block';
            if (err.message.includes('maximum number of open purchase requests')) {
                document.getElementById('new-purchase-request-btn').disabled = true;
            }
        });
}

function createPurchaseRequestForPart(partId) {
    openPurchaseModal(partId);
}

function renderPurchaseRequests(requests) {
    purchaseRequestsList.innerHTML = '';
    if (!requests || requests.length === 0) {
        purchaseRequestsList.innerHTML = '<p class="text-muted-foreground">No purchase requests found.</p>';
        return;
    }
    requests.forEach(request => {
        const card = document.createElement('div');
        card.className = 'request-card';
        const statusClass = `status-${request.status}`;
        // Extract part name and quantity from the first item (if exists)
        let partName = 'N/A';
        let quantity = 'N/A';
        if (request.items && request.items.length > 0) {
            const item = request.items[0];
            partName = (window._parts || []).find(p => p.partId === item.partId)?.name || 'Part #' + item.partId;
            quantity = item.quantity !== undefined ? item.quantity : 'N/A';
        }
        // Lookup supplier name from window._suppliers
        let supplierName = 'N/A';
        if (window._suppliers && window._suppliers.length > 0 && request.supplierId) {
            const supplier = window._suppliers.find(s => String(s.supplierId) === String(request.supplierId));
            supplierName = supplier ? supplier.name : 'Supplier #' + request.supplierId;
        }
        card.innerHTML = `
            <div class="request-header">
                <div>
                    <div class="request-title">${partName}</div>
                    <div class="request-meta">Request ID: ${request.requestId || ''} \u2022 Created: ${request.requestDate ? new Date(request.requestDate).toLocaleDateString() : ''}</div>
                </div>
                <span class="badge ${statusClass}">${request.status ? request.status.charAt(0).toUpperCase() + request.status.slice(1) : ''}</span>
            </div>
            <div class="request-details">
                <div class="request-detail">
                    <div class="request-detail-label">Quantity</div>
                    <div>${quantity}</div>
                </div>
                <div class="request-detail">
                    <div class="request-detail-label">Estimated Cost</div>
                    <div>$${Number(request.estimatedCost || request.totalAmount || 0).toFixed(2)}</div>
                </div>
                <div class="request-detail">
                    <div class="request-detail-label">Supplier</div>
                    <div>${supplierName}</div>
                </div>
            </div>
            <div class="request-actions">
                <button class="btn btn-sm btn-destructive" onclick="deletePurchaseRequest(${request.requestId})">Delete</button>
            </div>
        `;
        purchaseRequestsList.appendChild(card);
    });
}

function deletePurchaseRequest(requestId) {
    fetch(`/api/purchase-requests/${requestId}`, { method: 'DELETE', credentials: 'include' })
        .then(res => {
            if (!res.ok) throw new Error('Failed to delete request');
            fetchAndRenderPurchaseRequests();
        })
        .catch(err => {
            alert('Error deleting request: ' + err.message);
        });
}

// Statistics and Alerts
function updateStatistics(parts) {
    document.getElementById('total-parts-count').textContent = parts.length;
    const lowStockCount = parts.filter(part => part.stockLevel <= part.reorderLevel).length;
    const lowStockBadge = document.getElementById('low-stock-badge');
    if (lowStockCount > 0) {
        document.getElementById('low-stock-count').textContent = lowStockCount;
        lowStockBadge.style.display = 'flex';
    } else {
        lowStockBadge.style.display = 'none';
    }
}

function updateLowStockAlert(lowStockParts) {
    const alert = document.getElementById('low-stock-alert');
    const message = document.getElementById('low-stock-message');
    if (lowStockParts.length > 0) {
        const outOfStockParts = lowStockParts.filter(part => part.stockLevel === 0);
        let messageText = `You have ${lowStockParts.length} part(s) that need attention`;
        if (outOfStockParts.length > 0) {
            messageText += `, including ${outOfStockParts.length} out of stock items`;
        }
        messageText += '.';
        const itemsList = document.createElement('div');
        itemsList.className = 'low-stock-items';
        lowStockParts.forEach(part => {
            const item = document.createElement('div');
            item.className = 'low-stock-item';
            item.innerHTML = `
                <span>${part.name} - Stock: ${part.stockLevel}/${part.reorderLevel}</span>
                <button class="btn btn-sm btn-default" onclick="createPurchaseRequestForPart(${part.partId})">Order Now</button>
            `;
            itemsList.appendChild(item);
        });

        message.innerHTML = messageText;
        message.appendChild(itemsList);
        alert.style.display = 'flex';
    } else {
        alert.style.display = 'none';
    }
}

// Modal Management
function showConfirmModal(message, onConfirm) {
    document.getElementById('confirm-message').textContent = message;
    document.getElementById('confirm-action-btn').onclick = onConfirm;
    document.getElementById('confirm-modal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirm-modal').classList.remove('active');
}

function closeAllModals() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.classList.remove('active');
    });
    currentEditingPart = null;
}

// Utility Functions
function formatCurrency(amount) {
    return `$${amount.toFixed(2)}`;
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString();
}

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // ESC to close modals
    if (e.key === 'Escape') {
        closeAllModals();
    }

    // Ctrl/Cmd + N to add new part
    if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
        e.preventDefault();
        openPartModal();
    }
});

// Auto-save to localStorage (optional)
function saveToLocalStorage() {
    localStorage.setItem('computerStoreParts', JSON.stringify(parts));
    localStorage.setItem('computerStorePurchaseRequests', JSON.stringify(purchaseRequests));
}

function loadFromLocalStorage() {
    const savedParts = localStorage.getItem('computerStoreParts');
    const savedRequests = localStorage.getItem('computerStorePurchaseRequests');

    if (savedParts) {
        parts = JSON.parse(savedParts);
    }

    if (savedRequests) {
        purchaseRequests = JSON.parse(savedRequests);
    }
}

// Save data whenever it changes
function saveData() {
    saveToLocalStorage();
}

// Load data on page load
// Uncomment the next line if you want to enable localStorage persistence
// loadFromLocalStorage();
