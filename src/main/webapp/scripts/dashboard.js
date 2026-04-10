/**
 * adauga sau sterge un produs din dashboard folosit pe pagina de produse
 * @param {number} productId id ul produsului
 * @param {HTMLElement} btnElement butonul pe care s a dat click
 * @param {string} contextPath calea de baza a aplicatiei
 */
function toggleWatchlist(productId, btnElement, contextPath) {
    const icon = btnElement.querySelector('svg');

    if (!icon) {
        console.error('SVG icon not found! Ensure Lucide generated the icon.');
        return;
    }

    const isAdded = icon.classList.contains('text-red-500');
    const action = isAdded ? 'remove' : 'add';
    const url = contextPath + '/Dashboard';

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=' + action + '&productId=' + productId
    })
        .then(response => {
            if (!response.ok) throw new Error('Network or server error');
            return response.json();
        })
        .then(data => {
            if (data.status === 'success') {
                if (action === 'add') {
                    icon.setAttribute('fill', 'currentColor');
                    icon.classList.add('text-red-500');
                    icon.classList.remove('text-slate-400');
                } else {
                    icon.setAttribute('fill', 'none');
                    icon.classList.add('text-slate-400');
                    icon.classList.remove('text-red-500');
                }
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Detailed error:', error);
            alert('An error occurred while updating the Dashboard.');
        });
}

/**
 * sterge un produs din dashboard si da refresh folosit pe pagina dashboard
 * @param {number} productId id ul produsului
 * @param {string} contextPath calea de baza a aplicatiei
 */
function removeFromDashboard(productId, contextPath) {
    if (!confirm('Are you sure you want to remove this product from your Dashboard?')) {
        return;
    }

    fetch(contextPath + '/Dashboard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=remove&productId=' + productId
    })
        .then(response => {
            if (!response.ok) throw new Error('Server error');
            return response.json();
        })
        .then(data => {
            if (data.status === 'success') {
                window.location.reload();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. The product could not be removed.');
        });
}

// deschide modalul si populeaza datele
function openAlertModal(id, name, currentPrice) {
    document.getElementById('modalProductId').value = id;
    document.getElementById('modalCurrentPrice').value = currentPrice;
    document.getElementById('modalProductName').textContent = name;

    // resetam la zece la suta si calculam pretul
    document.getElementById('discountPercentage').value = 10;
    calculateTargetPrice();

    // afisare cu animatie tailwind
    const modal = document.getElementById('alertModal');
    const content = document.getElementById('alertModalContent');
    modal.classList.remove('hidden');
    setTimeout(() => {
        content.classList.remove('scale-95', 'opacity-0');
        content.classList.add('scale-100', 'opacity-100');
    }, 10);
}

// inchide modalul
function closeAlertModal() {
    const modal = document.getElementById('alertModal');
    const content = document.getElementById('alertModalContent');
    content.classList.remove('scale-100', 'opacity-100');
    content.classList.add('scale-95', 'opacity-0');
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 200);
}

// calculeaza pretul tinta live in functie de procentaj
function calculateTargetPrice() {
    const currentPrice = parseFloat(document.getElementById('modalCurrentPrice').value);
    const percent = parseFloat(document.getElementById('discountPercentage').value);

    if(!isNaN(currentPrice) && !isNaN(percent)) {
        const targetPrice = currentPrice - (currentPrice * (percent / 100));
        document.getElementById('targetPriceDisplay').textContent = targetPrice.toFixed(2) + ' RON';
    }
}

// salveaza alerta in baza de date folosind fetch
function savePriceAlert(contextPath) {
    const productId = document.getElementById('modalProductId').value;
    const percentage = document.getElementById('discountPercentage').value;

    fetch(`${contextPath}/SetAlert`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `productId=${productId}&percentage=${percentage}`
    })
        .then(response => response.json())
        .then(data => {
            // afisam mesajele de eroare sau succes in engleza
            if (data.status === 'success') {
                alert('Price alert saved successfully');
                closeAlertModal();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            // inregistram eroarea in consola in engleza
            console.error('Fetch error processing alert request', error);
            alert('Network error occurred while saving the alert');
        });
}
