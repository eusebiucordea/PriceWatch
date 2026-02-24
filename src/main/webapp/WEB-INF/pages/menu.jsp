    <header class="d-flex flex-wrap justify-content-center py-3 mb-4 border-bottom">
        <a href="${pageContext.request.contextPath}" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-body-emphasis text-decoration-none">
            <svg class="bi me-2" width="40" height="32" aria-hidden="true"></svg>
            <span class="fs-4">PriceWatch</span>
        </a>
        <ul class="nav nav-pills">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/about.jsp" class="nav-link">About</a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link">Dashboard</a>
            </li>
            <li class="nav-item">
                <a href="#" class="nav-link">Products</a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/Login" class="nav-link">Login</a>
            </li>
        </ul>
        <script>
            document.addEventListener("DOMContentLoaded", function() {
                // 1. Luăm link-ul paginii curente pe care se află utilizatorul
                const currentLocation = window.location.href;

                // 2. Selectăm toate link-urile din meniul tău
                const navLinks = document.querySelectorAll('.nav-pills .nav-link');

                // 3. Trecem prin fiecare link
                navLinks.forEach(link => {
                    // Dacă adresa din link se potrivește cu adresa paginii curente...
                    if (link.href === currentLocation) {
                        // ...îi adăugăm clasa 'active' pentru a se face albastru
                        link.classList.add('active');
                        link.setAttribute('aria-current', 'page');
                    } else {
                        // Altfel, ne asigurăm că nu o are
                        link.classList.remove('active');
                        link.removeAttribute('aria-current');
                    }
                });
            });
        </script>
    </header>
