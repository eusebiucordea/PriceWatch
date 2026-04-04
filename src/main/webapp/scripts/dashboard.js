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